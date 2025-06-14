package org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.UUID;

class EmailAddressTest {

    @Test
    @DisplayName("Should create EmailAddress with email")
    void shouldCreateEmailAddressWithEmail() {
        // Given
        String email = "john.doe@example.com";

        // When
        EmailAddress emailAddress = EmailAddress.builder()
                .emailAddress(email)
                .build();

        // Then
        assertThat(emailAddress.getEmailAddress()).isEqualTo(email);
        assertThat(emailAddress.getContactMechanismType()).isEqualTo("EMAIL");
    }

    @Test
    @DisplayName("Should update email address")
    void shouldUpdateEmailAddress() {
        // Given
        EmailAddress emailAddress = EmailAddress.builder()
                .emailAddress("old@example.com")
                .build();

        // When
        emailAddress.setEmailAddress("new@example.com");

        // Then
        assertThat(emailAddress.getEmailAddress()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Should inherit contact mechanism type")
    void shouldInheritContactMechanismType() {
        // Given & When
        EmailAddress emailAddress = EmailAddress.builder()
                .emailAddress("test@example.com")
                .build();

        // Then
        assertThat(emailAddress.getContactMechanismType()).isEqualTo("EMAIL");
        assertThat(emailAddress).isInstanceOf(ContactMechanism.class);
    }

    @Test
    @DisplayName("Should handle various email formats")
    void shouldHandleVariousEmailFormats() {
        // Given
        String simpleEmail = "user@domain.com";
        String complexEmail = "user.name+tag@sub.domain.co.uk";
        String numericEmail = "user123@domain456.com";

        // When
        EmailAddress email1 = EmailAddress.builder().emailAddress(simpleEmail).build();
        EmailAddress email2 = EmailAddress.builder().emailAddress(complexEmail).build();
        EmailAddress email3 = EmailAddress.builder().emailAddress(numericEmail).build();

        // Then
        assertThat(email1.getEmailAddress()).isEqualTo(simpleEmail);
        assertThat(email2.getEmailAddress()).isEqualTo(complexEmail);
        assertThat(email3.getEmailAddress()).isEqualTo(numericEmail);
    }

    @Test
    @DisplayName("Should compare email addresses by ID")
    void shouldCompareEmailAddressesById() {
        // Given
        EmailAddress email1 = EmailAddress.builder()
                .emailAddress("test1@example.com")
                .build();
        email1.setId(UUID.randomUUID());

        EmailAddress email2 = EmailAddress.builder()
                .emailAddress("test2@example.com")
                .build();
        email2.setId(email1.getId());

        EmailAddress email3 = EmailAddress.builder()
                .emailAddress("test1@example.com")
                .build();
        email3.setId(UUID.randomUUID());

        // Then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1).isNotEqualTo(email3);
    }
}
