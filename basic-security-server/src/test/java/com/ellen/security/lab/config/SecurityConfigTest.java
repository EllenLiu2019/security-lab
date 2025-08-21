package com.ellen.security.lab.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mvc;

    @Test
    @WithMockUser(authorities = "MYPREFIX_USER")
    void test_endpoint_when_authority_then_ok() throws Exception {
        this.mvc.perform(get("/hello?date=2025-08-10&name=Ellen"))
                .andExpect(status().isOk());
    }

    @Test
    void test_endpoint_without_authority_then_unauthorized() throws Exception {
        this.mvc.perform(get("/hello?date=2025-08-10&name=Ellen"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test_endpoint_without_authority_then_ok() throws Exception {
        this.mvc.perform(get("/actuator"))
                .andExpect(status().isOk());
    }

    @Test
    void test_get_password() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        System.out.println(encoder.encode("password"));
        System.out.println(encoder.encode("client"));
        System.out.println(encoder.encode("happy"));
        System.out.println(encoder.encode("secret"));
        System.out.println(encoder.encode("iYd8F#s0Eg"));
    }

}