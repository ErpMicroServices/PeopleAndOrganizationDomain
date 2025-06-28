package org.erp_microservices.peopleandorganizations.api.application.dto;

import lombok.Data;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.GenderType;

import java.time.LocalDate;

@Data
public class UpdatePersonInput {
    private String firstName;
    private String middleName;
    private String lastName;
    private String title;
    private String suffix;
    private LocalDate birthDate;
    private GenderType genderType;
    private String comment;
}
