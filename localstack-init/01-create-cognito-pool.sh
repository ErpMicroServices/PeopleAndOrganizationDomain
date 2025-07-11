#!/bin/bash
set -e

echo "Creating Cognito User Pool for local development..."

# Create user pool
aws --endpoint-url=http://localhost:4566 cognito-idp create-user-pool \
  --pool-name "people-org-dev-pool" \
  --policies '{"PasswordPolicy":{"MinimumLength":8,"RequireUppercase":false,"RequireLowercase":false,"RequireNumbers":false,"RequireSymbols":false}}' \
  --auto-verified-attributes email \
  --region us-east-1

# Get the user pool ID
POOL_ID=$(aws --endpoint-url=http://localhost:4566 cognito-idp list-user-pools \
  --max-results 10 \
  --region us-east-1 \
  --query 'UserPools[?PoolName==`people-org-dev-pool`].Id' \
  --output text)

echo "Created User Pool: $POOL_ID"

# Create user pool client
aws --endpoint-url=http://localhost:4566 cognito-idp create-user-pool-client \
  --user-pool-id $POOL_ID \
  --client-name "people-org-dev-client" \
  --generate-secret \
  --explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH \
  --supported-identity-providers COGNITO \
  --allowed-o-auth-flows code \
  --allowed-o-auth-scopes openid email profile \
  --allowed-o-auth-flows-user-pool-client \
  --callback-urls "http://localhost:8080/login/oauth2/code/cognito" "http://localhost:3000/callback" \
  --logout-urls "http://localhost:8080/logout" "http://localhost:3000" \
  --region us-east-1

# Get client details
CLIENT_ID=$(aws --endpoint-url=http://localhost:4566 cognito-idp list-user-pool-clients \
  --user-pool-id $POOL_ID \
  --region us-east-1 \
  --query 'UserPoolClients[0].ClientId' \
  --output text)

CLIENT_SECRET=$(aws --endpoint-url=http://localhost:4566 cognito-idp describe-user-pool-client \
  --user-pool-id $POOL_ID \
  --client-id $CLIENT_ID \
  --region us-east-1 \
  --query 'UserPoolClient.ClientSecret' \
  --output text)

echo "Created User Pool Client: $CLIENT_ID"

# Create user pool domain
aws --endpoint-url=http://localhost:4566 cognito-idp create-user-pool-domain \
  --domain "people-org-dev-$POOL_ID" \
  --user-pool-id $POOL_ID \
  --region us-east-1

# Create test users with different roles
echo "Creating test users..."

# Admin user
aws --endpoint-url=http://localhost:4566 cognito-idp admin-create-user \
  --user-pool-id $POOL_ID \
  --username admin@example.com \
  --user-attributes Name=email,Value=admin@example.com Name=email_verified,Value=true Name=custom:role,Value=ADMIN \
  --temporary-password "TempPass123!" \
  --message-action SUPPRESS \
  --region us-east-1

aws --endpoint-url=http://localhost:4566 cognito-idp admin-set-user-password \
  --user-pool-id $POOL_ID \
  --username admin@example.com \
  --password "AdminPass123!" \
  --permanent \
  --region us-east-1

# Regular user
aws --endpoint-url=http://localhost:4566 cognito-idp admin-create-user \
  --user-pool-id $POOL_ID \
  --username user@example.com \
  --user-attributes Name=email,Value=user@example.com Name=email_verified,Value=true Name=custom:role,Value=USER \
  --temporary-password "TempPass123!" \
  --message-action SUPPRESS \
  --region us-east-1

aws --endpoint-url=http://localhost:4566 cognito-idp admin-set-user-password \
  --user-pool-id $POOL_ID \
  --username user@example.com \
  --password "UserPass123!" \
  --permanent \
  --region us-east-1

# Read-only user
aws --endpoint-url=http://localhost:4566 cognito-idp admin-create-user \
  --user-pool-id $POOL_ID \
  --username readonly@example.com \
  --user-attributes Name=email,Value=readonly@example.com Name=email_verified,Value=true Name=custom:role,Value=READONLY \
  --temporary-password "TempPass123!" \
  --message-action SUPPRESS \
  --region us-east-1

aws --endpoint-url=http://localhost:4566 cognito-idp admin-set-user-password \
  --user-pool-id $POOL_ID \
  --username readonly@example.com \
  --password "ReadOnlyPass123!" \
  --permanent \
  --region us-east-1

# Store configuration in AWS Secrets Manager for the application
aws --endpoint-url=http://localhost:4566 secretsmanager create-secret \
  --name people-org/cognito-config \
  --secret-string "{\"userPoolId\":\"$POOL_ID\",\"clientId\":\"$CLIENT_ID\",\"clientSecret\":\"$CLIENT_SECRET\",\"region\":\"us-east-1\"}" \
  --region us-east-1

echo "Cognito setup complete!"
echo ""
echo "Configuration:"
echo "  User Pool ID: $POOL_ID"
echo "  Client ID: $CLIENT_ID"
echo "  Client Secret: $CLIENT_SECRET"
echo "  Region: us-east-1"
echo ""
echo "Test Users:"
echo "  admin@example.com / AdminPass123! (ADMIN role)"
echo "  user@example.com / UserPass123! (USER role)"
echo "  readonly@example.com / ReadOnlyPass123! (READONLY role)"
