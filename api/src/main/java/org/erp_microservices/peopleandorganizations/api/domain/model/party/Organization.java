package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organization")
@DiscriminatorValue("ORGANIZATION")
@Getter
@Setter
@AllArgsConstructor
public class Organization extends Party {

    public Organization() {
        super();
        setPartyType("ORGANIZATION");
    }

    @Column(name = "organization_name")
    private String name;

    @Column(name = "trading_name")
    private String tradingName;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    @Column(name = "tax_identification_number")
    private String taxIdNumber;

    @Column(name = "number_of_employees")
    private Integer numberOfEmployees;

    @Column(name = "industry")
    private String industry;

    @Builder
    public Organization(UUID id, PartyType partyTypeRef, String partyType, String comment,
                       List<PartyRole> roles, List<PartyName> names,
                       List<PartyIdentification> identifications,
                       List<PartyClassification> classifications,
                       String name, String tradingName, String registrationNumber,
                       LocalDate establishedDate, String taxIdNumber, Integer numberOfEmployees,
                       String industry) {
        super(id, partyTypeRef, "ORGANIZATION", comment, roles, names, identifications, classifications);
        this.name = name;
        this.tradingName = tradingName;
        this.registrationNumber = registrationNumber;
        this.establishedDate = establishedDate;
        this.taxIdNumber = taxIdNumber;
        this.numberOfEmployees = numberOfEmployees;
        this.industry = industry;
    }

    public Integer getYearsInBusiness() {
        if (establishedDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - establishedDate.getYear();
    }

    public boolean isLargeEnterprise() {
        return numberOfEmployees != null && numberOfEmployees >= 250;
    }

    public boolean isSmallMediumEnterprise() {
        return numberOfEmployees != null && numberOfEmployees < 250;
    }
}
