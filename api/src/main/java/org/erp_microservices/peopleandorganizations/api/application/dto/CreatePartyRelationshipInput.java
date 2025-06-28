package org.erp_microservices.peopleandorganizations.api.application.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePartyRelationshipInput {
    private String fromPartyId;
    private String toPartyId;
    private String relationshipTypeId;
    private LocalDate fromDate;
    private LocalDate thruDate;
    private String comment;
}
