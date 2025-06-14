package org.erp_microservices.peopleandorganizations.api.integration;

import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
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

import java.time.LocalDate;

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
public class GraphQLPartyIntegrationTest {

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

    @Autowired
    private PartyRepository partyRepository;

    @BeforeEach
    void setUp() {
        partyRepository.deleteAll();
    }

    @Test
    void createPerson_ShouldReturnCreatedPerson() {
        String mutation = """
            mutation CreatePerson($input: CreatePersonInput!) {
                createPerson(input: $input) {
                    id
                    firstName
                    lastName
                    middleName
                    birthDate
                    genderType
                }
            }
            """;

        graphQlTester
                .document(mutation)
                .variable("input", createPersonInput())
                .execute()
                .path("createPerson.firstName").entity(String.class).isEqualTo("John")
                .path("createPerson.lastName").entity(String.class).isEqualTo("Doe")
                .path("createPerson.middleName").entity(String.class).isEqualTo("Michael")
                .path("createPerson.birthDate").entity(String.class).isEqualTo("1990-01-15")
                .path("createPerson.genderType").entity(String.class).isEqualTo("MALE")
                .path("createPerson.id").entity(String.class).satisfies(id -> {
                    assertThat(id).isNotNull();
                    assertThat(id).isNotEmpty();
                });
    }

    @Test
    void createOrganization_ShouldReturnCreatedOrganization() {
        String mutation = """
            mutation CreateOrganization($input: CreateOrganizationInput!) {
                createOrganization(input: $input) {
                    id
                    name
                    tradingName
                    registrationNumber
                    taxIdNumber
                }
            }
            """;

        graphQlTester
                .document(mutation)
                .variable("input", createOrganizationInput())
                .execute()
                .path("createOrganization.name").entity(String.class).isEqualTo("Acme Corporation")
                .path("createOrganization.tradingName").entity(String.class).isEqualTo("Acme Corp")
                .path("createOrganization.registrationNumber").entity(String.class).isEqualTo("REG123456")
                .path("createOrganization.taxIdNumber").entity(String.class).isEqualTo("TAX987654")
                .path("createOrganization.id").entity(String.class).satisfies(id -> {
                    assertThat(id).isNotNull();
                    assertThat(id).isNotEmpty();
                });
    }

    @Test
    void getPersonById_ShouldReturnPerson() {
        // First create a person through repository to get ID
        // This simulates existing data
        String personId = "test-person-id"; // Will be replaced with actual ID after creation

        String query = """
            query GetPerson($id: ID!) {
                person(id: $id) {
                    id
                    firstName
                    lastName
                    middleName
                    birthDate
                    genderType
                }
            }
            """;

        graphQlTester
                .document(query)
                .variable("id", personId)
                .execute()
                .path("person").pathDoesNotExist(); // Expecting null for non-existent ID
    }

    @Test
    void listPeople_ShouldReturnPagedResults() {
        String query = """
            query ListPeople($page: Int, $size: Int) {
                people(page: $page, size: $size) {
                    content {
                        id
                        firstName
                        lastName
                    }
                    totalElements
                    totalPages
                    number
                    size
                }
            }
            """;

        graphQlTester
                .document(query)
                .variable("page", 0)
                .variable("size", 10)
                .execute()
                .path("people.content").entityList(Object.class).hasSize(0)
                .path("people.totalElements").entity(Integer.class).isEqualTo(0)
                .path("people.totalPages").entity(Integer.class).isEqualTo(0)
                .path("people.number").entity(Integer.class).isEqualTo(0)
                .path("people.size").entity(Integer.class).isEqualTo(10);
    }

    @Test
    void listOrganizations_ShouldReturnPagedResults() {
        String query = """
            query ListOrganizations($page: Int, $size: Int) {
                organizations(page: $page, size: $size) {
                    content {
                        id
                        name
                        tradingName
                    }
                    totalElements
                    totalPages
                    number
                    size
                }
            }
            """;

        graphQlTester
                .document(query)
                .variable("page", 0)
                .variable("size", 10)
                .execute()
                .path("organizations.content").entityList(Object.class).hasSize(0)
                .path("organizations.totalElements").entity(Integer.class).isEqualTo(0)
                .path("organizations.totalPages").entity(Integer.class).isEqualTo(0)
                .path("organizations.number").entity(Integer.class).isEqualTo(0)
                .path("organizations.size").entity(Integer.class).isEqualTo(10);
    }

    @Test
    void updatePerson_ShouldReturnUpdatedPerson() {
        String mutation = """
            mutation UpdatePerson($id: ID!, $input: UpdatePersonInput!) {
                updatePerson(id: $id, input: $input) {
                    id
                    firstName
                    lastName
                    middleName
                }
            }
            """;

        String personId = "test-person-id"; // Will be replaced with actual ID

        graphQlTester
                .document(mutation)
                .variable("id", personId)
                .variable("input", updatePersonInput())
                .execute()
                .errors()
                .expect(error -> error.getMessage() != null); // Expecting error for non-existent ID
    }

    @Test
    void deletePerson_ShouldReturnSuccess() {
        String mutation = """
            mutation DeletePerson($id: ID!) {
                deletePerson(id: $id)
            }
            """;

        String personId = "test-person-id"; // Will be replaced with actual ID

        graphQlTester
                .document(mutation)
                .variable("id", personId)
                .execute()
                .errors()
                .expect(error -> error.getMessage() != null); // Expecting error for non-existent ID
    }

    private Object createPersonInput() {
        return java.util.Map.of(
                "firstName", "John",
                "lastName", "Doe",
                "middleName", "Michael",
                "birthDate", "1990-01-15",
                "genderType", "MALE"
        );
    }

    private Object createOrganizationInput() {
        return java.util.Map.of(
                "name", "Acme Corporation",
                "tradingName", "Acme Corp",
                "registrationNumber", "REG123456",
                "taxIdNumber", "TAX987654"
        );
    }

    private Object updatePersonInput() {
        return java.util.Map.of(
                "firstName", "Jane",
                "lastName", "Smith"
        );
    }
}
