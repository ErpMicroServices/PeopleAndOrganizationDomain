#!/bin/bash

# LocalStack AWS Services Initialization Script
# This script sets up AWS services for local development

set -e

echo "🚀 Starting LocalStack AWS services initialization..."

# AWS CLI configuration
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1
export AWS_ENDPOINT_URL=http://localhost:4566

# Function to wait for LocalStack to be ready
wait_for_localstack() {
    echo "⏳ Waiting for LocalStack to be ready..."

    local max_attempts=30
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:4566/_localstack/health | grep -q '"localstack":'; then
            echo "✅ LocalStack is ready!"
            return 0
        fi

        echo "Attempt $attempt/$max_attempts: LocalStack not ready yet, waiting..."
        sleep 2
        ((attempt++))
    done

    echo "❌ LocalStack failed to start within expected time"
    exit 1
}

# Function to create Cognito User Pool
create_cognito_user_pool() {
    echo "🔐 Setting up Cognito User Pool..."

    # Create User Pool
    USER_POOL_ID=$(awslocal cognito-idp create-user-pool \
        --pool-name "people-org-user-pool" \
        --policies '{
            "PasswordPolicy": {
                "MinimumLength": 8,
                "RequireUppercase": true,
                "RequireLowercase": true,
                "RequireNumbers": true,
                "RequireSymbols": false
            }
        }' \
        --auto-verified-attributes email \
        --username-attributes email \
        --schema '[
            {
                "Name": "email",
                "AttributeDataType": "String",
                "Required": true,
                "Mutable": true
            },
            {
                "Name": "given_name",
                "AttributeDataType": "String",
                "Required": false,
                "Mutable": true
            },
            {
                "Name": "family_name",
                "AttributeDataType": "String",
                "Required": false,
                "Mutable": true
            }
        ]' \
        --query 'UserPool.Id' \
        --output text)

    echo "📝 Created User Pool with ID: $USER_POOL_ID"

    # Create User Pool Client
    CLIENT_ID=$(awslocal cognito-idp create-user-pool-client \
        --user-pool-id "$USER_POOL_ID" \
        --client-name "people-org-client" \
        --generate-secret \
        --explicit-auth-flows ADMIN_NO_SRP_AUTH ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH \
        --supported-identity-providers COGNITO \
        --callback-urls "http://localhost:8080/oauth2/callback" \
        --logout-urls "http://localhost:8080/logout" \
        --allowed-o-auth-flows authorization_code \
        --allowed-o-auth-scopes openid email profile \
        --allowed-o-auth-flows-user-pool-client \
        --query 'UserPoolClient.ClientId' \
        --output text)

    echo "📝 Created User Pool Client with ID: $CLIENT_ID"

    # Get Client Secret
    CLIENT_SECRET=$(awslocal cognito-idp describe-user-pool-client \
        --user-pool-id "$USER_POOL_ID" \
        --client-id "$CLIENT_ID" \
        --query 'UserPoolClient.ClientSecret' \
        --output text)

    # Create a test user
    awslocal cognito-idp admin-create-user \
        --user-pool-id "$USER_POOL_ID" \
        --username "testuser@example.com" \
        --user-attributes '[
            {"Name": "email", "Value": "testuser@example.com"},
            {"Name": "given_name", "Value": "Test"},
            {"Name": "family_name", "Value": "User"},
            {"Name": "email_verified", "Value": "true"}
        ]' \
        --temporary-password "TempPass123!" \
        --message-action SUPPRESS

    echo "👤 Created test user: testuser@example.com"

    # Set permanent password for test user
    awslocal cognito-idp admin-set-user-password \
        --user-pool-id "$USER_POOL_ID" \
        --username "testuser@example.com" \
        --password "TestPass123!" \
        --permanent

    echo "🔑 Set permanent password for test user"

    # Output configuration for application
    echo ""
    echo "🔧 Cognito Configuration for your application:"
    echo "USER_POOL_ID=$USER_POOL_ID"
    echo "CLIENT_ID=$CLIENT_ID"
    echo "CLIENT_SECRET=$CLIENT_SECRET"
    echo "ISSUER_URI=http://localhost:4566"
    echo ""
}

