package org.erp_microservices.peopleandorganizations.api.infrastructure.repository;

import org.erp_microservices.peopleandorganizations.api.domain.model.party.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PartyJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PartyJpaRepository partyJpaRepository;

    private Person testPerson;
    private Organization testOrganization;
    private PartyType personType;
    private PartyType organizationType;
    private PartyRoleType customerRoleType;
    private PartyRoleType employeeRoleType;

    @BeforeEach
    void setUp() {
        // Create and persist party types
        personType = new PartyType();
        personType.setDescription("PERSON");
        personType = entityManager.persistAndFlush(personType);

        organizationType = new PartyType();
        organizationType.setDescription("ORGANIZATION");
        organizationType = entityManager.persistAndFlush(organizationType);

        // Create and persist role types
        customerRoleType = new PartyRoleType();
        customerRoleType.setDescription("CUSTOMER");
        customerRoleType = entityManager.persistAndFlush(customerRoleType);

        employeeRoleType = new PartyRoleType();
        employeeRoleType.setDescription("EMPLOYEE");
        employeeRoleType = entityManager.persistAndFlush(employeeRoleType);

        // Create test person
        testPerson = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Michael")
                .birthDate(LocalDate.of(1990, 1, 15))
                .genderType(GenderType.MALE)
                .build();
        testPerson.setPartyType("PERSON");
        testPerson.setPartyTypeRef(personType);

        // Create test organization
        testOrganization = Organization.builder()
                .name("Acme Corporation")
                .tradingName("Acme Corp")
                .registrationNumber("REG123456")
                .taxIdNumber("TAX987654")
                .build();
        testOrganization.setPartyType("ORGANIZATION");
        testOrganization.setPartyTypeRef(organizationType);
    }

    @Test
    @DisplayName("Should save and find person")
    void shouldSaveAndFindPerson() {
        // When
        Person savedPerson = entityManager.persistAndFlush(testPerson);

        // Then
        assertThat(savedPerson.getId()).isNotNull();
        Party foundParty = partyJpaRepository.findById(savedPerson.getId()).orElse(null);
        assertThat(foundParty).isNotNull();
        assertThat(foundParty).isInstanceOf(Person.class);
        Person foundPerson = (Person) foundParty;
        assertThat(foundPerson.getFirstName()).isEqualTo("John");
        assertThat(foundPerson.getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Should save and find organization")
    void shouldSaveAndFindOrganization() {
        // When
        Organization savedOrg = entityManager.persistAndFlush(testOrganization);

        // Then
        assertThat(savedOrg.getId()).isNotNull();
        Party foundParty = partyJpaRepository.findById(savedOrg.getId()).orElse(null);
        assertThat(foundParty).isNotNull();
        assertThat(foundParty).isInstanceOf(Organization.class);
        Organization foundOrg = (Organization) foundParty;
        assertThat(foundOrg.getName()).isEqualTo("Acme Corporation");
    }

    @Test
    @DisplayName("Should find by party type")
    void shouldFindByPartyType() {
        // Given
        entityManager.persistAndFlush(testPerson);
        entityManager.persistAndFlush(testOrganization);

        // When
        List<Party> persons = partyJpaRepository.findByPartyType("PERSON");
        List<Party> organizations = partyJpaRepository.findByPartyType("ORGANIZATION");

        // Then
        assertThat(persons).hasSize(1);
        assertThat(persons.get(0)).isInstanceOf(Person.class);
        assertThat(organizations).hasSize(1);
        assertThat(organizations.get(0)).isInstanceOf(Organization.class);
    }

    @Test
    @DisplayName("Should find by name containing")
    void shouldFindByNameContaining() {
        // Given
        NameType legalNameType = new NameType();
        legalNameType.setDescription("Legal Name");
        legalNameType = entityManager.persistAndFlush(legalNameType);

        entityManager.persistAndFlush(testPerson);
        entityManager.persistAndFlush(testOrganization);

        // Add party names
        PartyName personName = PartyName.builder()
                .party(testPerson)
                .nameType(legalNameType)
                .name("John Doe")
                .build();
        testPerson.getNames().add(personName);

        PartyName orgName = PartyName.builder()
                .party(testOrganization)
                .nameType(legalNameType)
                .name("Acme Corporation")
                .build();
        testOrganization.getNames().add(orgName);

        entityManager.persistAndFlush(testPerson);
        entityManager.persistAndFlush(testOrganization);

        // When
        List<Party> foundByDoe = partyJpaRepository.findByNameContaining("Doe");
        List<Party> foundByAcme = partyJpaRepository.findByNameContaining("Acme");

        // Then
        assertThat(foundByDoe).hasSize(1);
        assertThat(foundByDoe.get(0)).isInstanceOf(Person.class);
        assertThat(foundByAcme).hasSize(1);
        assertThat(foundByAcme.get(0)).isInstanceOf(Organization.class);
    }

    @Test
    @DisplayName("Should find persons by last name")
    void shouldFindPersonsByLastName() {
        // Given
        entityManager.persistAndFlush(testPerson);
        Person anotherPerson = Person.builder()
                .firstName("Jane")
                .lastName("Doe")
                .build();
        anotherPerson.setPartyType("PERSON");
        anotherPerson.setPartyTypeRef(personType);
        entityManager.persistAndFlush(anotherPerson);

        // When
        List<Person> foundPersons = partyJpaRepository.findPersonsByLastName("Doe");

        // Then
        assertThat(foundPersons).hasSize(2);
        assertThat(foundPersons).extracting("firstName").containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    @DisplayName("Should find organizations by name")
    void shouldFindOrganizationsByName() {
        // Given
        entityManager.persistAndFlush(testOrganization);
        Organization anotherOrg = Organization.builder()
                .name("Acme Industries")
                .build();
        anotherOrg.setPartyType("ORGANIZATION");
        anotherOrg.setPartyTypeRef(organizationType);
        entityManager.persistAndFlush(anotherOrg);

        // When
        List<Organization> foundOrgs = partyJpaRepository.findOrganizationsByName("Acme");

        // Then
        assertThat(foundOrgs).hasSize(2);
        assertThat(foundOrgs).extracting("name").containsExactlyInAnyOrder("Acme Corporation", "Acme Industries");
    }

    @Test
    @DisplayName("Should find by party roles")
    void shouldFindByPartyRoles() {
        // Given
        PartyRole customerRole = PartyRole.builder()
                .party(testPerson)
                .roleType(customerRoleType)
                .fromDate(LocalDate.now())
                .build();
        testPerson.addPartyRole(customerRole);
        entityManager.persistAndFlush(testPerson);

        // When
        List<Party> customers = partyJpaRepository.findByActiveRole(customerRoleType.getDescription());

        // Then
        assertThat(customers).hasSize(1);
        assertThat(customers.get(0)).isEqualTo(testPerson);
    }

    @Test
    @DisplayName("Should count by party type")
    void shouldCountByPartyType() {
        // Given
        entityManager.persistAndFlush(testPerson);
        entityManager.persistAndFlush(testOrganization);
        Person anotherPerson = Person.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();
        anotherPerson.setPartyType("PERSON");
        anotherPerson.setPartyTypeRef(personType);
        entityManager.persistAndFlush(anotherPerson);

        // When
        long personCount = partyJpaRepository.countByPartyType("PERSON");
        long orgCount = partyJpaRepository.countByPartyType("ORGANIZATION");

        // Then
        assertThat(personCount).isEqualTo(2);
        assertThat(orgCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find by party identification")
    void shouldFindByPartyIdentification() {
        // Given
        IdentificationType ssnType = new IdentificationType();
        ssnType.setDescription("SSN");
        ssnType = entityManager.persistAndFlush(ssnType);

        PartyIdentification identification = new PartyIdentification();
        identification.setParty(testPerson);
        identification.setIdentificationType(ssnType);
        identification.setIdentifier("123-45-6789");
        identification.setFromDate(LocalDate.now());

        testPerson.addPartyIdentification(identification);
        entityManager.persistAndFlush(testPerson);

        // When
        Optional<Party> foundParty = partyJpaRepository
                .findByIdentification("123-45-6789", ssnType.getDescription());

        // Then
        assertThat(foundParty).isPresent();
        assertThat(foundParty.get()).isEqualTo(testPerson);
    }

    @Test
    @DisplayName("Should find by party classification")
    void shouldFindByPartyClassification() {
        // Given
        PartyClassificationType vipType = new PartyClassificationType();
        vipType.setDescription("VIP");
        vipType = entityManager.persistAndFlush(vipType);

        PartyClassification classification = new PartyClassification();
        classification.setParty(testPerson);
        classification.setClassificationType(vipType);
        classification.setValue("Gold");
        classification.setFromDate(LocalDate.now());

        testPerson.addPartyClassification(classification);
        entityManager.persistAndFlush(testPerson);

        // When
        List<Party> foundParties = partyJpaRepository
                .findByClassification(vipType.getDescription(), "Gold");

        // Then
        assertThat(foundParties).hasSize(1);
        assertThat(foundParties.get(0)).isEqualTo(testPerson);
    }
}
