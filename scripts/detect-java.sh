#!/bin/bash
# Detect and configure Java 21 for the build

# Try to find Java 21 using java_home
if command -v /usr/libexec/java_home > /dev/null 2>&1; then
    JAVA_21_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null || true)
    if [ -n "$JAVA_21_HOME" ]; then
        export JAVA_HOME="$JAVA_21_HOME"
        echo "✅ Using Java 21 from: $JAVA_HOME"
        return 0
    fi
fi

# Common Java 21 locations
JAVA_LOCATIONS=(
    "$HOME/Library/Java/JavaVirtualMachines/openjdk-21.0.2/Contents/Home"
    "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
    "/opt/homebrew/opt/openjdk@21"
    "/usr/lib/jvm/java-21-openjdk"
)

for location in "${JAVA_LOCATIONS[@]}"; do
    if [ -d "$location" ] && [ -f "$location/bin/java" ]; then
        export JAVA_HOME="$location"
        echo "✅ Using Java 21 from: $JAVA_HOME"
        return 0
    fi
done

echo "⚠️  Java 21 not found. Using system default Java."
echo "   This may cause build issues. Consider installing Java 21."
