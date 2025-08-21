package com.ellen.security.lab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ClientController {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @RequestMapping("/getAuthentication")
    public ResponseEntity<Map<String, Object>> getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = new HashMap<>();
        if (authentication instanceof OAuth2AuthenticationToken token) {
            DefaultOidcUser user = (DefaultOidcUser) token.getPrincipal();
            String idToken = user.getIdToken().getTokenValue();
            claims.put("idToken", idToken);
            claims.put("claims", user.getClaims());
            claims.put("email", user.getEmail());
            claims.put("profile", user.getProfile());
        }
        return ResponseEntity.ok(claims);
    }

    @RequestMapping("/getClient")
    public ResponseEntity<ClientRegistration> getClientRegistration() {
        ClientRegistration google = this.clientRegistrationRepository.findByRegistrationId("google");
        return ResponseEntity.ok(google);
    }

    @RequestMapping("/getAccessToken")
    public ResponseEntity<OAuth2AccessToken> getAuthorizedClient(Authentication authentication) {
        OAuth2AuthorizedClient authorizedClient =
                this.authorizedClientService.loadAuthorizedClient("google", authentication.getName());
        return ResponseEntity.ok(authorizedClient.getAccessToken());
    }
}
