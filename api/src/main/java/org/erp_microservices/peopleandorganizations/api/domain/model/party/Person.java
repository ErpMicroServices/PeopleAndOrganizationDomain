package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "person")
@DiscriminatorValue("PERSON")
@Getter
@Setter
@AllArgsConstructor
public class Person extends Party {

    public Person() {
        super();
        setPartyType("PERSON");
    }

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "title")
    private String title;

    @Column(name = "suffix")
    private String suffix;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    @Builder
    public Person(UUID id, PartyType partyTypeRef, String partyType, String comment,
                  List<PartyRole> roles, List<PartyName> names,
                  List<PartyIdentification> identifications,
                  List<PartyClassification> classifications,
                  String firstName, String middleName, String lastName,
                  String title, String suffix, LocalDate birthDate, GenderType genderType) {
        super(id, partyTypeRef, "PERSON", comment, roles, names, identifications, classifications);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.title = title;
        this.suffix = suffix;
        this.birthDate = birthDate;
        this.genderType = genderType;
    }

    public String getFullName() {
        StringBuilder name = new StringBuilder();
        if (title != null) {
            name.append(title).append(" ");
        }
        if (firstName != null) {
            name.append(firstName).append(" ");
        }
        if (middleName != null) {
            name.append(middleName).append(" ");
        }
        if (lastName != null) {
            name.append(lastName);
        }
        if (suffix != null) {
            name.append(" ").append(suffix);
        }
        return name.toString().trim();
    }

    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}
