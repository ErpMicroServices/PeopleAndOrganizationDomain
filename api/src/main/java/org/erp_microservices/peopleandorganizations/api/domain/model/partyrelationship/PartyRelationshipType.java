package org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "party_relationship_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PartyRelationshipType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "from_role_type", nullable = false)
    private String fromRoleType;

    @Column(name = "to_role_type", nullable = false)
    private String toRoleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_type_id")
    private PartyRelationshipType parentType;
}
