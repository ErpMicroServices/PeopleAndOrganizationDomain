Feature: Pre-commit hooks use external script files
  As a developer
  I want pre-commit hooks to use external script files
  So that they are maintainable, testable, and reliable

  Background:
    Given the .pre-commit-scripts directory exists
    And all scripts in the directory have executable permissions

  Scenario: Java 21 detection is shared across scripts
    Given a shared detect-java-21.sh script exists
    When any gradle-related pre-commit hook runs
    Then it sources the detect-java-21.sh script for Java detection
    And Java 21 detection logic is not duplicated

  Scenario: Gradle compile hook uses external script
    Given the run-gradle-compile.sh script exists
    When the gradle-compile pre-commit hook is triggered
    Then it executes .pre-commit-scripts/run-gradle-compile.sh
    And the script compiles Java code successfully

  Scenario: Gradle test hook uses external script
    Given the run-gradle-tests.sh script exists
    When the gradle-test-push pre-commit hook is triggered
    Then it executes .pre-commit-scripts/run-gradle-tests.sh
    And the script runs unit tests successfully

  Scenario: Gradle quality hook uses external script
    Given the run-gradle-quality.sh script exists
    When the gradle-quality-push pre-commit hook is triggered
    Then it executes .pre-commit-scripts/run-gradle-quality.sh
    And the script runs quality checks successfully

  Scenario: ESLint hook uses external script
    Given the run-eslint.sh script exists
    When the eslint pre-commit hook is triggered
    Then it executes .pre-commit-scripts/run-eslint.sh
    And the script runs ESLint checks in ui-components directory

  Scenario: All scripts can run independently
    Given all pre-commit scripts exist in .pre-commit-scripts directory
    When I run each script directly from command line
    Then each script executes without errors
    And provides appropriate error messages when preconditions are not met

  Scenario: Pre-commit configuration references external scripts
    Given all inline scripts have been extracted to files
    When I examine .pre-commit-config.yaml
    Then each hook entry references a script file
    And no hook contains inline multi-line bash code
    And language is set to "script" for local hooks

  Scenario: Scripts handle missing Java 21 gracefully
    Given Java 21 is not available in the environment
    When a gradle-related script is executed
    Then it displays helpful installation instructions
    And exits with error code 1
    And suggests bypass option for emergencies
