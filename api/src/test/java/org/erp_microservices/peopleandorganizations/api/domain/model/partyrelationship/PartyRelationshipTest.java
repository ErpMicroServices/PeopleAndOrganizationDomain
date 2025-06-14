package org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.Organization;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Party;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PartyRelationshipTest {

    private Party personParty;
    private Party organizationParty;
    private PartyRelationshipType relationshipType;

    @BeforeEach
    void setUp() {
        personParty = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        personParty.setId(UUID.randomUUID());

        organizationParty = Organization.builder()
                .name("Acme Corporation")
                .build();
        organizationParty.setId(UUID.randomUUID());

        relationshipType = new PartyRelationshipType();
        relationshipType.setId(UUID.randomUUID());
        relationshipType.setName("EMPLOYMENT");
        relationshipType.setFromRoleType("EMPLOYEE");
        relationshipType.setToRoleType("EMPLOYER");
    }

    @Test
    @DisplayName("Should create PartyRelationship with required fields")
    void shouldCreatePartyRelationshipWithRequiredFields() {
        // Given
        LocalDate fromDate = LocalDate.now();

        // When
        PartyRelationship relationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(fromDate)
                .build();

        // Then
        assertThat(relationship.getFromParty()).isEqualTo(personParty);
        assertThat(relationship.getToParty()).isEqualTo(organizationParty);
        assertThat(relationship.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(relationship.getFromDate()).isEqualTo(fromDate);
        assertThat(relationship.getThruDate()).isNull();
    }

    @Test
    @DisplayName("Should create PartyRelationship with all fields")
    void shouldCreatePartyRelationshipWithAllFields() {
        // Given
        LocalDate fromDate = LocalDate.now();
        LocalDate thruDate = fromDate.plusYears(2);
        String comment = "Full-time employment";

        // When
        PartyRelationship relationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(fromDate)
                .thruDate(thruDate)
                .comment(comment)
                .build();

        // Then
        assertThat(relationship.getFromParty()).isEqualTo(personParty);
        assertThat(relationship.getToParty()).isEqualTo(organizationParty);
        assertThat(relationship.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(relationship.getFromDate()).isEqualTo(fromDate);
        assertThat(relationship.getThruDate()).isEqualTo(thruDate);
        assertThat(relationship.getComment()).isEqualTo(comment);
    }

    @Test
    @DisplayName("Should check if relationship is active")
    void shouldCheckIfRelationshipIsActive() {
        // Given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        PartyRelationship activeRelationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(yesterday)
                .build();

        PartyRelationship expiredRelationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(yesterday)
                .thruDate(yesterday)
                .build();

        PartyRelationship futureRelationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(tomorrow)
                .build();

        // Then
        assertThat(activeRelationship.isActive()).isTrue();
        assertThat(expiredRelationship.isActive()).isFalse();
        assertThat(futureRelationship.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should check if relationship was active on specific date")
    void shouldCheckIfRelationshipWasActiveOnDate() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        LocalDate testDate = LocalDate.of(2024, 6, 15);
        LocalDate beforeStart = LocalDate.of(2023, 12, 31);
        LocalDate afterEnd = LocalDate.of(2025, 1, 1);

        PartyRelationship relationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(startDate)
                .thruDate(endDate)
                .build();

        // Then
        assertThat(relationship.isActiveOn(testDate)).isTrue();
        assertThat(relationship.isActiveOn(startDate)).isTrue();
        assertThat(relationship.isActiveOn(endDate)).isTrue();
        assertThat(relationship.isActiveOn(beforeStart)).isFalse();
        assertThat(relationship.isActiveOn(afterEnd)).isFalse();
    }

    @Test
    @DisplayName("Should handle open-ended relationship")
    void shouldHandleOpenEndedRelationship() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate futureDate = LocalDate.of(2030, 12, 31);

        PartyRelationship relationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(startDate)
                .thruDate(null)
                .build();

        // Then
        assertThat(relationship.isActiveOn(futureDate)).isTrue();
        assertThat(relationship.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should terminate relationship")
    void shouldTerminateRelationship() {
        // Given
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate terminationDate = LocalDate.now();

        PartyRelationship relationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(startDate)
                .build();

        // When
        relationship.setThruDate(terminationDate);

        // Then
        assertThat(relationship.getThruDate()).isEqualTo(terminationDate);
        assertThat(relationship.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should update relationship comment")
    void shouldUpdateRelationshipComment() {
        // Given
        PartyRelationship relationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(LocalDate.now())
                .comment("Initial comment")
                .build();

        // When
        relationship.setComment("Updated comment with more details");

        // Then
        assertThat(relationship.getComment()).isEqualTo("Updated comment with more details");
    }

    @Test
    @DisplayName("Should compare relationships by ID")
    void shouldCompareRelationshipsById() {
        // Given
        PartyRelationship relationship1 = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(LocalDate.now())
                .build();
        relationship1.setId(UUID.randomUUID());

        PartyRelationship relationship2 = PartyRelationship.builder()
                .fromParty(organizationParty)
                .toParty(personParty)
                .relationshipType(relationshipType)
                .fromDate(LocalDate.now())
                .build();
        relationship2.setId(relationship1.getId());

        PartyRelationship relationship3 = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(LocalDate.now())
                .build();
        relationship3.setId(UUID.randomUUID());

        // Then
        assertThat(relationship1).isEqualTo(relationship2);
        assertThat(relationship1).isNotEqualTo(relationship3);
    }

    @Test
    @DisplayName("Should handle null comment")
    void shouldHandleNullComment() {
        // Given & When
        PartyRelationship relationship = PartyRelationship.builder()
                .fromParty(personParty)
                .toParty(organizationParty)
                .relationshipType(relationshipType)
                .fromDate(LocalDate.now())
                .comment(null)
                .build();

        // Then
        assertThat(relationship.getComment()).isNull();
    }
}
