package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventListener {

    private final SecurityAuditService auditService;

    public SecurityEventListener(SecurityAuditService auditService) {
        this.auditService = auditService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        auditService.logSuccessfulAuthentication(event.getAuthentication());
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        auditService.logFailedAuthentication(event.getAuthentication(), event.getException());
    }

    @EventListener
    public void onAuthorizationDenied(AuthorizationDeniedEvent event) {
        var authentication = event.getAuthentication().get();
        if (authentication != null) {
            auditService.logAuthorizationFailure(
                authentication,
                event.getAuthorizationDecision().toString(),
                "Access Denied"
            );
        }
    }
}
