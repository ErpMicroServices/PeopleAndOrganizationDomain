package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.Party;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final PartyRepository partyRepository;
    private final SecurityUserMapper userMapper;

    public CustomUserDetailsService(PartyRepository partyRepository, SecurityUserMapper userMapper) {
        this.partyRepository = partyRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        return partyRepository.findByUsername(username)
            .map(userMapper::toSecurityUser)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public Optional<SecurityUser> loadUserByPartyId(UUID partyId) {
        return partyRepository.findById(partyId)
            .map(userMapper::toSecurityUser);
    }

    public Optional<SecurityUser> loadUserByEmail(String email) {
        return partyRepository.findByEmail(email)
            .map(userMapper::toSecurityUser);
    }
}
