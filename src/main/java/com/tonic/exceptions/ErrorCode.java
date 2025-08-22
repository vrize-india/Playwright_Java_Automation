package com.tonic.exceptions;

/**
 * Enum defining all possible error codes for Xray operations.
 */
public enum ErrorCode {
    AUTHENTICATION_FAILED("Authentication with Xray Cloud failed"),
    INVALID_PAYLOAD("Invalid payload format or content"),
    NETWORK_ERROR("Network communication error"),
    PERMISSION_DENIED("Insufficient permissions for the operation"),
    RESOURCE_NOT_FOUND("Requested resource not found"),
    CONFIGURATION_ERROR("Configuration or setup error"),
    DECRYPTION_ERROR("Failed to decrypt sensitive data"),
    HTTP_ERROR("HTTP request/response error"),
    VALIDATION_ERROR("Input validation failed"),
    UNKNOWN_ERROR("Unknown or unexpected error");
    
    private final String description;
    
    ErrorCode(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

