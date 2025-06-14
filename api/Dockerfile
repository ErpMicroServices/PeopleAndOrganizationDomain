# Multi-stage build for People and Organizations API
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /workspace/app

# Copy gradle wrapper and build files
COPY gradle gradle
COPY gradlew ./
COPY settings.gradle ./
COPY build.gradle ./

# Copy source code
COPY api/build.gradle api/
COPY api/src api/src
COPY database/build.gradle database/
COPY database/src database/src

# Build the application
RUN ./gradlew :api:build -x test --no-daemon

# Extract built JAR layers
RUN java -Djarmode=layertools -jar api/build/libs/*.jar extract

# Production stage
FROM eclipse-temurin:21-jre-alpine

# Add metadata
LABEL org.opencontainers.image.title="People and Organizations API"
LABEL org.opencontainers.image.description="People and Organizations Domain Microservice API"
LABEL org.opencontainers.image.vendor="ERP Microservices"
LABEL org.opencontainers.image.source="https://github.com/ErpMicroServices/PeopleAndOrganizationDomain"

# Create application user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Install security updates and required packages
RUN apk update && \
    apk upgrade && \
    apk add --no-cache \
        curl \
        tzdata && \
    rm -rf /var/cache/apk/*

# Set timezone
ENV TZ=UTC

# Create application directory
WORKDIR /app

# Copy application layers from builder stage
COPY --from=builder --chown=appuser:appgroup workspace/app/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup workspace/app/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup workspace/app/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup workspace/app/application/ ./

# Switch to non-root user
USER appuser:appgroup

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseJVMCICompiler \
               -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
