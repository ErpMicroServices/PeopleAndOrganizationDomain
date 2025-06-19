#!/bin/bash
set -e

# Cross-platform Java 21 detection and setup
# This script is sourced by other scripts to provide consistent Java 21 detection
# Sets JAVA21_HOME environment variable when Java 21 is found

echo "🔍 Detecting Java 21..."

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
    echo "❌ ERROR: Java 21 is required but was not found."
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
    echo "🚫 To bypass this check temporarily: git commit --no-verify"
    exit 1
fi

# Export for use by calling scripts
export JAVA21_HOME
