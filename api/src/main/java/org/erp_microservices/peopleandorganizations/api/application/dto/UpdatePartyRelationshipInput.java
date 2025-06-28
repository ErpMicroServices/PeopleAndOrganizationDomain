package org.erp_microservices.peopleandorganizations.api.application.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePartyRelationshipInput {
    private LocalDate thruDate;
    private String comment;
}
