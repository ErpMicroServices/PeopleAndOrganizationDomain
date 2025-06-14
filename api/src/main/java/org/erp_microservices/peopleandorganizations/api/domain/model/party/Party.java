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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Party {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
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
    
    public boolean hasRole(PartyRoleType roleType) {
        return roles.stream()
                .anyMatch(role -> role.isActive() && role.getRoleType().equals(roleType));
    }
    
    public PartyName getCurrentName(NameType nameType) {
        return names.stream()
                .filter(name -> name.isActive() && name.getNameType().equals(nameType))
                .findFirst()
                .orElse(null);
    }
    
    public List<PartyRole> getActiveRoles() {
        return roles.stream()
                .filter(PartyRole::isActive)
                .toList();
    }
    
    public void addRole(PartyRole role) {
        if (!hasRole(role.getRoleType())) {
            roles.add(role);
        }
    }
    
    public void removeRole(PartyRoleType roleType) {
        roles.stream()
                .filter(role -> role.getRoleType().equals(roleType) && role.isActive())
                .forEach(PartyRole::expire);
    }
    
    public void addPartyRole(PartyRole partyRole) {
        roles.add(partyRole);
        partyRole.setParty(this);
    }
    
    public void addPartyIdentification(PartyIdentification identification) {
        identifications.add(identification);
        identification.setParty(this);
    }
    
    public void addPartyClassification(PartyClassification classification) {
        classifications.add(classification);
        classification.setParty(this);
    }
}