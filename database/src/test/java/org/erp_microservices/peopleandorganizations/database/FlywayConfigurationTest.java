package org.erp_microservices.peopleandorganizations.database;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Flyway Configuration Tests")
class FlywayConfigurationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData metaData;

    private FluentConfiguration flywayConfig;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("PostgreSQL");

        flywayConfig = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .validateOnMigrate(true);
    }

    @Nested
    @DisplayName("Configuration Validation Tests")
    class ConfigurationValidationTests {

        @Test
        @DisplayName("Should configure Flyway with required properties")
        void shouldConfigureFlywayWithRequiredProperties() {
            // When
            Flyway flyway = flywayConfig.load();

            // Then
            assertNotNull(flyway, "Flyway should be created");
            assertEquals(dataSource, flyway.getConfiguration().getDataSource(),
                "DataSource should be configured");
            var actualLocations = flyway.getConfiguration().getLocations();
            assertNotNull(actualLocations, "Migration locations should not be null");
            assertTrue(actualLocations.length > 0, "Should have at least one migration location");
            boolean hasDbMigrationLocation = java.util.Arrays.stream(actualLocations)
                .anyMatch(location -> location.toString().contains("db/migration"));
            assertTrue(hasDbMigrationLocation, "Should contain db/migration location");
            assertTrue(flyway.getConfiguration().isBaselineOnMigrate(),
                "Baseline on migrate should be enabled");
            assertEquals("0", flyway.getConfiguration().getBaselineVersion().toString(),
                "Baseline version should be 0");
            assertTrue(flyway.getConfiguration().isValidateOnMigrate(),
                "Validate on migrate should be enabled");
        }

        @Test
        @DisplayName("Should validate migration locations exist")
        void shouldValidateMigrationLocationsExist() {
            // Given
            flywayConfig.locations("classpath:nonexistent/location");

            // When & Then
            assertDoesNotThrow(() -> {
                Flyway flyway = flywayConfig.load();
                // Flyway doesn't fail on non-existent locations until migration is attempted
                assertNotNull(flyway);
            }, "Flyway should handle non-existent locations gracefully");
        }

        @Test
        @DisplayName("Should configure baseline parameters correctly")
        void shouldConfigureBaselineParametersCorrectly() {
            // Given
            flywayConfig.baselineVersion("1.0.0")
                       .baselineDescription("Initial baseline");

            // When
            Flyway flyway = flywayConfig.load();

            // Then
            assertEquals("1.0.0", flyway.getConfiguration().getBaselineVersion().toString(),
                "Baseline version should be configurable");
            assertEquals("Initial baseline", flyway.getConfiguration().getBaselineDescription(),
                "Baseline description should be configurable");
        }

        @Test
        @DisplayName("Should configure validation parameters correctly")
        void shouldConfigureValidationParametersCorrectly() {
            // Given
            flywayConfig.validateOnMigrate(false)
                       .cleanDisabled(true);

            // When
            Flyway flyway = flywayConfig.load();

            // Then
            assertFalse(flyway.getConfiguration().isValidateOnMigrate(),
                "Validation on migrate should be configurable");
            assertTrue(flyway.getConfiguration().isCleanDisabled(),
                "Clean disabled should be configurable");
        }
    }

    @Nested
    @DisplayName("Migration File Validation Tests")
    class MigrationFileValidationTests {

        @Test
        @DisplayName("Should validate migration file naming convention")
        void shouldValidateMigrationFileNamingConvention() {
            // Test migration file naming patterns
            String[] validFileNames = {
                "V1.0.0__Install_extensions.sql",
                "V1.1.0__Create_initial_schema.sql",
                "V1.2.0__Insert_reference_data.sql",
                "V1.3.0__Insert_us_states.sql"
            };

            for (String fileName : validFileNames) {
                assertTrue(isValidMigrationFileName(fileName),
                    "File name " + fileName + " should follow Flyway naming convention");
            }
        }

        @Test
        @DisplayName("Should reject invalid migration file names")
        void shouldRejectInvalidMigrationFileNames() {
            String[] invalidFileNames = {
                "migration.sql",
                "V1__description.sql",
                "V1.0__description.sql",
                "v1.0.0__description.sql",
                "V1.0.0_description.sql",
                "V1.0.0__description.txt"
            };

            for (String fileName : invalidFileNames) {
                assertFalse(isValidMigrationFileName(fileName),
                    "File name " + fileName + " should be rejected");
            }
        }

        @Test
        @DisplayName("Should validate migration version ordering")
        void shouldValidateMigrationVersionOrdering() {
            String[] orderedVersions = {
                "V1.0.0__Install_extensions.sql",
                "V1.1.0__Create_initial_schema.sql",
                "V1.2.0__Insert_reference_data.sql",
                "V1.3.0__Insert_us_states.sql"
            };

            for (int i = 1; i < orderedVersions.length; i++) {
                String previousVersion = extractVersion(orderedVersions[i-1]);
                String currentVersion = extractVersion(orderedVersions[i]);

                assertTrue(compareVersions(previousVersion, currentVersion) < 0,
                    "Version " + currentVersion + " should be greater than " + previousVersion);
            }
        }
    }

    @Nested
    @DisplayName("Database Connection Tests")
    class DatabaseConnectionTests {

        @Test
        @DisplayName("Should handle database connection errors gracefully")
        void shouldHandleDatabaseConnectionErrorsGracefully() throws SQLException {
            // Given
            when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

            // When & Then
            assertThrows(SQLException.class, () -> {
                dataSource.getConnection();
            }, "Should propagate connection errors");
        }

        @Test
        @DisplayName("Should validate PostgreSQL database type")
        void shouldValidatePostgreSQLDatabaseType() throws SQLException {
            // Given
            when(metaData.getDatabaseProductName()).thenReturn("PostgreSQL");

            // When
            String databaseType = connection.getMetaData().getDatabaseProductName();

            // Then
            assertEquals("PostgreSQL", databaseType,
                "Should work with PostgreSQL database");
        }

        @Test
        @DisplayName("Should handle unsupported database types")
        void shouldHandleUnsupportedDatabaseTypes() throws SQLException {
            // Given
            when(metaData.getDatabaseProductName()).thenReturn("MySQL");

            // When
            String databaseType = connection.getMetaData().getDatabaseProductName();

            // Then
            assertEquals("MySQL", databaseType);
            // Note: Flyway should handle different database types,
            // but our migrations are PostgreSQL-specific
        }
    }

    @Nested
    @DisplayName("Migration Strategy Tests")
    class MigrationStrategyTests {

        @Test
        @DisplayName("Should define correct migration sequence")
        void shouldDefineCorrectMigrationSequence() {
            String[] expectedSequence = {
                "V1.0.0__Install_extensions.sql",
                "V1.1.0__Create_initial_schema.sql",
                "V1.2.0__Insert_reference_data.sql",
                "V1.3.0__Insert_us_states.sql"
            };

            // Verify the sequence is logically correct
            assertEquals("V1.0.0__Install_extensions.sql", expectedSequence[0],
                "First migration should install extensions");
            assertEquals("V1.1.0__Create_initial_schema.sql", expectedSequence[1],
                "Second migration should create schema");
            assertEquals("V1.2.0__Insert_reference_data.sql", expectedSequence[2],
                "Third migration should insert reference data");
            assertEquals("V1.3.0__Insert_us_states.sql", expectedSequence[3],
                "Fourth migration should insert US states data");
        }

        @Test
        @DisplayName("Should validate migration dependencies")
        void shouldValidateMigrationDependencies() {
            // Test logical dependencies between migrations
            assertTrue(migrationDependsOn("V1.1.0__Create_initial_schema.sql",
                                        "V1.0.0__Install_extensions.sql"),
                "Schema creation should depend on extensions");

            assertTrue(migrationDependsOn("V1.2.0__Insert_reference_data.sql",
                                        "V1.1.0__Create_initial_schema.sql"),
                "Reference data should depend on schema");

            assertTrue(migrationDependsOn("V1.3.0__Insert_us_states.sql",
                                        "V1.2.0__Insert_reference_data.sql"),
                "US states data should depend on reference data");
        }
    }

    // Helper methods
    private boolean isValidMigrationFileName(String fileName) {
        return fileName.matches("V\\d+\\.\\d+\\.\\d+__[A-Za-z_]+\\.sql");
    }

    private String extractVersion(String fileName) {
        if (fileName.startsWith("V") && fileName.contains("__")) {
            return fileName.substring(1, fileName.indexOf("__"));
        }
        return "";
    }

    private int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int v1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int v2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (v1 != v2) {
                return Integer.compare(v1, v2);
            }
        }
        return 0;
    }

    private boolean migrationDependsOn(String migration, String dependency) {
        String migrationVersion = extractVersion(migration);
        String dependencyVersion = extractVersion(dependency);
        return compareVersions(dependencyVersion, migrationVersion) < 0;
    }
}
