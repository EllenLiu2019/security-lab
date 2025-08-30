package com.ellen.security.lab.config.property;

import lombok.Data;

@Data
public class SecurityProperties {
    private boolean enabled = true;
    private String keystore;
    private String keystorePassword;
    private String truststore;
    private String truststorePassword;
}
