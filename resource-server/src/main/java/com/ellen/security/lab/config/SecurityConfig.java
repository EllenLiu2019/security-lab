package com.ellen.security.lab.config;


import com.ellen.security.lab.exceptionhandler.CustomBearerTokenAuthenticationEntryPoint;
import com.ellen.security.lab.filter.KeyValueLogger;
import com.ellen.security.lab.filter.LoggingFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private KeyValueLogger keyValueLogger;

    @Value("${group.id}")
    private String[] groupId;

    private static final String[] PUBLIC = {
            "/actuator",
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",
            "/ping"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(new Oauth2RoleConverter());
        http
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthConverter)
                                .decoder(jwtDecoder)
                        ))
                .authorizeHttpRequests(requestMatcherRegistry ->
                        requestMatcherRegistry
                                .requestMatchers(PUBLIC).permitAll()
                                .anyRequest().hasAnyAuthority(this.groupId))
                .exceptionHandling(exceptionHandler ->
                        exceptionHandler.authenticationEntryPoint(new CustomBearerTokenAuthenticationEntryPoint()))
                .addFilterAfter(new LoggingFilter(this.keyValueLogger), AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    @Profile("Async")
    public InitializingBean initializingBean() {
        return () -> SecurityContextHolder.setStrategyName(
                SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
}
