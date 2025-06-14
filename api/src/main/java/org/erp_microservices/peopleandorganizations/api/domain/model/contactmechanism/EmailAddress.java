package org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "email_address")
@DiscriminatorValue("EMAIL")
@Getter
@Setter
@AllArgsConstructor
public class EmailAddress extends ContactMechanism {

    public EmailAddress() {
        super();
        setContactMechanismType("EMAIL");
    }

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Builder
    public EmailAddress(UUID id, String comment, String emailAddress) {
        super(id, "EMAIL", comment);
        this.emailAddress = emailAddress;
    }
}
