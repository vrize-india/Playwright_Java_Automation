package com.vrize.exceptions;

/**
 * Base exception class for Xray integration errors.
 * Provides structured error information for better error handling and debugging.
 */
public class XrayException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String operation;
    
    public XrayException(ErrorCode errorCode, String operation, String message) {
        super(message);
        this.errorCode = errorCode;
        this.operation = operation;
    }
    
    public XrayException(ErrorCode errorCode, String operation, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.operation = operation;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getOperation() {
        return operation;
    }
    
    @Override
    public String getMessage() {
        return String.format("[%s] %s: %s", errorCode.name(), operation, super.getMessage());
    }
}


