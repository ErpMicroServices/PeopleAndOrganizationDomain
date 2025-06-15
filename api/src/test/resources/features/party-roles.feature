Feature: Party Role Management
  As a system user
  I want to assign and manage roles for parties
  So that I can define their capabilities and responsibilities in the system

  Background:
    Given the system is running
    And I am authenticated as an admin user
    And a person exists with first name "John" and last name "Doe"
    And an organization exists with name "Business Corp"

  Scenario: Assign customer role to a person
    When I assign the "CUSTOMER" role to the person
    Then the person should have the customer role
    And the role should be active
    And the role assignment should have a start date

  Scenario: Assign multiple roles to a party
    When I assign the following roles to the person:
      | roleType     | startDate  |
      | CUSTOMER     | 2024-01-01 |
      | EMPLOYEE     | 2024-02-01 |
      | CONTRACTOR   | 2024-03-01 |
    Then the person should have 3 active roles
    And each role should have the correct start date

  Scenario: Assign supplier role to an organization
    When I assign the "SUPPLIER" role to the organization
    Then the organization should have the supplier role
    And the role should be active

  Scenario: Role with validity period
    When I assign the "CONSULTANT" role to the person with:
      | startDate | 2024-01-01 |
      | endDate   | 2024-12-31 |
    Then the role should be valid for the specified period
    And the role should automatically expire after the end date

  Scenario: Update role information
    Given the person has a "CUSTOMER" role
    When I update the customer role with:
      | customerType | VIP        |
      | creditLimit  | 50000      |
    Then the role should have the updated information

  Scenario: Remove role from party
    Given the person has a "CONTRACTOR" role
    When I remove the contractor role from the person
    Then the person should no longer have the contractor role
    And the role should be marked as inactive

  Scenario: Query parties by role
    Given the following parties with roles exist:
      | partyName    | roleType  |
      | Alice Smith  | CUSTOMER  |
      | Bob Johnson  | CUSTOMER  |
      | Supplier Co  | SUPPLIER  |
    When I search for all parties with "CUSTOMER" role
    Then I should find 2 parties
    And both should be people, not organizations

  Scenario: Role-specific permissions
    When I assign the "ADMIN" role to the person
    Then the person should have administrative permissions
    And the person should be able to manage other users

  Scenario: Prevent duplicate role assignments
    Given the person already has a "CUSTOMER" role
    When I attempt to assign another "CUSTOMER" role to the person
    Then the operation should fail
    And the error should indicate the role is already assigned

  Scenario: Role hierarchy and inheritance
    When I assign the "MANAGER" role to the person
    And the manager role inherits permissions from "EMPLOYEE" role
    Then the person should have both manager and employee permissions

  Scenario: Conditional role assignment
    Given the person has the "EMPLOYEE" role
    When I assign the "TEAM_LEAD" role to the person
    Then the assignment should succeed because the person is an employee

  Scenario: Invalid role assignment
    When I attempt to assign a non-existent role "INVALID_ROLE" to the person
    Then the operation should fail
    And the error should indicate the role does not exist
