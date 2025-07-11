package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("JWT cannot be null");
        }

        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Try multiple claim names for roles
        Collection<GrantedAuthority> roleAuthorities = extractRoleAuthorities(jwt);

        // If no roles found, fall back to scope-based authorities
        if (roleAuthorities.isEmpty()) {
            return defaultGrantedAuthoritiesConverter.convert(jwt);
        }

        return roleAuthorities;
    }

    private Collection<GrantedAuthority> extractRoleAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Check custom:role claim (single role)
        Object customRole = jwt.getClaim("custom:role");
        if (customRole instanceof String) {
            authorities.add(createRoleAuthority((String) customRole));
        }

        // Check custom:roles claim (array of roles)
        Object customRoles = jwt.getClaim("custom:roles");
        if (customRoles instanceof Collection<?>) {
            ((Collection<?>) customRoles).stream()
                .filter(role -> role instanceof String)
                .map(role -> createRoleAuthority((String) role))
                .forEach(authorities::add);
        }

        // Check cognito:groups claim
        Object cognitoGroups = jwt.getClaim("cognito:groups");
        if (cognitoGroups instanceof Collection<?>) {
            ((Collection<?>) cognitoGroups).stream()
                .filter(group -> group instanceof String)
                .map(group -> createRoleAuthority((String) group))
                .forEach(authorities::add);
        }

        // Check realm_access.roles claim (Keycloak style)
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map<?, ?>) {
            Object roles = ((Map<?, ?>) realmAccess).get("roles");
            if (roles instanceof Collection<?>) {
                ((Collection<?>) roles).stream()
                    .filter(role -> role instanceof String)
                    .map(role -> createRoleAuthority((String) role))
                    .forEach(authorities::add);
            }
        }

        return authorities;
    }

    private GrantedAuthority createRoleAuthority(String role) {
        String upperRole = role.toUpperCase();
        if (upperRole.startsWith("ROLE_")) {
            return new SimpleGrantedAuthority(upperRole);
        }
        return new SimpleGrantedAuthority("ROLE_" + upperRole);
    }
}
