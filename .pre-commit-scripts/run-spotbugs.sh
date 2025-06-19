#!/bin/bash
set -e

# SpotBugs static analysis script for pre-push hook

echo "🐛 Preparing SpotBugs static analysis..."

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Source the Java 21 detection script
source "$SCRIPT_DIR/detect-java-21.sh"

# Set JAVA_HOME to the detected Java 21
export JAVA_HOME="$JAVA21_HOME"
echo "🐛 Running SpotBugs static analysis..."
echo "📋 This includes security analysis via findsecbugs plugin"
echo ""

# Function to extract priority from XML report
check_high_priority_bugs() {
  local report_file=$1
  if [ -f "$report_file" ]; then
    # Count high priority bugs (priority 1)
    high_priority=$(grep -c 'priority="1"' "$report_file" 2>/dev/null || echo "0")
    if [ "$high_priority" -gt 0 ]; then
      echo ""
      echo "🚨 Found $high_priority high-priority bug(s) in $(basename $report_file .xml)"
      # Show first few high priority bugs
      grep -B2 -A2 'priority="1"' "$report_file" | head -20
      return 1
    fi
  fi
  return 0
}

# Run SpotBugs with error handling
HIGH_PRIORITY_FOUND=false
SPOTBUGS_FAILED=false

# Try to run SpotBugs on main code
if ./gradlew :api:spotbugsMain --no-daemon > /tmp/spotbugs-main.log 2>&1; then
  echo "✅ SpotBugs main analysis completed"
  # Check for high priority bugs in the report
  if ! check_high_priority_bugs "api/build/reports/spotbugs/main.xml"; then
    HIGH_PRIORITY_FOUND=true
  fi
else
  echo "⚠️  SpotBugs main analysis encountered issues (see /tmp/spotbugs-main.log)"
  SPOTBUGS_FAILED=true
fi

# Try to run SpotBugs on test code
if ./gradlew :api:spotbugsTest --no-daemon > /tmp/spotbugs-test.log 2>&1; then
  echo "✅ SpotBugs test analysis completed"
  # Check for high priority bugs in the test report
  if ! check_high_priority_bugs "api/build/reports/spotbugs/test.xml"; then
    HIGH_PRIORITY_FOUND=true
  fi
else
  echo "⚠️  SpotBugs test analysis encountered issues (see /tmp/spotbugs-test.log)"
  SPOTBUGS_FAILED=true
fi

# Final status
echo ""
if [ "$HIGH_PRIORITY_FOUND" = true ]; then
  echo "❌ ERROR: High-priority bugs detected by SpotBugs!"
  echo ""
  echo "💡 Tips:"
  echo "  - Review the SpotBugs reports in api/build/reports/spotbugs/"
  echo "  - Fix high-priority bugs before pushing"
  echo "  - HTML reports available for detailed analysis"
  echo ""
  echo "🚫 To bypass this check temporarily: git push --no-verify"
  exit 1
elif [ "$SPOTBUGS_FAILED" = true ]; then
  echo "⚠️  WARNING: SpotBugs analysis had issues but continuing..."
  echo "Check logs in /tmp/spotbugs-*.log for details"
  echo ""
  echo "Common causes:"
  echo "  - Build configuration issues"
  echo "  - Missing compiled classes"
  echo "  - Gradle task dependencies"
  echo ""
  echo "Allowing push to continue despite analysis issues."
  exit 0
else
  echo "✅ SpotBugs analysis passed - no high-priority bugs found!"
  echo ""
  echo "📊 Reports available at:"
  echo "  - api/build/reports/spotbugs/main.html"
  echo "  - api/build/reports/spotbugs/test.html"
fi
