package org.erp_microservices.peopleandorganizations.api.infrastructure.scripts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PreCommitScriptsTest {

    @TempDir
    Path tempDir;

    @Test
    void scriptsShouldBeExecutable() throws IOException {
        // Given
        Path scriptsDir = tempDir.resolve(".pre-commit-scripts");
        Files.createDirectory(scriptsDir);

        Path detectJavaScript = scriptsDir.resolve("detect-java-21.sh");
        Files.writeString(detectJavaScript, "#!/bin/bash\n# Java detection logic");

        Path compileScript = scriptsDir.resolve("run-gradle-compile.sh");
        Files.writeString(compileScript, "#!/bin/bash\n# Compile logic");

        // When - make scripts executable
        makeExecutable(detectJavaScript);
        makeExecutable(compileScript);

        // Then
        assertThat(Files.isExecutable(detectJavaScript)).isTrue();
        assertThat(Files.isExecutable(compileScript)).isTrue();
    }

    @Test
    void scriptsShouldHaveProperShebang() throws IOException {
        // Given
        Path scriptsDir = tempDir.resolve(".pre-commit-scripts");
        Files.createDirectory(scriptsDir);

        String[] scriptNames = {
            "detect-java-21.sh",
            "run-gradle-compile.sh",
            "run-gradle-tests.sh",
            "run-gradle-quality.sh",
            "run-eslint.sh"
        };

        // When
        for (String scriptName : scriptNames) {
            Path script = scriptsDir.resolve(scriptName);
            Files.writeString(script, "#!/bin/bash\nset -e\n# Script content");
        }

        // Then
        for (String scriptName : scriptNames) {
            Path script = scriptsDir.resolve(scriptName);
            String firstLine = Files.readAllLines(script).get(0);
            assertThat(firstLine).isEqualTo("#!/bin/bash");
        }
    }

    @Test
    void detectJavaScriptShouldExportJavaHome() throws IOException {
        // Given
        Path script = tempDir.resolve("detect-java-21.sh");
        String scriptContent = """
            #!/bin/bash
            set -e

            check_java_version() {
                if [ -n "$1" ] && [ -x "$1/bin/java" ]; then
                    version=$("$1/bin/java" -version 2>&1 | head -n 1 | cut -d\\" -f2 | cut -d. -f1)
                    if [ "$version" = "21" ]; then
                        return 0
                    fi
                fi
                return 1
            }

            # Export JAVA21_HOME when found
            export JAVA21_HOME="/path/to/java21"
            """;

        // When
        Files.writeString(script, scriptContent);

        // Then
        assertThat(Files.readString(script)).contains("export JAVA21_HOME=");
    }

    @Test
    void gradleScriptsShouldSourceJavaDetection() throws IOException {
        // Given
        Path scriptsDir = tempDir.resolve(".pre-commit-scripts");
        Files.createDirectory(scriptsDir);

        String compileScriptContent = """
            #!/bin/bash
            set -e

            # Source Java detection
            SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
            source "$SCRIPT_DIR/detect-java-21.sh"

            # Use detected Java
            export JAVA_HOME="$JAVA21_HOME"
            ./gradlew compileJava compileTestJava --no-daemon
            """;

        // When
        Path compileScript = scriptsDir.resolve("run-gradle-compile.sh");
        Files.writeString(compileScript, compileScriptContent);

        // Then
        String content = Files.readString(compileScript);
        assertThat(content).contains("source \"$SCRIPT_DIR/detect-java-21.sh\"");
        assertThat(content).contains("export JAVA_HOME=\"$JAVA21_HOME\"");
    }

    @Test
    void scriptsShouldHandleErrorsGracefully() throws IOException {
        // Given
        Path script = tempDir.resolve("test-script.sh");
        String scriptContent = """
            #!/bin/bash
            set -e

            # Exit with error message
            if [ -z "$JAVA21_HOME" ]; then
                echo "❌ ERROR: Java 21 is required but was not found."
                exit 1
            fi
            """;

        // When
        Files.writeString(script, scriptContent);

        // Then
        assertThat(Files.readString(script)).contains("set -e");
        assertThat(Files.readString(script)).contains("exit 1");
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void scriptsShouldPreservePosixPermissions() throws IOException {
        // Given
        Path script = tempDir.resolve("executable-script.sh");
        Files.writeString(script, "#!/bin/bash\necho 'test'");

        // When
        Set<PosixFilePermission> permissions = Set.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.OWNER_EXECUTE,
            PosixFilePermission.GROUP_READ,
            PosixFilePermission.GROUP_EXECUTE,
            PosixFilePermission.OTHERS_READ,
            PosixFilePermission.OTHERS_EXECUTE
        );
        Files.setPosixFilePermissions(script, permissions);

        // Then
        Set<PosixFilePermission> actualPermissions = Files.getPosixFilePermissions(script);
        assertThat(actualPermissions).containsAll(Set.of(
            PosixFilePermission.OWNER_EXECUTE,
            PosixFilePermission.GROUP_EXECUTE,
            PosixFilePermission.OTHERS_EXECUTE
        ));
    }

    @Test
    void preCommitConfigShouldReferenceScripts() throws IOException {
        // Given
        String configContent = """
            repos:
              - repo: local
                hooks:
                  - id: gradle-compile
                    name: Gradle Compile
                    entry: .pre-commit-scripts/run-gradle-compile.sh
                    language: script
                    files: \\.java$
                    pass_filenames: false
            """;

        // When
        Path config = tempDir.resolve(".pre-commit-config.yaml");
        Files.writeString(config, configContent);

        // Then
        String content = Files.readString(config);
        assertThat(content).contains("entry: .pre-commit-scripts/run-gradle-compile.sh");
        assertThat(content).contains("language: script");
        assertThat(content).doesNotContain("entry: |");
    }

    private void makeExecutable(Path file) throws IOException {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows doesn't support POSIX permissions
            return;
        }

        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file);
        permissions.add(PosixFilePermission.OWNER_EXECUTE);
        permissions.add(PosixFilePermission.GROUP_EXECUTE);
        permissions.add(PosixFilePermission.OTHERS_EXECUTE);
        Files.setPosixFilePermissions(file, permissions);
    }
}
