package com.ellen.security.lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.web.client.RequestAttributePrincipalResolver;

public class PrincipalResolverConfig {

    @Configuration
    @Profile("per-request")
    public static class PerRequestPrincipalResolverConfiguration {

        @Bean
        public OAuth2ClientHttpRequestInterceptor.PrincipalResolver principalResolver() {
            return new RequestAttributePrincipalResolver();
        }

    }

    @Configuration
    @Profile("anonymous-user")
    public static class AnonymousUserPrincipalResolverConfiguration {

        @Bean
        public OAuth2ClientHttpRequestInterceptor.PrincipalResolver principalResolver() {
            AnonymousAuthenticationToken anonymousUser = new AnonymousAuthenticationToken("anonymous", "anonymousUser",
                    AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
            return (request) -> anonymousUser;
        }

    }
}
