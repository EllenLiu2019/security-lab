package com.ellen.security.lab.crypto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Queues;
import com.squareup.crypto.rsa.NativeRSAEngine;
import com.squareup.jnagmp.Gmp;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.BlockingQueue;

import static com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMByteEncryptor.AES_KEY_SIZE;
import static jakarta.xml.bind.DatatypeConverter.parseBase64Binary;

@Slf4j
@Component
public class EncryptionProcessor {

    // 96 bit IV - Ensure the uniqueness of encryption.
    private static final int GCM_IV_LENGTH = 12;

    // 128 bit tag - To ensure integrity, AES has 128-bit blocks.
    private static final int GCM_TAG_LENGTH = 128;

    private static final String SYMMETRIC_CIPHER_ALGORITHM = "AES/GCM/NoPadding"; // or ChaCha20-Poly1305

    private static final String KEY_PAIR_ALGORITHM = "RSA/ECB/OAEPWithSHA256AndMGF1Padding"; // or ECC


    static final BlockingQueue<CipherWrapper> cipherWrappers =
            Queues.newArrayBlockingQueue(Math.max(2, Runtime.getRuntime().availableProcessors()));

    private static Provider bcProvider;

    static {
        //Bouncy castle + GMP significantly improves the performance of RSA encryption due to
        //native modPow function, this will try to install the capability if available.
        bcProvider = Security.getProvider("BC");
        if (bcProvider == null) {
            bcProvider = new BouncyCastleProvider();
            Security.addProvider(bcProvider);
        }

        try {
            System.setProperty("jna.library.path", "/opt/homebrew/lib");
            Gmp.checkLoaded();
            bcProvider.put("Signature.RSA", NativeRSAEngine.class.getName());

            log.info("GMP Libs found, will use native RSA implementation (fastest)");
        } catch (Throwable e) {
            log.info("GMP Libs not found, will use pure Java RSA implementation. {}", e.getMessage(), e);
        }

        while (cipherWrappers.remainingCapacity() > 0) {
            cipherWrappers.add(new CipherWrapper());
        }
    }

    /**
     * Hybrid encryption:
     * Use a randomly generated "symmetric key" to AES-encrypt the payload,
     * and use the "public key" to RSA-encrypt the "symmetric key".
     */
    public static EncryptedContent encryptHybrid(String content, String publicKeyStr) {
        try {
            CipherWrapper wrapper = cipherWrappers.take();

            try {
                GCMParameterSpec spec = generateIVSpec();
                SecretKey aesKey = generateAES256Key();
                PublicKey publicKey = convertStringToPublicKey(publicKeyStr);

                // Symmetric encryption, encryption strength 256
                wrapper.aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
                byte[] EncryptedContent = wrapper.aesCipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

                // Asymmetric encryption, public key encryption random secret key
                byte[] pkEncAesKey = aesKey.getEncoded();
                wrapper.rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] aesEncrypted = wrapper.rsaCipher.doFinal(pkEncAesKey);

                return new EncryptedContent(
                        DatatypeConverter.printBase64Binary(EncryptedContent),
                        DatatypeConverter.printBase64Binary(aesEncrypted),
                        DatatypeConverter.printBase64Binary(spec.getIV()));
            } finally {
                cipherWrappers.add(wrapper);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static GCMParameterSpec generateIVSpec() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return new GCMParameterSpec(GCM_TAG_LENGTH, iv);
    }

    static SecretKey generateAES256Key() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", bcProvider);
        keyGenerator.init(AES_KEY_SIZE, new SecureRandom());
        return keyGenerator.generateKey();
    }

    public static PublicKey convertStringToPublicKey(String KeyString) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(parseBase64Binary(KeyString));
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    static String decryptHybrid(EncryptedContent encryptedContent, PrivateKey privateKey) {
        try {
            EncryptionProcessor.CipherWrapper wrapper = cipherWrappers.take();

            try {
                byte[] iv = DatatypeConverter.parseBase64Binary(encryptedContent.getIv());
                String encContent = encryptedContent.getContent();

                byte[] encryptedAesKey = DatatypeConverter.parseBase64Binary(encryptedContent.getKey());
                wrapper.rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] plainAesKey = wrapper.rsaCipher.doFinal(encryptedAesKey);
                SecretKey key = new SecretKeySpec(plainAesKey, "AES");

                wrapper.aesCipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
                byte[] EncryptedContent = wrapper.aesCipher.doFinal(DatatypeConverter.parseBase64Binary(encContent));

                return new String(EncryptedContent, StandardCharsets.UTF_8);
            } finally {
                cipherWrappers.add(wrapper);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class CipherWrapper {

        private final Cipher aesCipher;
        private final Cipher rsaCipher;

        CipherWrapper() {
            try {
                this.aesCipher = Cipher.getInstance(SYMMETRIC_CIPHER_ALGORITHM, bcProvider);
                this.rsaCipher = Cipher.getInstance(KEY_PAIR_ALGORITHM, bcProvider);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class EncryptedContent {

        private String content;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String key;

        private String iv;

        @SuppressWarnings("unused")
        public EncryptedContent() {
        }

        @SuppressWarnings("WeakerAccess")
        public EncryptedContent(String content, String key, String iv) {
            this.content = content;
            this.key = key;
            this.iv = iv;
        }

        @SuppressWarnings("unused")
        public String getContent() {
            return content;
        }

        @SuppressWarnings("unused")
        public String getKey() {
            return key;
        }

        @SuppressWarnings("unused")
        public String getIv() {
            return iv;
        }
    }
}
