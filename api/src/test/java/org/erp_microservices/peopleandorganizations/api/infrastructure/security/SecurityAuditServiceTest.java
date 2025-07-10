package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.security.authentication.event.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityAuditServiceTest {

    private SecurityAuditService auditService;

    @Mock
    private Logger logger;

    @Mock
    private SecurityEventRepository eventRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @Mock
    private WebAuthenticationDetails webDetails;

    @Captor
    private ArgumentCaptor<SecurityEvent> eventCaptor;

    @BeforeEach
    void setUp() {
        auditService = new SecurityAuditService(eventRepository);
        // Inject mock logger
        auditService.setLogger(logger);
    }

    @Test
    void logSuccessfulAuthentication_shouldCreateAuditEvent() {
        // Given
        String username = "john.doe";
        String ipAddress = "192.168.1.100";
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getDetails()).thenReturn(webDetails);
        when(webDetails.getRemoteAddress()).thenReturn(ipAddress);

        // When
        auditService.logSuccessfulAuthentication(authentication);

        // Then
        verify(eventRepository).save(eventCaptor.capture());
        SecurityEvent event = eventCaptor.getValue();

        assertThat(event.getEventType()).isEqualTo(SecurityEventType.AUTHENTICATION_SUCCESS);
        assertThat(event.getUsername()).isEqualTo(username);
        assertThat(event.getIpAddress()).isEqualTo(ipAddress);
        assertThat(event.getDetails()).contains("ROLE_USER", "ROLE_ADMIN");
        assertThat(event.getTimestamp()).isNotNull();
    }

    @Test
    void logFailedAuthentication_shouldCreateAuditEvent() {
        // Given
        String username = "john.doe";
        String ipAddress = "192.168.1.100";
        String failureReason = "Bad credentials";

        when(authentication.getName()).thenReturn(username);
        when(authentication.getDetails()).thenReturn(webDetails);
        when(webDetails.getRemoteAddress()).thenReturn(ipAddress);

        AuthenticationException exception = new BadCredentialsException(failureReason);

        // When
        auditService.logFailedAuthentication(authentication, exception);

        // Then
        verify(eventRepository).save(eventCaptor.capture());
        SecurityEvent event = eventCaptor.getValue();

        assertThat(event.getEventType()).isEqualTo(SecurityEventType.AUTHENTICATION_FAILURE);
        assertThat(event.getUsername()).isEqualTo(username);
        assertThat(event.getIpAddress()).isEqualTo(ipAddress);
        assertThat(event.getDetails()).contains(failureReason);
    }

    @Test
    void logSuccessfulAuthentication_withJwtToken_shouldExtractUserInfo() {
        // Given
        Jwt jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "RS256")
            .claim("sub", "user123")
            .claim("email", "john@example.com")
            .claim("username", "john.doe")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();

        JwtAuthenticationToken jwtAuth = new JwtAuthenticationToken(
            jwt,
            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        jwtAuth.setDetails(webDetails);
        when(webDetails.getRemoteAddress()).thenReturn("192.168.1.100");

        // When
        auditService.logSuccessfulAuthentication(jwtAuth);

        // Then
        verify(eventRepository).save(eventCaptor.capture());
        SecurityEvent event = eventCaptor.getValue();

        assertThat(event.getUsername()).isEqualTo("john.doe");
        assertThat(event.getDetails()).contains("email: john@example.com");
    }

    @Test
    void logAuthorizationFailure_shouldCreateAuditEvent() {
        // Given
        String username = "john.doe";
        String resource = "/api/admin/users";
        String requiredRole = "ROLE_ADMIN";

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenReturn(
            (Collection) Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // When
        auditService.logAuthorizationFailure(authentication, resource, requiredRole);

        // Then
        verify(eventRepository).save(eventCaptor.capture());
        SecurityEvent event = eventCaptor.getValue();

        assertThat(event.getEventType()).isEqualTo(SecurityEventType.AUTHORIZATION_FAILURE);
        assertThat(event.getUsername()).isEqualTo(username);
        assertThat(event.getDetails())
            .contains(resource)
            .contains(requiredRole)
            .contains("ROLE_USER");
    }

    @Test
    void logTokenRefresh_shouldCreateAuditEvent() {
        // Given
        String username = "john.doe";
        String oldTokenId = "old-token-123";
        String newTokenId = "new-token-456";

        // When
        auditService.logTokenRefresh(username, oldTokenId, newTokenId);

        // Then
        verify(eventRepository).save(eventCaptor.capture());
        SecurityEvent event = eventCaptor.getValue();

        assertThat(event.getEventType()).isEqualTo(SecurityEventType.TOKEN_REFRESH);
        assertThat(event.getUsername()).isEqualTo(username);
        assertThat(event.getDetails())
            .contains(oldTokenId)
            .contains(newTokenId);
    }

    @Test
    void logRateLimitExceeded_shouldCreateAuditEvent() {
        // Given
        String clientIp = "192.168.1.100";
        String endpoint = "/oauth/token";

        // When
        auditService.logRateLimitExceeded(clientIp, endpoint);

        // Then
        verify(eventRepository).save(eventCaptor.capture());
        SecurityEvent event = eventCaptor.getValue();

        assertThat(event.getEventType()).isEqualTo(SecurityEventType.RATE_LIMIT_EXCEEDED);
        assertThat(event.getIpAddress()).isEqualTo(clientIp);
        assertThat(event.getDetails()).contains(endpoint);
    }

    @Test
    void logSecurityConfigurationChange_shouldCreateAuditEvent() {
        // Given
        String adminUser = "admin";
        String changeType = "CORS_ORIGIN_ADDED";
        String changeDetails = "Added http://new-origin.com to allowed origins";

        // When
        auditService.logSecurityConfigurationChange(adminUser, changeType, changeDetails);

        // Then
        verify(eventRepository).save(eventCaptor.capture());
        SecurityEvent event = eventCaptor.getValue();

        assertThat(event.getEventType()).isEqualTo(SecurityEventType.CONFIGURATION_CHANGE);
        assertThat(event.getUsername()).isEqualTo(adminUser);
        assertThat(event.getDetails())
            .contains(changeType)
            .contains(changeDetails);
    }

    @Test
    void logSuspiciousActivity_shouldCreateAuditEvent() {
        // Given
        String clientIp = "192.168.1.100";
        String activityType = "MULTIPLE_FAILED_LOGINS";
        String details = "10 failed login attempts in 5 minutes";

        // When
        auditService.logSuspiciousActivity(clientIp, activityType, details);

        // Then
        verify(eventRepository).save(eventCaptor.capture());
        SecurityEvent event = eventCaptor.getValue();

        assertThat(event.getEventType()).isEqualTo(SecurityEventType.SUSPICIOUS_ACTIVITY);
        assertThat(event.getIpAddress()).isEqualTo(clientIp);
        assertThat(event.getDetails())
            .contains(activityType)
            .contains(details);
    }

    @Test
    void getRecentEvents_shouldReturnPagedResults() {
        // Given
        int page = 0;
        int size = 10;

        // When
        auditService.getRecentEvents(page, size);

        // Then
        verify(eventRepository).findAllByOrderByTimestampDesc(any());
    }

    @Test
    void getEventsByUsername_shouldReturnUserEvents() {
        // Given
        String username = "john.doe";

        // When
        auditService.getEventsByUsername(username);

        // Then
        verify(eventRepository).findByUsernameOrderByTimestampDesc(username);
    }

    @Test
    void getEventsByType_shouldReturnTypedEvents() {
        // Given
        SecurityEventType eventType = SecurityEventType.AUTHENTICATION_FAILURE;

        // When
        auditService.getEventsByType(eventType);

        // Then
        verify(eventRepository).findByEventTypeOrderByTimestampDesc(eventType);
    }

    @Test
    void logEvent_withNullAuthentication_shouldHandleGracefully() {
        // When
        auditService.logSuccessfulAuthentication(null);

        // Then
        verify(logger).error(eq("Failed to save security event"), any(Exception.class));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void logEvent_withExceptionDuringSave_shouldLogError() {
        // Given
        when(authentication.getName()).thenReturn("test-user");
        doThrow(new RuntimeException("Database error"))
            .when(eventRepository).save(any());

        // When
        auditService.logSuccessfulAuthentication(authentication);

        // Then
        verify(logger).error(contains("Failed to save security event"), any(Exception.class));
    }

    // Mock implementation of exception for testing
    private static class BadCredentialsException extends AuthenticationException {
        public BadCredentialsException(String msg) {
            super(msg);
        }
    }
}
