package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyRepositoryImplTest {

    @Mock
    private PartyJpaRepository partyJpaRepository;

    @InjectMocks
    private PartyRepositoryImpl partyRepository;

    private Person testPerson;
    private Organization testOrganization;
    private UUID personId;
    private UUID organizationId;

    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        organizationId = UUID.randomUUID();

        testPerson = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        testPerson.setId(personId);

        testOrganization = Organization.builder()
                .name("Acme Corporation")
                .build();
        testOrganization.setId(organizationId);
    }

    @Test
    @DisplayName("Should save party")
    void shouldSaveParty() {
        // Given
        when(partyJpaRepository.save(any(Party.class))).thenReturn(testPerson);

        // When
        Party savedParty = partyRepository.save(testPerson);

        // Then
        assertThat(savedParty).isEqualTo(testPerson);
        verify(partyJpaRepository).save(testPerson);
    }

    @Test
    @DisplayName("Should find party by ID")
    void shouldFindPartyById() {
        // Given
        when(partyJpaRepository.findById(any(UUID.class))).thenReturn(Optional.of(testPerson));

        // When
        Optional<Party> foundParty = partyRepository.findById(personId);

        // Then
        assertThat(foundParty).isPresent();
        assertThat(foundParty.get()).isEqualTo(testPerson);
        verify(partyJpaRepository).findById(personId);
    }

    @Test
    @DisplayName("Should return empty when party not found by ID")
    void shouldReturnEmptyWhenPartyNotFoundById() {
        // Given
        when(partyJpaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // When
        Optional<Party> foundParty = partyRepository.findById(personId);

        // Then
        assertThat(foundParty).isEmpty();
        verify(partyJpaRepository).findById(personId);
    }

    @Test
    @DisplayName("Should find all parties")
    void shouldFindAllParties() {
        // Given
        List<Party> parties = Arrays.asList(testPerson, testOrganization);
        when(partyJpaRepository.findAll()).thenReturn(parties);

        // When
        List<Party> foundParties = partyRepository.findAll();

        // Then
        assertThat(foundParties).hasSize(2);
        assertThat(foundParties).containsExactly(testPerson, testOrganization);
        verify(partyJpaRepository).findAll();
    }

    @Test
    @DisplayName("Should find parties by type")
    void shouldFindPartiesByType() {
        // Given
        PartyType partyType = new PartyType();
        partyType.setDescription("PERSON");
        List<Party> persons = Collections.singletonList(testPerson);
        when(partyJpaRepository.findByPartyType("PERSON")).thenReturn(persons);

        // When
        List<Party> foundParties = partyRepository.findByType(partyType);

        // Then
        assertThat(foundParties).hasSize(1);
        assertThat(foundParties).contains(testPerson);
        verify(partyJpaRepository).findByPartyType("PERSON");
    }

    @Test
    @DisplayName("Should find parties by role")
    void shouldFindPartiesByRole() {
        // Given
        PartyRoleType roleType = new PartyRoleType();
        roleType.setDescription("CUSTOMER");
        List<Party> parties = Collections.singletonList(testPerson);
        when(partyJpaRepository.findByActiveRole(roleType.getDescription())).thenReturn(parties);

        // When
        List<Party> foundParties = partyRepository.findByRole(roleType);

        // Then
        assertThat(foundParties).hasSize(1);
        assertThat(foundParties).contains(testPerson);
        verify(partyJpaRepository).findByActiveRole(roleType.getDescription());
    }

    @Test
    @DisplayName("Should find parties by name containing")
    void shouldFindPartiesByNameContaining() {
        // Given
        String namePart = "Doe";
        List<Party> parties = Collections.singletonList(testPerson);
        when(partyJpaRepository.findByNameContaining(namePart)).thenReturn(parties);

        // When
        List<Party> foundParties = partyRepository.findByNameContaining(namePart);

        // Then
        assertThat(foundParties).hasSize(1);
        assertThat(foundParties).contains(testPerson);
        verify(partyJpaRepository).findByNameContaining(namePart);
    }

    @Test
    @DisplayName("Should find persons by last name")
    void shouldFindPersonsByLastName() {
        // Given
        String lastName = "Doe";
        List<Person> persons = Collections.singletonList(testPerson);
        when(partyJpaRepository.findPersonsByLastName(lastName)).thenReturn(persons);

        // When
        List<Person> foundPersons = partyRepository.findPersonsByLastName(lastName);

        // Then
        assertThat(foundPersons).hasSize(1);
        assertThat(foundPersons).contains(testPerson);
        verify(partyJpaRepository).findPersonsByLastName(lastName);
    }

    @Test
    @DisplayName("Should find organizations by name")
    void shouldFindOrganizationsByName() {
        // Given
        String name = "Acme";
        List<Organization> organizations = Collections.singletonList(testOrganization);
        when(partyJpaRepository.findOrganizationsByName(name)).thenReturn(organizations);

        // When
        List<Organization> foundOrganizations = partyRepository.findOrganizationsByName(name);

        // Then
        assertThat(foundOrganizations).hasSize(1);
        assertThat(foundOrganizations).contains(testOrganization);
        verify(partyJpaRepository).findOrganizationsByName(name);
    }

    @Test
    @DisplayName("Should find parties by identification")
    void shouldFindPartiesByIdentification() {
        // Given
        String identifier = "123456789";
        IdentificationType type = new IdentificationType();
        type.setDescription("SSN");
        when(partyJpaRepository.findByIdentification(identifier, type.getDescription()))
                .thenReturn(Optional.of(testPerson));

        // When
        Optional<Party> foundParty = partyRepository.findByIdentification(identifier, type);

        // Then
        assertThat(foundParty).isPresent();
        assertThat(foundParty.get()).isEqualTo(testPerson);
        verify(partyJpaRepository).findByIdentification(identifier, type.getDescription());
    }

    @Test
    @DisplayName("Should find parties by classification")
    void shouldFindPartiesByClassification() {
        // Given
        PartyClassificationType classificationType = new PartyClassificationType();
        classificationType.setDescription("VIP");
        String value = "Gold";
        List<Party> parties = Collections.singletonList(testPerson);
        when(partyJpaRepository.findByClassification(classificationType.getDescription(), value))
                .thenReturn(parties);

        // When
        List<Party> foundParties = partyRepository.findByClassification(classificationType, value);

        // Then
        assertThat(foundParties).hasSize(1);
        assertThat(foundParties).contains(testPerson);
        verify(partyJpaRepository).findByClassification(classificationType.getDescription(), value);
    }

    @Test
    @DisplayName("Should delete party by ID")
    void shouldDeletePartyById() {
        // Given
        doNothing().when(partyJpaRepository).deleteById(any(UUID.class));

        // When
        partyRepository.deleteById(personId);

        // Then
        verify(partyJpaRepository).deleteById(personId);
    }

    @Test
    @DisplayName("Should check if party exists by ID")
    void shouldCheckIfPartyExistsById() {
        // Given
        when(partyJpaRepository.existsById(any(UUID.class))).thenReturn(true);

        // When
        boolean exists = partyRepository.existsById(personId);

        // Then
        assertThat(exists).isTrue();
        verify(partyJpaRepository).existsById(personId);
    }

    @Test
    @DisplayName("Should count all parties")
    void shouldCountAllParties() {
        // Given
        when(partyJpaRepository.count()).thenReturn(10L);

        // When
        long count = partyRepository.count();

        // Then
        assertThat(count).isEqualTo(10L);
        verify(partyJpaRepository).count();
    }

    @Test
    @DisplayName("Should count parties by type")
    void shouldCountPartiesByType() {
        // Given
        PartyType partyType = new PartyType();
        partyType.setDescription("PERSON");
        when(partyJpaRepository.countByPartyType("PERSON")).thenReturn(5L);

        // When
        long count = partyRepository.countByType(partyType);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(partyJpaRepository).countByPartyType("PERSON");
    }

    @Test
    @DisplayName("Should find all parties with pagination")
    void shouldFindAllPartiesWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Party> parties = Arrays.asList(testPerson, testOrganization);
        Page<Party> partyPage = new PageImpl<>(parties, pageable, 2);
        when(partyJpaRepository.findAll(pageable)).thenReturn(partyPage);

        // When
        Page<Party> foundParties = partyRepository.findAll(pageable);

        // Then
        assertThat(foundParties.getContent()).hasSize(2);
        assertThat(foundParties.getContent()).containsExactly(testPerson, testOrganization);
        assertThat(foundParties.getTotalElements()).isEqualTo(2);
        verify(partyJpaRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should save all parties")
    void shouldSaveAllParties() {
        // Given
        List<Party> parties = Arrays.asList(testPerson, testOrganization);
        when(partyJpaRepository.saveAll(anyIterable())).thenReturn(parties);

        // When
        List<Party> savedParties = partyRepository.saveAll(parties);

        // Then
        assertThat(savedParties).hasSize(2);
        assertThat(savedParties).containsExactly(testPerson, testOrganization);
        verify(partyJpaRepository).saveAll(parties);
    }

    @Test
    @DisplayName("Should delete all parties")
    void shouldDeleteAllParties() {
        // Given
        doNothing().when(partyJpaRepository).deleteAll();

        // When
        partyRepository.deleteAll();

        // Then
        verify(partyJpaRepository).deleteAll();
    }
}
