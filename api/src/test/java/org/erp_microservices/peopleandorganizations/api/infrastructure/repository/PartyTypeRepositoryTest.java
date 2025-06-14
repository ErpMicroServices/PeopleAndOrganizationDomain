package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.PartyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PartyTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PartyTypeRepository partyTypeRepository;

    private PartyType personType;
    private PartyType organizationType;

    @BeforeEach
    void setUp() {
        personType = new PartyType();
        personType.setDescription("PERSON");
        personType.setParent(null);

        organizationType = new PartyType();
        organizationType.setDescription("ORGANIZATION");
        organizationType.setParent(null);
    }

    @Test
    @DisplayName("Should save and find party type by ID")
    void shouldSaveAndFindPartyTypeById() {
        // When
        PartyType savedType = entityManager.persistAndFlush(personType);

        // Then
        assertThat(savedType.getId()).isNotNull();
        Optional<PartyType> foundType = partyTypeRepository.findById(savedType.getId());
        assertThat(foundType).isPresent();
        assertThat(foundType.get().getDescription()).isEqualTo("PERSON");
    }

    @Test
    @DisplayName("Should find party type by description")
    void shouldFindPartyTypeByDescription() {
        // Given
        entityManager.persistAndFlush(personType);
        entityManager.persistAndFlush(organizationType);

        // When
        Optional<PartyType> foundPerson = partyTypeRepository.findByDescription("PERSON");
        Optional<PartyType> foundOrg = partyTypeRepository.findByDescription("ORGANIZATION");
        Optional<PartyType> notFound = partyTypeRepository.findByDescription("UNKNOWN");

        // Then
        assertThat(foundPerson).isPresent();
        assertThat(foundPerson.get().getDescription()).isEqualTo("PERSON");
        assertThat(foundOrg).isPresent();
        assertThat(foundOrg.get().getDescription()).isEqualTo("ORGANIZATION");
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("Should handle hierarchical party types")
    void shouldHandleHierarchicalPartyTypes() {
        // Given
        PartyType savedPersonType = entityManager.persistAndFlush(personType);

        PartyType employeeType = new PartyType();
        employeeType.setDescription("EMPLOYEE");
        employeeType.setParent(savedPersonType);

        // When
        PartyType savedEmployeeType = entityManager.persistAndFlush(employeeType);

        // Then
        Optional<PartyType> foundEmployee = partyTypeRepository.findByDescription("EMPLOYEE");
        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getParent()).isNotNull();
        assertThat(foundEmployee.get().getParent().getDescription()).isEqualTo("PERSON");
    }

    @Test
    @DisplayName("Should count party types")
    void shouldCountPartyTypes() {
        // Given
        entityManager.persistAndFlush(personType);
        entityManager.persistAndFlush(organizationType);

        // When
        long count = partyTypeRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should delete party type")
    void shouldDeletePartyType() {
        // Given
        PartyType savedType = entityManager.persistAndFlush(personType);
        UUID typeId = savedType.getId();

        // When
        partyTypeRepository.deleteById(typeId);
        entityManager.flush();

        // Then
        Optional<PartyType> deleted = partyTypeRepository.findById(typeId);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should find all party types")
    void shouldFindAllPartyTypes() {
        // Given
        entityManager.persistAndFlush(personType);
        entityManager.persistAndFlush(organizationType);

        // When
        var allTypes = partyTypeRepository.findAll();

        // Then
        assertThat(allTypes).hasSize(2);
        assertThat(allTypes).extracting("description")
                .containsExactlyInAnyOrder("PERSON", "ORGANIZATION");
    }

    @Test
    @DisplayName("Should check if party type exists by ID")
    void shouldCheckIfPartyTypeExistsById() {
        // Given
        PartyType savedType = entityManager.persistAndFlush(personType);

        // When
        boolean exists = partyTypeRepository.existsById(savedType.getId());
        boolean notExists = partyTypeRepository.existsById(UUID.randomUUID());

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
