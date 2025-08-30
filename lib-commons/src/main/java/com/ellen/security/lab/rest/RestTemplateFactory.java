package com.ellen.security.lab.rest;

import com.ellen.security.lab.utils.ResourceUtil;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;


public class RestTemplateFactory {

    public static RestTemplate restTemplate(String keystoreFile, String keystorePassword) {
        SSLContext sslContext = createCustomSslContext(keystoreFile, keystorePassword);
        DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext);
        HttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder
                .create()
                .setTlsSocketStrategy(tlsStrategy)
                .setDefaultTlsConfig(TlsConfig
                        .custom()
                        .setHandshakeTimeout(Timeout.ofSeconds(30))
                        .setSupportedProtocols(TLS.V_1_3)
                        .build())
                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }

    public static SSLContext createCustomSslContext(String keystoreFile, String keystorePassword) {

        KeyManagerFactory keyManagerFactory;
        KeyStore keyStore = getKeyStore(keystoreFile, keystorePassword);
        try {
            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());
        } catch (KeyStoreException | UnrecoverableKeyException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        TrustManagerFactory trustManagerFactory;
        try {
            KeyStore trustStore = getKeyStore(keystoreFile, keystorePassword);
            trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        return sslContext;
    }

    public static KeyStore getKeyStore(String keystoreFile, String password) {
        KeyStore keyStore;
        try (InputStream is = ResourceUtil.getResourceAsStream(keystoreFile)) {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(is, password.toCharArray());
        } catch (NoSuchAlgorithmException | IOException | KeyStoreException | CertificateException e) {
            throw new RuntimeException(e);
        }
        return keyStore;
    }
}
