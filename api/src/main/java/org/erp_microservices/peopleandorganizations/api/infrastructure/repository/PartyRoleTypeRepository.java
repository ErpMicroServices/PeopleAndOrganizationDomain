package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.PartyRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartyRoleTypeRepository extends JpaRepository<PartyRoleType, UUID> {

    Optional<PartyRoleType> findByDescription(String description);
}
