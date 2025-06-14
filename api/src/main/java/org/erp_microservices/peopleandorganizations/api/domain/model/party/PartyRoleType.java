package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "party_role_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PartyRoleType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "description", nullable = false, unique = true)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PartyRoleType parent;

    public boolean isEmployee() {
        return "Employee".equals(description) ||
               (parent != null && parent.isEmployee());
    }

    public boolean isCustomer() {
        return "Customer".equals(description) ||
               (parent != null && parent.isCustomer());
    }

    public boolean isSupplier() {
        return "Supplier".equals(description) ||
               (parent != null && parent.isSupplier());
    }

    public boolean isContact() {
        return "Contact".equals(description) ||
               (parent != null && parent.isContact());
    }
}
