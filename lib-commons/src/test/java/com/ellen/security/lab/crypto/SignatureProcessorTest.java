package com.ellen.security.lab.crypto;

import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import static org.junit.jupiter.api.Assertions.*;

class SignatureProcessorTest {

    private SignatureProcessor signatureProcessor;
    private byte[] content;

    SignatureProcessorTest() {
        content = "this is a test".getBytes(StandardCharsets.UTF_8);
        signatureProcessor = new SignatureProcessor("classpath:dummy-keystore.p12", "changeit");
    }

    @Test
    void testHashAndVerify() {
        byte[] hash_256_1 = SignatureProcessor.doHash(content, 256);
        assertEquals(32, hash_256_1.length);

        byte[] hash_256_2 = SignatureProcessor.doHash(content, 256);
        assertArrayEquals(hash_256_1, hash_256_2);

        byte[] hash_512 = SignatureProcessor.doHash(content, 512);
        assertEquals(64, hash_512.length);
    }

    @Test
    void testSign() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        byte[] hash = SignatureProcessor.doHash(content, 256);
        String signedResult = signatureProcessor.sign(hash);

        // verify result
        String[] resultArray = signedResult.split("\\.");
        assertEquals(2, resultArray.length);
        Signature sig = Signature.getInstance("SHA256withRSA", signatureProcessor.bcProvider);
        sig.initVerify(signatureProcessor.getSigningPublicKey());
        sig.update(DatatypeConverter.parseHexBinary(resultArray[0]));
        assertTrue(sig.verify(DatatypeConverter.parseBase64Binary(resultArray[1])));
    }

}