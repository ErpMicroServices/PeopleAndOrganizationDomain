#!/bin/bash
set -e

# Integration tests script for pre-push hook
# Runs GraphQL integration tests with proper Docker environment

echo "🧪 Preparing integration tests..."

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"

# Source the Java 21 detection script
source "$SCRIPT_DIR/detect-java-21.sh"

# Set JAVA_HOME to the detected Java 21
export JAVA_HOME="$JAVA21_HOME"
echo "✅ Using Java 21 from: $JAVA_HOME"

# Check if Docker is running (required for database connectivity)
if ! docker info >/dev/null 2>&1; then
  echo "❌ ERROR: Docker is not running!"
  echo "Integration tests require Docker for PostgreSQL."
  echo ""
  echo "Please start Docker and try again."
  echo "🚫 To bypass this check temporarily: git push --no-verify"
  exit 1
fi

echo "🐳 Starting test dependencies..."
cd "$PROJECT_ROOT"

# Start test dependencies using Docker Compose
docker compose -f docker-compose.test.yml up -d --wait

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
until docker compose -f docker-compose.test.yml exec -T postgres pg_isready -U test_user -d test_db >/dev/null 2>&1; do
  echo "  Waiting for PostgreSQL..."
  sleep 2
done
echo "✅ PostgreSQL is ready!"

echo "🧪 Running integration tests..."
echo "⏱️  This may take a few minutes..."

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
    echo "  - Ensure Docker is running for PostgreSQL"
    echo "  - Run './scripts/run-tests.sh' locally to debug"
    echo ""
    echo "🚫 To bypass this check temporarily: git push --no-verify"
    # Clean up test dependencies even on failure
    docker compose -f docker-compose.test.yml down --volumes
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
    echo "  - Ensure Docker is running for PostgreSQL"
    echo "  - Run './scripts/run-tests.sh' locally to debug"
    echo ""
    echo "🚫 To bypass this check temporarily: git push --no-verify"
    # Clean up test dependencies even on failure
    docker compose -f docker-compose.test.yml down --volumes
    exit 1
  }
fi

echo ""
echo "✅ Integration tests passed!"

# Clean up test dependencies
echo "🧹 Cleaning up test dependencies..."
docker compose -f docker-compose.test.yml down --volumes
