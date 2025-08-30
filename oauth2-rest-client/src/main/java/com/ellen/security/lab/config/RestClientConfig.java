package com.ellen.security.lab.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver;
import org.springframework.security.oauth2.client.web.client.SecurityContextHolderPrincipalResolver;
import org.springframework.web.client.RestClient;

import static org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor.ClientRegistrationIdResolver;

@Configuration
public class RestClientConfig {

    @Value("${resource-server.baseUrl}")
    private String resourceUrl;

    @Bean
    public RestClient restClient(OAuth2AuthorizedClientManager authorizedClientManager,
                                 OAuth2AuthorizedClientRepository authorizedClientRepository,
                                 ClientRegistrationIdResolver clientRegistrationIdResolver,
                                 OAuth2ClientHttpRequestInterceptor.PrincipalResolver principalResolver,
                                 RestClient.Builder builder) {

        // 1. interceptor has OAuth2AuthorizedClientManager,
        //    which enables restClient to have the capability of authentication AND authorization
        // 2. interceptor places a Bearer token in the Authorization header of an outbound request.
        OAuth2ClientHttpRequestInterceptor requestInterceptor =
                new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        requestInterceptor.setClientRegistrationIdResolver(clientRegistrationIdResolver);

        // handle the failure by removing an invalid OAuth2AuthorizedClient
        // within the context of an HttpServletRequest
        OAuth2AuthorizationFailureHandler authorizationFailureHandler = OAuth2ClientHttpRequestInterceptor
                .authorizationFailureHandler(authorizedClientRepository);
        requestInterceptor.setAuthorizationFailureHandler(authorizationFailureHandler);

        requestInterceptor.setPrincipalResolver(principalResolver);

        return builder
                .baseUrl(this.resourceUrl)
                .requestInterceptor(requestInterceptor)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2ClientHttpRequestInterceptor.ClientRegistrationIdResolver clientRegistrationIdResolver() {
        return new RequestAttributeClientRegistrationIdResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2ClientHttpRequestInterceptor.PrincipalResolver principalResolver() {
        return new SecurityContextHolderPrincipalResolver();
    }

}
