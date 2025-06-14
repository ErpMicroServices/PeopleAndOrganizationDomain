package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "party_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PartyType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "description", nullable = false, unique = true)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PartyType parent;

    public boolean isPerson() {
        return "Person".equals(description) ||
               (parent != null && parent.isPerson());
    }

    public boolean isOrganization() {
        return "Organization".equals(description) ||
               (parent != null && parent.isOrganization());
    }

    public boolean isLegalOrganization() {
        return "Legal Organization".equals(description) ||
               (parent != null && parent.isLegalOrganization());
    }

    public boolean isInformalOrganization() {
        return "Informal Organization".equals(description) ||
               (parent != null && parent.isInformalOrganization());
    }
}
