package org.erp_microservices.peopleandorganizations.api.application.graphql.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.erp_microservices.peopleandorganizations.api.application.dto.CreatePartyRelationshipInput;
import org.erp_microservices.peopleandorganizations.api.application.dto.UpdatePartyRelationshipInput;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Party;
import org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship.PartyRelationship;
import org.erp_microservices.peopleandorganizations.api.domain.model.partyrelationship.PartyRelationshipType;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRelationshipRepository;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRelationshipTypeRepository;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PartyRelationshipResolver {

    private final PartyRepository partyRepository;
    private final PartyRelationshipRepository partyRelationshipRepository;
    private final PartyRelationshipTypeRepository partyRelationshipTypeRepository;

    @QueryMapping
    public List<PartyRelationship> partyRelationships(@Argument String partyId,
                                                     @Argument int page,
                                                     @Argument int size) {
        log.debug("Fetching party relationships for party: {}", partyId);
        UUID partyUuid = UUID.fromString(partyId);
        return partyRelationshipRepository.findByPartyId(partyUuid);
    }

    @QueryMapping
    public Optional<PartyRelationship> partyRelationship(@Argument String id) {
        log.debug("Fetching party relationship with id: {}", id);
        return partyRelationshipRepository.findById(UUID.fromString(id));
    }

    @QueryMapping
    public List<PartyRelationshipType> partyRelationshipTypes() {
        log.debug("Fetching all party relationship types");
        return partyRelationshipTypeRepository.findAll();
    }

    @QueryMapping
    public Optional<PartyRelationshipType> partyRelationshipType(@Argument String id) {
        log.debug("Fetching party relationship type with id: {}", id);
        return partyRelationshipTypeRepository.findById(UUID.fromString(id));
    }

    @MutationMapping
    public PartyRelationship createPartyRelationship(@Argument CreatePartyRelationshipInput input) {
        log.debug("Creating party relationship with input: {}", input);

        Party fromParty = partyRepository.findById(UUID.fromString(input.getFromPartyId()))
                .orElseThrow(() -> new IllegalArgumentException("From party not found with id: " + input.getFromPartyId()));

        Party toParty = partyRepository.findById(UUID.fromString(input.getToPartyId()))
                .orElseThrow(() -> new IllegalArgumentException("To party not found with id: " + input.getToPartyId()));

        PartyRelationshipType relationshipType = partyRelationshipTypeRepository.findById(UUID.fromString(input.getRelationshipTypeId()))
                .orElseThrow(() -> new IllegalArgumentException("Relationship type not found with id: " + input.getRelationshipTypeId()));

        PartyRelationship relationship = PartyRelationship.builder()
                .fromParty(fromParty)
                .toParty(toParty)
                .relationshipType(relationshipType)
                .fromDate(input.getFromDate() != null ? input.getFromDate() : LocalDate.now())
                .thruDate(input.getThruDate())
                .comment(input.getComment())
                .build();

        PartyRelationship savedRelationship = partyRelationshipRepository.save(relationship);
        log.info("Created party relationship with id: {}", savedRelationship.getId());

        return savedRelationship;
    }

    @MutationMapping
    public PartyRelationship updatePartyRelationship(@Argument String id,
                                                    @Argument UpdatePartyRelationshipInput input) {
        log.debug("Updating party relationship {} with input: {}", id, input);

        PartyRelationship relationship = partyRelationshipRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Party relationship not found with id: " + id));

        if (input.getThruDate() != null) {
            relationship.setThruDate(input.getThruDate());
        }
        if (input.getComment() != null) {
            relationship.setComment(input.getComment());
        }

        PartyRelationship updatedRelationship = partyRelationshipRepository.save(relationship);
        log.info("Updated party relationship with id: {}", updatedRelationship.getId());

        return updatedRelationship;
    }

    @MutationMapping
    public PartyRelationship terminatePartyRelationship(@Argument String id) {
        log.debug("Terminating party relationship with id: {}", id);

        PartyRelationship relationship = partyRelationshipRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Party relationship not found with id: " + id));

        relationship.setThruDate(LocalDate.now());

        PartyRelationship terminatedRelationship = partyRelationshipRepository.save(relationship);
        log.info("Terminated party relationship with id: {}", terminatedRelationship.getId());

        return terminatedRelationship;
    }
}
