package com.ellen.security.lab.config.property;

import lombok.Data;
import java.util.List;


@Data
public class JwtProperties {
    private String jwkSetUri;
    private List<String> jwsAlgorithms = List.of("RS256");
    private String issuerUri;
}
