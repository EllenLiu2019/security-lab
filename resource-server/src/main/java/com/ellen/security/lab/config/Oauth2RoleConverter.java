package com.ellen.security.lab.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Oauth2RoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final Map<String, String> CLAIM_MAP =
            Map.of("authorities", "",
                    "scope", "SCOPE_");

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Map<String, Object> claims = source.getClaims();
        CLAIM_MAP.forEach((key, prefix) -> {
            if (claims.containsKey(key)) {
                Object value = claims.get(key);
                if (value instanceof List list) {
                    list.forEach(v -> authorities.add(new SimpleGrantedAuthority(prefix + v)));
                } else {
                    authorities.add(new SimpleGrantedAuthority(prefix + value));
                }
            }
        });
        return authorities;
    }
}
