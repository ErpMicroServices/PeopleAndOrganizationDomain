package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.PartyRoleType;
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
class PartyRoleTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PartyRoleTypeRepository partyRoleTypeRepository;

    private PartyRoleType customerRoleType;
    private PartyRoleType employeeRoleType;
    private PartyRoleType supplierRoleType;

    @BeforeEach
    void setUp() {
        customerRoleType = new PartyRoleType();
        customerRoleType.setDescription("CUSTOMER");
        customerRoleType.setParent(null);

        employeeRoleType = new PartyRoleType();
        employeeRoleType.setDescription("EMPLOYEE");
        employeeRoleType.setParent(null);

        supplierRoleType = new PartyRoleType();
        supplierRoleType.setDescription("SUPPLIER");
        supplierRoleType.setParent(null);
    }

    @Test
    @DisplayName("Should save and find party role type by ID")
    void shouldSaveAndFindPartyRoleTypeById() {
        // When
        PartyRoleType savedType = entityManager.persistAndFlush(customerRoleType);

        // Then
        assertThat(savedType.getId()).isNotNull();
        Optional<PartyRoleType> foundType = partyRoleTypeRepository.findById(savedType.getId());
        assertThat(foundType).isPresent();
        assertThat(foundType.get().getDescription()).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("Should find party role type by description")
    void shouldFindPartyRoleTypeByDescription() {
        // Given
        entityManager.persistAndFlush(customerRoleType);
        entityManager.persistAndFlush(employeeRoleType);
        entityManager.persistAndFlush(supplierRoleType);

        // When
        Optional<PartyRoleType> foundCustomer = partyRoleTypeRepository.findByDescription("CUSTOMER");
        Optional<PartyRoleType> foundEmployee = partyRoleTypeRepository.findByDescription("EMPLOYEE");
        Optional<PartyRoleType> foundSupplier = partyRoleTypeRepository.findByDescription("SUPPLIER");
        Optional<PartyRoleType> notFound = partyRoleTypeRepository.findByDescription("UNKNOWN");

        // Then
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getDescription()).isEqualTo("CUSTOMER");
        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getDescription()).isEqualTo("EMPLOYEE");
        assertThat(foundSupplier).isPresent();
        assertThat(foundSupplier.get().getDescription()).isEqualTo("SUPPLIER");
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("Should handle hierarchical party role types")
    void shouldHandleHierarchicalPartyRoleTypes() {
        // Given
        PartyRoleType savedEmployeeType = entityManager.persistAndFlush(employeeRoleType);

        PartyRoleType managerRoleType = new PartyRoleType();
        managerRoleType.setDescription("MANAGER");
        managerRoleType.setParent(savedEmployeeType);

        PartyRoleType executiveRoleType = new PartyRoleType();
        executiveRoleType.setDescription("EXECUTIVE");
        executiveRoleType.setParent(savedEmployeeType);

        // When
        entityManager.persistAndFlush(managerRoleType);
        entityManager.persistAndFlush(executiveRoleType);

        // Then
        Optional<PartyRoleType> foundManager = partyRoleTypeRepository.findByDescription("MANAGER");
        Optional<PartyRoleType> foundExecutive = partyRoleTypeRepository.findByDescription("EXECUTIVE");

        assertThat(foundManager).isPresent();
        assertThat(foundManager.get().getParent()).isNotNull();
        assertThat(foundManager.get().getParent().getDescription()).isEqualTo("EMPLOYEE");

        assertThat(foundExecutive).isPresent();
        assertThat(foundExecutive.get().getParent()).isNotNull();
        assertThat(foundExecutive.get().getParent().getDescription()).isEqualTo("EMPLOYEE");
    }

    @Test
    @DisplayName("Should count party role types")
    void shouldCountPartyRoleTypes() {
        // Given
        entityManager.persistAndFlush(customerRoleType);
        entityManager.persistAndFlush(employeeRoleType);
        entityManager.persistAndFlush(supplierRoleType);

        // When
        long count = partyRoleTypeRepository.count();

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should delete party role type")
    void shouldDeletePartyRoleType() {
        // Given
        PartyRoleType savedType = entityManager.persistAndFlush(customerRoleType);
        UUID typeId = savedType.getId();

        // When
        partyRoleTypeRepository.deleteById(typeId);
        entityManager.flush();

        // Then
        Optional<PartyRoleType> deleted = partyRoleTypeRepository.findById(typeId);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should find all party role types")
    void shouldFindAllPartyRoleTypes() {
        // Given
        entityManager.persistAndFlush(customerRoleType);
        entityManager.persistAndFlush(employeeRoleType);
        entityManager.persistAndFlush(supplierRoleType);

        // When
        var allTypes = partyRoleTypeRepository.findAll();

        // Then
        assertThat(allTypes).hasSize(3);
        assertThat(allTypes).extracting("description")
                .containsExactlyInAnyOrder("CUSTOMER", "EMPLOYEE", "SUPPLIER");
    }

    @Test
    @DisplayName("Should check if party role type exists by ID")
    void shouldCheckIfPartyRoleTypeExistsById() {
        // Given
        PartyRoleType savedType = entityManager.persistAndFlush(customerRoleType);

        // When
        boolean exists = partyRoleTypeRepository.existsById(savedType.getId());
        boolean notExists = partyRoleTypeRepository.existsById(UUID.randomUUID());

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should update party role type description")
    void shouldUpdatePartyRoleTypeDescription() {
        // Given
        PartyRoleType savedType = entityManager.persistAndFlush(customerRoleType);

        // When
        savedType.setDescription("VALUED_CUSTOMER");
        PartyRoleType updatedType = entityManager.persistAndFlush(savedType);

        // Then
        Optional<PartyRoleType> found = partyRoleTypeRepository.findById(updatedType.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("VALUED_CUSTOMER");
    }
}
