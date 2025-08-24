package com.ellen.security.lab.utils;


import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMStringEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class EncryptorDecryptor {

    private final StringEncryptor encryptor;

    public EncryptorDecryptor() throws IOException {
        String key = IOUtils.toString(
                Objects.requireNonNull(Files.newInputStream(Paths.get("../../", "secret_key.b64"))),
                StandardCharsets.UTF_8);
        SimpleGCMConfig config = new SimpleGCMConfig();
        config.setAlgorithm("AES/GCM/NoPadding");
        config.setSecretKeyPassword(key);
        config.setSecretKeyIterations(1000);
        config.setSecretKeySalt("c3NwaXRfc2FsdF9zd2VldAo=");
        config.setSecretKeyAlgorithm("PBKDF2WithHmacSHA256");
        this.encryptor = new SimpleGCMStringEncryptor(config);
    }

    @Test
    public void test() {
        log.info("ENC({})", encryptor.encrypt("happy@email.com"));
    }

    @Test
    public void decrypt() {
        String encrypted = "ENC(ZheRl0RmoUsJdywreJNZrjBZJICfZyDuQT4A9pOejeYR4l9t0M/gzkDQqE7K)";
        encrypted = StringUtils.stripStart(encrypted, "ENC(");
        encrypted = StringUtils.stripEnd(encrypted, ")");
        log.info(encryptor.decrypt(encrypted));
    }

    @Test
    public void encrypt_decrypt() {
        String str = "Hello World";
        String encrypted = encryptor.encrypt(str);
        String decrypted = encryptor.decrypt(encrypted);

        assertEquals(str, decrypted);
    }
}
