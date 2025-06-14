package org.erp_microservices.peopleandorganizations.api.domain.model.party;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "name_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class NameType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "description", nullable = false, unique = true)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private NameType parent;

    public boolean isLegalName() {
        return "Legal Name".equals(description) ||
               (parent != null && parent.isLegalName());
    }

    public boolean isTradeStyleName() {
        return "Trade Style Name".equals(description) ||
               (parent != null && parent.isTradeStyleName());
    }

    public boolean isNickname() {
        return "Nickname".equals(description) ||
               (parent != null && parent.isNickname());
    }
}
