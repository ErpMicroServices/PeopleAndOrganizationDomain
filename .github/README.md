# GitHub Actions Workflows

This directory contains the CI/CD pipelines for the People and Organizations Domain microservice. These workflows ensure code quality, security, and reliable deployment processes.

## Overview

Our CI/CD strategy implements a comprehensive quality-first approach with the following principles:

- **Quality Gates**: All code must pass rigorous quality checks before merge
- **Security-First**: Continuous security scanning at multiple levels
- **Test-Driven**: Comprehensive testing including unit, integration, and BDD tests
- **Container-Native**: Docker-based builds and deployments
- **Multi-Environment**: Staging and production deployment pipelines

## Workflow Files

### 1. CI Pipeline (`ci.yml`)

**Purpose**: Validates pull requests and main branch pushes with comprehensive quality checks.

**Triggered by**:

- Pull requests to `main` or `develop` branches
- Direct pushes to `main` or `develop` branches

**Jobs**:

#### `java-ci` - Core Build & Test

- Sets up Java 21 with PostgreSQL 16 service
- Runs unit tests (excluding integration tests)
- Runs integration tests with @Tag("integration")
- Generates JaCoCo coverage reports
- Uploads test results and coverage to Codecov

#### `code-quality` - Static Analysis

- Runs Checkstyle for code style compliance
- Executes PMD for code quality analysis
- Performs SpotBugs analysis with FindSecBugs security rules
- Uploads quality reports as artifacts

#### `security-scan` - Security Analysis

- OWASP Dependency Check for known vulnerabilities
- Trivy filesystem scanning for security issues
- Uploads SARIF reports to GitHub Security tab
- Fails build on high-severity vulnerabilities

#### `ui-components` - Frontend Testing

- Node.js 18 setup with npm caching
- ESLint for code style
- Jest/Vitest for component testing
- Production build validation

#### `docker-build` - Container Validation

- Multi-platform Docker builds (linux/amd64, linux/arm64)
- Container image security scanning with Trivy
- Build caching for performance optimization

#### `quality-gate` - Final Validation

- Validates all previous jobs passed
- Enforces 80% minimum code coverage
- Provides comprehensive quality summary

**Quality Requirements**:

- 100% test pass rate (no failures allowed)
- 80% minimum code coverage
- Zero high-severity security vulnerabilities
- All code style checks must pass

### 2. CD Pipeline (`cd.yml`)

**Purpose**: Automated deployment to staging and production environments.

**Triggered by**:

- Pushes to `main` branch (staging deployment)
- Release tags (production deployment)
- Manual workflow dispatch

**Jobs**:

#### `pre-deployment` - Validation

- Re-runs core tests to ensure deployment readiness
- Generates version information for container tagging

#### `build-and-push` - Container Registry

- Builds optimized Docker images
- Pushes to GitHub Container Registry (ghcr.io)
- Generates Software Bill of Materials (SBOM)
- Security scans published containers

#### `deploy-staging` - Staging Environment

- Deploys to staging environment on main branch pushes
- Runs smoke tests against deployed application
- Provides deployment notifications

#### `deploy-production` - Production Environment

- Deploys to production on release tags
- Comprehensive health checks
- Blue/green deployment strategy support

#### `database-migration` - Schema Updates

- Flyway database migrations for each environment
- Backup verification and rollback capability
- Environment-specific migration validation

#### `post-deployment` - Verification

- Comprehensive deployment verification
- Performance monitoring setup
- Status page updates

### 3. Workflow Validation (`workflow-validation.yml`)

**Purpose**: Validates GitHub Actions workflow files for syntax and completeness.

**Triggered by**:

- Changes to `.github/workflows/` directory
- Changes to build configuration files

**Validation Checks**:

- YAML syntax validation
- Required job presence verification
- Security tool configuration checks
- Workflow completeness assessment

## Environment Configuration

### Required Secrets

The workflows require the following secrets to be configured in GitHub repository settings:

```bash
# Container Registry (automatically provided by GitHub)
GITHUB_TOKEN  # Automatically available, used for ghcr.io

# Optional: External deployment secrets
STAGING_DEPLOY_KEY      # SSH key for staging deployment
PRODUCTION_DEPLOY_KEY   # SSH key for production deployment
CODECOV_TOKEN          # Optional: for enhanced Codecov integration
```

### Environment Variables

Key environment variables used across workflows:

```yaml
JAVA_VERSION: '21'           # Java version for builds
NODE_VERSION: '18'           # Node.js version for UI
REGISTRY: ghcr.io           # Container registry
GRADLE_OPTS: '-Xmx2048m'    # JVM settings for Gradle
```

## Quality Tools Configuration

### Code Quality Tools

All quality tools are configured with project-specific rules:

- **Checkstyle**: `config/checkstyle/checkstyle.xml`
  - Google Java Style with project customizations
  - Line length limit: 120 characters
  - Comprehensive naming and formatting rules

- **PMD**: `config/pmd/pmd-rules.xml`
  - Best practices and code smell detection
  - Security rule set included
  - Custom complexity thresholds

