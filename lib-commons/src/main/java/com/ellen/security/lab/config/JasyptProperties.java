package com.ellen.security.lab.config;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ConfigurationProperties(prefix = "jasypt.encryptor")
public class JasyptProperties {

    private final static String FILE_PREFIX = "FILE:";

    private final static String CLASSPATH_PREFIX = "CLASSPATH:";

    private String algorithm;

    private String secretKeyAlgorithm;

    private String salt;

    private String password;

    private String passwordContent;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getSecretKeyAlgorithm() {
        return secretKeyAlgorithm;
    }

    public void setSecretKeyAlgorithm(String secretKeyAlgorithm) {
        this.secretKeyAlgorithm = secretKeyAlgorithm;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPassword() {
        if (this.passwordContent == null) {
            if (this.password == null || this.password.isEmpty()) {
                throw new RuntimeException("Please set jasypt.encryptor.password");
            }
            if (password.startsWith(FILE_PREFIX)) {
                try (InputStream is = new FileInputStream(password.substring(FILE_PREFIX.length()))) {
                    passwordContent = IOUtils.toString(is, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load encrypted value from file: " + password);
                }
            } else if (password.startsWith(CLASSPATH_PREFIX)) {
                try (InputStream is = this.getClass().getClassLoader()
                        .getResourceAsStream(password.substring(CLASSPATH_PREFIX.length()))) {
                    passwordContent = IOUtils.toString(is, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load encrypted value from classpath: " + password);
                }
            } else {
                passwordContent = password;
            }
        }
        return this.passwordContent;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
