# syntax=docker/dockerfile:1

# Build stage
FROM gradle:8.13-jdk21-alpine AS builder

# Set working directory
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Copy source code
COPY api api
COPY database database

# Set executable permissions for gradlew
RUN chmod +x gradlew

# Build the application (skip tests for faster builds)
RUN ./gradlew :api:bootJar --no-daemon --parallel

# Runtime stage
FROM openjdk:21-jdk-slim

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Install required packages and clean up
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/api/build/libs/api-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to appuser
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Configure JVM for container environment
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Set up signal handling for graceful shutdown
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
