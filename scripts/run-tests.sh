#!/bin/bash
# Script to run tests with required dependencies

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "🐳 Starting test dependencies..."
docker compose -f "$PROJECT_ROOT/docker-compose.test.yml" up -d

echo "⏳ Waiting for PostgreSQL to be ready..."
max_attempts=30
attempt=0
while ! docker exec people-org-postgres-test pg_isready -U postgres > /dev/null 2>&1; do
    attempt=$((attempt + 1))
    if [ $attempt -eq $max_attempts ]; then
        echo "❌ PostgreSQL failed to start after $max_attempts attempts"
        docker compose -f "$PROJECT_ROOT/docker-compose.test.yml" logs
        docker compose -f "$PROJECT_ROOT/docker-compose.test.yml" down
        exit 1
    fi
    echo "  Waiting for PostgreSQL... (attempt $attempt/$max_attempts)"
    sleep 1
done

echo "✅ PostgreSQL is ready!"

# Source Java detection script if it exists
if [ -f "$SCRIPT_DIR/detect-java.sh" ]; then
    source "$SCRIPT_DIR/detect-java.sh"
fi

echo "🧪 Running all tests..."
cd "$PROJECT_ROOT"
./gradlew clean test integrationTest

TEST_EXIT_CODE=$?

echo "🧹 Cleaning up test dependencies..."
docker compose -f "$PROJECT_ROOT/docker-compose.test.yml" down

if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "✅ All tests passed!"
else
    echo "❌ Tests failed with exit code $TEST_EXIT_CODE"
fi

exit $TEST_EXIT_CODE
