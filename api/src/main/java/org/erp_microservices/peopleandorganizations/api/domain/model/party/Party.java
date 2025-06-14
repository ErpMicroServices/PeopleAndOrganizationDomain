package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "party")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "party_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@AllArgsConstructor
public abstract class Party {

    public Party() {
        this.roles = new ArrayList<>();
        this.names = new ArrayList<>();
        this.identifications = new ArrayList<>();
        this.classifications = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_type_id", nullable = false)
    private PartyType partyTypeRef;

    @Column(name = "party_type", insertable = false, updatable = false)
    private String partyType;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyRole> roles = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyName> names = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyIdentification> identifications = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyClassification> classifications = new ArrayList<>();

    @PostLoad
    private void initializeCollections() {
        if (roles == null) roles = new ArrayList<>();
        if (names == null) names = new ArrayList<>();
        if (identifications == null) identifications = new ArrayList<>();
        if (classifications == null) classifications = new ArrayList<>();
    }

    public boolean hasRole(PartyRoleType roleType) {
        return getRoles().stream()
                .anyMatch(role -> role.isActive() && role.getRoleType().equals(roleType));
    }

    public PartyName getCurrentName(NameType nameType) {
        return getNames().stream()
                .filter(name -> name.isActive() && name.getNameType().equals(nameType))
                .findFirst()
                .orElse(null);
    }

    public List<PartyRole> getActiveRoles() {
        return getRoles().stream()
                .filter(PartyRole::isActive)
                .toList();
    }

    public void addRole(PartyRole role) {
        if (!hasRole(role.getRoleType())) {
            getRoles().add(role);
        }
    }

    public void removeRole(PartyRoleType roleType) {
        getRoles().stream()
                .filter(role -> role.getRoleType().equals(roleType) && role.isActive())
                .forEach(PartyRole::expire);
    }

    public List<PartyRole> getRoles() {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        return roles;
    }

    public List<PartyName> getNames() {
        if (names == null) {
            names = new ArrayList<>();
        }
        return names;
    }

    public List<PartyIdentification> getIdentifications() {
        if (identifications == null) {
            identifications = new ArrayList<>();
        }
        return identifications;
    }

    public List<PartyClassification> getClassifications() {
        if (classifications == null) {
            classifications = new ArrayList<>();
        }
        return classifications;
    }

    public void addPartyRole(PartyRole partyRole) {
        getRoles().add(partyRole);
        partyRole.setParty(this);
    }

    public void addPartyIdentification(PartyIdentification identification) {
        getIdentifications().add(identification);
        identification.setParty(this);
    }

    public void addPartyClassification(PartyClassification classification) {
        getClassifications().add(classification);
        classification.setParty(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return java.util.Objects.equals(id, party.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
