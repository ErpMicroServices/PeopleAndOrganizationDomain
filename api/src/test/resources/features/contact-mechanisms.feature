Feature: Contact Mechanism Management
  As a system user
  I want to manage contact information for parties
  So that I can communicate with people and organizations

  Background:
    Given the system is running
    And I am authenticated as an admin user
    And a person exists with first name "John" and last name "Doe"

  Scenario: Add email address to a person
    When I add an email address "john.doe@example.com" to the person
    Then the email address should be associated with the person
    And the email address should be retrievable for the person

  Scenario: Add multiple email addresses
    When I add the following email addresses to the person:
      | email                    | purpose |
      | john.doe@work.com       | work    |
      | john.doe@personal.com   | home    |
    Then the person should have 2 email addresses
    And each email address should have the correct purpose

  Scenario: Add postal address to a person
    When I add a postal address to the person with:
      | street1    | 123 Main Street    |
      | city       | Springfield        |
      | state      | IL                 |
      | postalCode | 62701             |
      | country    | USA               |
    Then the postal address should be associated with the person
    And the address should be retrievable for the person

  Scenario: Add phone number to a person
    When I add a phone number "555-123-4567" to the person
    Then the phone number should be associated with the person
    And the phone number should be retrievable for the person

  Scenario: Update contact mechanism
    Given the person has an email address "old@example.com"
    When I update the email address to "new@example.com"
    Then the person should have the updated email address
    And the old email address should no longer be associated

  Scenario: Remove contact mechanism
    Given the person has an email address "remove@example.com"
    When I remove the email address from the person
    Then the email address should no longer be associated with the person

  Scenario: Validate email format
    When I attempt to add an invalid email address "invalid-email" to the person
    Then the operation should fail with validation errors
    And the error should indicate invalid email format

  Scenario: Contact mechanism with purpose and validity dates
    When I add an email address "business@example.com" to the person with:
      | purpose   | business   |
      | validFrom | 2024-01-01 |
      | validTo   | 2024-12-31 |
    Then the email should be associated with the specified purpose and validity period
