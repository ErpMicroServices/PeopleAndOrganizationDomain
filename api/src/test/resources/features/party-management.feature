Feature: Party Management
  As a system user
  I want to manage people and organizations
  So that I can track business relationships

  Background:
    Given the system is running
    And I am authenticated as an admin user

  Scenario: Create a new person
    When I create a person with:
      | firstName | John      |
      | lastName  | Doe       |
      | birthDate | 1985-03-15 |
    Then the person should be created successfully
    And the person should have a unique identifier
    And the person should be retrievable by their identifier

  Scenario: Create a new organization
    When I create an organization with:
      | name        | Acme Corporation |
      | description | Technology company |
      | foundedDate | 2000-01-01       |
    Then the organization should be created successfully
    And the organization should have a unique identifier
    And the organization should be retrievable by their identifier

  Scenario: Update person information
    Given a person exists with first name "Jane" and last name "Smith"
    When I update the person's first name to "Janet"
    Then the person's first name should be "Janet"
    And the person should be retrievable with the updated information

  Scenario: Find person by name
    Given the following people exist:
      | firstName | lastName |
      | Alice     | Johnson  |
      | Bob       | Wilson   |
      | Charlie   | Brown    |
    When I search for people with last name "Johnson"
    Then I should find 1 person
    And the person should have first name "Alice"

  Scenario: Delete a person
    Given a person exists with first name "Test" and last name "User"
    When I delete the person
    Then the person should be removed from the system
    And I should not be able to retrieve the person by their identifier

  Scenario: Create person with validation errors
    When I attempt to create a person with:
      | firstName |          |
      | lastName  | TestUser |
    Then the creation should fail with validation errors
    And the error should indicate that first name is required
