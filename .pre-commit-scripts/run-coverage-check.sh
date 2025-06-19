#!/bin/bash
set -e

# Cross-platform Java 21 detection and setup
echo "🔍 Detecting Java 21 for test coverage validation..."

# Function to check if Java version is 21
check_java_version() {
  if [ -n "$1" ] && [ -x "$1/bin/java" ]; then
    version=$("$1/bin/java" -version 2>&1 | head -n 1 | cut -d\" -f2 | cut -d. -f1)
    if [ "$version" = "21" ]; then
      return 0
    fi
  fi
  return 1
}

# Try different methods to find Java 21
JAVA21_HOME=""

# Method 1: Check if current JAVA_HOME is Java 21
if check_java_version "$JAVA_HOME"; then
  JAVA21_HOME="$JAVA_HOME"
  echo "✅ Using current JAVA_HOME: $JAVA_HOME"

# Method 2: macOS - use java_home utility
elif command -v /usr/libexec/java_home >/dev/null 2>&1; then
  if TEMP_JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null); then
    if check_java_version "$TEMP_JAVA_HOME"; then
      JAVA21_HOME="$TEMP_JAVA_HOME"
      echo "✅ Found Java 21 via macOS java_home: $JAVA21_HOME"
    fi
  fi

# Method 3: Check common Java installation locations
else
  for java_path in \
    "/usr/lib/jvm/java-21-openjdk" \
    "/usr/lib/jvm/java-21-oracle" \
    "/usr/lib/jvm/temurin-21-jdk" \
    "/opt/java/openjdk-21" \
    "/usr/java/jdk-21" \
    "/Library/Java/JavaVirtualMachines/*/Contents/Home" \
    "$HOME/.sdkman/candidates/java/21.*" \
    "$HOME/.jenv/versions/21.*"; do

    # Handle glob patterns
    for expanded_path in $java_path; do
      if check_java_version "$expanded_path"; then
        JAVA21_HOME="$expanded_path"
        echo "✅ Found Java 21 at: $JAVA21_HOME"
        break 2
      fi
    done
  done
fi

# If Java 21 not found, provide helpful error message
if [ -z "$JAVA21_HOME" ]; then
  echo "❌ ERROR: Java 21 is required for test coverage validation but was not found."
  echo ""
  echo "📋 Please install Java 21 using one of these methods:"
  echo ""
  echo "🍎 macOS:"
  echo "   brew install openjdk@21"
  echo "   # or download from: https://adoptium.net/temurin/releases/"
  echo ""
  echo "🐧 Linux (Ubuntu/Debian):"
  echo "   sudo apt update && sudo apt install openjdk-21-jdk"
  echo ""
  echo "🐧 Linux (RHEL/CentOS):"
  echo "   sudo yum install java-21-openjdk-devel"
  echo ""
  echo "🪟 Windows:"
  echo "   Download from: https://adoptium.net/temurin/releases/"
  echo "   Or use: winget install EclipseFoundation.Temurin.21.JDK"
  echo ""
  echo "🔧 Alternative: Use SDKMAN (cross-platform):"
  echo "   curl -s \"https://get.sdkman.io\" | bash"
  echo "   source ~/.sdkman/bin/sdkman-init.sh"
  echo "   sdk install java 21-tem"
  echo ""
  echo "💡 After installation, ensure Java 21 is in your PATH or set JAVA_HOME"
  echo "   Example: export JAVA_HOME=/path/to/java-21"
  echo ""
  echo "🚫 To bypass this check temporarily: git push --no-verify"
  exit 1
fi

# Set JAVA_HOME and run coverage validation
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
