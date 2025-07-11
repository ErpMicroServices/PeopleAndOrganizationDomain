package org.erp_microservices.peopleandorganizations.api.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@Profile("integration-test")
public class IntegrationTestConfig {

    @Bean
    @Primary
    public JwtDecoder mockJwtDecoder() {
        return token -> {
            Map<String, Object> headers = new HashMap<>();
            headers.put("alg", "RS256");
            headers.put("typ", "JWT");

            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", "test-user");
            claims.put("iss", "https://test.issuer.com");
            claims.put("aud", "test-audience");
            claims.put("exp", Instant.now().plusSeconds(3600).getEpochSecond());
            claims.put("iat", Instant.now().getEpochSecond());
            claims.put("cognito:groups", new String[]{"test-group"});

            return new Jwt(
                    token,
                    Instant.now(),
                    Instant.now().plusSeconds(3600),
                    headers,
                    claims
            );
        };
    }
}
