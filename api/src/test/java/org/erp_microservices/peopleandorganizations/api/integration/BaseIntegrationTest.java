package org.erp_microservices.peopleandorganizations.api.integration;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.vault.enabled=false",
        "spring.cloud.config.enabled=false",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://test.issuer.com",
        "security.disabled=false"
    }
)
@AutoConfigureHttpGraphQlTester
@Transactional
@Tag("integration")
@ActiveProfiles({"test", "integration-test"})
@Import(IntegrationTestConfig.class)
public abstract class BaseIntegrationTest {
    // Common setup for all integration tests
}
