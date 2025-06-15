Feature: Security and Authentication
  As a security-conscious system
  I want to enforce proper authentication and authorization
  So that only authorized users can access and modify data

  Background:
    Given the system is running
    And the authentication service is available

  Scenario: Successful authentication with valid credentials
    When I authenticate with valid credentials:
      | username | admin@example.com |
      | password | validPassword123  |
    Then I should be authenticated successfully
    And I should receive an access token
    And the token should have appropriate expiration time

  Scenario: Failed authentication with invalid credentials
    When I attempt to authenticate with invalid credentials:
      | username | admin@example.com |
      | password | wrongPassword     |
    Then the authentication should fail
    And I should receive an authentication error
    And no access token should be provided

  Scenario: Access protected resource with valid token
    Given I am authenticated with a valid token
    When I access a protected GraphQL endpoint
    Then the request should be successful
    And I should receive the requested data

  Scenario: Access protected resource without authentication
    When I attempt to access a protected GraphQL endpoint without authentication
    Then the request should be rejected
    And I should receive an unauthorized error
    And the response should indicate authentication is required

  Scenario: Access protected resource with expired token
    Given I have an expired authentication token
    When I attempt to access a protected GraphQL endpoint
    Then the request should be rejected
    And I should receive a token expired error
    And I should be prompted to re-authenticate

  Scenario: Role-based access control for admin operations
    Given I am authenticated as a regular user
    When I attempt to perform an admin-only operation like deleting a party
    Then the request should be rejected
    And I should receive an authorization error
    And the error should indicate insufficient permissions

  Scenario: Role-based access control for user operations
    Given I am authenticated as an admin user
    When I perform user operations like creating a person
    Then the request should be successful
    And the operation should be completed

  Scenario: User can only access their own data
    Given I am authenticated as user "user1@example.com"
    And another user "user2@example.com" exists
    When I attempt to access user2's personal information
    Then the request should be rejected
    And I should receive an access denied error

  Scenario: Data filtering based on user permissions
    Given I am authenticated as a regional manager
    And data exists for multiple regions
    When I query for organizational data
    Then I should only see data for my assigned region
    And data from other regions should be filtered out

  Scenario: Audit logging for sensitive operations
    Given I am authenticated as an admin user
    When I delete a person record
    Then the deletion should be logged in the audit trail
    And the log should include my user ID, timestamp, and operation details

  Scenario: Rate limiting for API calls
    Given I am authenticated with a valid token
    When I make more than 100 API calls per minute
    Then subsequent calls should be rate limited
    And I should receive a rate limit exceeded error

  Scenario: OAuth2 token refresh
    Given I have a valid refresh token
    When my access token expires
    And I use the refresh token to get a new access token
    Then I should receive a new valid access token
    And I should be able to continue making authenticated requests

  Scenario: Security headers in API responses
    When I make any API request
    Then the response should include security headers:
      | header                | value                |
      | X-Content-Type-Options | nosniff              |
      | X-Frame-Options       | DENY                 |
      | X-XSS-Protection      | 1; mode=block        |

  Scenario: Input validation and sanitization
    When I submit data with potentially malicious content:
      | field     | value                    |
      | firstName | <script>alert('xss')</script> |
    Then the input should be sanitized
    And no script execution should occur
    And the data should be safely stored
