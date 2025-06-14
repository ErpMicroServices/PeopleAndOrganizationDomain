package org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "postal_address")
@DiscriminatorValue("POSTAL_ADDRESS")
@Getter
@Setter
@AllArgsConstructor
public class PostalAddress extends ContactMechanism {

    public PostalAddress() {
        super();
        setContactMechanismType("POSTAL_ADDRESS");
    }

    @Column(name = "address1", nullable = false)
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state_province", nullable = false)
    private String stateProvince;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "postal_code_extension")
    private String postalCodeExtension;

    @Column(name = "country", nullable = false)
    private String country;

    @Builder
    public PostalAddress(UUID id, String comment, String address1, String address2,
                        String city, String stateProvince, String postalCode,
                        String postalCodeExtension, String country) {
        super(id, "POSTAL_ADDRESS", comment);
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.postalCodeExtension = postalCodeExtension;
        this.country = country;
    }
}
