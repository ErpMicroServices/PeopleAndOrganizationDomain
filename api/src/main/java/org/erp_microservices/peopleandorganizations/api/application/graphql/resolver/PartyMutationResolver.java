package org.erp_microservices.peopleandorganizations.api.application.graphql.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.erp_microservices.peopleandorganizations.api.application.dto.CreateOrganizationInput;
import org.erp_microservices.peopleandorganizations.api.application.dto.CreatePersonInput;
import org.erp_microservices.peopleandorganizations.api.application.dto.UpdateOrganizationInput;
import org.erp_microservices.peopleandorganizations.api.application.dto.UpdatePersonInput;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Organization;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Party;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Person;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PartyMutationResolver {

    private final PartyRepository partyRepository;

    @MutationMapping
    public Person createPerson(@Argument CreatePersonInput input) {
        log.debug("Creating person with input: {}", input);

        Person person = new Person();
        person.setFirstName(input.getFirstName());
        person.setMiddleName(input.getMiddleName());
        person.setLastName(input.getLastName());
        person.setTitle(input.getTitle());
        person.setSuffix(input.getSuffix());
        person.setBirthDate(input.getBirthDate());
        person.setGenderType(input.getGenderType());
        person.setComment(input.getComment());

        Party savedParty = partyRepository.save(person);
        log.info("Created person with id: {}", savedParty.getId());

        return (Person) savedParty;
    }

    @MutationMapping
    public Person updatePerson(@Argument String id, @Argument UpdatePersonInput input) {
        log.debug("Updating person {} with input: {}", id, input);

        Person person = partyRepository.findById(UUID.fromString(id))
                .filter(party -> party instanceof Person)
                .map(party -> (Person) party)
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + id));

        if (input.getFirstName() != null) {
            person.setFirstName(input.getFirstName());
        }
        if (input.getMiddleName() != null) {
            person.setMiddleName(input.getMiddleName());
        }
        if (input.getLastName() != null) {
            person.setLastName(input.getLastName());
        }
        if (input.getTitle() != null) {
            person.setTitle(input.getTitle());
        }
        if (input.getSuffix() != null) {
            person.setSuffix(input.getSuffix());
        }
        if (input.getBirthDate() != null) {
            person.setBirthDate(input.getBirthDate());
        }
        if (input.getGenderType() != null) {
            person.setGenderType(input.getGenderType());
        }
        if (input.getComment() != null) {
            person.setComment(input.getComment());
        }

        Party updatedParty = partyRepository.save(person);
        log.info("Updated person with id: {}", updatedParty.getId());

        return (Person) updatedParty;
    }

    @MutationMapping
    public boolean deletePerson(@Argument String id) {
        log.debug("Deleting person with id: {}", id);

        UUID partyId = UUID.fromString(id);
        if (!partyRepository.existsById(partyId)) {
            throw new IllegalArgumentException("Person not found with id: " + id);
        }

        partyRepository.deleteById(partyId);
        log.info("Deleted person with id: {}", id);

        return true;
    }

    @MutationMapping
    public Organization createOrganization(@Argument CreateOrganizationInput input) {
        log.debug("Creating organization with input: {}", input);

        Organization organization = new Organization();
        organization.setName(input.getName());
        organization.setTradingName(input.getTradingName());
        organization.setRegistrationNumber(input.getRegistrationNumber());
        organization.setTaxIdNumber(input.getTaxIdNumber());
        organization.setComment(input.getComment());

        Party savedParty = partyRepository.save(organization);
        log.info("Created organization with id: {}", savedParty.getId());

        return (Organization) savedParty;
    }

    @MutationMapping
    public Organization updateOrganization(@Argument String id, @Argument UpdateOrganizationInput input) {
        log.debug("Updating organization {} with input: {}", id, input);

        Organization organization = partyRepository.findById(UUID.fromString(id))
                .filter(party -> party instanceof Organization)
                .map(party -> (Organization) party)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found with id: " + id));

        if (input.getName() != null) {
            organization.setName(input.getName());
        }
        if (input.getTradingName() != null) {
            organization.setTradingName(input.getTradingName());
        }
        if (input.getRegistrationNumber() != null) {
            organization.setRegistrationNumber(input.getRegistrationNumber());
        }
        if (input.getTaxIdNumber() != null) {
            organization.setTaxIdNumber(input.getTaxIdNumber());
        }
        if (input.getComment() != null) {
            organization.setComment(input.getComment());
        }

        Party updatedParty = partyRepository.save(organization);
        log.info("Updated organization with id: {}", updatedParty.getId());

        return (Organization) updatedParty;
    }

    @MutationMapping
    public boolean deleteOrganization(@Argument String id) {
        log.debug("Deleting organization with id: {}", id);

        UUID partyId = UUID.fromString(id);
        if (!partyRepository.existsById(partyId)) {
            throw new IllegalArgumentException("Organization not found with id: " + id);
        }

        partyRepository.deleteById(partyId);
        log.info("Deleted organization with id: {}", id);

        return true;
    }
}
