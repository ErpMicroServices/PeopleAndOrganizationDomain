#!/bin/bash
set -e

# ESLint script for pre-commit hook
# Runs ESLint on JavaScript/TypeScript files in ui-components

echo "🔍 Running ESLint checks..."

# Check if we're in the ui-components directory or need to change to it
if [ -d "ui-components" ]; then
    cd ui-components
elif [ "$(basename "$PWD")" != "ui-components" ]; then
    echo "❌ ERROR: ui-components directory not found!"
    echo "This script should be run from the project root."
    exit 1
fi

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "⚠️  node_modules not found. Running npm install..."
    if ! npm install; then
        echo "❌ npm install failed!"
        exit 1
    fi
fi

# Run ESLint
echo "📋 Linting JavaScript/TypeScript files..."

if npm run lint; then
    echo "✅ ESLint checks passed!"
    exit 0
else
    echo "❌ ESLint found issues!"
    echo "Fix the linting errors before committing."
    echo "To automatically fix some issues, run: npm run lint -- --fix"
    exit 1
fi
