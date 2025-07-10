Feature: OAuth2 Security with AWS Cognito Integration
  As a system administrator
  I want OAuth2 security integrated with AWS Cognito
  So that I can ensure proper authentication and authorization for all API endpoints

  Background:
    Given the application is configured with AWS Cognito OAuth2 provider
    And the following roles are configured: ADMIN, USER, READONLY
    And a mock Cognito service is available for testing

  Scenario: Unauthenticated access to protected endpoint is rejected
    Given no authentication token is provided
    When I make a GraphQL request to query parties
    Then the response status should be 401 Unauthorized
    And the response should contain an authentication error message

  Scenario: Valid JWT token allows access to protected endpoint
    Given I have a valid JWT token with USER role
    When I make a GraphQL request to query parties
    Then the response status should be 200 OK
    And the response should contain party data

  Scenario: Expired JWT token is rejected
    Given I have an expired JWT token
    When I make a GraphQL request to query parties
    Then the response status should be 401 Unauthorized
    And the response should contain a token expiration error message

  Scenario: Invalid JWT token signature is rejected
    Given I have a JWT token with invalid signature
    When I make a GraphQL request to query parties
    Then the response status should be 401 Unauthorized
    And the response should contain an invalid token error message

  Scenario: ADMIN role can access admin endpoints
    Given I have a valid JWT token with ADMIN role
    When I make a GraphQL mutation to delete a party
    Then the response status should be 200 OK
    And the party should be deleted successfully

  Scenario: USER role cannot access admin endpoints
    Given I have a valid JWT token with USER role
    When I make a GraphQL mutation to delete a party
    Then the response status should be 403 Forbidden
    And the response should contain an insufficient privileges error

  Scenario: READONLY role can only read data
    Given I have a valid JWT token with READONLY role
    When I make a GraphQL query to read party data
    Then the response status should be 200 OK
    When I make a GraphQL mutation to create a party
    Then the response status should be 403 Forbidden

  Scenario: Token refresh with valid refresh token
    Given I have a valid refresh token
    When I request a new access token
    Then the response status should be 200 OK
    And the response should contain a new valid JWT token
    And the new token should work for authenticated requests

  Scenario: CORS headers are properly configured
    Given I have a valid JWT token
    When I make a preflight OPTIONS request from allowed origin
    Then the response should contain proper CORS headers
    And the Access-Control-Allow-Origin header should match the allowed origin
    And the Access-Control-Allow-Methods should include GET, POST, OPTIONS
    And the Access-Control-Allow-Headers should include Authorization, Content-Type

  Scenario: Rate limiting for authentication endpoints
    Given the rate limit is set to 5 requests per minute
    When I make 6 authentication requests within a minute
    Then the 6th request should return 429 Too Many Requests
    And the response should include a Retry-After header

  Scenario: Security headers are present in responses
    Given I have a valid JWT token
    When I make any authenticated request
    Then the response should include security headers
    And X-Content-Type-Options should be "nosniff"
    And X-Frame-Options should be "DENY"
    And Strict-Transport-Security should be present

  Scenario: Audit logging for security events
    Given audit logging is enabled
    When I perform a failed login attempt
    Then an audit log entry should be created
    And the log should contain timestamp, IP address, and failure reason
    When I successfully authenticate
    Then an audit log entry should be created for successful login

  Scenario: GraphiQL endpoint requires authentication in production
    Given the application is running with production profile
    When I access the /graphiql endpoint without authentication
    Then the response status should be 401 Unauthorized

  Scenario: Health check endpoint is accessible without authentication
    Given no authentication token is provided
    When I access the /actuator/health endpoint
    Then the response status should be 200 OK
    And authentication should not be required

  Scenario: JWT token validation with Cognito JWK Set
    Given AWS Cognito is configured with a JWK Set URI
    And I have a JWT token signed with a key from the JWK Set
    When I make an authenticated request
    Then the token signature should be validated against the JWK Set
    And the request should be successful if validation passes

  Scenario: User context is properly extracted from JWT
    Given I have a valid JWT token with user claims
    And the token contains username "john.doe" and email "john@example.com"
    When I make a GraphQL query for current user info
    Then the response should contain the correct username and email
    And the user context should be available in GraphQL resolvers

  Scenario: Custom user details service integration
    Given a custom user details service is configured
    And I have a valid JWT token with subject "user123"
    When I make an authenticated request
    Then the user details should be loaded from the custom service
    And additional user attributes should be available in the security context

  Scenario: OAuth2 client credentials flow for service-to-service
    Given a service client is registered in Cognito
    When the service requests a token using client credentials
    Then a valid service token should be issued
    And the token should allow access to service endpoints
    But the token should not allow access to user-specific endpoints
