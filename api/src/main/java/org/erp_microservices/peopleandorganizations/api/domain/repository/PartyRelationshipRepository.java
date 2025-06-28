package org.erp_microservices.peopleandorganizations.api.domain.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship.PartyRelationship;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartyRelationshipRepository {

    PartyRelationship save(PartyRelationship partyRelationship);

    List<PartyRelationship> saveAll(Iterable<PartyRelationship> partyRelationships);

    Optional<PartyRelationship> findById(UUID id);

    boolean existsById(UUID id);

    List<PartyRelationship> findAll();

    List<PartyRelationship> findByPartyId(UUID partyId);

    List<PartyRelationship> findByFromPartyId(UUID fromPartyId);

    List<PartyRelationship> findByToPartyId(UUID toPartyId);

    long count();

    void deleteById(UUID id);

    void delete(PartyRelationship partyRelationship);

    void deleteAll();
}
