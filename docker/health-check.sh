#!/bin/bash

# Comprehensive Health Check Script for People and Organizations Domain
# This script checks the health of all Docker services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
POSTGRES_HOST="${POSTGRES_HOST:-localhost}"
POSTGRES_PORT="${POSTGRES_PORT:-5432}"
POSTGRES_USER="${POSTGRES_USER:-people_org_user}"
POSTGRES_DB="${POSTGRES_DB:-people_and_organizations}"

API_HOST="${API_HOST:-localhost}"
API_PORT="${API_PORT:-8080}"

REDIS_HOST="${REDIS_HOST:-localhost}"
REDIS_PORT="${REDIS_PORT:-6379}"

LOCALSTACK_HOST="${LOCALSTACK_HOST:-localhost}"
LOCALSTACK_PORT="${LOCALSTACK_PORT:-4566}"

# Function to print status
print_status() {
    local service=$1
    local status=$2
    local message=$3

    if [ "$status" = "OK" ]; then
        echo -e "${GREEN}✅ $service:${NC} $message"
    elif [ "$status" = "WARNING" ]; then
        echo -e "${YELLOW}⚠️  $service:${NC} $message"
    else
        echo -e "${RED}❌ $service:${NC} $message"
    fi
}

# Function to check PostgreSQL
check_postgres() {
    echo -e "${BLUE}🐘 Checking PostgreSQL...${NC}"

    if command -v pg_isready >/dev/null 2>&1; then
        if pg_isready -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" >/dev/null 2>&1; then
            print_status "PostgreSQL" "OK" "Database is ready and accepting connections"

            # Check if database exists
            if PGPASSWORD="$POSTGRES_PASSWORD" psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT 1;" >/dev/null 2>&1; then  # pragma: allowlist secret
                print_status "PostgreSQL" "OK" "Database '$POSTGRES_DB' is accessible"
            else
                print_status "PostgreSQL" "ERROR" "Database '$POSTGRES_DB' is not accessible"
                return 1
            fi
        else
            print_status "PostgreSQL" "ERROR" "Database is not ready"
            return 1
        fi
    else
        # Fallback to netcat if pg_isready is not available
        if timeout 5 bash -c "echo > /dev/tcp/$POSTGRES_HOST/$POSTGRES_PORT" >/dev/null 2>&1; then
            print_status "PostgreSQL" "WARNING" "Port is open (pg_isready not available for full check)"
        else
            print_status "PostgreSQL" "ERROR" "Cannot connect to PostgreSQL"
            return 1
        fi
    fi
}

# Function to check Redis
check_redis() {
    echo -e "${BLUE}🔴 Checking Redis...${NC}"

    if command -v redis-cli >/dev/null 2>&1; then
        if redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" ping >/dev/null 2>&1; then
            print_status "Redis" "OK" "Redis is responding to ping"
        else
            print_status "Redis" "ERROR" "Redis is not responding"
            return 1
        fi
    else
        # Fallback to netcat
        if timeout 5 bash -c "echo > /dev/tcp/$REDIS_HOST/$REDIS_PORT" >/dev/null 2>&1; then
            print_status "Redis" "WARNING" "Port is open (redis-cli not available for full check)"
        else
            print_status "Redis" "ERROR" "Cannot connect to Redis"
            return 1
        fi
    fi
}

# Function to check LocalStack
check_localstack() {
    echo -e "${BLUE}☁️  Checking LocalStack...${NC}"

    if curl -s -f "http://$LOCALSTACK_HOST:$LOCALSTACK_PORT/_localstack/health" >/dev/null 2>&1; then
        local health_response=$(curl -s "http://$LOCALSTACK_HOST:$LOCALSTACK_PORT/_localstack/health")

        # Check if specific services are running
        if echo "$health_response" | grep -q '"cognito-idp": "available"'; then
            print_status "LocalStack" "OK" "Cognito service is available"
        else
            print_status "LocalStack" "WARNING" "Cognito service may not be available"
        fi

        if echo "$health_response" | grep -q '"s3": "available"'; then
            print_status "LocalStack" "OK" "S3 service is available"
        else
            print_status "LocalStack" "WARNING" "S3 service may not be available"
        fi

        if echo "$health_response" | grep -q '"secretsmanager": "available"'; then  # pragma: allowlist secret
            print_status "LocalStack" "OK" "Secrets Manager service is available"
        else
            print_status "LocalStack" "WARNING" "Secrets Manager service may not be available"
        fi
    else
        print_status "LocalStack" "ERROR" "LocalStack health check failed"
        return 1
    fi
}

