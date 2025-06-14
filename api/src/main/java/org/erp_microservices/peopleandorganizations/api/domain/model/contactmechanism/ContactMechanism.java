package org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "contact_mechanism")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "contact_mechanism_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class ContactMechanism {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;
    
    @Column(name = "contact_mechanism_type", insertable = false, updatable = false)
    private String contactMechanismType;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
}