# Function to create S3 buckets
create_s3_buckets() {
    echo "🪣 Setting up S3 buckets..."

    # Create application buckets
    awslocal s3 mb s3://people-org-documents
    awslocal s3 mb s3://people-org-backups
    awslocal s3 mb s3://people-org-logs

    echo "📦 Created S3 buckets:"
    echo "  - people-org-documents"
    echo "  - people-org-backups"
    echo "  - people-org-logs"

    # Set up bucket policies (optional - for development)
    awslocal s3api put-bucket-cors \
        --bucket people-org-documents \
        --cors-configuration '{
            "CORSRules": [
                {
                    "AllowedHeaders": ["*"],
                    "AllowedMethods": ["GET", "POST", "PUT", "DELETE"],
                    "AllowedOrigins": ["http://localhost:3000", "http://localhost:8080"],
                    "MaxAgeSeconds": 3000
                }
            ]
        }'

    echo "✅ S3 buckets configured with CORS"
}

# Function to create Secrets Manager secrets
create_secrets() {
    echo "🔐 Setting up Secrets Manager..."

    # Database credentials
    awslocal secretsmanager create-secret \
        --name "people-org/database" \
        --description "Database credentials for People and Organizations service" \
        --secret-string '{
            "username": "people_org_user",
            "password": "dev_password_123",  # pragma: allowlist secret
            "host": "postgres",
            "port": "5432",
            "dbname": "people_and_organizations"
        }'

    # OAuth credentials
    awslocal secretsmanager create-secret \
        --name "people-org/oauth" \
        --description "OAuth credentials for People and Organizations service" \
        --secret-string '{
            "client_id": "test-client-id",
            "client_secret": "test-client-secret"  # pragma: allowlist secret
        }'

    # Application secrets
    awslocal secretsmanager create-secret \
        --name "people-org/app-secrets" \
        --description "Application secrets for People and Organizations service" \
        --secret-string '{
            "jwt_secret": "local-development-jwt-secret-key-12345",  # pragma: allowlist secret
            "encryption_key": "local-development-encryption-key-67890"
        }'

    echo "🔑 Created secrets in Secrets Manager"
}

# Function to create IAM roles and policies
create_iam_resources() {
    echo "👥 Setting up IAM resources..."

    # Create application role
    awslocal iam create-role \
        --role-name PeopleOrgApplicationRole \
        --assume-role-policy-document '{
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "ec2.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                }
            ]
        }'

    # Create policy for S3 access
    awslocal iam create-policy \
        --policy-name PeopleOrgS3Policy \
        --policy-document '{
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Action": [
                        "s3:GetObject",
                        "s3:PutObject",
                        "s3:DeleteObject",
                        "s3:ListBucket"
                    ],
                    "Resource": [
                        "arn:aws:s3:::people-org-*",
                        "arn:aws:s3:::people-org-*/*"
                    ]
                }
            ]
        }'

    # Create policy for Secrets Manager access
    awslocal iam create-policy \
        --policy-name PeopleOrgSecretsPolicy \
        --policy-document '{
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Action": [
                        "secretsmanager:GetSecretValue",
                        "secretsmanager:DescribeSecret"
                    ],
                    "Resource": "arn:aws:secretsmanager:*:*:secret:people-org/*"
                }
            ]
        }'

    echo "🛡️ Created IAM roles and policies"
}

# Function to display summary
display_summary() {
    echo ""
    echo "🎉 LocalStack AWS services initialization completed!"
    echo ""
    echo "📋 Available Services:"
    echo "  - Cognito User Pool: for authentication"
    echo "  - S3 Buckets: for file storage"
    echo "  - Secrets Manager: for secure configuration"
    echo "  - IAM: for access control"
    echo ""
    echo "🔗 Access URLs:"
    echo "  - LocalStack Dashboard: http://localhost:4566"
    echo "  - Health Check: http://localhost:4566/_localstack/health"
    echo ""
    echo "👤 Test User Credentials:"
    echo "  - Username: testuser@example.com"
    echo "  - Password: TestPass123!"
    echo ""
    echo "🛠️ AWS CLI Commands (use 'awslocal' instead of 'aws'):"
    echo "  - List S3 buckets: awslocal s3 ls"
    echo "  - List secrets: awslocal secretsmanager list-secrets"
    echo "  - Describe user pool: awslocal cognito-idp list-user-pools --max-items 10"
    echo ""
}

# Main execution
main() {
    echo "🔄 Initializing LocalStack AWS services for People and Organizations Domain..."

    # Wait for LocalStack to be ready
    wait_for_localstack

    # Initialize services
    create_cognito_user_pool
    create_s3_buckets
    create_secrets
    create_iam_resources

    # Display summary
    display_summary

    echo "✅ LocalStack initialization completed successfully!"
}

# Execute main function
main "$@"