- **SpotBugs**: `config/spotbugs/spotbugs-exclude.xml`
  - FindSecBugs plugin for security analysis
  - Lombok and Spring Boot exclusions
  - JPA entity-specific exclusions

- **OWASP**: `config/owasp/suppressions.xml`
  - Dependency vulnerability scanning
  - False positive suppressions for dev dependencies
  - CVSS 7.0+ threshold for build failures

### Coverage Requirements

- **Minimum Coverage**: 80% for all new code
- **Exclusions**:
  - Application main classes
  - Configuration classes
  - DTOs and simple data classes
  - Lombok-generated methods

## Container Strategy

### Multi-stage Docker Build

Our Dockerfile implements security best practices:

1. **Builder Stage**: Eclipse Temurin JDK 21 Alpine
   - Gradle build with test exclusion for faster builds
   - JAR layer extraction for optimized caching

2. **Runtime Stage**: Eclipse Temurin JRE 21 Alpine
   - Non-root user execution (appuser:appgroup)
   - Security updates and minimal package installation
   - Health checks and proper signal handling

### Container Security

- **Base Image**: Official Eclipse Temurin with security updates
- **User Permissions**: Non-root execution (UID/GID 1001)
- **Health Checks**: HTTP endpoint monitoring
- **Resource Limits**: Container-aware JVM settings
- **Security Scanning**: Trivy analysis for vulnerabilities

## Deployment Environments

### Staging Environment

- **URL**: https://staging-api.people-organizations.example.com
- **Deployment**: Automatic on main branch merge
- **Purpose**: Integration testing and feature validation
- **Database**: Staging PostgreSQL instance

### Production Environment

- **URL**: https://api.people-organizations.example.com
- **Deployment**: Manual on release tags
- **Purpose**: Live user traffic
- **Database**: Production PostgreSQL cluster

## Monitoring and Observability

### Metrics and Logging

- **Application Logs**: Structured JSON logging
- **Performance Metrics**: Micrometer with Spring Boot Actuator
- **Health Checks**: Custom health indicators
- **Distributed Tracing**: OpenTelemetry integration (planned)

### Alerting

- **Build Failures**: GitHub notifications and team alerts
- **Security Vulnerabilities**: SARIF integration with GitHub Security
- **Deployment Issues**: Slack/Teams notifications (when configured)

## Troubleshooting

### Common Issues

#### Build Failures

1. **Java Version Mismatch**

   ```bash
   # Check gradle.properties for Java 21 configuration
   org.gradle.java.home=/path/to/java-21
   ```

2. **Test Failures**

   ```bash
   # Run tests locally with same environment
   ./gradlew test integrationTest
   ```

3. **Coverage Below Threshold**

   ```bash
   # Generate coverage report
   ./gradlew jacocoTestReport
   # Check build/reports/jacoco/test/html/index.html
   ```

#### Security Scan Issues

1. **OWASP False Positives**
   - Add suppressions to `config/owasp/suppressions.xml`
   - Include justification in suppression notes

2. **Container Vulnerabilities**
   - Update base images in Dockerfile
   - Review Trivy scan results in GitHub Security tab

#### Deployment Issues

1. **Database Migration Failures**

   ```bash
   # Check Flyway migration status
   ./gradlew :database:flywayInfo
   ```

2. **Container Registry Issues**
   - Verify GITHUB_TOKEN permissions
   - Check GitHub Container Registry settings

### Getting Help

- **CI/CD Issues**: Check workflow run logs in GitHub Actions tab
- **Quality Gate Failures**: Review artifacts uploaded by failed jobs
- **Security Concerns**: Check GitHub Security tab for detailed reports
- **Container Issues**: Review Docker build logs and Trivy scan results

## Best Practices

### Pull Request Workflow

1. **Create Feature Branch**: `git checkout -b feature/your-feature`
2. **Write Tests First**: Follow TDD approach
3. **Local Quality Checks**: `./gradlew qualityGate`
4. **Create Pull Request**: All CI checks must pass
5. **Code Review**: Minimum 2 approvals required
6. **Merge**: Only after all quality gates pass

### Release Process

1. **Create Release Branch**: `git checkout -b release/v1.x.x`
2. **Update Version Numbers**: In relevant build files
3. **Create Release Tag**: `git tag v1.x.x`
4. **Push Tag**: Triggers production deployment
5. **Monitor Deployment**: Verify health checks and metrics

### Security Guidelines

- **Never commit secrets** to repository
- **Review dependency updates** for security implications
- **Monitor security scan results** regularly
- **Update base images** monthly or on security advisories
- **Follow principle of least privilege** for deployment credentials

## Continuous Improvement

This CI/CD pipeline is continuously evolving. Recent improvements include:

- **Performance**: Gradle caching reduces build times by ~50%
- **Security**: Multi-layer vulnerability scanning
- **Reliability**: Quality gates prevent broken deployments
- **Observability**: Comprehensive metrics and logging

Future enhancements planned:

- **BDD Integration**: Cucumber test automation
- **Performance Testing**: Load testing in CI pipeline
- **Multi-region Deployment**: Geographic redundancy
- **Chaos Engineering**: Resilience testing automation
