#!/bin/bash
set -e

# Gradle compilation script for pre-commit hook
# Compiles Java source and test code to catch syntax errors early

echo "🚀 Running Gradle compilation check..."

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Source the Java 21 detection script
source "$SCRIPT_DIR/detect-java-21.sh"

# Set JAVA_HOME to the detected Java 21
export JAVA_HOME="$JAVA21_HOME"

echo "📦 Compiling Java source and test code..."

# Run Gradle compilation
# --no-daemon: Don't leave a background process running
# clean: Ensure fresh compilation
if ./gradlew clean compileJava compileTestJava --no-daemon; then
    echo "✅ Compilation successful!"
    exit 0
else
    echo "❌ Compilation failed!"
    echo "Fix the compilation errors before committing."
    exit 1
fi
