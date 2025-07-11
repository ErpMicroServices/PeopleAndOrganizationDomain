package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtAuthenticationConverterTest {

    private JwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new JwtAuthenticationConverter();
    }

    @Test
    void convert_shouldExtractRolesFromCustomRoleClaim() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("custom:role", "ADMIN");

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_ADMIN");
    }

    @Test
    void convert_shouldExtractMultipleRolesFromArray() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("custom:roles", Arrays.asList("ADMIN", "USER"));

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void convert_shouldExtractRolesFromCognitoGroups() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("cognito:groups", Arrays.asList("Admins", "Users"));

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_ADMINS", "ROLE_USERS");
    }

    @Test
    void convert_shouldHandleNoRoleClaims() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("email", "user@example.com");

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities()).isEmpty();
    }

    @Test
    void convert_shouldNormalizeRoleNames() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("custom:role", "admin"); // lowercase

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_ADMIN");
    }

    @Test
    void convert_shouldPrefixRolesWithROLE() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("custom:role", "ROLE_ADMIN"); // Already prefixed

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_ADMIN"); // Should not double-prefix
    }

    @Test
    void convert_shouldHandleNullJwt() {
        // When/Then
        assertThatThrownBy(() -> converter.convert(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("JWT cannot be null");
    }

    @Test
    void convert_shouldExtractUserAttributes() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("email", "user@example.com");
        claims.put("email_verified", true);
        claims.put("username", "johndoe");
        claims.put("custom:role", "USER");

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication).isInstanceOf(JwtAuthenticationToken.class);
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        assertThat(jwtAuth.getToken()).isEqualTo(jwt);
        assertThat(jwtAuth.getTokenAttributes())
            .containsEntry("email", "user@example.com")
            .containsEntry("email_verified", true)
            .containsEntry("username", "johndoe");
    }

    @Test
    void convert_shouldHandleComplexRoleStructures() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");

        // Complex role structure from some identity providers
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("admin", "user", "developer"));
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER", "ROLE_DEVELOPER");
    }

    @Test
    void convert_shouldIgnoreInvalidRoleTypes() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("custom:role", 123); // Invalid type (number instead of string)

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication.getAuthorities()).isEmpty();
    }

    @Test
    void convert_shouldHandleScopeBasedAuthorities() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "service-client");
        claims.put("scope", "read write admin");

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("SCOPE_read", "SCOPE_write", "SCOPE_admin");
    }

    @Test
    void convert_shouldPrioritizeRolesOverScopes() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("custom:role", "ADMIN");
        claims.put("scope", "read write"); // Should be ignored when roles are present

        Jwt jwt = createJwt(claims);

        // When
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Then
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_ADMIN")
            .doesNotContain("SCOPE_read", "SCOPE_write");
    }

    private Jwt createJwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("mock-token")
            .header("alg", "RS256")
            .header("typ", "JWT")
            .claims(c -> c.putAll(claims))
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
    }
}
