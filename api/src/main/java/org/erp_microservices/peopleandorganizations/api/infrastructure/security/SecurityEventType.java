package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

public enum SecurityEventType {
    AUTHENTICATION_SUCCESS,
    AUTHENTICATION_FAILURE,
    AUTHORIZATION_FAILURE,
    TOKEN_REFRESH,
    RATE_LIMIT_EXCEEDED,
    CONFIGURATION_CHANGE,
    SUSPICIOUS_ACTIVITY,
    LOGOUT,
    PASSWORD_CHANGE,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED
}
