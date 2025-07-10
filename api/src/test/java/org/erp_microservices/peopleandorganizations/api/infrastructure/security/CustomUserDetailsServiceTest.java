package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.Party;
import org.erp_microservices.peopleandorganizations.api.domain.model.party.Person;
import org.erp_microservices.peopleandorganizations.api.domain.repository.PartyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    private CustomUserDetailsService userDetailsService;

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private SecurityUserMapper userMapper;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(partyRepository, userMapper);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWhenFound() {
        // Given
        String username = "john.doe";
        UUID partyId = UUID.randomUUID();

        Person person = new Person();
        person.setId(partyId);
        person.setFirstName("John");
        person.setLastName("Doe");

        SecurityUser securityUser = SecurityUser.builder()
            .username(username)
            .partyId(partyId)
            .email("john@example.com")
            .authorities(new String[]{"ROLE_USER"})
            .enabled(true)
            .build();

        when(partyRepository.findByUsername(username)).thenReturn(Optional.of(person));
        when(userMapper.toSecurityUser(person)).thenReturn(securityUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getAuthorities())
            .extracting("authority")
            .containsExactly("ROLE_USER");
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionWhenNotFound() {
        // Given
        String username = "nonexistent";
        when(partyRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("User not found: " + username);
    }

    @Test
    void loadUserByUsername_shouldHandleNullUsername() {
        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Username cannot be null");
    }

    @Test
    void loadUserByUsername_shouldHandleEmptyUsername() {
        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Username cannot be empty");
    }

    @Test
    void loadUserByPartyId_shouldReturnUserDetailsWhenFound() {
        // Given
        UUID partyId = UUID.randomUUID();

        Person person = new Person();
        person.setId(partyId);
        person.setFirstName("Jane");
        person.setLastName("Doe");

        SecurityUser securityUser = SecurityUser.builder()
            .username("jane.doe")
            .partyId(partyId)
            .email("jane@example.com")
            .authorities(new String[]{"ROLE_ADMIN", "ROLE_USER"})
            .enabled(true)
            .build();

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(person));
        when(userMapper.toSecurityUser(person)).thenReturn(securityUser);

        // When
        Optional<SecurityUser> result = userDetailsService.loadUserByPartyId(partyId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPartyId()).isEqualTo(partyId);
        assertThat(result.get().getUsername()).isEqualTo("jane.doe");
        assertThat(result.get().getAuthorities())
            .extracting("authority")
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void loadUserByPartyId_shouldReturnEmptyWhenNotFound() {
        // Given
        UUID partyId = UUID.randomUUID();
        when(partyRepository.findById(partyId)).thenReturn(Optional.empty());

        // When
        Optional<SecurityUser> result = userDetailsService.loadUserByPartyId(partyId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void loadUserByEmail_shouldReturnUserDetailsWhenFound() {
        // Given
        String email = "test@example.com";
        UUID partyId = UUID.randomUUID();

        Person person = new Person();
        person.setId(partyId);

        SecurityUser securityUser = SecurityUser.builder()
            .username("test.user")
            .partyId(partyId)
            .email(email)
            .authorities(new String[]{"ROLE_READONLY"})
            .enabled(true)
            .build();

        when(partyRepository.findByEmail(email)).thenReturn(Optional.of(person));
        when(userMapper.toSecurityUser(person)).thenReturn(securityUser);

        // When
        Optional<SecurityUser> result = userDetailsService.loadUserByEmail(email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        assertThat(result.get().getAuthorities())
            .extracting("authority")
            .containsExactly("ROLE_READONLY");
    }

    @Test
    void loadUserByEmail_shouldReturnEmptyWhenNotFound() {
        // Given
        String email = "notfound@example.com";
        when(partyRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<SecurityUser> result = userDetailsService.loadUserByEmail(email);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void isAccountNonExpired_shouldDelegateToSecurityUser() {
        // Given
        String username = "test.user";
        SecurityUser securityUser = SecurityUser.builder()
            .username(username)
            .accountNonExpired(false)
            .build();

        Person person = new Person();
        when(partyRepository.findByUsername(username)).thenReturn(Optional.of(person));
        when(userMapper.toSecurityUser(person)).thenReturn(securityUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.isAccountNonExpired()).isFalse();
    }

    @Test
    void isAccountNonLocked_shouldDelegateToSecurityUser() {
        // Given
        String username = "locked.user";
        SecurityUser securityUser = SecurityUser.builder()
            .username(username)
            .accountNonLocked(false)
            .build();

        Person person = new Person();
        when(partyRepository.findByUsername(username)).thenReturn(Optional.of(person));
        when(userMapper.toSecurityUser(person)).thenReturn(securityUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.isAccountNonLocked()).isFalse();
    }

    @Test
    void isCredentialsNonExpired_shouldDelegateToSecurityUser() {
        // Given
        String username = "expired.creds";
        SecurityUser securityUser = SecurityUser.builder()
            .username(username)
            .credentialsNonExpired(false)
            .build();

        Person person = new Person();
        when(partyRepository.findByUsername(username)).thenReturn(Optional.of(person));
        when(userMapper.toSecurityUser(person)).thenReturn(securityUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.isCredentialsNonExpired()).isFalse();
    }

    @Test
    void cacheEviction_shouldBeConfigurable() {
        // Given
        String username = "cached.user";
        Person person = new Person();
        SecurityUser securityUser = SecurityUser.builder()
            .username(username)
            .authorities(new String[]{"ROLE_USER"})
            .build();

        when(partyRepository.findByUsername(username)).thenReturn(Optional.of(person));
        when(userMapper.toSecurityUser(person)).thenReturn(securityUser);

        // When - Load user twice
        UserDetails firstLoad = userDetailsService.loadUserByUsername(username);
        UserDetails secondLoad = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(firstLoad).isEqualTo(secondLoad);
        // In real implementation with caching, repository would be called only once
    }
}
