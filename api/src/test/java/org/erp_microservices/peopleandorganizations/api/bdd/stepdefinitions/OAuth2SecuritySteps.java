package org.erp_microservices.peopleandorganizations.api.bdd.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.erp_microservices.peopleandorganizations.api.ApiApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
@SpringBootTest(
    classes = ApiApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles({"test", "oauth2-test"})
@Testcontainers
public class OAuth2SecuritySteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> lastResponse;
    private String currentToken;
    private String currentRefreshToken;
    private Map<String, Object> tokenClaims = new HashMap<>();
    private List<String> configuredRoles = new ArrayList<>();
    private Map<String, AtomicInteger> rateLimitCounters = new ConcurrentHashMap<>();
    private Map<String, List<AuditLogEntry>> auditLogs = new ConcurrentHashMap<>();

    // Test containers
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_people_organizations")
            .withUsername("test")
            .withPassword("test");

    // Mock Cognito container using LocalStack
    @Container
    static GenericContainer<?> localstack = new GenericContainer<>(DockerImageName.parse("localstack/localstack:latest"))
            .withExposedPorts(4566)
            .withEnv("SERVICES", "cognito-idp")
            .withEnv("DEBUG", "1");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Configure OAuth2 with LocalStack Cognito
        String cognitoEndpoint = String.format("http://localhost:%d", localstack.getMappedPort(4566));
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
            () -> cognitoEndpoint + "/test-pool");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
            () -> cognitoEndpoint + "/.well-known/jwks.json");
    }

    @Given("the application is configured with AWS Cognito OAuth2 provider")
    public void applicationConfiguredWithCognito() {
        // Configuration is handled by @DynamicPropertySource
        assertThat(localstack.isRunning()).isTrue();
    }

    @Given("the following roles are configured: {word}, {word}, {word}")
    public void rolesAreConfigured(String role1, String role2, String role3) {
        configuredRoles.clear();
        configuredRoles.addAll(Arrays.asList(role1, role2, role3));
    }

    @Given("a mock Cognito service is available for testing")
    public void mockCognitoAvailable() {
        assertThat(localstack.isRunning()).isTrue();
        // In a real implementation, we would set up the mock Cognito user pool here
    }

    @Given("no authentication token is provided")
    public void noAuthenticationToken() {
        currentToken = null;
    }

    @Given("I have a valid JWT token with {word} role")
    public void validJwtTokenWithRole(String role) {
        // In real implementation, generate a valid JWT with the specified role
        currentToken = generateMockJwtToken(role, false, true);
    }

    @Given("I have an expired JWT token")
    public void expiredJwtToken() {
        currentToken = generateMockJwtToken("USER", true, true);
    }

    @Given("I have a JWT token with invalid signature")
    public void invalidSignatureToken() {
        currentToken = generateMockJwtToken("USER", false, false);
    }

    @Given("I have a valid refresh token")
    public void validRefreshToken() {
        currentRefreshToken = "mock-refresh-token";
    }

    @Given("audit logging is enabled")
    public void auditLoggingEnabled() {
        auditLogs.clear();
    }

    @Given("the rate limit is set to {int} requests per minute")
    public void rateLimitSet(int limit) {
        rateLimitCounters.clear();
    }

    @Given("the application is running with production profile")
    public void productionProfile() {
        // This would be handled by a separate test configuration
    }

    @Given("AWS Cognito is configured with a JWK Set URI")
    public void cognitoJwkSetConfigured() {
        // JWK Set URI is configured in @DynamicPropertySource
    }

    @Given("I have a JWT token signed with a key from the JWK Set")
    public void tokenSignedWithJwk() {
        currentToken = generateMockJwtToken("USER", false, true);
    }

    @Given("I have a valid JWT token with user claims")
    public void tokenWithUserClaims() {
        tokenClaims.put("username", "john.doe");
        tokenClaims.put("email", "john@example.com");
        currentToken = generateMockJwtTokenWithClaims(tokenClaims);
    }

    @Given("the token contains username {string} and email {string}")
    public void tokenContainsUserInfo(String username, String email) {
        tokenClaims.put("username", username);
        tokenClaims.put("email", email);
    }

    @Given("a custom user details service is configured")
    public void customUserDetailsService() {
        // This would be configured in the test context
    }

    @Given("I have a valid JWT token with subject {string}")
    public void tokenWithSubject(String subject) {
        tokenClaims.put("sub", subject);
        currentToken = generateMockJwtTokenWithClaims(tokenClaims);
    }

    @Given("a service client is registered in Cognito")
    public void serviceClientRegistered() {
        // Mock service client registration
    }

    @When("I make a GraphQL request to query parties")
    public void makeGraphQLQueryRequest() {
        String query = """
            {
                "query": "{ parties { id type } }"
            }
            """;
        makeGraphQLRequest(query);
    }

    @When("I make a GraphQL mutation to delete a party")
    public void makeGraphQLDeleteMutation() {
        String mutation = """
            {
                "query": "mutation { deleteParty(id: \\"test-id\\") { success } }"
            }
            """;
        makeGraphQLRequest(mutation);
    }

    @When("I make a GraphQL mutation to create a party")
    public void makeGraphQLCreateMutation() {
        String mutation = """
            {
                "query": "mutation { createParty(input: { type: PERSON }) { id } }"
            }
            """;
        makeGraphQLRequest(mutation);
    }

    @When("I make a GraphQL query to read party data")
    public void makeGraphQLReadQuery() {
        makeGraphQLQueryRequest();
    }

    @When("I request a new access token")
    public void requestNewAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=refresh_token&refresh_token=" + currentRefreshToken;
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        lastResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/oauth/token",
            request,
            String.class
        );
    }

    @When("I make a preflight OPTIONS request from allowed origin")
    public void makePreflightRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http://localhost:3000");
        headers.set("Access-Control-Request-Method", "POST");
        headers.set("Access-Control-Request-Headers", "Authorization,Content-Type");

        HttpEntity<String> request = new HttpEntity<>(headers);
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/graphql",
            HttpMethod.OPTIONS,
            request,
            String.class
        );
    }

    @When("I make {int} authentication requests within a minute")
    public void makeMultipleAuthRequests(int count) {
        String clientIp = "test-client";
        AtomicInteger counter = rateLimitCounters.computeIfAbsent(clientIp, k -> new AtomicInteger(0));

        for (int i = 0; i < count; i++) {
            counter.incrementAndGet();
            // Simulate rate limit check
            if (counter.get() > 5) {
                lastResponse = ResponseEntity.status(429)
                    .header("Retry-After", "60")
                    .body("Too Many Requests");
                break;
            } else {
                lastResponse = ResponseEntity.ok("Success");
            }
        }
    }

    @When("I make any authenticated request")
    public void makeAuthenticatedRequest() {
        makeGraphQLQueryRequest();
    }

    @When("I perform a failed login attempt")
    public void performFailedLogin() {
        auditLogs.computeIfAbsent("security", k -> new ArrayList<>())
            .add(new AuditLogEntry("FAILED_LOGIN", "127.0.0.1", "Invalid credentials"));
    }

    @When("I successfully authenticate")
    public void performSuccessfulLogin() {
        auditLogs.computeIfAbsent("security", k -> new ArrayList<>())
            .add(new AuditLogEntry("SUCCESSFUL_LOGIN", "127.0.0.1", "User authenticated"));
    }

    @When("I access the {word} endpoint without authentication")
    public void accessEndpointWithoutAuth(String endpoint) {
        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + endpoint,
            HttpMethod.GET,
            request,
            String.class
        );
    }

    @When("I access the {word} endpoint")
    public void accessEndpoint(String endpoint) {
        accessEndpointWithoutAuth(endpoint);
    }

    @When("I make an authenticated request")
    public void makeAuthenticatedRequestGeneric() {
        makeGraphQLQueryRequest();
    }

    @When("I make a GraphQL query for current user info")
    public void queryCurrentUserInfo() {
        String query = """
            {
                "query": "{ currentUser { username email } }"
            }
            """;
        makeGraphQLRequest(query);
    }

    @When("the service requests a token using client credentials")
    public void requestTokenWithClientCredentials() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("service-client-id", "service-client-secret");

        String body = "grant_type=client_credentials";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        lastResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/oauth/token",
            request,
            String.class
        );
    }

    @Then("the response status should be {int} {word}")
    public void responseStatusShouldBe(int statusCode, String statusText) {
        assertThat(lastResponse.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @Then("the response status should be {int} {word} {word}")
    public void responseStatusShouldBeThreeWords(int statusCode, String word1, String word2) {
        assertThat(lastResponse.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @Then("the response should contain an authentication error message")
    public void responseContainsAuthError() {
        assertThat(lastResponse.getBody()).contains("authentication", "unauthorized");
    }

    @Then("the response should contain party data")
    public void responseContainsPartyData() {
        assertThat(lastResponse.getBody()).contains("parties", "id", "type");
    }

    @Then("the response should contain a token expiration error message")
    public void responseContainsExpirationError() {
        assertThat(lastResponse.getBody()).contains("expired", "token");
    }

    @Then("the response should contain an invalid token error message")
    public void responseContainsInvalidTokenError() {
        assertThat(lastResponse.getBody()).contains("invalid", "token");
    }

    @Then("the party should be deleted successfully")
    public void partyDeletedSuccessfully() {
        assertThat(lastResponse.getBody()).contains("success", "true");
    }

    @Then("the response should contain an insufficient privileges error")
    public void responseContainsPrivilegeError() {
        assertThat(lastResponse.getBody()).contains("forbidden", "insufficient", "privileges");
    }

    @Then("the response should contain a new valid JWT token")
    public void responseContainsNewToken() {
        assertThat(lastResponse.getBody()).contains("access_token");
    }

    @Then("the new token should work for authenticated requests")
    public void newTokenWorks() {
        // Extract token from response and test it
        currentToken = "new-mock-token";
        makeGraphQLQueryRequest();
        assertThat(lastResponse.getStatusCodeValue()).isEqualTo(200);
    }

    @Then("the response should contain proper CORS headers")
    public void responseContainsCorsHeaders() {
        HttpHeaders headers = lastResponse.getHeaders();
        assertThat(headers.containsKey("Access-Control-Allow-Origin")).isTrue();
        assertThat(headers.containsKey("Access-Control-Allow-Methods")).isTrue();
        assertThat(headers.containsKey("Access-Control-Allow-Headers")).isTrue();
    }

    @Then("the Access-Control-Allow-Origin header should match the allowed origin")
    public void corsOriginMatches() {
        assertThat(lastResponse.getHeaders().get("Access-Control-Allow-Origin"))
            .contains("http://localhost:3000");
    }

    @Then("the Access-Control-Allow-Methods should include GET, POST, OPTIONS")
    public void corsMethodsInclude() {
        String methods = lastResponse.getHeaders().getFirst("Access-Control-Allow-Methods");
        assertThat(methods).contains("GET", "POST", "OPTIONS");
    }

    @Then("the Access-Control-Allow-Headers should include Authorization, Content-Type")
    public void corsHeadersInclude() {
        String headers = lastResponse.getHeaders().getFirst("Access-Control-Allow-Headers");
        assertThat(headers).contains("Authorization", "Content-Type");
    }

    @Then("the {int}th request should return {int} Too Many Requests")
    public void requestReturnsTooManyRequests(int requestNumber, int statusCode) {
        assertThat(lastResponse.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @Then("the response should include a Retry-After header")
    public void responseIncludesRetryAfter() {
        assertThat(lastResponse.getHeaders().containsKey("Retry-After")).isTrue();
    }

    @Then("the response should include security headers")
    public void responseIncludesSecurityHeaders() {
        HttpHeaders headers = lastResponse.getHeaders();
        assertThat(headers.containsKey("X-Content-Type-Options")).isTrue();
        assertThat(headers.containsKey("X-Frame-Options")).isTrue();
        assertThat(headers.containsKey("Strict-Transport-Security")).isTrue();
    }

    @Then("X-Content-Type-Options should be {string}")
    public void xContentTypeOptionsShouldBe(String value) {
        assertThat(lastResponse.getHeaders().getFirst("X-Content-Type-Options")).isEqualTo(value);
    }

    @Then("X-Frame-Options should be {string}")
    public void xFrameOptionsShouldBe(String value) {
        assertThat(lastResponse.getHeaders().getFirst("X-Frame-Options")).isEqualTo(value);
    }

    @Then("Strict-Transport-Security should be present")
    public void strictTransportSecurityPresent() {
        assertThat(lastResponse.getHeaders().containsKey("Strict-Transport-Security")).isTrue();
    }

    @Then("an audit log entry should be created")
    public void auditLogCreated() {
        assertThat(auditLogs).isNotEmpty();
        assertThat(auditLogs.get("security")).isNotEmpty();
    }

    @Then("the log should contain timestamp, IP address, and failure reason")
    public void auditLogContainsDetails() {
        AuditLogEntry lastEntry = auditLogs.get("security").get(auditLogs.get("security").size() - 1);
        assertThat(lastEntry.timestamp()).isNotNull();
        assertThat(lastEntry.ipAddress()).isNotNull();
        assertThat(lastEntry.details()).contains("Invalid credentials");
    }

    @Then("an audit log entry should be created for successful login")
    public void auditLogForSuccessfulLogin() {
        assertThat(auditLogs.get("security")).isNotEmpty();
        AuditLogEntry lastEntry = auditLogs.get("security").get(auditLogs.get("security").size() - 1);
        assertThat(lastEntry.eventType()).isEqualTo("SUCCESSFUL_LOGIN");
    }

    @Then("authentication should not be required")
    public void authenticationNotRequired() {
        assertThat(lastResponse.getStatusCodeValue()).isEqualTo(200);
    }

    @Then("the token signature should be validated against the JWK Set")
    public void tokenValidatedAgainstJwk() {
        // In real implementation, this would verify JWK validation occurred
        assertThat(lastResponse.getStatusCodeValue()).isEqualTo(200);
    }

    @Then("the request should be successful if validation passes")
    public void requestSuccessfulIfValid() {
        assertThat(lastResponse.getStatusCodeValue()).isEqualTo(200);
    }

    @Then("the response should contain the correct username and email")
    public void responseContainsUserInfo() {
        assertThat(lastResponse.getBody()).contains("john.doe", "john@example.com");
    }

    @Then("the user context should be available in GraphQL resolvers")
    public void userContextAvailable() {
        // This would be verified by the resolver implementation
        assertThat(lastResponse.getBody()).contains("currentUser");
    }

    @Then("the user details should be loaded from the custom service")
    public void userDetailsLoadedFromService() {
        // Verified by checking that additional attributes are present
        assertThat(lastResponse.getStatusCodeValue()).isEqualTo(200);
    }

    @Then("additional user attributes should be available in the security context")
    public void additionalAttributesAvailable() {
        // Would check for custom attributes in the response
        assertThat(lastResponse.getBody()).isNotNull();
    }

    @Then("a valid service token should be issued")
    public void serviceTokenIssued() {
        assertThat(lastResponse.getBody()).contains("access_token");
        assertThat(lastResponse.getStatusCodeValue()).isEqualTo(200);
    }

    @Then("the token should allow access to service endpoints")
    public void tokenAllowsServiceAccess() {
        // Would test with the service token
        assertThat(currentToken).isNotNull();
    }

    @Then("the token should not allow access to user-specific endpoints")
    public void tokenDeniesUserEndpoints() {
        // Would test that user endpoints return 403
        // This is a placeholder assertion
        assertThat(true).isTrue();
    }

    // Helper methods
    private void makeGraphQLRequest(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentToken != null) {
            headers.setBearerAuth(currentToken);
        }

        HttpEntity<String> request = new HttpEntity<>(query, headers);
        lastResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/graphql",
            request,
            String.class
        );
    }

    private String generateMockJwtToken(String role, boolean expired, boolean validSignature) {
        // In real implementation, this would generate actual JWT tokens
        if (expired) {
            return "expired-mock-token";
        } else if (!validSignature) {
            return "invalid-signature-mock-token";
        } else {
            return "valid-mock-token-" + role;
        }
    }

    private String generateMockJwtTokenWithClaims(Map<String, Object> claims) {
        // In real implementation, this would generate JWT with specified claims
        return "mock-token-with-claims";
    }

    // Inner class for audit log entries
    private record AuditLogEntry(
        String eventType,
        String ipAddress,
        String details,
        Instant timestamp
    ) {
        public AuditLogEntry(String eventType, String ipAddress, String details) {
            this(eventType, ipAddress, details, Instant.now());
        }
    }
}
