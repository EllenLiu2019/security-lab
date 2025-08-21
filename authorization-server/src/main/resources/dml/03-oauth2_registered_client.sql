truncate table oauth2_registered_client;

INSERT INTO `oauth2_registered_client`( `client_id`
                                      , `client_id_issued_at`
                                      , `client_secret`
                                      , `client_secret_expires_at`
                                      , `client_name`
                                      , `client_authentication_methods`
                                      , `authorization_grant_types`
                                      , `redirect_uris`
                                      , `post_logout_redirect_uris`
                                      , `scopes`
                                      , `client_settings`
                                      , `token_settings`)
VALUES ('apiBanking-client'
        ,CURDATE()
        ,'{bcrypt}$2a$10$LWvzUsVjt78GK.7RRKVPMei7n.XmLyZ9WjQmfH7DPTzEfLaeisEPS' -- client
        ,null
        ,'apiBanking-client'
        ,'client_secret_basic'
        ,'authorization_code,refresh_token'
        ,'http://localhost:8082/login/oauth2/code/apiBanking-client'
        ,'http://localhost:9000/logout'
        ,'openid,email'
        ,'{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}'
        ,'{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",600.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",36000.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}'
        );

INSERT INTO `oauth2_registered_client`( `client_id`
                                      , `client_id_issued_at`
                                      , `client_secret`
                                      , `client_secret_expires_at`
                                      , `client_name`
                                      , `client_authentication_methods`
                                      , `authorization_grant_types`
                                      , `redirect_uris`
                                      , `post_logout_redirect_uris`
                                      , `scopes`
                                      , `client_settings`
                                      , `token_settings`)
VALUES ('apiBanking-public-client'
       ,CURDATE()
       ,null
       ,null
       ,'apiBanking-public-client'
       ,'none'
       ,'authorization_code,refresh_token'
       ,'http://localhost:8082/login/oauth2/code/apiBanking-public-client'
       ,'http://localhost:9000/logout'
       ,'openid,email'
       ,'{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":false}'
       ,'{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":false,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",600.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",36000.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}'
       );

INSERT INTO `oauth2_registered_client`( `client_id`
                                      , `client_id_issued_at`
                                      , `client_secret`
                                      , `client_secret_expires_at`
                                      , `client_name`
                                      , `client_authentication_methods`
                                      , `authorization_grant_types`
                                      , `redirect_uris`
                                      , `post_logout_redirect_uris`
                                      , `scopes`
                                      , `client_settings`
                                      , `token_settings`)
VALUES ('apiBanking-apis'
       ,CURDATE()
       ,'{bcrypt}$2a$10$Cqkk/.a2YEH7mPkRzgUDTeOr/IkKvjG6YAVU7m/tzJuTy2czcx.ZO' -- secret
       ,null
       ,'apiBanking-apis'
       ,'client_secret_basic'
       ,'client_credentials'
       ,null
       ,'http://localhost:9000/logout'
       ,'openid'
       ,'{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}'
       ,'{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",600.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}'
       );