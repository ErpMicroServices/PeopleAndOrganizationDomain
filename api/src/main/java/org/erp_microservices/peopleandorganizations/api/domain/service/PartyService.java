package org.erp_microservices.peopleandorganizations.api.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.*;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
import org.erp_microservices.peopleandorganizations.api.infrastructure.repository.PartyRoleTypeRepository;
import org.erp_microservices.peopleandorganizations.api.infrastructure.repository.PartyTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PartyService {

    private final PartyRepository partyRepository;
    private final PartyTypeRepository partyTypeRepository;
    private final PartyRoleTypeRepository partyRoleTypeRepository;

    public Party createPerson(String firstName, String lastName, String middleName,
                             String title, String suffix, LocalDate birthDate, GenderType genderType) {
        log.info("Creating new person: {} {}", firstName, lastName);

        PartyType personType = partyTypeRepository.findByDescription("Person")
                .orElseThrow(() -> new IllegalStateException("Person party type not found"));

        Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .title(title)
                .suffix(suffix)
                .birthDate(birthDate)
                .genderType(genderType)
                .build();
        person.setPartyTypeRef(personType);

        return partyRepository.save(person);
    }

    public Party createOrganization(String organizationName, LocalDate establishedDate,
                                   String taxIdentificationNumber, Integer numberOfEmployees,
                                   String industry, String organizationType) {
        log.info("Creating new organization: {}", organizationName);

        PartyType orgType = partyTypeRepository.findByDescription(organizationType)
                .orElseGet(() -> partyTypeRepository.findByDescription("Organization")
                        .orElseThrow(() -> new IllegalStateException("Organization party type not found")));

        Organization organization = Organization.builder()
                .name(organizationName)
                .establishedDate(establishedDate)
                .taxIdNumber(taxIdentificationNumber)
                .numberOfEmployees(numberOfEmployees)
                .industry(industry)
                .build();
        organization.setPartyTypeRef(orgType);

        return partyRepository.save(organization);
    }

    public Party addRoleToParty(UUID partyId, String roleTypeName) {
        log.info("Adding role {} to party {}", roleTypeName, partyId);

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("Party not found: " + partyId));

        PartyRoleType roleType = partyRoleTypeRepository.findByDescription(roleTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Role type not found: " + roleTypeName));

        if (!party.hasRole(roleType)) {
            PartyRole role = PartyRole.builder()
                    .id(UUID.randomUUID())
                    .party(party)
                    .roleType(roleType)
                    .fromDate(LocalDate.now())
                    .build();

            party.addRole(role);
            return partyRepository.save(party);
        }

        return party;
    }

    public Party removeRoleFromParty(UUID partyId, String roleTypeName) {
        log.info("Removing role {} from party {}", roleTypeName, partyId);

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("Party not found: " + partyId));

        PartyRoleType roleType = partyRoleTypeRepository.findByDescription(roleTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Role type not found: " + roleTypeName));

        party.removeRole(roleType);
        return partyRepository.save(party);
    }

    public Party addNameToParty(UUID partyId, String name, String nameTypeName) {
        log.info("Adding name {} of type {} to party {}", name, nameTypeName, partyId);

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("Party not found: " + partyId));

        PartyName partyName = PartyName.builder()
                .id(UUID.randomUUID())
                .party(party)
                .name(name)
                .nameType(NameType.builder()
                        .description(nameTypeName)
                        .build())
                .fromDate(LocalDate.now())
                .build();

        party.getNames().add(partyName);
        return partyRepository.save(party);
    }

    public Party updateParty(UUID partyId, String comment) {
        log.info("Updating party {}", partyId);

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("Party not found: " + partyId));

        party.setComment(comment);
        return partyRepository.save(party);
    }

    @Transactional(readOnly = true)
    public Party getParty(UUID partyId) {
        return partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("Party not found: " + partyId));
    }

    @Transactional(readOnly = true)
    public List<Party> findPartiesByRole(String roleTypeName) {
        PartyRoleType roleType = partyRoleTypeRepository.findByDescription(roleTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Role type not found: " + roleTypeName));

        return partyRepository.findByRole(roleType);
    }

    @Transactional(readOnly = true)
    public List<Party> searchPartiesByName(String namePart) {
        return partyRepository.findByNameContaining(namePart);
    }

    @Transactional(readOnly = true)
    public List<Person> searchPersonsByLastName(String lastName) {
        return partyRepository.findPersonsByLastName(lastName);
    }

    @Transactional(readOnly = true)
    public List<Organization> searchOrganizationsByName(String name) {
        return partyRepository.findOrganizationsByName(name);
    }

    public void deleteParty(UUID partyId) {
        log.info("Deleting party {}", partyId);

        if (!partyRepository.existsById(partyId)) {
            throw new IllegalArgumentException("Party not found: " + partyId);
        }

        partyRepository.deleteById(partyId);
    }
}
