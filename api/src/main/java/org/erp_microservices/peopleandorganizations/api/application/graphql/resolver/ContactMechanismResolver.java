package org.erp_microservices.peopleandorganizations.api.application.graphql.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.erp_microservices.peopleandorganizations.api.application.dto.AddEmailInput;
import org.erp_microservices.peopleandorganizations.api.application.dto.AddPhoneInput;
import org.erp_microservices.peopleandorganizations.api.application.dto.AddPostalAddressInput;
import org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism.ContactMechanism;
import org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism.EmailAddress;
import org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism.PostalAddress;
import org.erp_microservices.peopleandorganizations.api.domain.model.contactmechanism.TelecomNumber;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Party;
import org.erp_microservices.peopleandorganizations.api.domain.repository.ContactMechanismRepository;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContactMechanismResolver {

    private final PartyRepository partyRepository;
    private final ContactMechanismRepository contactMechanismRepository;

    @QueryMapping
    public List<ContactMechanism> partyContactMechanisms(@Argument String partyId) {
        log.debug("Fetching contact mechanisms for party: {}", partyId);
        // For now, return empty list as PartyContactMechanism relationship is not implemented
        // TODO: Implement PartyContactMechanism relationship
        return Collections.emptyList();
    }

    @MutationMapping
    public ContactMechanism addEmailToParty(@Argument AddEmailInput input) {
        log.debug("Adding email to party: {}", input);

        Party party = partyRepository.findById(UUID.fromString(input.getPartyId()))
                .orElseThrow(() -> new IllegalArgumentException("Party not found with id: " + input.getPartyId()));

        EmailAddress emailAddress = EmailAddress.builder()
                .emailAddress(input.getEmailAddress())
                .comment(input.getComment())
                .build();

        EmailAddress savedEmail = contactMechanismRepository.save(emailAddress);
        log.info("Created email address with id: {}", savedEmail.getId());

        // TODO: Create PartyContactMechanism relationship

        return savedEmail;
    }

    @MutationMapping
    public ContactMechanism addPhoneToParty(@Argument AddPhoneInput input) {
        log.debug("Adding phone to party: {}", input);

        Party party = partyRepository.findById(UUID.fromString(input.getPartyId()))
                .orElseThrow(() -> new IllegalArgumentException("Party not found with id: " + input.getPartyId()));

        TelecomNumber telecomNumber = TelecomNumber.builder()
                .countryCode(input.getCountryCode())
                .areaCode(input.getAreaCode())
                .phoneNumber(input.getPhoneNumber())
                .extension(input.getExtension())
                .comment(input.getComment())
                .build();

        TelecomNumber savedPhone = contactMechanismRepository.save(telecomNumber);
        log.info("Created telecom number with id: {}", savedPhone.getId());

        // TODO: Create PartyContactMechanism relationship

        return savedPhone;
    }

    @MutationMapping
    public ContactMechanism addPostalAddressToParty(@Argument AddPostalAddressInput input) {
        log.debug("Adding postal address to party: {}", input);

        Party party = partyRepository.findById(UUID.fromString(input.getPartyId()))
                .orElseThrow(() -> new IllegalArgumentException("Party not found with id: " + input.getPartyId()));

        PostalAddress postalAddress = PostalAddress.builder()
                .address1(input.getAddress1())
                .address2(input.getAddress2())
                .city(input.getCity())
                .stateProvince(input.getStateProvince())
                .postalCode(input.getPostalCode())
                .postalCodeExtension(input.getPostalCodeExtension())
                .country(input.getCountry())
                .comment(input.getComment())
                .build();

        PostalAddress savedAddress = contactMechanismRepository.save(postalAddress);
        log.info("Created postal address with id: {}", savedAddress.getId());

        // TODO: Create PartyContactMechanism relationship

        return savedAddress;
    }

    @MutationMapping
    public boolean removeContactMechanismFromParty(@Argument String partyId,
                                                  @Argument String contactMechanismId) {
        log.debug("Removing contact mechanism {} from party {}", contactMechanismId, partyId);

        // Verify party exists
        Party party = partyRepository.findById(UUID.fromString(partyId))
                .orElseThrow(() -> new IllegalArgumentException("Party not found with id: " + partyId));

        // Verify contact mechanism exists
        ContactMechanism contactMechanism = contactMechanismRepository.findById(UUID.fromString(contactMechanismId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contact mechanism not found with id: " + contactMechanismId));

        // TODO: Remove PartyContactMechanism relationship
        // For now, just delete the contact mechanism
        contactMechanismRepository.deleteById(UUID.fromString(contactMechanismId));

        log.info("Removed contact mechanism {} from party {}", contactMechanismId, partyId);
        return true;
    }

    @MutationMapping
    public ContactMechanism updateContactMechanismPurposes(@Argument String contactMechanismId,
                                                         @Argument List<String> purposes) {
        log.debug("Updating purposes for contact mechanism {}: {}", contactMechanismId, purposes);

        ContactMechanism contactMechanism = contactMechanismRepository.findById(UUID.fromString(contactMechanismId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contact mechanism not found with id: " + contactMechanismId));

        // TODO: Update PartyContactMechanism purposes

        return contactMechanism;
    }
}
