package org.erp_microservices.peopleandorganizations.api.application.dto;

import java.time.LocalDate;

public record PersonInput(
    String firstName,
    String middleName,
    String lastName,
    String title,
    String suffix,
    LocalDate birthDate,
    String genderType,
    String comment
) {}
