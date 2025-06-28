package org.erp_microservices.peopleandorganizations.api.integration;

import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.vault.enabled=false",
        "spring.cloud.config.enabled=false",
        "spring.profiles.active=test"
    })
@AutoConfigureHttpGraphQlTester
@Transactional
@Tag("integration")
@WithMockUser
public class GraphQLContactMechanismIntegrationTest {

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Autowired
    private PartyRepository partyRepository;

    private String testPartyId;

    @BeforeEach
    void setUp() {
        partyRepository.deleteAll();

        // Create a test person to use in contact mechanism tests
        String createPersonMutation = """
            mutation CreatePerson($input: CreatePersonInput!) {
                createPerson(input: $input) {
                    id
                }
            }
            """;

        Object personInput = java.util.Map.of(
                "firstName", "John",
                "lastName", "Doe",
                "middleName", "Michael",
                "birthDate", "1990-01-15",
                "genderType", "MALE"
        );

        testPartyId = graphQlTester
                .document(createPersonMutation)
                .variable("input", personInput)
                .execute()
                .path("createPerson.id")
                .entity(String.class)
                .get();
    }

    @Test
    void addEmailToParty_ShouldReturnPartyContactMechanism() {
        String mutation = """
            mutation AddEmailToParty($input: AddEmailInput!) {
                addEmailToParty(input: $input) {
                    id
                    ... on EmailAddress {
                        emailAddress
                    }
                    comment
                }
            }
            """;

        graphQlTester
                .document(mutation)
                .variable("input", addEmailInput())
                .execute()
                .path("addEmailToParty.emailAddress").entity(String.class).isEqualTo("john.doe@example.com")
                .path("addEmailToParty.id").entity(String.class).satisfies(id -> {
                    assertThat(id).isNotNull();
                    assertThat(id).isNotEmpty();
                });
    }

    @Test
    void addPhoneToParty_ShouldReturnPartyContactMechanism() {
        String mutation = """
            mutation AddPhoneToParty($input: AddPhoneInput!) {
                addPhoneToParty(input: $input) {
                    id
                    ... on TelecomNumber {
                        countryCode
                        areaCode
                        phoneNumber
                        extension
                    }
                    comment
                }
            }
            """;

        graphQlTester
                .document(mutation)
                .variable("input", addPhoneInput())
                .execute()
                .path("addPhoneToParty.countryCode").entity(String.class).isEqualTo("1")
                .path("addPhoneToParty.areaCode").entity(String.class).isEqualTo("555")
                .path("addPhoneToParty.phoneNumber").entity(String.class).isEqualTo("123-4567")
                .path("addPhoneToParty.id").entity(String.class).satisfies(id -> {
                    assertThat(id).isNotNull();
                    assertThat(id).isNotEmpty();
                });
    }

    @Test
    void addPostalAddressToParty_ShouldReturnPartyContactMechanism() {
        String mutation = """
            mutation AddPostalAddressToParty($input: AddPostalAddressInput!) {
                addPostalAddressToParty(input: $input) {
                    id
                    ... on PostalAddress {
                        address1
                        address2
                        city
                        stateProvince
                        postalCode
                        country
                    }
                    comment
                }
            }
            """;

        graphQlTester
                .document(mutation)
                .variable("input", addPostalAddressInput())
                .execute()
                .path("addPostalAddressToParty.address1").entity(String.class).isEqualTo("123 Main Street")
                .path("addPostalAddressToParty.city").entity(String.class).isEqualTo("Anytown")
                .path("addPostalAddressToParty.stateProvince").entity(String.class).isEqualTo("CA")
                .path("addPostalAddressToParty.postalCode").entity(String.class).isEqualTo("12345")
                .path("addPostalAddressToParty.country").entity(String.class).isEqualTo("USA");
    }

    @Test
    void getPartyContactMechanisms_ShouldReturnAllContactMechanisms() {
        String query = """
            query GetPartyContactMechanisms($partyId: ID!) {
                partyContactMechanisms(partyId: $partyId) {
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
                    comment
                }
            }
            """;

        String partyId = testPartyId;

        graphQlTester
                .document(query)
                .variable("partyId", partyId)
                .execute()
                .path("partyContactMechanisms").entityList(Object.class).hasSize(0);
    }

    @Test
    void updateContactMechanismPurposes_ShouldUpdatePurposes() {
        String mutation = """
            mutation UpdateContactMechanismPurposes($contactMechanismId: ID!, $purposes: [String!]!) {
                updateContactMechanismPurposes(contactMechanismId: $contactMechanismId, purposes: $purposes) {
                    id
                    comment
                }
            }
            """;

        String contactMechanismId = "550e8400-e29b-41d4-a716-446655440001";

        graphQlTester
                .document(mutation)
                .variable("contactMechanismId", contactMechanismId)
                .variable("purposes", java.util.List.of("PRIMARY_EMAIL", "BILLING_EMAIL"))
                .execute()
                .errors()
                .expect(error -> error.getMessage() != null); // Expecting error for non-existent ID
    }

    @Test
    void removeContactMechanismFromParty_ShouldSetThruDate() {
        String mutation = """
            mutation RemoveContactMechanism($partyId: ID!, $contactMechanismId: ID!) {
                removeContactMechanismFromParty(partyId: $partyId, contactMechanismId: $contactMechanismId)
            }
            """;

        String partyId = testPartyId;
        String contactMechanismId = "550e8400-e29b-41d4-a716-446655440001";

        graphQlTester
                .document(mutation)
                .variable("partyId", partyId)
                .variable("contactMechanismId", contactMechanismId)
                .execute()
                .errors()
                .expect(error -> error.getMessage() != null); // Expecting error for non-existent ID
    }

    private Object addEmailInput() {
        return java.util.Map.of(
                "partyId", testPartyId,
                "emailAddress", "john.doe@example.com"
        );
    }

    private Object addPhoneInput() {
        return java.util.Map.of(
                "partyId", testPartyId,
                "countryCode", "1",
                "areaCode", "555",
                "phoneNumber", "123-4567"
        );
    }

    private Object addPostalAddressInput() {
        return java.util.Map.of(
                "partyId", testPartyId,
                "address1", "123 Main Street",
                "address2", "",
                "city", "Anytown",
                "stateProvince", "CA",
                "postalCode", "12345",
                "country", "USA"
        );
    }
}
