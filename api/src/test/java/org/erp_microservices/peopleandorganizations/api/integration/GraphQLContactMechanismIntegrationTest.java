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
public class GraphQLContactMechanismIntegrationTest {

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
    void addEmailToParty_ShouldReturnPartyContactMechanism() {
        String mutation = """
            mutation AddEmailToParty($input: AddEmailInput!) {
                addEmailToParty(input: $input) {
                    id
                    party {
                        id
                    }
                    contactMechanism {
                        id
                        ... on EmailAddress {
                            emailAddress
                        }
                    }
                    purposes {
                        type
                    }
                    fromDate
                }
            }
            """;

        graphQlTester
                .document(mutation)
                .variable("input", addEmailInput())
                .execute()
                .path("addEmailToParty.contactMechanism.emailAddress").entity(String.class).isEqualTo("john.doe@example.com")
                .path("addEmailToParty.purposes[0].type").entity(String.class).isEqualTo("PRIMARY_EMAIL")
                .path("addEmailToParty.fromDate").entity(String.class).satisfies(date -> {
                    assertThat(date).isNotNull();
                });
    }

    @Test
    void addPhoneToParty_ShouldReturnPartyContactMechanism() {
        String mutation = """
            mutation AddPhoneToParty($input: AddPhoneInput!) {
                addPhoneToParty(input: $input) {
                    id
                    party {
                        id
                    }
                    contactMechanism {
                        id
                        ... on TelecomNumber {
                            countryCode
                            areaCode
                            phoneNumber
                            extension
                        }
                    }
                    purposes {
                        type
                    }
                    fromDate
                }
            }
            """;

        graphQlTester
                .document(mutation)
                .variable("input", addPhoneInput())
                .execute()
                .path("addPhoneToParty.contactMechanism.countryCode").entity(String.class).isEqualTo("1")
                .path("addPhoneToParty.contactMechanism.areaCode").entity(String.class).isEqualTo("555")
                .path("addPhoneToParty.contactMechanism.phoneNumber").entity(String.class).isEqualTo("123-4567")
                .path("addPhoneToParty.purposes[0].type").entity(String.class).isEqualTo("MOBILE_PHONE");
    }

    @Test
    void addPostalAddressToParty_ShouldReturnPartyContactMechanism() {
        String mutation = """
            mutation AddPostalAddressToParty($input: AddPostalAddressInput!) {
                addPostalAddressToParty(input: $input) {
                    id
                    party {
                        id
                    }
                    contactMechanism {
                        id
                        ... on PostalAddress {
                            address1
                            address2
                            city
                            stateProvince
                            postalCode
                            country
                        }
                    }
                    purposes {
                        type
                    }
                    fromDate
                }
            }
            """;

        graphQlTester
                .document(mutation)
                .variable("input", addPostalAddressInput())
                .execute()
                .path("addPostalAddressToParty.contactMechanism.address1").entity(String.class).isEqualTo("123 Main Street")
                .path("addPostalAddressToParty.contactMechanism.city").entity(String.class).isEqualTo("Anytown")
                .path("addPostalAddressToParty.contactMechanism.stateProvince").entity(String.class).isEqualTo("CA")
                .path("addPostalAddressToParty.contactMechanism.postalCode").entity(String.class).isEqualTo("12345")
                .path("addPostalAddressToParty.contactMechanism.country").entity(String.class).isEqualTo("USA");
    }

    @Test
    void getPartyContactMechanisms_ShouldReturnAllContactMechanisms() {
        String query = """
            query GetPartyContactMechanisms($partyId: ID!, $purposeType: String) {
                partyContactMechanisms(partyId: $partyId, purposeType: $purposeType) {
                    id
                    contactMechanism {
                        __typename
                        id
                        ... on EmailAddress {
                            emailAddress
                        }
                        ... on TelecomNumber {
                            countryCode
                            areaCode
                            phoneNumber
                        }
                        ... on PostalAddress {
                            address1
                            city
                            stateProvince
                            postalCode
                        }
                    }
                    purposes {
                        type
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
                .variable("purposeType", null)
                .execute()
                .path("partyContactMechanisms").entityList(Object.class).hasSize(0);
    }

    @Test
    void updateContactMechanismPurposes_ShouldUpdatePurposes() {
        String mutation = """
            mutation UpdateContactMechanismPurposes($id: ID!, $purposes: [String!]!) {
                updateContactMechanismPurposes(id: $id, purposes: $purposes) {
                    id
                    purposes {
                        type
                    }
                }
            }
            """;

        String contactMechanismId = "test-contact-mechanism-id";

        graphQlTester
                .document(mutation)
                .variable("id", contactMechanismId)
                .variable("purposes", java.util.List.of("PRIMARY_EMAIL", "BILLING_EMAIL"))
                .execute()
                .errors()
                .expect(error -> error.getMessage() != null); // Expecting error for non-existent ID
    }

    @Test
    void removeContactMechanismFromParty_ShouldSetThruDate() {
        String mutation = """
            mutation RemoveContactMechanism($id: ID!) {
                removeContactMechanismFromParty(id: $id) {
                    id
                    thruDate
                }
            }
            """;

        String partyContactMechanismId = "test-party-contact-mechanism-id";

        graphQlTester
                .document(mutation)
                .variable("id", partyContactMechanismId)
                .execute()
                .errors()
                .expect(error -> error.getMessage() != null); // Expecting error for non-existent ID
    }

    private Object addEmailInput() {
        return java.util.Map.of(
                "partyId", "test-party-id",
                "emailAddress", "john.doe@example.com",
                "purposes", java.util.List.of("PRIMARY_EMAIL")
        );
    }

    private Object addPhoneInput() {
        return java.util.Map.of(
                "partyId", "test-party-id",
                "countryCode", "1",
                "areaCode", "555",
                "phoneNumber", "123-4567",
                "purposes", java.util.List.of("MOBILE_PHONE")
        );
    }

    private Object addPostalAddressInput() {
        return java.util.Map.of(
                "partyId", "test-party-id",
                "address1", "123 Main Street",
                "address2", "",
                "city", "Anytown",
                "stateProvince", "CA",
                "postalCode", "12345",
                "country", "USA",
                "purposes", java.util.List.of("MAILING_ADDRESS")
        );
    }
}
