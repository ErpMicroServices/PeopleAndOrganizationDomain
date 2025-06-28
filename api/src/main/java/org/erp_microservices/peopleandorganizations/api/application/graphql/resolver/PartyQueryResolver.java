package org.erp_microservices.peopleandorganizations.api.application.graphql.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Organization;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Person;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PartyQueryResolver {

    private final PartyRepository partyRepository;

    @QueryMapping
    public Optional<Person> person(@Argument String id) {
        log.debug("Fetching person with id: {}", id);
        return partyRepository.findById(UUID.fromString(id))
                .filter(party -> party instanceof Person)
                .map(party -> (Person) party);
    }

    @QueryMapping
    public Page<Person> people(@Argument int page,
                             @Argument int size) {
        log.debug("Fetching people - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return partyRepository.findByPartyType("PERSON", pageable)
                .map(party -> (Person) party);
    }

    @QueryMapping
    public Optional<Organization> organization(@Argument String id) {
        log.debug("Fetching organization with id: {}", id);
        return partyRepository.findById(UUID.fromString(id))
                .filter(party -> party instanceof Organization)
                .map(party -> (Organization) party);
    }

    @QueryMapping
    public Page<Organization> organizations(@Argument int page,
                                          @Argument int size) {
        log.debug("Fetching organizations - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return partyRepository.findByPartyType("ORGANIZATION", pageable)
                .map(party -> (Organization) party);
    }
}
