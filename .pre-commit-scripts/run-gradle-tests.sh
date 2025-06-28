#!/bin/bash
set -e

# Gradle unit tests script for pre-push hook
# Runs unit tests with proper Docker environment before pushing

echo "🧪 Running Gradle unit tests..."

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"

# Source the Java 21 detection script
source "$SCRIPT_DIR/detect-java-21.sh"

# Set JAVA_HOME to the detected Java 21
export JAVA_HOME="$JAVA21_HOME"
echo "✅ Using Java 21 from: $JAVA_HOME"

# Check if Docker is running (required for database-dependent tests)
if ! docker info >/dev/null 2>&1; then
  echo "❌ ERROR: Docker is not running!"
  echo "Unit tests require Docker for database connectivity."
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

echo "🔬 Executing unit tests..."

# Run Gradle unit tests (excluding integration tests)
# Note: Some unit tests (like repository tests) require database connectivity
if ./gradlew test -x integrationTest --no-daemon; then
    echo "✅ All unit tests passed!"
    # Clean up test dependencies
    docker compose -f docker-compose.test.yml down --volumes
    exit 0
else
    echo "❌ Unit tests failed!"
    echo "Fix the failing tests before pushing."
    echo "To see detailed test results, check:"
    echo "  api/build/reports/tests/test/index.html"
    # Clean up test dependencies even on failure
    docker compose -f docker-compose.test.yml down --volumes
    exit 1
fi
