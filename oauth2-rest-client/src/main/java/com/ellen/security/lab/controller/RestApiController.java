package com.ellen.security.lab.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@RestController
@RequiredArgsConstructor
public class RestApiController {

    @Value("${auth-server.register-client-id}")
    private String clientId;

    private final RestClient restClient;

    @RequestMapping("/")
    public String getMessages() {
        return this.restClient.get()
                .uri("/hello?date=2025-08-10&name=Ellen")
                 // This demonstrates the default way of resolving the clientRegistrationId
                 // through attributes. See ClientRegistrationIdResolverConfig for trying another.
                .attributes(clientRegistrationId(this.clientId))
                .retrieve()
                .body(String.class);
    }
}
