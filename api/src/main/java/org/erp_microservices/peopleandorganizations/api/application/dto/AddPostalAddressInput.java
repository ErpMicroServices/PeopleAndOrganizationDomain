package org.erp_microservices.peopleandorganizations.api.application.dto;

import lombok.Data;

@Data
public class AddPostalAddressInput {
    private String partyId;
    private String address1;
    private String address2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String postalCodeExtension;
    private String country;
    private String comment;
}
