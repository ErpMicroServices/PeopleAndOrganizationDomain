package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship.PartyRelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartyRelationshipTypeJpaRepository extends JpaRepository<PartyRelationshipType, UUID> {

    Optional<PartyRelationshipType> findByName(String name);
}
