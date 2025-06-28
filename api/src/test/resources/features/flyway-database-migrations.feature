Feature: Flyway Database Migrations
  As a developer
  I want to manage database schema changes through Flyway migrations
  So that database evolution is version-controlled and automated

  Background:
    Given a clean database environment
    And Flyway is properly configured

  Scenario: Flyway migration configuration is valid
    Given Flyway is configured in the database module
    When I validate the Flyway configuration
    Then the Flyway configuration should be valid
    And all required properties should be set

  Scenario: Initial database migration executes successfully
    Given a clean PostgreSQL database
    When I run Flyway migrate
    Then the migration should complete successfully
    And the flyway_schema_history table should exist
    And all baseline migrations should be recorded

  Scenario: Extensions are installed correctly
    Given a clean database
    When the V1.0.0__Install_extensions migration runs
    Then the uuid-ossp extension should be available
    And the pgcrypto extension should be available
    And no migration errors should occur

  Scenario: Core schema is created properly
    Given the extensions migration has completed
    When the V1.1.0__Create_initial_schema migration runs
    Then all core tables should exist
    And all foreign key constraints should be valid
    And all indexes should be created
    And the schema structure should match the JPA entities

  Scenario: Reference data is loaded correctly
    Given the core schema migration has completed
    When the V1.2.0__Insert_reference_data migration runs
    Then all party types should be loaded
    And all party role types should be loaded
    And all party classification types should be loaded
    And all identification types should be loaded
    And the data should match the expected reference values

  Scenario: US states data is loaded correctly
    Given the reference data migration has completed
    When the V1.3.0__Insert_us_states migration runs
    Then all 50 US states should be loaded
    And all US territories should be loaded
    And state codes should be unique
    And state names should be unique

  Scenario: Migration versioning follows semantic versioning
    Given Flyway migrations exist
    When I examine the migration file names
    Then all migrations should follow V{major}.{minor}.{patch}__{description}.sql format
    And migration versions should be in ascending order
    And no version conflicts should exist

  Scenario: Migration validation in CI/CD pipeline
    Given migrations are committed to the repository
    When the CI/CD pipeline runs
    Then Flyway validation should pass
    And migration checksums should be verified
    And no pending migrations should be reported

  Scenario: Database rollback capability
    Given migrations have been applied
    When a rollback is required
    Then rollback scripts should be available for reversible migrations
    And the rollback process should restore the previous state
    And data integrity should be maintained

  Scenario: Different environment configurations
    Given multiple deployment environments
    When Flyway runs in different environments
    Then each environment should use appropriate connection settings
    And migration behavior should be consistent across environments
    And environment-specific data migrations should apply correctly

  Scenario: Migration error handling
    Given a migration with intentional errors
    When Flyway attempts to run the migration
    Then the migration should fail gracefully
    And the database should remain in a consistent state
    And the error should be clearly reported
    And subsequent migrations should not run

  Scenario: Baseline existing database
    Given an existing database with schema
    When Flyway baseline is executed
    Then a baseline entry should be created in flyway_schema_history
    And subsequent migrations should apply correctly
    And existing data should be preserved
