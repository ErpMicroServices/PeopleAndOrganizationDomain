package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship.PartyRelationshipType;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRelationshipTypeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PartyRelationshipTypeRepositoryImpl implements PartyRelationshipTypeRepository {

    private final PartyRelationshipTypeJpaRepository jpaRepository;

    @Override
    public PartyRelationshipType save(PartyRelationshipType relationshipType) {
        return jpaRepository.save(relationshipType);
    }

    @Override
    public List<PartyRelationshipType> saveAll(Iterable<PartyRelationshipType> relationshipTypes) {
        return jpaRepository.saveAll(relationshipTypes);
    }

    @Override
    public Optional<PartyRelationshipType> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<PartyRelationshipType> findByName(String name) {
        return jpaRepository.findByName(name);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<PartyRelationshipType> findAll() {
        return jpaRepository.findAll();
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
    public void delete(PartyRelationshipType relationshipType) {
        jpaRepository.delete(relationshipType);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
