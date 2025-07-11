package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class RepositoryTestConfig {
    // This empty configuration ensures that @DataJpaTest tests
    // use the external database configured in application-test.yml
    // instead of trying to use Testcontainers
}
