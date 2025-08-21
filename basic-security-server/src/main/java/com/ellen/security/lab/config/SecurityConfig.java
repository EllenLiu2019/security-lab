package com.ellen.security.lab.config;

import com.ellen.security.lab.filter.KeyValueLogger;
import com.ellen.security.lab.filter.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private KeyValueLogger keyValueLogger;

    private static final String[] PUBLIC = {
            "/actuator",
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",
            "/ping"
    };

    @Bean
    public UserDetailsService defaultUsers() {
        UserDetails user = User
                .withUsername("user")
                .password("{bcrypt}$2a$10$UiBVFM7eowJIkNBKMDjaIeqTkm50xIxn6ZC4/C33uTRdfeKdbMpda")
                .authorities("MYPREFIX_USER")
                .build();
        UserDetails admin = User
                .withUsername("admin")
                .password("{bcrypt}$2a$10$UiBVFM7eowJIkNBKMDjaIeqTkm50xIxn6ZC4/C33uTRdfeKdbMpda")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC).permitAll()
                        .anyRequest().hasRole("USER"))
                .addFilterAfter(new LoggingFilter(this.keyValueLogger), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("MYPREFIX_");
    }

}
