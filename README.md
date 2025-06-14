# People and Organizations Domain

A comprehensive microservice for managing people, organizations, and their relationships in enterprise systems.

## Overview

The People and Organizations Domain microservice provides the foundational data models and business logic for:

- **Customer Relationship Management (CRM)**
- **Human Resource Management (HRM)**
- **Vendor Relationship Management (VRM)**
- **Help Desk Systems**
- Any system requiring sophisticated party management

This service implements the **Party Model** pattern, providing a flexible and extensible foundation for representing individuals, organizations, and their complex relationships.

## Key Features

### 🏢 Party Management

- **Unified Party Model**: Abstract representation for both people and organizations
- **Flexible Role System**: Assign multiple roles (Customer, Supplier, Employee, Partner, etc.)
- **Dynamic Classifications**: Categorize parties (VIP Customer, Gold Partner, etc.)
- **External Identifications**: Track SSN, EIN, passport numbers, and other identifiers

### 📞 Contact Mechanisms

- **Multiple Contact Types**: Email addresses, phone numbers, postal addresses
- **Purpose-Based Contacts**: Billing address, shipping address, work phone, etc.
- **Time-Bounded Validity**: Track when contact information is valid

### 🤝 Relationship Management

- **Typed Relationships**: Employment, ownership, partnership, family relationships
- **Bidirectional Tracking**: "John works for Acme" and "Acme employs John"
- **Historical Records**: Track relationship changes over time

### 🔍 Advanced Querying

- **GraphQL API**: Flexible, efficient data fetching
- **Complex Searches**: Find parties by role, classification, or relationship
- **Pagination Support**: Handle large datasets efficiently

## Getting Started

### Prerequisites

- Java 21 or higher
- PostgreSQL 15+
- Docker (for development with Testcontainers)
- Node.js 18+ (for UI components)

### Quick Start

1. **Clone the repository**

   ```bash
   git clone https://github.com/ErpMicroServices/PeopleAndOrganizationDomain.git
   cd PeopleAndOrganizationDomain
   ```

2. **Set up the database**

   ```bash
   createdb people_and_organizations
   ```

3. **Configure environment** (see Configuration section below)

4. **Build and run**

   ```bash
   ./gradlew build
   ./gradlew :api:bootRun
   ```

5. **Access the API**
   - GraphQL endpoint: http://localhost:8080/graphql
   - GraphiQL UI: http://localhost:8080/graphiql (development only)

## Configuration

### Required Environment Variables

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/people_and_organizations
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password

# Security (OAuth2/AWS Cognito) - Optional
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_CLIENT_ID=your_client_id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_CLIENT_SECRET=your_secret
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_COGNITO_ISSUER_URI=https://cognito-idp.region.amazonaws.com/poolId
```

## API Examples

### Create a Person

```graphql
mutation CreatePerson {
  createPerson(input: {
    firstName: "John"
    lastName: "Doe"
    birthDate: "1990-01-15"
    genderType: MALE
  }) {
    id
    firstName
    lastName
  }
}
```

### Find Organizations by Name

```graphql
query FindOrganizations {
  organizations(name: "Acme", page: 0, size: 10) {
    content {
      id
      name
      taxIdNumber
    }
    totalElements
  }
}
```

### Add Contact Information

```graphql
mutation AddEmail {
  addEmailToParty(input: {
    partyId: "123e4567-e89b-12d3-a456-426614174000"
    emailAddress: "john.doe@example.com"
    purposes: ["PRIMARY_EMAIL", "WORK_EMAIL"]
  }) {
    id
    contactMechanism {
      ... on EmailAddress {
        emailAddress
      }
    }
  }
}
```

## Project Structure

```
PeopleAndOrganizationDomain/
├── api/                    # Spring Boot GraphQL API
├── database/              # Database migrations (Flyway)
├── ui-components/         # React component library
├── features/              # BDD feature specifications
└── CLAUDE.md             # Development workflow documentation
```

## Development

### Pre-commit Hooks

This project uses pre-commit hooks to ensure code quality before commits reach the repository.

#### Setup

```bash
# Install pre-commit (if not already installed)
pip install pre-commit

# Install the git hooks
pre-commit install
pre-commit install --hook-type commit-msg
pre-commit install --hook-type pre-push
```

#### What Gets Checked

**On every commit:**

- Trailing whitespace removal
- End-of-file fixes
- YAML/JSON/XML syntax validation
- Large file prevention (>1MB)
- Merge conflict markers
- Secret detection
- Java compilation
- ESLint for JavaScript/React code
- Markdown formatting

**Before push:**

- Unit tests
- Code quality checks (Checkstyle, PMD)

#### Running Hooks Manually

```bash
# Run all hooks on all files
pre-commit run --all-files

# Run specific hook
pre-commit run gradle-compile --all-files
```

### Building from Source

```bash
# Full build with tests
./gradlew build

# Build without tests
./gradlew build -x test

# Run specific module
./gradlew :api:build
```

### Running Tests

```bash
# All tests
./gradlew test

# API tests only
./gradlew :api:test

# With test coverage report
./gradlew test jacocoTestReport
```

### Database Migrations

The project uses Flyway for database version control. Migrations are automatically applied on application startup.

To run migrations manually:

```bash
./gradlew :database:flywayMigrate
```

## Contributing

We follow a strict Test-Driven Development (TDD) approach:

1. **Write tests first** - All features begin with failing tests
2. **Implement to pass** - Write minimal code to make tests pass
3. **Refactor** - Improve code while keeping tests green
4. **Document** - Update documentation for significant changes

For detailed development workflow and standards, see [CLAUDE.md](CLAUDE.md).

### Reporting Issues

Please report issues at: https://github.com/ErpMicroServices/PeopleAndOrganizationDomain/issues

### Pull Requests

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Write tests for your changes
4. Implement your changes
5. Ensure all tests pass (`./gradlew test`)
6. Commit your changes
7. Push to your fork
8. Open a Pull Request

## License

[License information to be added]

## Support

For questions and support:

- GitHub Issues: https://github.com/ErpMicroServices/PeopleAndOrganizationDomain/issues
- Documentation: See [CLAUDE.md](CLAUDE.md) for technical details

## Roadmap

- [ ] Complete GraphQL schema implementation
- [ ] Add Flyway database migrations
- [ ] Implement OAuth2 security
- [ ] Create React UI component library
- [ ] Add caching layer
- [ ] Implement audit logging
- [ ] Add event sourcing for party changes

---

For technical implementation details, build commands, and troubleshooting, see [CLAUDE.md](CLAUDE.md).
