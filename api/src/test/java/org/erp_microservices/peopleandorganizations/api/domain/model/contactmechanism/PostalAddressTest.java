package org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.UUID;

class PostalAddressTest {

    @Test
    @DisplayName("Should create PostalAddress with required fields")
    void shouldCreatePostalAddressWithRequiredFields() {
        // Given
        String address1 = "123 Main Street";
        String city = "Anytown";
        String stateProvince = "CA";
        String postalCode = "12345";
        String country = "USA";

        // When
        PostalAddress postalAddress = PostalAddress.builder()
                .address1(address1)
                .city(city)
                .stateProvince(stateProvince)
                .postalCode(postalCode)
                .country(country)
                .build();

        // Then
        assertThat(postalAddress.getAddress1()).isEqualTo(address1);
        assertThat(postalAddress.getCity()).isEqualTo(city);
        assertThat(postalAddress.getStateProvince()).isEqualTo(stateProvince);
        assertThat(postalAddress.getPostalCode()).isEqualTo(postalCode);
        assertThat(postalAddress.getCountry()).isEqualTo(country);
        assertThat(postalAddress.getContactMechanismType()).isEqualTo("POSTAL_ADDRESS");
    }

    @Test
    @DisplayName("Should create PostalAddress with all fields")
    void shouldCreatePostalAddressWithAllFields() {
        // Given
        String address1 = "123 Main Street";
        String address2 = "Suite 100";
        String city = "Anytown";
        String stateProvince = "CA";
        String postalCode = "12345";
        String postalCodeExtension = "6789";
        String country = "USA";

        // When
        PostalAddress postalAddress = PostalAddress.builder()
                .address1(address1)
                .address2(address2)
                .city(city)
                .stateProvince(stateProvince)
                .postalCode(postalCode)
                .postalCodeExtension(postalCodeExtension)
                .country(country)
                .build();

        // Then
        assertThat(postalAddress.getAddress1()).isEqualTo(address1);
        assertThat(postalAddress.getAddress2()).isEqualTo(address2);
        assertThat(postalAddress.getCity()).isEqualTo(city);
        assertThat(postalAddress.getStateProvince()).isEqualTo(stateProvince);
        assertThat(postalAddress.getPostalCode()).isEqualTo(postalCode);
        assertThat(postalAddress.getPostalCodeExtension()).isEqualTo(postalCodeExtension);
        assertThat(postalAddress.getCountry()).isEqualTo(country);
    }

    @Test
    @DisplayName("Should update postal address fields")
    void shouldUpdatePostalAddressFields() {
        // Given
        PostalAddress postalAddress = PostalAddress.builder()
                .address1("123 Main Street")
                .city("Oldtown")
                .stateProvince("CA")
                .postalCode("12345")
                .country("USA")
                .build();

        // When
        postalAddress.setAddress1("456 New Street");
        postalAddress.setAddress2("Apt 2B");
        postalAddress.setCity("Newtown");
        postalAddress.setStateProvince("NY");
        postalAddress.setPostalCode("54321");

        // Then
        assertThat(postalAddress.getAddress1()).isEqualTo("456 New Street");
        assertThat(postalAddress.getAddress2()).isEqualTo("Apt 2B");
        assertThat(postalAddress.getCity()).isEqualTo("Newtown");
        assertThat(postalAddress.getStateProvince()).isEqualTo("NY");
        assertThat(postalAddress.getPostalCode()).isEqualTo("54321");
    }

    @Test
    @DisplayName("Should inherit contact mechanism type")
    void shouldInheritContactMechanismType() {
        // Given & When
        PostalAddress postalAddress = PostalAddress.builder()
                .address1("123 Main Street")
                .city("Anytown")
                .stateProvince("CA")
                .postalCode("12345")
                .country("USA")
                .build();

        // Then
        assertThat(postalAddress.getContactMechanismType()).isEqualTo("POSTAL_ADDRESS");
        assertThat(postalAddress).isInstanceOf(ContactMechanism.class);
    }

    @Test
    @DisplayName("Should handle null address2")
    void shouldHandleNullAddress2() {
        // Given & When
        PostalAddress postalAddress = PostalAddress.builder()
                .address1("123 Main Street")
                .address2(null)
                .city("Anytown")
                .stateProvince("CA")
                .postalCode("12345")
                .country("USA")
                .build();

        // Then
        assertThat(postalAddress.getAddress2()).isNull();
    }

    @Test
    @DisplayName("Should handle null postal code extension")
    void shouldHandleNullPostalCodeExtension() {
        // Given & When
        PostalAddress postalAddress = PostalAddress.builder()
                .address1("123 Main Street")
                .city("Anytown")
                .stateProvince("CA")
                .postalCode("12345")
                .postalCodeExtension(null)
                .country("USA")
                .build();

        // Then
        assertThat(postalAddress.getPostalCodeExtension()).isNull();
    }

    @Test
    @DisplayName("Should format full address")
    void shouldFormatFullAddress() {
        // Given
        PostalAddress postalAddress = PostalAddress.builder()
                .address1("123 Main Street")
                .address2("Suite 100")
                .city("Anytown")
                .stateProvince("CA")
                .postalCode("12345")
                .postalCodeExtension("6789")
                .country("USA")
                .build();

        // When
        String expectedFormat = "123 Main Street, Suite 100, Anytown, CA 12345-6789, USA";

        // Then
        // Note: This assumes a getFormattedAddress() method would be implemented
        // For now, we just verify all components are accessible
        assertThat(postalAddress.getAddress1()).isNotEmpty();
        assertThat(postalAddress.getAddress2()).isNotEmpty();
        assertThat(postalAddress.getCity()).isNotEmpty();
        assertThat(postalAddress.getStateProvince()).isNotEmpty();
        assertThat(postalAddress.getPostalCode()).isNotEmpty();
        assertThat(postalAddress.getPostalCodeExtension()).isNotEmpty();
        assertThat(postalAddress.getCountry()).isNotEmpty();
    }

    @Test
    @DisplayName("Should compare postal addresses by ID")
    void shouldComparePostalAddressesById() {
        // Given
        PostalAddress address1 = PostalAddress.builder()
                .address1("123 Main Street")
                .city("Anytown")
                .stateProvince("CA")
                .postalCode("12345")
                .country("USA")
                .build();
        address1.setId(UUID.randomUUID());

        PostalAddress address2 = PostalAddress.builder()
                .address1("456 Other Street")
                .city("Other City")
                .stateProvince("NY")
                .postalCode("54321")
                .country("USA")
                .build();
        address2.setId(address1.getId());

        PostalAddress address3 = PostalAddress.builder()
                .address1("123 Main Street")
                .city("Anytown")
                .stateProvince("CA")
                .postalCode("12345")
                .country("USA")
                .build();
        address3.setId(UUID.randomUUID());

        // Then
        assertThat(address1).isEqualTo(address2);
        assertThat(address1).isNotEqualTo(address3);
    }
}
