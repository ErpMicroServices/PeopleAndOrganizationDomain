package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism.ContactMechanism;
import org.erp_microservices.peopleandorganizations.api.domain.repository.ContactMechanismRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ContactMechanismRepositoryImpl implements ContactMechanismRepository {

    private final ContactMechanismJpaRepository jpaRepository;

    @Override
    public <S extends ContactMechanism> S save(S contactMechanism) {
        return jpaRepository.save(contactMechanism);
    }

    @Override
    public <S extends ContactMechanism> List<S> saveAll(Iterable<S> contactMechanisms) {
        return jpaRepository.saveAll(contactMechanisms);
    }

    @Override
    public Optional<ContactMechanism> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<ContactMechanism> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<ContactMechanism> findAllById(Iterable<UUID> ids) {
        return jpaRepository.findAllById(ids);
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
    public void delete(ContactMechanism contactMechanism) {
        jpaRepository.delete(contactMechanism);
    }

    @Override
    public void deleteAll(Iterable<? extends ContactMechanism> contactMechanisms) {
        jpaRepository.deleteAll(contactMechanisms);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
