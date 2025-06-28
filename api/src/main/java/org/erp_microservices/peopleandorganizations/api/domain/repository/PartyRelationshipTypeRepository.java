package org.erp_microservices.peopleandorganizations.api.domain.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship.PartyRelationshipType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartyRelationshipTypeRepository {

    PartyRelationshipType save(PartyRelationshipType relationshipType);

    List<PartyRelationshipType> saveAll(Iterable<PartyRelationshipType> relationshipTypes);

    Optional<PartyRelationshipType> findById(UUID id);

    Optional<PartyRelationshipType> findByName(String name);

    boolean existsById(UUID id);

    List<PartyRelationshipType> findAll();

    long count();

    void deleteById(UUID id);

    void delete(PartyRelationshipType relationshipType);

    void deleteAll();
}
