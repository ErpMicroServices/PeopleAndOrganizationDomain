package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "party_classification_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PartyClassificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "description", nullable = false, unique = true)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PartyClassificationType parent;

    public boolean isIndustryClassification() {
        return "Industry".equals(description) ||
               (parent != null && parent.isIndustryClassification());
    }

    public boolean isSizeClassification() {
        return "Size".equals(description) ||
               (parent != null && parent.isSizeClassification());
    }

    public boolean isMinorityClassification() {
        return "Minority".equals(description) ||
               (parent != null && parent.isMinorityClassification());
    }
}
