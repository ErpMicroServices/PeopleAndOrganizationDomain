package org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "telecom_number")
@DiscriminatorValue("TELECOM_NUMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TelecomNumber extends ContactMechanism {

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "extension")
    private String extension;

    @Builder
    public TelecomNumber(UUID id, String comment, String countryCode,
                        String areaCode, String phoneNumber, String extension) {
        super(id, "TELECOM_NUMBER", comment);
        this.countryCode = countryCode;
        this.areaCode = areaCode;
        this.phoneNumber = phoneNumber;
        this.extension = extension;
    }
}
