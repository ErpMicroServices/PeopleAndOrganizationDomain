package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.UUID;

class OrganizationTest {

    @Test
    @DisplayName("Should create Organization with required fields")
    void shouldCreateOrganizationWithRequiredFields() {
        // Given
        String name = "Acme Corporation";

        // When
        Organization organization = Organization.builder()
                .name(name)
                .build();

        // Then
        assertThat(organization.getName()).isEqualTo(name);
        assertThat(organization.getPartyType()).isEqualTo("ORGANIZATION");
    }

    @Test
    @DisplayName("Should create Organization with all fields")
    void shouldCreateOrganizationWithAllFields() {
        // Given
        String name = "Acme Corporation";
        String tradingName = "Acme Corp";
        String registrationNumber = "REG123456";
        String taxIdNumber = "TAX987654";

        // When
        Organization organization = Organization.builder()
                .name(name)
                .tradingName(tradingName)
                .registrationNumber(registrationNumber)
                .taxIdNumber(taxIdNumber)
                .build();

        // Then
        assertThat(organization.getName()).isEqualTo(name);
        assertThat(organization.getTradingName()).isEqualTo(tradingName);
        assertThat(organization.getRegistrationNumber()).isEqualTo(registrationNumber);
        assertThat(organization.getTaxIdNumber()).isEqualTo(taxIdNumber);
    }

    @Test
    @DisplayName("Should update organization details")
    void shouldUpdateOrganizationDetails() {
        // Given
        Organization organization = Organization.builder()
                .name("Acme Corporation")
                .build();

        // When
        organization.setName("New Corp");
        organization.setTradingName("New Trading Name");
        organization.setRegistrationNumber("NEW123");
        organization.setTaxIdNumber("NEWTAX456");

        // Then
        assertThat(organization.getName()).isEqualTo("New Corp");
        assertThat(organization.getTradingName()).isEqualTo("New Trading Name");
        assertThat(organization.getRegistrationNumber()).isEqualTo("NEW123");
        assertThat(organization.getTaxIdNumber()).isEqualTo("NEWTAX456");
    }

    @Test
    @DisplayName("Should inherit party type from Party")
    void shouldInheritPartyType() {
        // Given & When
        Organization organization = Organization.builder()
                .name("Acme Corporation")
                .build();

        // Then
        assertThat(organization.getPartyType()).isEqualTo("ORGANIZATION");
        assertThat(organization).isInstanceOf(Party.class);
    }

    @Test
    @DisplayName("Should handle null trading name")
    void shouldHandleNullTradingName() {
        // Given & When
        Organization organization = Organization.builder()
                .name("Acme Corporation")
                .tradingName(null)
                .build();

        // Then
        assertThat(organization.getTradingName()).isNull();
    }

    @Test
    @DisplayName("Should handle null registration number")
    void shouldHandleNullRegistrationNumber() {
        // Given & When
        Organization organization = Organization.builder()
                .name("Acme Corporation")
                .registrationNumber(null)
                .build();

        // Then
        assertThat(organization.getRegistrationNumber()).isNull();
    }

    @Test
    @DisplayName("Should handle null tax ID number")
    void shouldHandleNullTaxIdNumber() {
        // Given & When
        Organization organization = Organization.builder()
                .name("Acme Corporation")
                .taxIdNumber(null)
                .build();

        // Then
        assertThat(organization.getTaxIdNumber()).isNull();
    }

    @Test
    @DisplayName("Should compare organizations by ID")
    void shouldCompareOrganizationsById() {
        // Given
        Organization org1 = Organization.builder()
                .name("Acme Corporation")
                .build();
        org1.setId(UUID.randomUUID());

        Organization org2 = Organization.builder()
                .name("Different Corp")
                .build();
        org2.setId(org1.getId());

        Organization org3 = Organization.builder()
                .name("Acme Corporation")
                .build();
        org3.setId(UUID.randomUUID());

        // Then
        assertThat(org1).isEqualTo(org2);
        assertThat(org1).isNotEqualTo(org3);
    }
}
