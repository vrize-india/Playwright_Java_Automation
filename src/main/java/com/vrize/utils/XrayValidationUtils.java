package com.vrize.utils;

import com.vrize.exceptions.XrayException;
import static com.vrize.exceptions.ErrorCode.VALIDATION_ERROR;

/**
 * Utility class for input validation throughout Xray integration.
 * Provides fail-fast validation with clear error messages.
 */
public class XrayValidationUtils {
    
    /**
     * Validates that a string value is not null or blank.
     * @param value The value to validate
     * @param parameterName The name of the parameter for error messages
     * @throws XrayException if validation fails
     */
    public static void requireNonBlank(String value, String parameterName) {
        if (value == null || value.trim().isEmpty()) {
            throw new XrayException(VALIDATION_ERROR, "Input Validation", 
                parameterName + " cannot be null or blank");
        }
    }
    
    /**
     * Validates that a string value is a valid Jira issue key format.
     * @param key The Jira key to validate
     * @param parameterName The name of the parameter for error messages
     * @throws XrayException if validation fails
     */
    public static void requireValidJiraKey(String key, String parameterName) {
        requireNonBlank(key, parameterName);
        if (!key.matches("[A-Z]+-\\d+")) {
            throw new XrayException(VALIDATION_ERROR, "Input Validation", 
                parameterName + " must be a valid Jira key format (e.g., TONIC-123)");
        }
    }
    
    /**
     * Validates that a file exists and is readable.
     * @param filePath The file path to validate
     * @param parameterName The name of the parameter for error messages
     * @throws XrayException if validation fails
     */
    public static void requireFileExists(String filePath, String parameterName) {
        requireNonBlank(filePath, parameterName);
        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            throw new XrayException(VALIDATION_ERROR, "File Validation", 
                parameterName + " file does not exist: " + filePath);
        }
        if (!file.canRead()) {
            throw new XrayException(VALIDATION_ERROR, "File Validation", 
                parameterName + " file is not readable: " + filePath);
        }
    }
    
    /**
     * Validates that a URL string is well-formed.
     * @param url The URL string to validate
     * @param parameterName The name of the parameter for error messages
     * @throws XrayException if validation fails
     */
    public static void requireValidUrl(String url, String parameterName) {
        requireNonBlank(url, parameterName);
        try {
            new java.net.URL(url);
        } catch (java.net.MalformedURLException e) {
            throw new XrayException(VALIDATION_ERROR, "URL Validation", 
                parameterName + " is not a valid URL: " + url, e);
        }
    }
    
    /**
     * Validates that a value is not null.
     * @param value The value to validate
     * @param parameterName The name of the parameter for error messages
     * @throws XrayException if validation fails
     */
    public static void requireNonNull(Object value, String parameterName) {
        if (value == null) {
            throw new XrayException(VALIDATION_ERROR, "Input Validation", 
                parameterName + " cannot be null");
        }
    }
    
    /**
     * Validates that a collection is not null or empty.
     * @param collection The collection to validate
     * @param parameterName The name of the parameter for error messages
     * @throws XrayException if validation fails
     */
    public static void requireNonEmpty(java.util.Collection<?> collection, String parameterName) {
        requireNonNull(collection, parameterName);
        if (collection.isEmpty()) {
            throw new XrayException(VALIDATION_ERROR, "Input Validation", 
                parameterName + " cannot be empty");
        }
    }
    
    /**
     * Validates that a string array is not null or empty.
     * @param array The array to validate
     * @param parameterName The name of the parameter for error messages
     * @throws XrayException if validation fails
     */
    public static void requireNonEmpty(String[] array, String parameterName) {
        requireNonNull(array, parameterName);
        if (array.length == 0) {
            throw new XrayException(VALIDATION_ERROR, "Input Validation", 
                parameterName + " cannot be empty");
        }
    }
    
    /**
     * Validates that a number is positive (greater than zero).
     * @param value The number to validate
     * @param parameterName The name of the parameter for error messages
     * @throws XrayException if validation fails
     */
    public static void requirePositive(Number value, String parameterName) {
        requireNonNull(value, parameterName);
        if (value.doubleValue() <= 0) {
            throw new XrayException(VALIDATION_ERROR, "Input Validation", 
                parameterName + " must be positive, got: " + value);
        }
    }
    
    /**
     * Validates that a number is non-negative (greater than or equal to zero).
     * @param value The number to validate
     * @param parameterName The name of the parameter for error messages
     * @throws XrayException if validation fails
     */
    public static void requireNonNegative(Number value, String parameterName) {
        requireNonNull(value, parameterName);
        if (value.doubleValue() < 0) {
            throw new XrayException(VALIDATION_ERROR, "Input Validation", 
                parameterName + " must be non-negative, got: " + value);
        }
    }
    
    /**
     * Validates that a string matches a specific pattern.
     * @param value The string to validate
     * @param pattern The regex pattern to match
     * @param parameterName The name of the parameter for error messages
     * @param description Description of the expected format
     * @throws XrayException if validation fails
     */
    public static void requirePattern(String value, String pattern, String parameterName, String description) {
        requireNonBlank(value, parameterName);
        if (!value.matches(pattern)) {
            throw new XrayException(VALIDATION_ERROR, "Input Validation", 
                parameterName + " must match pattern: " + description + " (got: " + value + ")");
        }
    }
}

