package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "id_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class IdentificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "description", nullable = false, unique = true)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private IdentificationType parent;

    public boolean isSocialSecurityNumber() {
        return "Social Security Number".equals(description) ||
               (parent != null && parent.isSocialSecurityNumber());
    }

    public boolean isDriversLicense() {
        return "Driver's License".equals(description) ||
               (parent != null && parent.isDriversLicense());
    }

    public boolean isPassport() {
        return "Passport".equals(description) ||
               (parent != null && parent.isPassport());
    }

    public boolean isTaxIdentificationNumber() {
        return "Tax Identification Number".equals(description) ||
               (parent != null && parent.isTaxIdentificationNumber());
    }
}
