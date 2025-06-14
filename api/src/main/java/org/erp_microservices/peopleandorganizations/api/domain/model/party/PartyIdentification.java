package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "party_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PartyIdentification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "ident")
    private String identifier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_id", nullable = false)
    private IdentificationType identificationType;

    @Column(name = "from_date", nullable = false)
    @Builder.Default
    private LocalDate fromDate = LocalDate.now();

    @Column(name = "thru_date")
    private LocalDate thruDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    public boolean isActive() {
        return isActive(LocalDate.now());
    }

    public boolean isActive(LocalDate asOfDate) {
        return !fromDate.isAfter(asOfDate) &&
               (thruDate == null || !thruDate.isBefore(asOfDate));
    }
}
