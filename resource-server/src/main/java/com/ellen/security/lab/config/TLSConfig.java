package com.ellen.security.lab.config;

import com.ellen.security.lab.rest.RestTemplateFactory;
import com.ellen.security.lab.utils.ResourceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

@Configuration
public class TLSConfig {

    @Value("${security.keystore}")
    private String keystore;

    @Value("${security.keystorePassword}")
    private String keystorePassword;

    @Value("${security.truststore}")
    private String truststore;

    @Value("${security.truststorePassword}")
    private String truststorePassword;

    public void setTruststore() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (InputStream trustStoreStream = ResourceUtil.getResourceAsStream(truststore)) {
            trustStore.load(trustStoreStream, truststorePassword.toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);

        context.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    @Bean
    public RestOperations jwtRestTemplate() {
        return RestTemplateFactory.restTemplate(truststore, truststorePassword);
    }
}

