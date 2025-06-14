package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.*;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyRepositoryImpl implements PartyRepository {

    private final PartyJpaRepository jpaRepository;

    @Override
    @Transactional
    public Party save(Party party) {
        return jpaRepository.save(party);
    }

    @Override
    public Optional<Party> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Party> findByType(PartyType partyType) {
        return jpaRepository.findByPartyType(partyType.getDescription().toUpperCase());
    }

    @Override
    public List<Party> findByRole(PartyRoleType roleType) {
        return jpaRepository.findByActiveRole(roleType.getDescription());
    }

    @Override
    public List<Party> findByNameContaining(String namePart) {
        return jpaRepository.findByNameContaining(namePart);
    }

    @Override
    public List<Person> findPersonsByLastName(String lastName) {
        return jpaRepository.findPersonsByLastName(lastName);
    }

    @Override
    public List<Organization> findOrganizationsByName(String name) {
        return jpaRepository.findOrganizationsByName(name);
    }

    @Override
    public Optional<Party> findByIdentification(String identifier, IdentificationType type) {
        return jpaRepository.findByIdentification(identifier, type.getDescription());
    }

    @Override
    public List<Party> findByClassification(PartyClassificationType classificationType, String value) {
        return jpaRepository.findByClassification(classificationType.getDescription(), value);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByType(PartyType partyType) {
        return jpaRepository.countByPartyType(partyType.getDescription().toUpperCase());
    }

    @Override
    @Transactional
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    public List<Party> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Party> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public List<Party> saveAll(Iterable<Party> parties) {
        return jpaRepository.saveAll(parties);
    }
}
