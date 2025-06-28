#!/bin/bash
# Script to start development dependencies

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "🐳 Starting development dependencies..."

# Check if user wants full stack or just essentials
if [ "$1" == "--full" ]; then
    echo "Starting full development stack..."
    docker compose -f "$PROJECT_ROOT/docker compose.yml" up -d
else
    echo "Starting essential services only (PostgreSQL)..."
    docker compose -f "$PROJECT_ROOT/docker compose.test.yml" up -d
    echo ""
    echo "💡 Tip: Use './scripts/start-dependencies.sh --full' to start all services including LocalStack, Redis, etc."
fi

echo ""
echo "⏳ Waiting for services to be ready..."

# Wait for PostgreSQL
if docker ps --format '{{.Names}}' | grep -q "people-org-postgres"; then
    container_name="people-org-postgres"
else
    container_name="people-org-postgres-test"
fi

max_attempts=30
attempt=0
while ! docker exec "$container_name" pg_isready -U postgres > /dev/null 2>&1; do
    attempt=$((attempt + 1))
    if [ $attempt -eq $max_attempts ]; then
        echo "❌ PostgreSQL failed to start after $max_attempts attempts"
        docker compose logs
        exit 1
    fi
    echo "  Waiting for PostgreSQL... (attempt $attempt/$max_attempts)"
    sleep 1
done

echo "✅ PostgreSQL is ready!"

echo ""
echo "📊 Service Status:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(NAMES|people-org-)"

echo ""
echo "🚀 Development dependencies are ready!"
echo ""
echo "Connection details:"
echo "  PostgreSQL: localhost:5432"
echo "  Database: people_and_organizations"
echo "  Username: postgres (test) or people_org_user (full)"
echo "  Password: postgres (test) or dev_password_123 (full)"

if [ "$1" == "--full" ]; then
    echo "  LocalStack: localhost:4566"
    echo "  Redis: localhost:6379"
fi

echo ""
echo "To stop all services, run: ./scripts/stop-dependencies.sh"
