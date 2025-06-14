package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.Organization;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Party;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.PartyType;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartyJpaRepository extends JpaRepository<Party, UUID> {

    List<Party> findByPartyType(String partyType);

    @Query("SELECT DISTINCT p FROM Party p JOIN p.roles r WHERE r.roleType.description = :roleDescription AND r.thruDate IS NULL")
    List<Party> findByActiveRole(@Param("roleDescription") String roleDescription);

    @Query("SELECT DISTINCT p FROM Party p JOIN p.names n WHERE LOWER(n.name) LIKE LOWER(CONCAT('%', :namePart, '%')) AND n.thruDate IS NULL")
    List<Party> findByNameContaining(@Param("namePart") String namePart);

    @Query("SELECT p FROM Person p WHERE LOWER(p.lastName) = LOWER(:lastName)")
    List<Person> findPersonsByLastName(@Param("lastName") String lastName);

    @Query("SELECT o FROM Organization o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Organization> findOrganizationsByName(@Param("name") String name);

    @Query("SELECT DISTINCT p FROM Party p JOIN p.identifications i WHERE i.identifier = :identifier AND i.identificationType.description = :typeDescription AND i.thruDate IS NULL")
    Optional<Party> findByIdentification(@Param("identifier") String identifier, @Param("typeDescription") String typeDescription);

    @Query("SELECT DISTINCT p FROM Party p JOIN p.classifications c WHERE c.classificationType.description = :typeDescription AND c.value = :value AND c.thruDate IS NULL")
    List<Party> findByClassification(@Param("typeDescription") String typeDescription, @Param("value") String value);

    long countByPartyType(String partyType);
}
