package org.erp_microservices.peopleandorganizations.api.domain.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartyRepository {
    
    Party save(Party party);
    
    Optional<Party> findById(UUID id);
    
    List<Party> findByType(PartyType partyType);
    
    List<Party> findByRole(PartyRoleType roleType);
    
    List<Party> findByNameContaining(String namePart);
    
    List<Person> findPersonsByLastName(String lastName);
    
    List<Organization> findOrganizationsByName(String name);
    
    Optional<Party> findByIdentification(String identifier, IdentificationType type);
    
    List<Party> findByClassification(PartyClassificationType classificationType, String value);
    
    void deleteById(UUID id);
    
    boolean existsById(UUID id);
    
    long count();
    
    long countByType(PartyType partyType);
}