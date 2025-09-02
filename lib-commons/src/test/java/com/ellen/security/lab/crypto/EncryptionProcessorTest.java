package com.ellen.security.lab.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.*;

import static com.ellen.security.lab.crypto.SignatureProcessor.SIGNING_KEY;
import static com.ellen.security.lab.rest.RestTemplateFactory.getKeyStore;
import static jakarta.xml.bind.DatatypeConverter.printBase64Binary;
import static jakarta.xml.bind.DatatypeConverter.printHexBinary;
import static org.junit.jupiter.api.Assertions.*;

class EncryptionProcessorTest {

    private static PrivateKey privateKey;
    private static PublicKey originalPublicKey;
    private static String publicKeyString;

    @BeforeAll
    static void setUp() throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        KeyStore keyStore = getKeyStore("classpath:dummy-keystore.p12", "changeit");
        privateKey = (PrivateKey) keyStore.getKey(SIGNING_KEY, "changeit".toCharArray());
        originalPublicKey = keyStore.getCertificate(SIGNING_KEY).getPublicKey();
        publicKeyString = printBase64Binary(originalPublicKey.getEncoded());
    }

    @Test
    void testConvertStringToPublicKey() {
        PublicKey convertedPublicKey = EncryptionProcessor.convertStringToPublicKey(publicKeyString);

        assertNotNull(convertedPublicKey);
        assertEquals("RSA", convertedPublicKey.getAlgorithm());
        assertArrayEquals(originalPublicKey.getEncoded(), convertedPublicKey.getEncoded());
    }

    @Test
    void testEncryptDecrypt() {
        String plainContext = "test data for encryption";
        EncryptionProcessor.EncryptedContent encryptedContent =
                EncryptionProcessor.encryptHybrid(plainContext, publicKeyString);
        assertNotNull(encryptedContent);

        String decryptedContext = EncryptionProcessor.decryptHybrid(encryptedContent, privateKey);
        assertEquals(plainContext, decryptedContext);
    }

    @Test
    void testGenerateSecretKeyLength() throws NoSuchAlgorithmException, NoSuchProviderException {
        SecretKey secretKey = EncryptionProcessor.generateAES256Key();
        byte[] keyBytes = secretKey.getEncoded();
        assertEquals(256 >> 3, keyBytes.length);
    }

    @Test
    void testGenerateSecretCreatesUniqueKeys() throws NoSuchAlgorithmException, NoSuchProviderException {
        SecretKey key1 = EncryptionProcessor.generateAES256Key();
        SecretKey key2 = EncryptionProcessor.generateAES256Key();

        assertNotNull(key1);
        assertNotNull(key2);
        assertNotEquals(printHexBinary(key1.getEncoded()), printHexBinary(key2.getEncoded())
        );
    }

}