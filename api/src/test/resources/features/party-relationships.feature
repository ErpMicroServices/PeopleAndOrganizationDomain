Feature: Party Relationship Management
  As a system user
  I want to manage relationships between parties
  So that I can track business and personal connections

  Background:
    Given the system is running
    And I am authenticated as an admin user
    And a person exists with first name "John" and last name "Employee"
    And an organization exists with name "Tech Corp"

  Scenario: Create employment relationship
    When I create an employment relationship between the person and organization with:
      | relationshipType | EMPLOYMENT |
      | startDate       | 2024-01-01 |
      | position        | Developer  |
    Then the employment relationship should be created successfully
    And the person should be an employee of the organization
    And the relationship should have the specified start date and position

  Scenario: Create partnership between organizations
    Given another organization exists with name "Partner Corp"
    When I create a partnership relationship between "Tech Corp" and "Partner Corp" with:
      | relationshipType | PARTNERSHIP |
      | startDate       | 2024-01-01  |
      | agreementType   | Strategic   |
    Then the partnership should be created successfully
    And both organizations should be linked as partners

  Scenario: Update relationship information
    Given an employment relationship exists between the person and organization
    When I update the relationship with:
      | position | Senior Developer |
      | salary   | 75000           |
    Then the relationship should have the updated information

  Scenario: End a relationship
    Given an employment relationship exists between the person and organization
    When I end the relationship with end date "2024-12-31"
    Then the relationship should have the specified end date
    And the relationship should be marked as ended

  Scenario: Query relationships by type
    Given the following relationships exist:
      | fromParty    | toParty     | relationshipType |
      | John Doe     | Tech Corp   | EMPLOYMENT       |
      | Jane Smith   | Tech Corp   | EMPLOYMENT       |
      | Tech Corp    | Client Corp | CUSTOMER         |
    When I query for all employment relationships for "Tech Corp"
    Then I should find 2 employment relationships

  Scenario: Relationship with roles
    When I create a relationship between the person and organization with:
      | relationshipType | EMPLOYMENT |
      | fromRole        | EMPLOYEE   |
      | toRole          | EMPLOYER   |
      | startDate       | 2024-01-01 |
    Then both parties should have the appropriate roles in the relationship

  Scenario: Prevent duplicate relationships
    Given an employment relationship already exists between the person and organization
    When I attempt to create another employment relationship between the same parties
    Then the operation should fail
    And the error should indicate a duplicate relationship exists

  Scenario: Relationship hierarchy
    Given a parent organization "Parent Corp" exists
    And a subsidiary organization "Child Corp" exists
    When I create an ownership relationship with "Parent Corp" owning "Child Corp"
    Then the ownership hierarchy should be established
    And "Child Corp" should be a subsidiary of "Parent Corp"
