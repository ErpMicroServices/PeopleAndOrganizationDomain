package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonTest {

    @Test
    @DisplayName("Should create Person with all required fields")
    void shouldCreatePersonWithRequiredFields() {
        // Given
        String firstName = "John";
        String lastName = "Doe";

        // When
        Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();

        // Then
        assertThat(person.getFirstName()).isEqualTo(firstName);
        assertThat(person.getLastName()).isEqualTo(lastName);
        assertThat(person.getPartyType()).isEqualTo("PERSON");
    }

    @Test
    @DisplayName("Should create Person with all fields")
    void shouldCreatePersonWithAllFields() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String middleName = "Michael";
        LocalDate birthDate = LocalDate.of(1990, 1, 15);
        GenderType genderType = GenderType.MALE;

        // When
        Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .birthDate(birthDate)
                .genderType(genderType)
                .build();

        // Then
        assertThat(person.getFirstName()).isEqualTo(firstName);
        assertThat(person.getLastName()).isEqualTo(lastName);
        assertThat(person.getMiddleName()).isEqualTo(middleName);
        assertThat(person.getBirthDate()).isEqualTo(birthDate);
        assertThat(person.getGenderType()).isEqualTo(genderType);
    }

    @Test
    @DisplayName("Should update person details")
    void shouldUpdatePersonDetails() {
        // Given
        Person person = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        // When
        person.setFirstName("Jane");
        person.setLastName("Smith");
        person.setMiddleName("Ann");

        // Then
        assertThat(person.getFirstName()).isEqualTo("Jane");
        assertThat(person.getLastName()).isEqualTo("Smith");
        assertThat(person.getMiddleName()).isEqualTo("Ann");
    }

    @Test
    @DisplayName("Should inherit party type from Party")
    void shouldInheritPartyType() {
        // Given & When
        Person person = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        // Then
        assertThat(person.getPartyType()).isEqualTo("PERSON");
        assertThat(person).isInstanceOf(Party.class);
    }

    @Test
    @DisplayName("Should handle null middle name")
    void shouldHandleNullMiddleName() {
        // Given & When
        Person person = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName(null)
                .build();

        // Then
        assertThat(person.getMiddleName()).isNull();
    }

    @Test
    @DisplayName("Should handle null birth date")
    void shouldHandleNullBirthDate() {
        // Given & When
        Person person = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(null)
                .build();

        // Then
        assertThat(person.getBirthDate()).isNull();
    }

    @Test
    @DisplayName("Should handle null gender type")
    void shouldHandleNullGenderType() {
        // Given & When
        Person person = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .genderType(null)
                .build();

        // Then
        assertThat(person.getGenderType()).isNull();
    }

    @Test
    @DisplayName("Should compare persons by ID")
    void shouldComparePersonsById() {
        // Given
        Person person1 = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        person1.setId(UUID.randomUUID());

        Person person2 = Person.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();
        person2.setId(person1.getId());

        Person person3 = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        person3.setId(UUID.randomUUID());

        // Then
        assertThat(person1).isEqualTo(person2);
        assertThat(person1).isNotEqualTo(person3);
    }
}
