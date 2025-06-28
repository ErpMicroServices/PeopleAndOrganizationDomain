package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship.PartyRelationship;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRelationshipRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PartyRelationshipRepositoryImpl implements PartyRelationshipRepository {

    private final PartyRelationshipJpaRepository jpaRepository;

    @Override
    public PartyRelationship save(PartyRelationship partyRelationship) {
        return jpaRepository.save(partyRelationship);
    }

    @Override
    public List<PartyRelationship> saveAll(Iterable<PartyRelationship> partyRelationships) {
        return jpaRepository.saveAll(partyRelationships);
    }

    @Override
    public Optional<PartyRelationship> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<PartyRelationship> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<PartyRelationship> findByPartyId(UUID partyId) {
        List<PartyRelationship> relationships = new ArrayList<>();
        relationships.addAll(jpaRepository.findByFromPartyId(partyId));
        relationships.addAll(jpaRepository.findByToPartyId(partyId));
        return relationships;
    }

    @Override
    public List<PartyRelationship> findByFromPartyId(UUID fromPartyId) {
        return jpaRepository.findByFromPartyId(fromPartyId);
    }

    @Override
    public List<PartyRelationship> findByToPartyId(UUID toPartyId) {
        return jpaRepository.findByToPartyId(toPartyId);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(PartyRelationship partyRelationship) {
        jpaRepository.delete(partyRelationship);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
