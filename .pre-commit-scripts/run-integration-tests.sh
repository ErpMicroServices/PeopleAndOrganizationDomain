#!/bin/bash
set -e

# Integration tests script for pre-push hook
# Runs Cucumber/BDD integration tests with Testcontainers

echo "🧪 Preparing integration tests..."

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Source the Java 21 detection script
source "$SCRIPT_DIR/detect-java-21.sh"

# Set JAVA_HOME to the detected Java 21
export JAVA_HOME="$JAVA21_HOME"
echo "🚀 Running integration tests with Java 21..."
echo "⏱️  This may take a few minutes as tests use Testcontainers for PostgreSQL..."
echo ""

# Check if Docker is running (required for Testcontainers)
if ! docker info >/dev/null 2>&1; then
  echo "❌ ERROR: Docker is not running!"
  echo "Integration tests require Docker for Testcontainers."
  echo ""
  echo "Please start Docker and try again."
  echo "🚫 To bypass this check temporarily: git push --no-verify"
  exit 1
fi

# Run integration tests with a timeout (if available)
if command -v timeout >/dev/null 2>&1; then
  timeout 300 ./gradlew integrationTest --no-daemon || {
  exit_code=$?
  if [ $exit_code -eq 124 ]; then
    echo ""
    echo "❌ ERROR: Integration tests timed out after 5 minutes!"
    echo "This might indicate a problem with the tests or database connection."
  else
    echo ""
    echo "❌ ERROR: Integration tests failed!"
  fi
  echo ""
  echo "💡 Tips:"
  echo "  - Check test output above for specific failures"
  echo "  - Ensure Docker is running for Testcontainers"
  echo "  - Run './gradlew integrationTest' locally to debug"
  echo ""
  echo "🚫 To bypass this check temporarily: git push --no-verify"
  exit $exit_code
  }
else
  # No timeout command available (macOS), run without timeout
  ./gradlew integrationTest --no-daemon || {
    echo ""
    echo "❌ ERROR: Integration tests failed!"
    echo ""
    echo "💡 Tips:"
    echo "  - Check test output above for specific failures"
    echo "  - Ensure Docker is running for Testcontainers"
    echo "  - Run './gradlew integrationTest' locally to debug"
    echo ""
    echo "🚫 To bypass this check temporarily: git push --no-verify"
    exit 1
  }
fi

echo ""
echo "✅ Integration tests passed!"
