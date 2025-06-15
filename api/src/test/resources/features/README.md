# BDD Testing with Cucumber

This directory contains Behavior-Driven Development (BDD) tests using Cucumber and Gherkin syntax for the People and Organizations Domain microservice.

## Overview

The BDD tests provide comprehensive coverage of all domain operations, ensuring that business requirements are met through executable specifications written in natural language.

## Feature Files

### Core Domain Features
- **party-management.feature** - Creating, updating, and managing people and organizations
- **contact-mechanisms.feature** - Email addresses, postal addresses, and phone numbers
- **party-relationships.feature** - Employment, partnerships, and organizational relationships
- **party-roles.feature** - Customer, supplier, employee, and other role assignments

### API and Infrastructure Features
- **api-operations.feature** - GraphQL queries, mutations, and subscriptions
- **security.feature** - Authentication, authorization, and security controls

## Running BDD Tests

### Command Line
```bash
# Run all BDD tests
./gradlew bddTest

# Run BDD tests with detailed output
./gradlew bddTest --info

# Run specific feature file
./gradlew bddTest -Dcucumber.features=src/test/resources/features/party-management.feature
```

### Test Reports
BDD test reports are generated in:
- HTML Report: `build/reports/cucumber/index.html`
- JSON Report: `build/reports/cucumber/cucumber.json`

## Writing New BDD Tests

### Gherkin Syntax Guidelines

1. **Feature**: Describes the feature being tested
2. **Background**: Common setup steps for all scenarios
3. **Scenario**: Individual test case
4. **Given**: Initial context/preconditions
5. **When**: Actions performed
6. **Then**: Expected outcomes

### Example Structure
```gherkin
Feature: Feature Name
  As a [role]
  I want to [goal]
  So that [benefit]

  Background:
    Given common setup steps

  Scenario: Descriptive scenario name
    Given initial context
    When action is performed
    Then expected outcome occurs
    And additional verification
```

### Step Definition Guidelines

1. **Location**: Place step definitions in `src/test/java/.../bdd/stepdefinitions/`
2. **Naming**: Use descriptive class names ending with "Steps"
3. **Annotations**: Use `@Given`, `@When`, `@Then` from cucumber-java
4. **Data Tables**: Use `DataTable` parameter for complex data
5. **State Management**: Store test state in instance variables

### Best Practices

1. **Reusable Steps**: Create common steps in `CommonSteps.java`
2. **Clear Language**: Write scenarios in business language, not technical terms
3. **Single Responsibility**: Each scenario should test one specific behavior
4. **Data Independence**: Scenarios should not depend on data from other scenarios
5. **Meaningful Names**: Use descriptive scenario and step names

## Test Data Management

### Database Setup
- Tests use Testcontainers for isolated PostgreSQL instances
- Each test scenario runs with a clean database state
- Test data is created through step definitions, not external fixtures

### Authentication
- Test authentication is set up through `CommonSteps`
- Different user roles (admin, regular user) are available
- OAuth2 tokens are mocked for testing

## Integration with CI/CD

BDD tests are automatically executed in the CI/CD pipeline:
- **Pre-commit**: BDD tests run during pre-commit hooks
- **Pull Requests**: Full BDD test suite runs on PR creation
- **Main Branch**: Complete test suite including BDD runs on main branch pushes

## Coverage Requirements

- **Minimum 80% BDD coverage** as mandated by project standards
- All critical business scenarios must be covered
- All GraphQL operations must have BDD scenarios
- Security scenarios for all access patterns

## Troubleshooting

### Common Issues

1. **Step Definition Not Found**: Ensure step definition methods match Gherkin text exactly
2. **Database Connection**: Verify Docker is running for Testcontainers
3. **Authentication Failures**: Check test user setup in step definitions
4. **Compilation Errors**: Ensure all required dependencies are available

### Debug Mode
```bash
# Run with debug output
./gradlew bddTest --debug

# Run single scenario
./gradlew bddTest -Dcucumber.options="--name 'Create a new person'"
```

## Future Enhancements

As the domain models and services are implemented, the following enhancements are planned:

1. **Complete Step Definitions**: Implement all TODOs in step definition classes
2. **GraphQL Integration**: Add actual GraphQL query execution in API scenarios
3. **Advanced Scenarios**: Add complex business workflow scenarios
4. **Performance Tests**: Add BDD scenarios for performance requirements
5. **Error Scenarios**: Expand error handling and edge case coverage

## Maintenance

- **Regular Updates**: Keep feature files updated with new business requirements
- **Refactoring**: Regularly refactor step definitions to avoid duplication
- **Documentation**: Update this README when adding new features or patterns
- **Review**: Include BDD tests in code review process
