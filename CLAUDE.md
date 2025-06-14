# CLAUDE.md - People and Organizations Domain

This file provides project-specific guidance to Claude Code when working with this repository.

## Development Standards Reference

This project follows my universal development standards documented in ~/.claude/CLAUDE.md.

## Project-Specific Overrides/Extensions

- **GraphQL-First Development**: While maintaining TDD principles, integration tests focus on GraphQL API endpoints rather than REST
- **Domain-Driven Design**: Strict adherence to DDD tactical patterns with clear separation of domain, application, and infrastructure layers
- **Multi-Module Structure**: Gradle multi-module project requiring module-specific commands

## Project Overview

The People and Organizations Domain microservice provides core business models for managing people (individuals), organizations (companies, groups), and their relationships. It serves as a foundational service for ERP systems, providing flexible party management with roles, relationships, classifications, and contact mechanisms.

## Architecture Overview

### DDD Layer Architecture
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
- **Party Model**: Universal abstraction for Person and Organization entities
- **Party Roles**: Flexible role system (Customer, Supplier, Employee, etc.)
- **Party Classifications**: Dynamic categorization (VIP, Gold Customer, etc.)
- **Party Identifications**: External IDs (SSN, EIN, Passport, etc.)
- **Contact Mechanisms**: Polymorphic contact info (EmailAddress, PostalAddress, TelecomNumber)
- **Party Relationships**: Typed relationships between parties (Employment, Ownership, etc.)

## Technology Stack

- **Java 21** (configured via gradle.properties to handle Java 24 environment)
- **Spring Boot 3.4.5**
- **Spring GraphQL** for API layer
- **Spring Data JPA** with PostgreSQL
- **Spring Security** with OAuth2 (AWS Cognito ready)
- **Testcontainers** for integration testing
- **Lombok** for boilerplate reduction
- **Gradle 8.13** (multi-module build)
- **PostgreSQL 15+** for persistence
- **React/Vite** for UI components (planned)

## Project Structure

```
PeopleAndOrganizationDomain/
├── api/                     # Spring Boot GraphQL API module
│   ├── src/main/java/      # Production code (DDD layers)
│   ├── src/test/java/      # Test code
│   └── build.gradle        # API module build config
├── database/               # Database module (migrations)
│   └── src/main/sql/      # SQL migration scripts
├── ui-components/          # React component library
│   ├── src/               # React components
│   └── package.json       # NPM configuration
├── build.gradle           # Root build configuration
├── settings.gradle        # Multi-module settings
└── gradle.properties      # Java 21 configuration
```

## Project-Specific Commands

### Environment Setup
```bash
# Ensure Java 21 is available (project uses gradle.properties to configure)
java -version  # Should show Java 21 or compatible

# Clone and setup
git clone <repository>
cd PeopleAndOrganizationDomain
./gradlew build
```

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
./gradlew :api:compileJava   # Compile only
./gradlew :api:compileTestJava # Compile tests

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

### Deployment
```bash
# Build production JAR
./gradlew :api:bootJar

# Run with external configuration
java -jar api/build/libs/api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Docker deployment (when Dockerfile is added)
# docker build -t people-org-api .
# docker run -p 8080:8080 people-org-api
```

## Configuration

### Required Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/people_and_organizations
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# OAuth2 (AWS Cognito)
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_CLIENT_ID=your_client_id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_CLIENT_SECRET=your_secret
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_COGNITO_ISSUER_URI=https://cognito-idp.region.amazonaws.com/poolId
```

### Application Configuration
- Main config: `api/src/main/resources/application.yml`
- GraphQL endpoint: `/graphql`
- GraphiQL UI: `/graphiql` (development only)
- Default port: 8080

## Testing Approach

### Test Categories
1. **Unit Tests**: Domain model and service logic testing
   - Location: `src/test/java/.../domain/model/`
   - Coverage requirement: 90% (excluding getters/setters)

2. **Integration Tests**: GraphQL API endpoint testing
   - Location: `src/test/java/.../integration/`
   - Uses: Testcontainers for PostgreSQL
   - Coverage requirement: 80% minimum

3. **Repository Tests**: JPA repository testing
   - Location: `src/test/java/.../infrastructure/repository/`
   - Uses: @DataJpaTest with H2 or Testcontainers

4. **BDD Tests**: Cucumber tests (planned, not implemented)
   - Will use Gherkin syntax
   - Focus on business scenarios

## Current Implementation Status

### ✅ Completed Features
- Domain model implementation (Party, Person, Organization)
- Contact mechanism models (EmailAddress, PostalAddress, TelecomNumber)
- Party relationship models
- Repository interfaces and implementations
- Comprehensive unit test coverage for domain models
- Integration test structure for GraphQL API
- Multi-module Gradle build configuration
- Java 21 compatibility configuration

### 📋 TODO Items
- [ ] GraphQL schema definition (`api/src/main/resources/graphql/schema.graphqls`)
- [ ] GraphQL resolver implementations
- [ ] DTO layer for GraphQL types
- [ ] Database migration scripts
- [ ] BDD/Cucumber test implementation
- [ ] OAuth2 security integration completion
- [ ] React UI components
- [ ] API documentation (GraphQL schema documentation)
- [ ] Performance optimization (lazy loading, caching)

### ⚠️ Known Limitations
- GraphQL schema not yet defined (tests expect it but will fail)
- No database migrations (Flyway/Liquibase not configured)
- Security not fully implemented (OAuth2 config exists but not enforced)
- No caching layer implemented
- No audit logging implemented

## Troubleshooting

### Java Version Issues
**Problem**: Build fails with "Unsupported class file major version 68"
**Solution**: Ensure `gradle.properties` contains correct Java 21 path. The project is configured to use Java 21 even if Java 24 is the system default.

### Test Compilation Errors
**Problem**: Tests fail to compile with "cannot find symbol" errors
**Solution**: Run `./gradlew clean :api:compileJava` before running tests. Some IDEs may need project refresh.

### PostgreSQL Connection Issues
**Problem**: Tests fail with database connection errors
**Solution**: 
1. Ensure Docker is running (for Testcontainers)
2. For local development, ensure PostgreSQL is running on port 5432
3. Check database name matches: `people_and_organizations`

### GraphQL Test Failures
**Problem**: Integration tests fail with "No GraphQL schema found"
**Solution**: This is expected until GraphQL schema is implemented. Tests are written first following TDD.

---

**Note**: For universal development workflow, see ~/.claude/CLAUDE.md. Use the command `/start-feature {issue-number}` to begin feature development following standard workflow.