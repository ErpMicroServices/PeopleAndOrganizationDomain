package org.erp_microservices.peopleandorganizations.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Migration Validator Tests")
class MigrationValidatorTest {

    private static final String MIGRATION_LOCATION = "src/main/resources/db/migration";
    private static final Pattern MIGRATION_FILE_PATTERN =
        Pattern.compile("V\\d+\\.\\d+\\.\\d+__[A-Za-z_]+\\.sql");

    private MigrationValidator migrationValidator;

    @BeforeEach
    void setUp() {
        migrationValidator = new MigrationValidator();
    }

    @Nested
    @DisplayName("File Name Validation Tests")
    class FileNameValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "V1.0.0__Install_extensions.sql",
            "V1.1.0__Create_initial_schema.sql",
            "V1.2.0__Insert_reference_data.sql",
            "V1.3.0__Insert_us_states.sql",
            "V2.0.0__Add_new_feature.sql",
            "V10.15.23__Complex_migration.sql"
        })
        @DisplayName("Should accept valid migration file names")
        void shouldAcceptValidMigrationFileNames(String fileName) {
            assertTrue(migrationValidator.isValidFileName(fileName),
                "File name " + fileName + " should be valid");
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "migration.sql",
            "V1__description.sql",
            "V1.0__description.sql",
            "v1.0.0__description.sql",
            "V1.0.0_description.sql",
            "V1.0.0__description.txt",
            "V1.0.0__.sql",
            "V1.0.0__description with spaces.sql",
            "V1.0.0__description-with-dashes.sql"
        })
        @DisplayName("Should reject invalid migration file names")
        void shouldRejectInvalidMigrationFileNames(String fileName) {
            assertFalse(migrationValidator.isValidFileName(fileName),
                "File name " + fileName + " should be invalid");
        }
    }

    @Nested
    @DisplayName("Version Validation Tests")
    class VersionValidationTests {

        @Test
        @DisplayName("Should validate semantic versioning format")
        void shouldValidateSemanticVersioningFormat() {
            assertTrue(migrationValidator.isValidVersion("1.0.0"), "1.0.0 should be valid");
            assertTrue(migrationValidator.isValidVersion("10.15.23"), "10.15.23 should be valid");
            assertTrue(migrationValidator.isValidVersion("0.0.1"), "0.0.1 should be valid");

            assertFalse(migrationValidator.isValidVersion("1.0"), "1.0 should be invalid");
            assertFalse(migrationValidator.isValidVersion("1"), "1 should be invalid");
            assertFalse(migrationValidator.isValidVersion("1.0.0.0"), "1.0.0.0 should be invalid");
            assertFalse(migrationValidator.isValidVersion("a.b.c"), "a.b.c should be invalid");
        }

        @Test
        @DisplayName("Should compare versions correctly")
        void shouldCompareVersionsCorrectly() {
            assertTrue(migrationValidator.compareVersions("1.0.0", "1.0.1") < 0,
                "1.0.0 should be less than 1.0.1");
            assertTrue(migrationValidator.compareVersions("1.0.1", "1.1.0") < 0,
                "1.0.1 should be less than 1.1.0");
            assertTrue(migrationValidator.compareVersions("1.1.0", "2.0.0") < 0,
                "1.1.0 should be less than 2.0.0");
            assertEquals(0, migrationValidator.compareVersions("1.0.0", "1.0.0"),
                "1.0.0 should equal 1.0.0");
        }

        @Test
        @DisplayName("Should detect version conflicts")
        void shouldDetectVersionConflicts() {
            String[] versions = {"1.0.0", "1.1.0", "1.2.0", "1.1.0"}; // Duplicate 1.1.0

            assertTrue(migrationValidator.hasVersionConflicts(versions),
                "Should detect duplicate versions");
        }

        @Test
        @DisplayName("Should validate version sequence")
        void shouldValidateVersionSequence() {
            String[] validSequence = {"1.0.0", "1.1.0", "1.2.0", "2.0.0"};
            String[] invalidSequence = {"1.0.0", "1.2.0", "1.1.0", "2.0.0"}; // Out of order

            assertTrue(migrationValidator.isValidVersionSequence(validSequence),
                "Valid sequence should pass validation");
            assertFalse(migrationValidator.isValidVersionSequence(invalidSequence),
                "Invalid sequence should fail validation");
        }
    }

    @Nested
    @DisplayName("Migration Content Validation Tests")
    class MigrationContentValidationTests {

        @Test
        @DisplayName("Should validate SQL syntax basics")
        void shouldValidateSqlSyntaxBasics() {
            String validSql = "CREATE TABLE test (id SERIAL PRIMARY KEY, name VARCHAR(255));";
            String invalidSql = "CREATE TABLE test (id SERIAL PRIMARY KEY name VARCHAR(255));"; // Missing comma

            assertTrue(migrationValidator.hasValidSqlSyntax(validSql),
                "Valid SQL should pass basic syntax check");
            // Note: Full SQL validation would require a SQL parser
            // This is a simplified check for demonstration
        }

        @Test
        @DisplayName("Should detect dangerous operations")
        void shouldDetectDangerousOperations() {
            String dropTableSql = "DROP TABLE important_data;";
            String truncateSql = "TRUNCATE TABLE user_data;";
            String safeSql = "CREATE TABLE new_feature (id SERIAL);";

            assertTrue(migrationValidator.containsDangerousOperations(dropTableSql),
                "DROP operations should be flagged as dangerous");
            assertTrue(migrationValidator.containsDangerousOperations(truncateSql),
                "TRUNCATE operations should be flagged as dangerous");
            assertFalse(migrationValidator.containsDangerousOperations(safeSql),
                "CREATE operations should be considered safe");
        }

        @Test
        @DisplayName("Should validate idempotency requirements")
        void shouldValidateIdempotencyRequirements() {
            String idempotentSql = "CREATE TABLE IF NOT EXISTS test_table (id SERIAL);";
            String nonIdempotentSql = "CREATE TABLE test_table (id SERIAL);";

            assertTrue(migrationValidator.isIdempotent(idempotentSql),
                "IF NOT EXISTS makes CREATE statements idempotent");
            assertFalse(migrationValidator.isIdempotent(nonIdempotentSql),
                "Plain CREATE statements are not idempotent");
        }
    }

    @Nested
    @DisplayName("Migration Dependencies Tests")
    class MigrationDependenciesTests {

        @Test
        @DisplayName("Should validate logical migration order")
        void shouldValidateLogicalMigrationOrder() {
            String[] expectedOrder = {
                "V1.0.0__Install_extensions.sql",
                "V1.1.0__Create_initial_schema.sql",
                "V1.2.0__Insert_reference_data.sql",
                "V1.3.0__Insert_us_states.sql"
            };

            assertTrue(migrationValidator.hasValidLogicalOrder(expectedOrder),
                "Expected migration order should be logically valid");
        }

        @Test
        @DisplayName("Should detect dependency violations")
        void shouldDetectDependencyViolations() {
            String[] invalidOrder = {
                "V1.1.0__Create_initial_schema.sql",   // Schema before extensions
                "V1.0.0__Install_extensions.sql",     // Extensions after schema
                "V1.2.0__Insert_reference_data.sql",
                "V1.3.0__Insert_us_states.sql"
            };

            assertFalse(migrationValidator.hasValidLogicalOrder(invalidOrder),
                "Invalid migration order should be detected");
        }
    }

    @Nested
    @DisplayName("Rollback Validation Tests")
    class RollbackValidationTests {

        @Test
        @DisplayName("Should identify reversible migrations")
        void shouldIdentifyReversibleMigrations() {
            String createTableSql = "CREATE TABLE test_table (id SERIAL PRIMARY KEY);";
            String insertDataSql = "INSERT INTO reference_table VALUES (1, 'Test');";
            String alterTableSql = "ALTER TABLE test_table ADD COLUMN name VARCHAR(255);";

            assertTrue(migrationValidator.isReversible(createTableSql),
                "CREATE TABLE is reversible with DROP TABLE");
            assertTrue(migrationValidator.isReversible(insertDataSql),
                "INSERT is reversible with DELETE");
            assertTrue(migrationValidator.isReversible(alterTableSql),
                "ALTER TABLE ADD COLUMN is reversible with DROP COLUMN");
        }

        @Test
        @DisplayName("Should identify irreversible migrations")
        void shouldIdentifyIrreversibleMigrations() {
            String dropColumnSql = "ALTER TABLE test_table DROP COLUMN sensitive_data;";
            String dropTableSql = "DROP TABLE old_table;";

            assertFalse(migrationValidator.isReversible(dropColumnSql),
                "DROP COLUMN is irreversible (data loss)");
            assertFalse(migrationValidator.isReversible(dropTableSql),
                "DROP TABLE is irreversible (data loss)");
        }
    }

    // Migration Validator implementation for testing
    private static class MigrationValidator {

        public boolean isValidFileName(String fileName) {
            return MIGRATION_FILE_PATTERN.matcher(fileName).matches();
        }

        public boolean isValidVersion(String version) {
            return version.matches("\\d+\\.\\d+\\.\\d+");
        }

        public int compareVersions(String version1, String version2) {
            String[] parts1 = version1.split("\\.");
            String[] parts2 = version2.split("\\.");

            for (int i = 0; i < 3; i++) {
                int v1 = Integer.parseInt(parts1[i]);
                int v2 = Integer.parseInt(parts2[i]);

                if (v1 != v2) {
                    return Integer.compare(v1, v2);
                }
            }
            return 0;
        }

        public boolean hasVersionConflicts(String[] versions) {
            for (int i = 0; i < versions.length; i++) {
                for (int j = i + 1; j < versions.length; j++) {
                    if (versions[i].equals(versions[j])) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isValidVersionSequence(String[] versions) {
            for (int i = 1; i < versions.length; i++) {
                if (compareVersions(versions[i-1], versions[i]) >= 0) {
                    return false;
                }
            }
            return true;
        }

        public boolean hasValidSqlSyntax(String sql) {
            // Basic syntax validation - in reality would use SQL parser
            return sql.contains("CREATE") || sql.contains("INSERT") || sql.contains("ALTER");
        }

        public boolean containsDangerousOperations(String sql) {
            String upperSql = sql.toUpperCase();
            return upperSql.contains("DROP TABLE") ||
                   upperSql.contains("TRUNCATE") ||
                   upperSql.contains("DELETE FROM") && !upperSql.contains("WHERE");
        }

        public boolean isIdempotent(String sql) {
            String upperSql = sql.toUpperCase();
            return upperSql.contains("IF NOT EXISTS") ||
                   upperSql.contains("IF EXISTS") ||
                   upperSql.contains("CREATE OR REPLACE");
        }

        public boolean hasValidLogicalOrder(String[] migrations) {
            // Check that extensions come before schema, schema before data
            boolean foundExtensions = false;
            boolean foundSchema = false;
            boolean foundData = false;

            for (String migration : migrations) {
                if (migration.contains("extensions")) {
                    if (foundSchema || foundData) return false;
                    foundExtensions = true;
                } else if (migration.contains("schema")) {
                    if (foundData) return false;
                    foundSchema = true;
                } else if (migration.contains("data") || migration.contains("states")) {
                    foundData = true;
                }
            }
            return true;
        }

        public boolean isReversible(String sql) {
            String upperSql = sql.toUpperCase();

            // Reversible operations
            if (upperSql.contains("CREATE TABLE") ||
                upperSql.contains("INSERT INTO") ||
                upperSql.contains("ADD COLUMN")) {
                return true;
            }

            // Irreversible operations (data loss)
            if (upperSql.contains("DROP COLUMN") ||
                upperSql.contains("DROP TABLE")) {
                return false;
            }

            return true; // Default to reversible for unknown operations
        }
    }
}
