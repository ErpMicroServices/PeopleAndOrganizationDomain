# Integration Tests Configuration

This document describes the integration test setup and configuration for the People and Organizations Domain microservice.

## Overview

The project includes comprehensive integration tests that verify the GraphQL API endpoints work correctly with a real PostgreSQL database. All 17 integration tests are currently passing with 100% success rate.

## Test Environment

- **Database**: PostgreSQL running in Docker container
- **Test Framework**: Spring Boot Test with HttpGraphQlTester
- **Test Data**: Created dynamically in each test setup
- **Transaction Management**: Each test runs in a transaction that's rolled back

## Pre-Push Hook Configuration

The pre-push hooks have been configured to properly run tests with the Docker environment:

- Unit tests run with PostgreSQL connectivity
- Integration tests run with full Docker setup
- Coverage validation includes database-dependent tests
- Automatic cleanup of test containers after execution

## Running Tests

Use the provided script to run all tests with proper environment setup:

```bash
./scripts/run-tests.sh
```

This script:
1. Starts Docker Compose test dependencies
2. Waits for PostgreSQL to be ready
3. Runs unit tests and integration tests
4. Cleans up test containers

## Test Coverage

- **Integration Tests**: 17 tests covering all GraphQL endpoints
- **Success Rate**: 100% passing
- **Test Categories**: Party, ContactMechanism, PartyRelationship APIs
