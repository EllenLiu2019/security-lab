package com.ellen.security.lab.crypto;

import com.ellen.security.lab.crypto.spi.NativeSHA256withRSA;
import com.ellen.security.lab.crypto.spi.NativeSHA512withRSA;
import com.google.common.hash.Hashing;
import com.squareup.crypto.rsa.NativeRSAEngine;
import com.squareup.jnagmp.Gmp;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.cert.Certificate;

import static com.ellen.security.lab.rest.RestTemplateFactory.getKeyStore;

@Slf4j
public class SignatureProcessor {

    static final String SIGNING_KEY = "signning-key";

    Provider bcProvider = new BouncyCastleProvider();

    private PublicKey signingPublicKey;
    private PrivateKey signingKey;

    // Bouncy castle + GMP significantly improves the performance of RSA encryption due to
    // native modPow function, this will try to install the capability if available.
    public SignatureProcessor(String keyStoreFile, String keyStorePassword) {
        try {
            KeyStore keyStore = getKeyStore(keyStoreFile, keyStorePassword);
            if (keyStore.containsAlias(SIGNING_KEY)) {
                try {
                    System.setProperty("jna.library.path", "/opt/homebrew/lib");
                    Gmp.checkLoaded();

                    bcProvider.put("Signature.RSA", NativeRSAEngine.class.getName());
                    bcProvider.put("Signature.SHA256withRSA", NativeSHA256withRSA.class.getName());
                    bcProvider.put("Signature.SHA512withRSA", NativeSHA512withRSA.class.getName());

                    log.info("Detected Gmp libs, RSA based signature operations will be optimised !! ");

                } catch (Throwable e) {
                    log.warn("Error when attempting to load Gmp lib: ", e);
                    log.info("No Gmp lib detected, RSA based signature operations will be SLOW :( ");

                    assert bcProvider != null;
                    bcProvider.put("Signature.RSA", DigestSignatureSpi.noneRSA.class.getName());
                    bcProvider.put("Signature.SHA256withRSA", DigestSignatureSpi.SHA256.class.getName());
                    bcProvider.put("Signature.SHA512withRSA", DigestSignatureSpi.SHA512.class.getName());
                }

                this.signingKey = (PrivateKey) keyStore.getKey(SIGNING_KEY, keyStorePassword.toCharArray());
                Certificate cert = keyStore.getCertificate(SIGNING_KEY);
                if (cert != null) {
                    this.signingPublicKey = cert.getPublicKey();
                }
            }
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
            log.warn("Found invalid keystore / password combination, replacing unsafe RestTemplate", e);
        }
    }

    public static byte[] doHash(byte[] content, int bitLength) {
        return switch (bitLength) {
            case 256 -> Hashing.sha256().hashBytes(content).asBytes();
            case 384 -> Hashing.sha384().hashBytes(content).asBytes();
            case 512 -> Hashing.sha512().hashBytes(content).asBytes();
            default -> throw new IllegalArgumentException("Unsupported bit length: " + bitLength);
        };
    }

    public String sign(byte[] bytes) {
        try {
            log.debug("Generating signature using local key.. ");

            Signature sig = Signature.getInstance("SHA256withRSA", bcProvider);
            sig.initSign(signingKey);
            sig.update(bytes);
            return DatatypeConverter.printHexBinary(bytes) + "." + DatatypeConverter.printBase64Binary(sig.sign());
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public PublicKey getSigningPublicKey() {
        return this.signingPublicKey;
    }
}