# Function to check Spring Boot API
check_api() {
    echo -e "${BLUE}🌸 Checking Spring Boot API...${NC}"

    # Check if the port is open
    if ! timeout 5 bash -c "echo > /dev/tcp/$API_HOST/$API_PORT" >/dev/null 2>&1; then
        print_status "API" "ERROR" "API port $API_PORT is not open"
        return 1
    fi

    # Check health endpoint
    if curl -s -f "http://$API_HOST:$API_PORT/actuator/health" >/dev/null 2>&1; then
        local health_response=$(curl -s "http://$API_HOST:$API_PORT/actuator/health")
        local status=$(echo "$health_response" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)

        if [ "$status" = "UP" ]; then
            print_status "API" "OK" "Application health check passed"

            # Check database connectivity through API
            if echo "$health_response" | grep -q '"db":'; then
                local db_status=$(echo "$health_response" | grep -o '"db":{"status":"[^"]*"' | cut -d'"' -f6)
                if [ "$db_status" = "UP" ]; then
                    print_status "API" "OK" "Database connectivity through API is healthy"
                else
                    print_status "API" "WARNING" "Database connectivity through API shows: $db_status"
                fi
            fi
        else
            print_status "API" "ERROR" "Application health check failed with status: $status"
            return 1
        fi
    else
        print_status "API" "ERROR" "API health endpoint is not responding"
        return 1
    fi

    # Check GraphQL endpoint
    if curl -s -f "http://$API_HOST:$API_PORT/graphql" -H "Content-Type: application/json" -d '{"query": "query { __schema { queryType { name } } }"}' >/dev/null 2>&1; then
        print_status "API" "OK" "GraphQL endpoint is responding"
    else
        print_status "API" "WARNING" "GraphQL endpoint may not be fully initialized"
    fi
}

# Function to check Docker containers (if running in Docker context)
check_docker_containers() {
    echo -e "${BLUE}🐳 Checking Docker containers...${NC}"

    if command -v docker >/dev/null 2>&1; then
        # Check if containers are running
        local containers=("people-org-postgres" "people-org-redis" "people-org-localstack" "people-org-api")

        for container in "${containers[@]}"; do
            if docker ps --format "table {{.Names}}" | grep -q "$container"; then
                local status=$(docker inspect --format="{{.State.Health.Status}}" "$container" 2>/dev/null || echo "no-healthcheck")
                if [ "$status" = "healthy" ]; then
                    print_status "Docker" "OK" "Container $container is healthy"
                elif [ "$status" = "no-healthcheck" ]; then
                    print_status "Docker" "WARNING" "Container $container is running (no health check configured)"
                else
                    print_status "Docker" "WARNING" "Container $container status: $status"
                fi
            else
                print_status "Docker" "ERROR" "Container $container is not running"
            fi
        done
    else
        print_status "Docker" "WARNING" "Docker command not available"
    fi
}

# Function to check network connectivity
check_network() {
    echo -e "${BLUE}🌐 Checking network connectivity...${NC}"

    # Check internal Docker network (if applicable)
    if docker network ls | grep -q "people-org-network"; then
        print_status "Network" "OK" "Docker network 'people-org-network' exists"
    else
        print_status "Network" "WARNING" "Docker network 'people-org-network' not found"
    fi

    # Check port accessibility
    local ports=("$POSTGRES_PORT" "$REDIS_PORT" "$LOCALSTACK_PORT" "$API_PORT")
    local hosts=("$POSTGRES_HOST" "$REDIS_HOST" "$LOCALSTACK_HOST" "$API_HOST")

    for i in "${!ports[@]}"; do
        local port="${ports[$i]}"
        local host="${hosts[$i]}"

        if timeout 5 bash -c "echo > /dev/tcp/$host/$port" >/dev/null 2>&1; then
            print_status "Network" "OK" "Port $port on $host is accessible"
        else
            print_status "Network" "ERROR" "Port $port on $host is not accessible"
        fi
    done
}

# Function to display summary
display_summary() {
    echo ""
    echo -e "${BLUE}📊 Health Check Summary${NC}"
    echo "========================="
    echo "Timestamp: $(date)"
    echo "Environment: ${ENVIRONMENT:-development}"
    echo ""
    echo "Service Endpoints:"
    echo "  - API: http://$API_HOST:$API_PORT"
    echo "  - PostgreSQL: $POSTGRES_HOST:$POSTGRES_PORT"
    echo "  - Redis: $REDIS_HOST:$REDIS_PORT"
    echo "  - LocalStack: http://$LOCALSTACK_HOST:$LOCALSTACK_PORT"
    echo ""
    echo "Health Check URLs:"
    echo "  - API Health: http://$API_HOST:$API_PORT/actuator/health"
    echo "  - LocalStack Health: http://$LOCALSTACK_HOST:$LOCALSTACK_PORT/_localstack/health"
    echo "  - GraphQL Playground: http://$API_HOST:$API_PORT/graphiql"
    echo ""
}

# Main function
main() {
    echo -e "${BLUE}🏥 Starting comprehensive health check for People and Organizations Domain...${NC}"
    echo ""

    local exit_code=0

    # Run all health checks
    check_docker_containers || exit_code=1
    echo ""

    check_network || exit_code=1
    echo ""

    check_postgres || exit_code=1
    echo ""

    check_redis || exit_code=1
    echo ""

    check_localstack || exit_code=1
    echo ""

    check_api || exit_code=1
    echo ""

    # Display summary
    display_summary

    if [ $exit_code -eq 0 ]; then
        echo -e "${GREEN}🎉 All health checks passed!${NC}"
    else
        echo -e "${RED}💥 Some health checks failed. Check the output above for details.${NC}"
    fi

    exit $exit_code
}

# Run main function if script is executed directly
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
