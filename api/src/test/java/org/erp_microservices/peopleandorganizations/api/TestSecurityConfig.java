package org.erp_microservices.peopleandorganizations.api;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtDecoder mockJwtDecoder() {
        return token -> {
            // Create a mock JWT for testing
            return Jwt.withTokenValue("test-token")
                    .header("alg", "RS256")
                    .header("typ", "JWT")
                    .issuer("https://test.issuer.com")
                    .subject("test-user")
                    .claim("cognito:groups", new String[]{"test-group"})
                    .build();
        };
    }
}
