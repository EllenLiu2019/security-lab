package com.ellen.security.lab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver;

import static org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor.ClientRegistrationIdResolver;

@Configuration
public class ClientRegistrationIdResolverConfig {


    /**
     * This demonstrates a custom {@link ClientRegistrationIdResolver} that simply
     * resolves the {@code clientRegistrationId} from the current user.
     *
     * @return a custom {@link ClientRegistrationIdResolver}
     */
    private static ClientRegistrationIdResolver currentUserClientRegistrationIdResolver() {
        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        return request-> {
            Authentication authentication = securityContextHolderStrategy.getContext().getAuthentication();
            return (authentication instanceof OAuth2AuthenticationToken principal)
                    ? principal.getAuthorizedClientRegistrationId() : null;
        };
    }

    /**
     * This demonstrates a composite {@link ClientRegistrationIdResolver} that tries to
     * resolve the {@code clientRegistrationId} in multiple ways.
     * <p>
     * <ol>
     * <li>resolve the {@code clientRegistrationId} from attributes</li>
     * <li>use the {@code clientRegistrationId} from OAuth 2.0 or OpenID Connect 1.0
     * Login</li>
     * <li>use the default {@code clientRegistrationId}</li>
     * </ol>
     *
     * @param defaultClientRegistrationId the default {@code clientRegistrationId}
     * @return a custom {@link ClientRegistrationIdResolver}
     */
    private static ClientRegistrationIdResolver compositeClientRegistrationIdResolver(
            String defaultClientRegistrationId) {
        ClientRegistrationIdResolver requestAttributes = new RequestAttributeClientRegistrationIdResolver();
        ClientRegistrationIdResolver currentUser = currentUserClientRegistrationIdResolver();
        return (request) -> {
            String clientRegistrationId = requestAttributes.resolve(request);
            if (clientRegistrationId == null) {
                clientRegistrationId = currentUser.resolve(request);
            }
            if (clientRegistrationId == null) {
                clientRegistrationId = defaultClientRegistrationId;
            }
            return clientRegistrationId;
        };
    }

    /**
     * This demonstrates a custom {@link ClientRegistrationIdResolver} that requires
     * authentication using OAuth 2.0 or Open ID Connect 1.0. If the user is not logged
     * in, they are sent to the login page prior to obtaining an access token.
     *
     * @return a custom {@link ClientRegistrationIdResolver}
     */
    private static ClientRegistrationIdResolver authenticationRequiredClientRegistrationIdResolver() {
        ClientRegistrationIdResolver currentUser = currentUserClientRegistrationIdResolver();
        return (request) -> {
            String clientRegistrationId = currentUser.resolve(request);
            if (clientRegistrationId == null) {
                throw new AccessDeniedException(
                        "Authentication with OAuth 2.0 or OpenID Connect 1.0 Login is required");
            }
            return clientRegistrationId;
        };
    }

    @Configuration
    @Profile("current-user")
    public static class CurrentUserClientRegistrationIdResolverConfiguration {

        @Bean
        public ClientRegistrationIdResolver clientRegistrationIdResolver() {
            return currentUserClientRegistrationIdResolver();
        }

    }

    @Configuration
    @Profile("composite")
    public static class CompositeClientRegistrationIdResolverConfiguration {

        @Value("${auth-server.register-client-id}")
        private String clientId;

        @Bean
        public ClientRegistrationIdResolver clientRegistrationIdResolver() {
            return compositeClientRegistrationIdResolver(clientId);
        }

    }

    @Configuration
    @Profile("authentication-required")
    public static class AuthenticationRequiredClientRegistrationIdResolverConfiguration {

        @Bean
        public ClientRegistrationIdResolver clientRegistrationIdResolver() {
            return authenticationRequiredClientRegistrationIdResolver();
        }

    }
}
