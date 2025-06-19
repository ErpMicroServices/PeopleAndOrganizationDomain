#!/bin/bash
set -e

# Gradle unit tests script for pre-push hook
# Runs unit tests (excluding integration tests) before pushing

echo "🧪 Running Gradle unit tests..."

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Source the Java 21 detection script
source "$SCRIPT_DIR/detect-java-21.sh"

# Set JAVA_HOME to the detected Java 21
export JAVA_HOME="$JAVA21_HOME"

echo "🔬 Executing unit tests..."

# Run Gradle unit tests
# test: Run unit tests
# -x integrationTest: Exclude integration tests (they're run separately)
# --no-daemon: Don't leave a background process running
if ./gradlew test -x integrationTest --no-daemon; then
    echo "✅ All unit tests passed!"
    exit 0
else
    echo "❌ Unit tests failed!"
    echo "Fix the failing tests before pushing."
    echo "To see detailed test results, check:"
    echo "  api/build/reports/tests/test/index.html"
    exit 1
fi
