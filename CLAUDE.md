# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Commands

### Build and Test
```bash
# Build entire project
./gradlew build

# Run all tests
./gradlew test

# Clean build artifacts
./gradlew clean

# API module specific
./gradlew :api:build         # Build API module
./gradlew :api:test          # Run API tests  
./gradlew :api:bootRun       # Run Spring Boot application

# Database module
./gradlew :database:build    # Build database module

# UI Components (React/Vite)
cd ui-components
npm install                  # Install dependencies
npm run dev                  # Run development server
npm run build                # Build for production
npm run lint                 # Run ESLint
npm run preview              # Preview production build
```

## Architecture Overview

This is a **People and Organizations Domain** microservice implementing Domain-Driven Design (DDD) patterns. It provides core models for managing people, organizations, and their relationships.

### Project Structure
- **Multi-module Gradle project** with three main modules:
  - `api/` - Spring Boot GraphQL API with DDD architecture
  - `database/` - PostgreSQL migrations and SQL scripts
  - `ui-components/` - React components with Vite

### API Module Architecture (DDD Layers)
```
api/src/main/java/org/erp_microservices/peopleandorganizations/api/
├── application/          # Application layer (DTOs, GraphQL resolvers)
│   ├── dto/             # Data transfer objects
│   └── graphql/         # GraphQL resolvers
├── domain/              # Domain layer (pure business logic)
│   ├── model/           # Domain entities and value objects
│   │   ├── party/       # Party aggregate (Person, Organization)
│   │   ├── contactmechanism/
│   │   ├── communication/
│   │   └── partyrelationship/
│   ├── repository/      # Repository interfaces (abstractions)
│   └── service/         # Domain services
└── infrastructure/      # Infrastructure layer (implementations)
    └── repository/      # JPA repository implementations
```

### Key Domain Concepts
- **Party Model**: Base abstraction for Person and Organization entities
- **Party Roles**: Flexible role system (e.g., Customer, Supplier, Employee)
- **Party Classifications**: Categorization system for parties
- **Party Identifications**: External IDs (SSN, EIN, etc.)
- **Contact Mechanisms**: Phone, email, address management
- **Party Relationships**: Relationships between parties

### Technology Stack
- **Java 21** with Spring Boot 3.4.5
- **Spring GraphQL** for API layer
- **Spring Data JPA** with PostgreSQL
- **Spring Security** with OAuth2 (AWS Cognito)
- **Testcontainers** for integration testing
- **Lombok** for reducing boilerplate

## Development Workflow

The project follows a strict BDD-driven workflow as defined in the README:

1. Write BDD features in Gherkin format
2. Create feature branch
3. Database work: data integrity tests → migrations → tests
4. API work: tests → implementation  
5. UI work: tests → components
6. Resolve CI/CD defects
7. Release

### Quality Requirements
- **90% unit test coverage** (excluding getters/setters)
- **80% BDD test coverage** minimum
- All static code analysis checks must pass
- OWASP Top 10 security compliance

## Database Configuration
- PostgreSQL database: `people_and_organizations`
- Default connection: `localhost:5432`
- SQL scripts location: `database/src/main/sql/`

## GraphQL Development
- GraphQL endpoint: `/graphql`
- GraphiQL UI enabled in development
- Schema location: `api/src/main/resources/graphql/`

## Testing Approach
- Unit tests: Standard JUnit in `src/test/java`
- Integration tests: Testcontainers with PostgreSQL
- BDD tests: Cucumber (planned, not yet implemented)
- Test data setup: Use `TestcontainersConfiguration.java`

## Current Implementation Status
- Basic domain models implemented (Party, Person, Organization)
- Repository pattern with JPA implementations
- GraphQL infrastructure ready but no schema defined yet
- OAuth2 security configured but not fully integrated
- BDD testing framework mentioned but not implemented