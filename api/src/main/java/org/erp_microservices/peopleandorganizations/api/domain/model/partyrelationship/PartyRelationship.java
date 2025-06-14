package org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship;

import jakarta.persistence.*;
import lombok.*;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Party;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "party_relationship")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_party_id", nullable = false)
    private Party fromParty;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_party_id", nullable = false)
    private Party toParty;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relationship_type_id", nullable = false)
    private PartyRelationshipType relationshipType;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "thru_date")
    private LocalDate thruDate;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return (fromDate == null || !fromDate.isAfter(now)) &&
               (thruDate == null || thruDate.isAfter(now));
    }

    public boolean isActiveOn(LocalDate date) {
        return (fromDate == null || !fromDate.isAfter(date)) &&
               (thruDate == null || !thruDate.isBefore(date));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyRelationship that = (PartyRelationship) o;
        return java.util.Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
