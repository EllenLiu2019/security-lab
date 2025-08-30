package com.ellen.security.lab.config;

import com.ellen.security.lab.config.property.SecurityProperties;
import com.ellen.security.lab.config.property.JwtProperties;
import com.ellen.security.lab.rest.RestTemplateFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestOperations;

@Configuration
@Profile("prod")
public class JwtDecoderConfig {

    @Bean
    @ConfigurationProperties("spring.security.oauth2.resourceserver.jwt")
    public JwtProperties jwtproperties() {
        return new JwtProperties();
    }

    @Bean
    @ConfigurationProperties("auth-server.security")
    public SecurityProperties authServerSecurityProperties() {
        return new SecurityProperties();
    }

    @Bean
    public RestOperations jwtRestTemplate(@Qualifier("authServerSecurityProperties") SecurityProperties securityProperties) {
        return RestTemplateFactory.restTemplate(securityProperties);
    }

    @Bean
    public JwtDecoder jwtDecoderByJwkKeySetUri(JwtProperties jwtProperties, RestOperations jwtRestTemplate) {
        NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder builder = NimbusJwtDecoder
                .withJwkSetUri(jwtProperties.getJwkSetUri())
                .restOperations(jwtRestTemplate);
        return builder.build();
    }
}
