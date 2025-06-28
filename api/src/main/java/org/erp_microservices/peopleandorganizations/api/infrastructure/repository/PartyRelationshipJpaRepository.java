package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship.PartyRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartyRelationshipJpaRepository extends JpaRepository<PartyRelationship, UUID> {

    List<PartyRelationship> findByFromPartyId(UUID fromPartyId);

    List<PartyRelationship> findByToPartyId(UUID toPartyId);
}
