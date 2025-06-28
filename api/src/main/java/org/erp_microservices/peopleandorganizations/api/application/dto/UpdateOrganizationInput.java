package org.erp_microservices.peopleandorganizations.api.application.dto;

import lombok.Data;

@Data
public class UpdateOrganizationInput {
    private String name;
    private String tradingName;
    private String registrationNumber;
    private String taxIdNumber;
    private String comment;
}
