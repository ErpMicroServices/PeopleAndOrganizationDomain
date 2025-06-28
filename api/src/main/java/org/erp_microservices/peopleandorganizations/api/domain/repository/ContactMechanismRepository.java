package org.erp_microservices.peopleandorganizations.api.domain.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism.ContactMechanism;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactMechanismRepository {

    <S extends ContactMechanism> S save(S contactMechanism);

    <S extends ContactMechanism> List<S> saveAll(Iterable<S> contactMechanisms);

    Optional<ContactMechanism> findById(UUID id);

    boolean existsById(UUID id);

    List<ContactMechanism> findAll();

    List<ContactMechanism> findAllById(Iterable<UUID> ids);

    long count();

    void deleteById(UUID id);

    void delete(ContactMechanism contactMechanism);

    void deleteAll(Iterable<? extends ContactMechanism> contactMechanisms);

    void deleteAll();
}
