package com.ellen.security.lab.config;

import com.ellen.security.lab.detector.FilePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMStringEncryptor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JasyptProperties.class)
public class JasyptConfig {

    @Bean
    public StringEncryptor jasyptStringEncryptor(JasyptProperties jasyptProperties) {
        SimpleGCMConfig config = new SimpleGCMConfig();
        config.setAlgorithm(jasyptProperties.getAlgorithm());
        config.setSecretKeyPassword(jasyptProperties.getPassword());
        config.setSecretKeyIterations(1000);
        config.setSecretKeySalt(jasyptProperties.getSalt());
        config.setSecretKeyAlgorithm(jasyptProperties.getSecretKeyAlgorithm());
        return new SimpleGCMStringEncryptor(config);
    }

    @Bean
    public EncryptablePropertyDetector encryptablePropertyDetector() {
        return new FilePropertyDetector();
    }
}
