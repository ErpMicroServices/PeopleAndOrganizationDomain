#!/bin/bash
set -e

# Test coverage validation script for pre-push hook
# Ensures code coverage meets minimum requirements

echo "📊 Preparing test coverage validation..."

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Source the Java 21 detection script
source "$SCRIPT_DIR/detect-java-21.sh"

# Set JAVA_HOME to the detected Java 21
export JAVA_HOME="$JAVA21_HOME"
echo "📊 Validating test coverage..."
echo "📋 Minimum required coverage: 80%"
echo ""

# Function to extract coverage percentage from XML report
extract_coverage() {
  local xml_file=$1
  if [ -f "$xml_file" ]; then
    # Extract instruction coverage percentage
    coverage=$(grep -o 'type="INSTRUCTION"[^>]*' "$xml_file" | head -1 | grep -o 'covered="[0-9]*"' | cut -d'"' -f2)
    missed=$(grep -o 'type="INSTRUCTION"[^>]*' "$xml_file" | head -1 | grep -o 'missed="[0-9]*"' | cut -d'"' -f2)

    if [ -n "$coverage" ] && [ -n "$missed" ]; then
      total=$((coverage + missed))
      if [ $total -gt 0 ]; then
        percentage=$(awk "BEGIN {printf \"%.1f\", ($coverage / $total) * 100}")
        echo "$percentage"
        return 0
      fi
    fi
  fi
  echo "0.0"
  return 1
}

# Run tests with coverage
echo "🧪 Running tests with coverage measurement..."
if ./gradlew :api:test :api:jacocoTestReport --no-daemon > /tmp/coverage-test.log 2>&1; then
  echo "✅ Tests completed successfully"
else
  echo "❌ ERROR: Tests failed!"
  echo ""
  echo "💡 Tips:"
  echo "  - Check test output in /tmp/coverage-test.log"
  echo "  - Fix failing tests before pushing"
  echo "  - Run './gradlew :api:test' locally to debug"
  echo ""
  echo "🚫 To bypass this check temporarily: git push --no-verify"
  exit 1
fi

# Extract and display coverage
XML_REPORT="api/build/reports/jacoco/test/jacocoTestReport.xml"
if [ -f "$XML_REPORT" ]; then
  coverage_percent=$(extract_coverage "$XML_REPORT")
  echo ""
  echo "📈 Current test coverage: ${coverage_percent}%"
  echo ""
else
  echo "⚠️  WARNING: Coverage report not found at $XML_REPORT"
  coverage_percent="0.0"
fi

# Run coverage verification
echo "🔍 Verifying coverage meets minimum threshold..."
if ./gradlew :api:jacocoTestCoverageVerification --no-daemon > /tmp/coverage-verify.log 2>&1; then
  echo "✅ Coverage validation PASSED - meets 80% threshold!"
  echo ""
  echo "📊 Coverage reports available at:"
  echo "  - HTML: api/build/reports/jacoco/test/html/index.html"
  echo "  - XML: api/build/reports/jacoco/test/jacocoTestReport.xml"
  echo ""
  echo "💡 View detailed coverage report:"
  echo "   open api/build/reports/jacoco/test/html/index.html"
else
  echo "❌ ERROR: Coverage validation FAILED - below 80% threshold!"
  echo ""
  echo "📊 Current coverage: ${coverage_percent}%"
  echo "📋 Required coverage: 80.0%"
  echo ""
  echo "💡 Tips to improve coverage:"
  echo "  - Write unit tests for uncovered code"
  echo "  - Focus on business logic and service layers"
  echo "  - View coverage report: open api/build/reports/jacoco/test/html/index.html"
  echo "  - Excluded from coverage: DTOs, configs, generated code"
  echo ""
  echo "📁 Detailed verification log: /tmp/coverage-verify.log"
  echo ""
  echo "🚫 To bypass this check temporarily: git push --no-verify"
  exit 1
fi
