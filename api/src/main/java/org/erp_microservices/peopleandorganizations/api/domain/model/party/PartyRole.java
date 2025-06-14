package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "party_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_role_type_id", nullable = false)
    private PartyRoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @Column(name = "from_date", nullable = false)
    @Builder.Default
    private LocalDate fromDate = LocalDate.now();

    @Column(name = "thru_date")
    private LocalDate thruDate;

    public boolean isActive() {
        return isActive(LocalDate.now());
    }

    public boolean isActive(LocalDate asOfDate) {
        return !fromDate.isAfter(asOfDate) &&
               (thruDate == null || thruDate.isAfter(asOfDate));
    }

    public boolean isActiveOn(LocalDate date) {
        return (fromDate == null || !fromDate.isAfter(date)) &&
               (thruDate == null || !thruDate.isBefore(date));
    }

    public void expire() {
        expire(LocalDate.now());
    }

    public void expire(LocalDate expirationDate) {
        if (thruDate == null || thruDate.isAfter(expirationDate)) {
            thruDate = expirationDate;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyRole partyRole = (PartyRole) o;
        return java.util.Objects.equals(id, partyRole.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
