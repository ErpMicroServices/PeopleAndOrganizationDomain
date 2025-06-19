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
- Docker and Docker Compose (recommended for development)
- Node.js 18+ (for UI components)

### Quick Start

Choose your preferred development approach:

#### 🐳 Docker Development (Recommended)

1. **Clone the repository**

   ```bash
   git clone https://github.com/ErpMicroServices/PeopleAndOrganizationDomain.git
   cd PeopleAndOrganizationDomain
   ```

2. **Start the complete development environment**

   ```bash
   docker compose up
   ```

   This will start:
   - PostgreSQL database with initialization
   - Redis for caching
   - LocalStack for AWS services simulation
   - Spring Boot API application

3. **Access the services**
   - GraphQL endpoint: http://localhost:8080/graphql
   - GraphiQL UI: http://localhost:8080/graphiql
   - PostgreSQL: localhost:5432
   - LocalStack: http://localhost:4566

#### 🛠️ Traditional Development

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

## 🐳 Docker Development Environment

This project provides a complete Docker-based development environment with all dependencies included.

### Docker Setup

The Docker environment includes:

- **Spring Boot API**: The main application
- **PostgreSQL 15**: Database with automatic initialization
- **Redis**: Caching layer for future use
- **LocalStack**: AWS services simulation (Cognito, S3, Secrets Manager)
- **Nginx**: Reverse proxy (production only)

### Development Commands

```bash
# Start all services
docker compose up

# Start services in background
docker compose up -d

# View logs
docker compose logs -f

# Stop all services
docker compose down

# Rebuild and start (after code changes)
docker compose up --build

# Start specific service
docker compose up postgres redis
```

### Production Deployment

```bash
# Set environment variables (see .env.example)
cp .env.example .env
# Edit .env with your production values

# Start production environment
docker compose -f docker-compose.prod.yml up -d
```

### Docker Services

#### API Service

- **Port**: 8080
- **Health Check**: http://localhost:8080/actuator/health
- **GraphQL**: http://localhost:8080/graphql
- **Multi-stage build** with optimized layers

#### PostgreSQL Database

- **Port**: 5432
- **Database**: people_and_organizations
- **User**: people_org_user
- **Password**: dev_password_123 (development)
- **Initialization**: Automatic schema setup

#### Redis Cache

- **Port**: 6379
- **Purpose**: Caching and session storage

#### LocalStack (Development Only)

- **Port**: 4566
- **Services**: Cognito, S3, Secrets Manager, IAM
- **Dashboard**: http://localhost:4566
- **Automatic AWS services setup**

### Health Monitoring

Run the comprehensive health check:

```bash
# Check all services
./docker/health-check.sh

# Check specific services (when containers are running)
docker compose exec api curl http://localhost:8080/actuator/health
docker compose exec postgres pg_isready -U people_org_user
```

### Development Workflow with Docker

1. **Make code changes** in your IDE
2. **Rebuild and restart** the API service:
   ```bash
   docker compose up --build api
   ```
3. **Test your changes** using GraphiQL or the health check script
4. **View logs** for debugging:
   ```bash
   docker compose logs -f api
   ```

### Troubleshooting Docker

#### Common Issues

**Port Already in Use**
```bash
# Find what's using the port
lsof -ti:8080
# Kill the process
kill -9 <PID>
```

**Database Connection Issues**
```bash
# Check PostgreSQL logs
docker compose logs postgres

# Connect to database directly
docker compose exec postgres psql -U people_org_user -d people_and_organizations
```

**Container Won't Start**
```bash
# Check container status
docker compose ps

# View detailed logs
docker compose logs <service-name>

# Rebuild from scratch
docker compose down -v
docker compose up --build
```

**Clean Restart**
```bash
# Remove all containers and volumes
docker compose down -v

# Remove images (optional)
docker rmi $(docker images -q "people-org*")

# Start fresh
docker compose up --build
```

### Docker Configuration Files

- **`Dockerfile`**: Multi-stage build for the API
- **`docker-compose.yml`**: Development environment
- **`docker-compose.prod.yml`**: Production environment
- **`.dockerignore`**: Files excluded from Docker builds
- **`docker/postgres/init.sql`**: Database initialization
- **`docker/localstack/init-aws.sh`**: AWS services setup
- **`docker/health-check.sh`**: Comprehensive health monitoring

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
- Integration tests (with Testcontainers/PostgreSQL)
- Code quality checks (Checkstyle, PMD)
- SpotBugs static analysis (includes security checks via findsecbugs)
- Test coverage validation (80% minimum threshold)

#### Prerequisites for Pre-push Hooks

- **Docker must be running** for integration tests (uses Testcontainers)
- Integration tests may take up to 5 minutes
- SpotBugs analysis focuses on high-priority bugs only
- Security vulnerabilities detected via findsecbugs plugin
- Test coverage must meet 80% minimum threshold
- Coverage excludes DTOs, configs, and generated code
- To bypass hooks in emergencies: `git push --no-verify`

#### Running Hooks Manually

```bash
# Run all hooks on all files
pre-commit run --all-files

# Run specific hook
pre-commit run gradle-compile --all-files

# Run integration tests manually
pre-commit run gradle-integration-tests-push --all-files
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

- [x] **Docker and Docker Compose setup** ✅
- [ ] Complete GraphQL schema implementation
- [ ] Add Flyway database migrations
- [ ] Implement OAuth2 security
- [ ] Create React UI component library
- [ ] Add caching layer
- [ ] Implement audit logging
- [ ] Add event sourcing for party changes

---

For technical implementation details, build commands, and troubleshooting, see [CLAUDE.md](CLAUDE.md).
