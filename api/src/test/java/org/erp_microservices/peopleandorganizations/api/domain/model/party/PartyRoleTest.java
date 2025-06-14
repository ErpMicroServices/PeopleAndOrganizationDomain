package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PartyRoleTest {

    private Party party;
    private PartyRoleType roleType;

    @BeforeEach
    void setUp() {
        party = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        party.setId(UUID.randomUUID());

        roleType = new PartyRoleType();
        roleType.setId(UUID.randomUUID());
        roleType.setDescription("CUSTOMER");
    }

    @Test
    @DisplayName("Should create PartyRole with required fields")
    void shouldCreatePartyRoleWithRequiredFields() {
        // Given
        LocalDate fromDate = LocalDate.now();

        // When
        PartyRole partyRole = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(fromDate)
                .build();

        // Then
        assertThat(partyRole.getParty()).isEqualTo(party);
        assertThat(partyRole.getRoleType()).isEqualTo(roleType);
        assertThat(partyRole.getFromDate()).isEqualTo(fromDate);
        assertThat(partyRole.getThruDate()).isNull();
    }

    @Test
    @DisplayName("Should create PartyRole with all fields")
    void shouldCreatePartyRoleWithAllFields() {
        // Given
        LocalDate fromDate = LocalDate.now();
        LocalDate thruDate = fromDate.plusMonths(6);

        // When
        PartyRole partyRole = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(fromDate)
                .thruDate(thruDate)
                .build();

        // Then
        assertThat(partyRole.getParty()).isEqualTo(party);
        assertThat(partyRole.getRoleType()).isEqualTo(roleType);
        assertThat(partyRole.getFromDate()).isEqualTo(fromDate);
        assertThat(partyRole.getThruDate()).isEqualTo(thruDate);
    }

    @Test
    @DisplayName("Should check if role is active")
    void shouldCheckIfRoleIsActive() {
        // Given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        PartyRole activeRole = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(yesterday)
                .build();

        PartyRole expiredRole = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(yesterday)
                .thruDate(yesterday)
                .build();

        PartyRole futureRole = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(tomorrow)
                .build();

        // Then
        assertThat(activeRole.isActive()).isTrue();
        assertThat(expiredRole.isActive()).isFalse();
        assertThat(futureRole.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should check if role was active on specific date")
    void shouldCheckIfRoleWasActiveOnDate() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        LocalDate testDate = LocalDate.of(2024, 3, 15);
        LocalDate beforeStart = LocalDate.of(2023, 12, 31);
        LocalDate afterEnd = LocalDate.of(2024, 7, 1);

        PartyRole partyRole = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(startDate)
                .thruDate(endDate)
                .build();

        // Then
        assertThat(partyRole.isActiveOn(testDate)).isTrue();
        assertThat(partyRole.isActiveOn(startDate)).isTrue();
        assertThat(partyRole.isActiveOn(endDate)).isTrue();
        assertThat(partyRole.isActiveOn(beforeStart)).isFalse();
        assertThat(partyRole.isActiveOn(afterEnd)).isFalse();
    }

    @Test
    @DisplayName("Should handle open-ended role (null thruDate)")
    void shouldHandleOpenEndedRole() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate futureDate = LocalDate.of(2025, 12, 31);

        PartyRole partyRole = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(startDate)
                .thruDate(null)
                .build();

        // Then
        assertThat(partyRole.isActiveOn(futureDate)).isTrue();
        assertThat(partyRole.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should terminate role")
    void shouldTerminateRole() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate terminationDate = LocalDate.now();

        PartyRole partyRole = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(startDate)
                .build();

        // When
        partyRole.setThruDate(terminationDate);

        // Then
        assertThat(partyRole.getThruDate()).isEqualTo(terminationDate);
        assertThat(partyRole.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should compare party roles by ID")
    void shouldComparePartyRolesById() {
        // Given
        PartyRole role1 = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(LocalDate.now())
                .build();
        role1.setId(UUID.randomUUID());

        PartyRole role2 = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(LocalDate.now())
                .build();
        role2.setId(role1.getId());

        PartyRole role3 = PartyRole.builder()
                .party(party)
                .roleType(roleType)
                .fromDate(LocalDate.now())
                .build();
        role3.setId(UUID.randomUUID());

        // Then
        assertThat(role1).isEqualTo(role2);
        assertThat(role1).isNotEqualTo(role3);
    }
}
