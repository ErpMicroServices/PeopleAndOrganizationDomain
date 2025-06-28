package org.erp_microservices.peopleandorganizations.api.application.dto;

import lombok.Data;

@Data
public class AddEmailInput {
    private String partyId;
    private String emailAddress;
    private String comment;
}
