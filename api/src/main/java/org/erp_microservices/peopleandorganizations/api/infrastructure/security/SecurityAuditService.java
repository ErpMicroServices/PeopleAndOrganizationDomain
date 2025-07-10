package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityAuditService {

    private Logger logger = LoggerFactory.getLogger(SecurityAuditService.class);
    private final SecurityEventRepository eventRepository;

    public SecurityAuditService(SecurityEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void logSuccessfulAuthentication(Authentication authentication) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                .eventType(SecurityEventType.AUTHENTICATION_SUCCESS)
                .username(extractUsername(authentication))
                .ipAddress(extractIpAddress(authentication))
                .details(extractAuthenticationDetails(authentication))
                .timestamp(Instant.now())
                .build();

            eventRepository.save(event);
            logger.info("Authentication success for user: {}", event.getUsername());
        } catch (Exception e) {
            logger.error("Failed to save security event", e);
        }
    }

    public void logFailedAuthentication(Authentication authentication, AuthenticationException exception) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                .eventType(SecurityEventType.AUTHENTICATION_FAILURE)
                .username(extractUsername(authentication))
                .ipAddress(extractIpAddress(authentication))
                .details("Failure reason: " + exception.getMessage())
                .timestamp(Instant.now())
                .build();

            eventRepository.save(event);
            logger.warn("Authentication failure for user: {} - {}", event.getUsername(), exception.getMessage());
        } catch (Exception e) {
            logger.error("Failed to save security event", e);
        }
    }

    public void logAuthorizationFailure(Authentication authentication, String resource, String requiredRole) {
        try {
            String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

            SecurityEvent event = SecurityEvent.builder()
                .eventType(SecurityEventType.AUTHORIZATION_FAILURE)
                .username(extractUsername(authentication))
                .ipAddress(extractIpAddress(authentication))
                .details(String.format("Resource: %s, Required: %s, User has: %s",
                    resource, requiredRole, authorities))
                .timestamp(Instant.now())
                .build();

            eventRepository.save(event);
            logger.warn("Authorization failure for user: {} accessing {}", event.getUsername(), resource);
        } catch (Exception e) {
            logger.error("Failed to save security event", e);
        }
    }

    public void logTokenRefresh(String username, String oldTokenId, String newTokenId) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                .eventType(SecurityEventType.TOKEN_REFRESH)
                .username(username)
                .details(String.format("Old token: %s, New token: %s", oldTokenId, newTokenId))
                .timestamp(Instant.now())
                .build();

            eventRepository.save(event);
            logger.info("Token refresh for user: {}", username);
        } catch (Exception e) {
            logger.error("Failed to save security event", e);
        }
    }

    public void logRateLimitExceeded(String clientIp, String endpoint) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                .eventType(SecurityEventType.RATE_LIMIT_EXCEEDED)
                .ipAddress(clientIp)
                .details("Endpoint: " + endpoint)
                .timestamp(Instant.now())
                .build();

            eventRepository.save(event);
            logger.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, endpoint);
        } catch (Exception e) {
            logger.error("Failed to save security event", e);
        }
    }

    public void logSecurityConfigurationChange(String adminUser, String changeType, String changeDetails) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                .eventType(SecurityEventType.CONFIGURATION_CHANGE)
                .username(adminUser)
                .details(String.format("Change type: %s, Details: %s", changeType, changeDetails))
                .timestamp(Instant.now())
                .build();

            eventRepository.save(event);
            logger.info("Security configuration change by {}: {}", adminUser, changeType);
        } catch (Exception e) {
            logger.error("Failed to save security event", e);
        }
    }

    public void logSuspiciousActivity(String clientIp, String activityType, String details) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                .eventType(SecurityEventType.SUSPICIOUS_ACTIVITY)
                .ipAddress(clientIp)
                .details(String.format("Activity: %s, Details: %s", activityType, details))
                .timestamp(Instant.now())
                .build();

            eventRepository.save(event);
            logger.warn("Suspicious activity detected from IP: {} - {}", clientIp, activityType);
        } catch (Exception e) {
            logger.error("Failed to save security event", e);
        }
    }

    public Page<SecurityEvent> getRecentEvents(int page, int size) {
        return eventRepository.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
    }

    public List<SecurityEvent> getEventsByUsername(String username) {
        return eventRepository.findByUsernameOrderByTimestampDesc(username);
    }

    public List<SecurityEvent> getEventsByType(SecurityEventType eventType) {
        return eventRepository.findByEventTypeOrderByTimestampDesc(eventType);
    }

    private String extractUsername(Authentication authentication) {
        if (authentication == null) {
            return "unknown";
        }

        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            // Try different claim names for username
            Object username = jwt.getClaim("username");
            if (username != null) {
                return username.toString();
            }
            Object email = jwt.getClaim("email");
            if (email != null) {
                return email.toString();
            }
        }

        return authentication.getName();
    }

    private String extractIpAddress(Authentication authentication) {
        if (authentication == null || authentication.getDetails() == null) {
            return "unknown";
        }

        if (authentication.getDetails() instanceof WebAuthenticationDetails) {
            return ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }

        return "unknown";
    }

    private String extractAuthenticationDetails(Authentication authentication) {
        StringBuilder details = new StringBuilder();

        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            Object email = jwt.getClaim("email");
            if (email != null) {
                details.append("email: ").append(email).append(", ");
            }
        }

        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(", "));
        details.append("authorities: ").append(authorities);

        return details.toString();
    }

    // Package-private for testing
    void setLogger(Logger logger) {
        this.logger = logger;
    }
}
