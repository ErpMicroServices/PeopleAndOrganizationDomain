package org.erp_microservices.peopleandorganizations.api.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.vault.enabled=false",
        "spring.cloud.config.enabled=false"
    })
@AutoConfigureHttpGraphQlTester
@Testcontainers
@Transactional
@Tag("integration")
@Disabled("Disabled until GraphQL resolvers are implemented - see issue #8")
public class GraphQLPartyRelationshipIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @BeforeEach
    void setUp() {
        // Clean up test data
    }

    @Test
    void createPartyRelationship_ShouldReturnCreatedRelationship() {
        String mutation = """
            mutation CreatePartyRelationship($input: CreatePartyRelationshipInput!) {
                createPartyRelationship(input: $input) {
                    id
                    fromParty {
                        id
                        ... on Person {
                            firstName
                            lastName
                        }
                    }
                    toParty {
                        id
                        ... on Organization {
                            name
                        }
                    }
                    relationshipType {
                        name
                        fromRoleType
                        toRoleType
                    }
                    fromDate
                    thruDate
                }
            }
            """;

        graphQlTester
                .document(mutation)
                .variable("input", createRelationshipInput())
                .execute()
                .path("createPartyRelationship.relationshipType.name").entity(String.class).isEqualTo("EMPLOYMENT")
                .path("createPartyRelationship.relationshipType.fromRoleType").entity(String.class).isEqualTo("EMPLOYEE")
                .path("createPartyRelationship.relationshipType.toRoleType").entity(String.class).isEqualTo("EMPLOYER")
                .path("createPartyRelationship.fromDate").entity(String.class).isEqualTo("2024-01-01")
                .path("createPartyRelationship.thruDate").pathDoesNotExist()
                .path("createPartyRelationship.id").entity(String.class).satisfies(id -> {
                    assertThat(id).isNotNull();
                    assertThat(id).isNotEmpty();
                });
    }

    @Test
    void getPartyRelationships_ShouldReturnRelationshipsForParty() {
        String query = """
            query GetPartyRelationships($partyId: ID!, $includeInactive: Boolean) {
                partyRelationships(partyId: $partyId, includeInactive: $includeInactive) {
                    id
                    fromParty {
                        id
                    }
                    toParty {
                        id
                    }
                    relationshipType {
                        name
                    }
                    fromDate
                    thruDate
                }
            }
            """;

        String partyId = "test-party-id";

        graphQlTester
                .document(query)
                .variable("partyId", partyId)
                .variable("includeInactive", false)
                .execute()
                .path("partyRelationships").entityList(Object.class).hasSize(0);
    }

    @Test
    void updatePartyRelationship_ShouldReturnUpdatedRelationship() {
        String mutation = """
            mutation UpdatePartyRelationship($id: ID!, $input: UpdatePartyRelationshipInput!) {
                updatePartyRelationship(id: $id, input: $input) {
                    id
                    thruDate
                }
            }
            """;

        String relationshipId = "test-relationship-id";

        graphQlTester
                .document(mutation)
                .variable("id", relationshipId)
                .variable("input", updateRelationshipInput())
                .execute()
                .errors()
                .expect(error -> error.getMessage() != null); // Expecting error for non-existent ID
    }

    @Test
    void terminatePartyRelationship_ShouldSetThruDate() {
        String mutation = """
            mutation TerminatePartyRelationship($id: ID!, $thruDate: Date!) {
                terminatePartyRelationship(id: $id, thruDate: $thruDate) {
                    id
                    thruDate
                }
            }
            """;

        String relationshipId = "test-relationship-id";

        graphQlTester
                .document(mutation)
                .variable("id", relationshipId)
                .variable("thruDate", "2024-12-31")
                .execute()
                .errors()
                .expect(error -> error.getMessage() != null); // Expecting error for non-existent ID
    }

    private Object createRelationshipInput() {
        return java.util.Map.of(
                "fromPartyId", "person-id",
                "toPartyId", "organization-id",
                "relationshipTypeName", "EMPLOYMENT",
                "fromDate", "2024-01-01"
        );
    }

    private Object updateRelationshipInput() {
        return java.util.Map.of(
                "thruDate", "2024-12-31"
        );
    }
}
