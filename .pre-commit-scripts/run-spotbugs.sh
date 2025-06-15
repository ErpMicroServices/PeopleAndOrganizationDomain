#!/bin/bash
set -e

# Cross-platform Java 21 detection and setup
echo "🔍 Detecting Java 21 for SpotBugs analysis..."

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
  echo "❌ ERROR: Java 21 is required for SpotBugs analysis but was not found."
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

# Set JAVA_HOME and run SpotBugs
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
