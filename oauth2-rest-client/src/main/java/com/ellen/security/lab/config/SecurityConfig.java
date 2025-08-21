package com.ellen.security.lab.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC = {
            "/actuator",
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",
            "/ping"
    };

    @Value("${auth-server.login-client-id}")
    private String loginClientId;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/public/**", "/error").permitAll()
                        .requestMatchers(PUBLIC).permitAll()
                        .anyRequest().authenticated())
                /*
                 * Customize the login page used in the redirect when the AuthenticationEntryPoint
                 * is triggered. This sample switches the URL based on the profile.
                 * See application.yml.
                 */
                .oauth2Login(oauth2LoginConfig ->
                        oauth2LoginConfig.loginPage(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + '/' +this.loginClientId))
                .oauth2Client(withDefaults());
        // @formatter:on

        return http.build();
    }

}
