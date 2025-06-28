package org.erp_microservices.peopleandorganizations.api.application.dto;

import lombok.Data;

@Data
public class AddPhoneInput {
    private String partyId;
    private String countryCode;
    private String areaCode;
    private String phoneNumber;
    private String extension;
    private String comment;
}
