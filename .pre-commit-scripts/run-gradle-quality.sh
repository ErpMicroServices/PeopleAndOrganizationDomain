#!/bin/bash
set -e

# Gradle code quality checks script for pre-push hook
# Runs Checkstyle and PMD static analysis

echo "🔍 Running Gradle code quality checks..."

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Source the Java 21 detection script
source "$SCRIPT_DIR/detect-java-21.sh"

# Set JAVA_HOME to the detected Java 21
export JAVA_HOME="$JAVA21_HOME"

echo "📋 Running static code analysis..."

# Run Gradle quality checks
# check: Runs all verification tasks including checkstyle and PMD
# --no-daemon: Don't leave a background process running
# Note: We use 'check' to run all quality tasks defined in the build
if ./gradlew check -x test -x integrationTest --no-daemon; then
    echo "✅ All code quality checks passed!"
    exit 0
else
    echo "❌ Code quality checks failed!"
    echo "Fix the style and quality issues before pushing."
    echo "To see detailed reports, check:"
    echo "  */build/reports/checkstyle/"
    echo "  */build/reports/pmd/"
    echo "  */build/reports/spotbugs/"
    exit 1
fi
