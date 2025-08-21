package com.ellen.security.lab.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mvc;

    @Test
    void test_endpoint_when_authority_then_ok() throws Exception {
        this.mvc.perform(get("/getAuthentication"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void test_endpoint_without_authority_then_ok() throws Exception {
        this.mvc.perform(get("/actuator"))
                .andExpect(status().isOk());
    }

    @Test
    void test_logout() throws Exception {
        this.mvc.perform(logout());
    }
}