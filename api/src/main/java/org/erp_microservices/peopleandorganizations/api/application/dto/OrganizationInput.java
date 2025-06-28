package org.erp_microservices.peopleandorganizations.api.application.dto;

import java.time.LocalDate;

public record OrganizationInput(
    String name,
    String tradingName,
    String registrationNumber,
    LocalDate establishedDate,
    String taxIdNumber,
    Integer numberOfEmployees,
    String industry,
    String comment
) {}
