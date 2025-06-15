Feature: GraphQL API Operations
  As a client application
  I want to perform GraphQL operations
  So that I can interact with the People and Organizations domain

  Background:
    Given the system is running
    And the GraphQL endpoint is available
    And I am authenticated with valid credentials

  Scenario: Query person by ID via GraphQL
    Given a person exists with ID "12345"
    When I execute the GraphQL query:
      """
      query GetPerson($id: ID!) {
        person(id: $id) {
          id
          firstName
          lastName
          birthDate
        }
      }
      """
    With variables:
      | id | 12345 |
    Then the response should be successful
    And the response should contain the person's information

  Scenario: Create person via GraphQL mutation
    When I execute the GraphQL mutation:
      """
      mutation CreatePerson($input: CreatePersonInput!) {
        createPerson(input: $input) {
          id
          firstName
          lastName
          createdAt
        }
      }
      """
    With input:
      | firstName | Sarah      |
      | lastName  | Connor     |
      | birthDate | 1970-05-15 |
    Then the mutation should be successful
    And a new person should be created
    And the response should include the person's ID

  Scenario: Query organizations with filtering
    Given multiple organizations exist
    When I execute the GraphQL query:
      """
      query GetOrganizations($filter: OrganizationFilter) {
        organizations(filter: $filter) {
          edges {
            node {
              id
              name
              foundedDate
            }
          }
          totalCount
        }
      }
      """
    With filter:
      | name | Tech |
    Then the response should contain organizations with "Tech" in the name
    And the total count should reflect the filtered results

  Scenario: Add contact mechanism via GraphQL
    Given a person exists with ID "person123"
    When I execute the GraphQL mutation:
      """
      mutation AddEmailAddress($personId: ID!, $email: String!) {
        addEmailAddress(personId: $personId, email: $email) {
          id
          emailAddress
          party {
            id
          }
        }
      }
      """
    With variables:
      | personId | person123           |
      | email    | test@example.com    |
    Then the email address should be added successfully
    And the email should be associated with the person

  Scenario: GraphQL error handling for invalid input
    When I execute the GraphQL mutation:
      """
      mutation CreatePerson($input: CreatePersonInput!) {
        createPerson(input: $input) {
          id
          firstName
          lastName
        }
      }
      """
    With invalid input:
      | firstName |         |
      | lastName  | Invalid |
    Then the mutation should fail
    And the response should contain validation errors
    And the error should specify the invalid field

  Scenario: Query with nested relationships
    Given a person with contact mechanisms and roles exists
    When I execute the GraphQL query:
      """
      query GetPersonWithDetails($id: ID!) {
        person(id: $id) {
          id
          firstName
          lastName
          contactMechanisms {
            id
            type
            ... on EmailAddress {
              emailAddress
            }
            ... on PostalAddress {
              street1
              city
              state
            }
          }
          roles {
            id
            roleType
            validFrom
            validTo
          }
        }
      }
      """
    Then the response should include all nested relationship data
    And the contact mechanisms should be properly typed
    And the roles should include validity periods

  Scenario: Pagination in GraphQL queries
    Given 50 people exist in the system
    When I execute the GraphQL query:
      """
      query GetPeople($first: Int, $after: String) {
        people(first: $first, after: $after) {
          edges {
            node {
              id
              firstName
              lastName
            }
            cursor
          }
          pageInfo {
            hasNextPage
            hasPreviousPage
            startCursor
            endCursor
          }
        }
      }
      """
    With pagination parameters:
      | first | 10 |
    Then the response should contain 10 people
    And pagination info should indicate more pages available
    And each edge should have a cursor for further pagination

  Scenario: GraphQL subscription for real-time updates
    When I subscribe to person creation events:
      """
      subscription PersonCreated {
        personCreated {
          id
          firstName
          lastName
          createdAt
        }
      }
      """
    And a new person is created in another session
    Then I should receive a subscription notification
    And the notification should contain the new person's details
