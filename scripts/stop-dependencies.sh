#!/bin/bash
# Script to stop development dependencies

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "🛑 Stopping development dependencies..."

# Check which compose files have running containers
if docker ps --format '{{.Names}}' | grep -q "people-org-postgres-test"; then
    echo "Stopping test dependencies..."
    docker compose -f "$PROJECT_ROOT/docker compose.test.yml" down
fi

if docker ps --format '{{.Names}}' | grep -q "people-org-"; then
    echo "Stopping full development stack..."
    docker compose -f "$PROJECT_ROOT/docker compose.yml" down
fi

echo "✅ All dependencies stopped!"
