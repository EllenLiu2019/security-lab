package com.ellen.security.lab.config;

import com.ellen.security.lab.config.property.SecurityProperties;
import com.ellen.security.lab.rest.RestTemplateFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestClient;


@Configuration
@Profile("prod")
public class AuthorizedClientProviderConfig {

    @Bean
    @ConfigurationProperties("auth-server.security")
    public SecurityProperties authServerSecurityProperties() {
        return new SecurityProperties();
    }

    @Bean
    public ClientHttpRequestFactory authRequestFactory(@Qualifier("authServerSecurityProperties")
                                                       SecurityProperties authServerSecurityProperties) {
        return RestTemplateFactory.clientHttpRequestFactory(authServerSecurityProperties);
    }

    @Bean
    public ClientCredentialsOAuth2AuthorizedClientProvider clientProvider(@Qualifier("authRequestFactory")
                                                                              ClientHttpRequestFactory authRequestFactory) {
        ClientCredentialsOAuth2AuthorizedClientProvider clientProvider =
                new ClientCredentialsOAuth2AuthorizedClientProvider();
        RestClientClientCredentialsTokenResponseClient tokenResponseClient =
                new RestClientClientCredentialsTokenResponseClient();
        tokenResponseClient.setRestClient(this.restClient(authRequestFactory));
        clientProvider.setAccessTokenResponseClient(tokenResponseClient);
        return clientProvider;
    }

    private RestClient restClient(ClientHttpRequestFactory authRequestFactory) {
        return RestClient.builder()
                .messageConverters((messageConverters) -> {
                    messageConverters.clear();
                    messageConverters.add(new FormHttpMessageConverter());
                    messageConverters.add(new OAuth2AccessTokenResponseHttpMessageConverter());
                })
                .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
                .requestFactory(authRequestFactory)
                .build();
    }

}
