package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.Party;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.PartyRole;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Person;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SecurityUserMapper {

    public SecurityUser toSecurityUser(Party party) {
        SecurityUser.SecurityUserBuilder builder = SecurityUser.builder()
            .partyId(party.getId())
            .username(extractUsername(party))
            .email(extractEmail(party))
            .authorities(extractAuthorities(party))
            .enabled(isEnabled(party));

        // Set password if available (usually not stored in Party)
        // In real implementation, password would come from a separate authentication table
        builder.password("{noop}password"); // NoOp encoder for development

        return builder.build();
    }

    private String extractUsername(Party party) {
        // For Person, use firstName.lastName format
        if (party instanceof Person) {
            Person person = (Person) party;
            String firstName = person.getFirstName() != null ? person.getFirstName().toLowerCase() : "";
            String lastName = person.getLastName() != null ? person.getLastName().toLowerCase() : "";
            return firstName + "." + lastName;
        }

        // For other party types, use the party ID
        return party.getId().toString();
    }

    private String extractEmail(Party party) {
        // In real implementation, would look up email from contact mechanisms
        return party.getId() + "@example.com";
    }

    private String[] extractAuthorities(Party party) {
        List<String> authorities = new ArrayList<>();

        // Extract authorities from party roles
        List<PartyRole> roles = party.getRoles();
        if (roles != null) {
            for (PartyRole role : roles) {
                String roleName = normalizeRoleName(role.getRoleType().getDescription());
                authorities.add("ROLE_" + roleName);
            }
        }

        // Default role if no roles assigned
        if (authorities.isEmpty()) {
            authorities.add("ROLE_USER");
        }

        return authorities.toArray(new String[0]);
    }

    private String normalizeRoleName(String roleName) {
        return roleName.toUpperCase()
            .replace(" ", "_")
            .replace("-", "_");
    }

    private boolean isEnabled(Party party) {
        // Check if party has an active status
        // In real implementation, would check party status or validity dates
        return true;
    }
}
