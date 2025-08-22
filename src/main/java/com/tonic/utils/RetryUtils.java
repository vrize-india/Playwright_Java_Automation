package com.tonic.utils;

import com.tonic.annotations.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Utility class for retry-related operations and configuration.
 * Provides helper methods for working with retry settings and annotations.
 * 
 * @author Tonic Automation Team
 */
public final class RetryUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);
    
    private RetryUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets the retry count for a specific test method.
     * Priority: @Retry annotation on method > @Retry annotation on class > global configuration
     * 
     * @param testMethod The test method
     * @return The retry count for the test method
     */
    public static int getRetryCount(Method testMethod) {
        try {
            // Check for @Retry annotation on the method
            if (testMethod.isAnnotationPresent(Retry.class)) {
                Retry retryAnnotation = testMethod.getAnnotation(Retry.class);
                return retryAnnotation.value();
            }
            
            // Check for @Retry annotation on the class
            Class<?> testClass = testMethod.getDeclaringClass();
            if (testClass.isAnnotationPresent(Retry.class)) {
                Retry retryAnnotation = testClass.getAnnotation(Retry.class);
                return retryAnnotation.value();
            }
            
            // If no annotation found, use global configuration
            return ConfigManager.getIntConfigValue("retry.count", "retry.count", 3);
        } catch (Exception e) {
            logger.warn("Error getting retry count for method: {}, using default", testMethod.getName(), e);
            return 3;
        }
    }
    
    /**
     * Checks if retry is enabled for a specific test method.
     * Priority: @Retry annotation on method > @Retry annotation on class > global configuration
     * 
     * @param testMethod The test method
     * @return true if retry is enabled for the test method, false otherwise
     */
    public static boolean isRetryEnabled(Method testMethod) {
        try {
            // Check for @Retry annotation on the method
            if (testMethod.isAnnotationPresent(Retry.class)) {
                Retry retryAnnotation = testMethod.getAnnotation(Retry.class);
                return retryAnnotation.enabled();
            }
            
            // Check for @Retry annotation on the class
            Class<?> testClass = testMethod.getDeclaringClass();
            if (testClass.isAnnotationPresent(Retry.class)) {
                Retry retryAnnotation = testClass.getAnnotation(Retry.class);
                return retryAnnotation.enabled();
            }
            
            // If no annotation found, use global configuration
            return ConfigManager.getBooleanConfigValue("retry.enabled", "retry.enabled", true);
        } catch (Exception e) {
            logger.warn("Error checking retry enabled for method: {}, using default", testMethod.getName(), e);
            return true;
        }
    }
    
    /**
     * Gets the global retry count from configuration.
     * 
     * @return The global retry count
     */
    public static int getGlobalRetryCount() {
        try {
            return ConfigManager.getIntConfigValue("retry.count", "retry.count", 3);
        } catch (Exception e) {
            logger.warn("Error reading global retry count, using default: 3", e);
            return 3;
        }
    }
    
    /**
     * Checks if retry is globally enabled from configuration.
     * 
     * @return true if retry is globally enabled, false otherwise
     */
    public static boolean isGlobalRetryEnabled() {
        try {
            return ConfigManager.getBooleanConfigValue("retry.enabled", "retry.enabled", true);
        } catch (Exception e) {
            logger.warn("Error reading global retry enabled setting, using default: true", e);
            return true;
        }
    }
    
    /**
     * Sets the global retry count via system property.
     * 
     * @param retryCount The retry count to set
     */
    public static void setGlobalRetryCount(int retryCount) {
        if (retryCount < 0) {
            logger.warn("Invalid retry count: {}. Must be >= 0. Using 0.", retryCount);
            retryCount = 0;
        }
        System.setProperty("retry.count", String.valueOf(retryCount));
        logger.info("Global retry count set to: {}", retryCount);
    }
    
    /**
     * Sets the global retry enabled setting via system property.
     * 
     * @param enabled Whether retry should be enabled
     */
    public static void setGlobalRetryEnabled(boolean enabled) {
        System.setProperty("retry.enabled", String.valueOf(enabled));
        logger.info("Global retry enabled set to: {}", enabled);
    }
    
    /**
     * Gets the retry configuration summary for a test method.
     * 
     * @param testMethod The test method
     * @return A string describing the retry configuration for the method
     */
    public static String getRetryConfigSummary(Method testMethod) {
        try {
            StringBuilder summary = new StringBuilder();
            summary.append("Retry config for ").append(testMethod.getName()).append(": ");
            
            if (testMethod.isAnnotationPresent(Retry.class)) {
                Retry retryAnnotation = testMethod.getAnnotation(Retry.class);
                summary.append("Method annotation - Count: ").append(retryAnnotation.value())
                       .append(", Enabled: ").append(retryAnnotation.enabled());
            } else if (testMethod.getDeclaringClass().isAnnotationPresent(Retry.class)) {
                Retry retryAnnotation = testMethod.getDeclaringClass().getAnnotation(Retry.class);
                summary.append("Class annotation - Count: ").append(retryAnnotation.value())
                       .append(", Enabled: ").append(retryAnnotation.enabled());
            } else {
                summary.append("Global config - Count: ").append(getGlobalRetryCount())
                       .append(", Enabled: ").append(isGlobalRetryEnabled());
            }
            
            return summary.toString();
        } catch (Exception e) {
            logger.warn("Error getting retry config summary for method: {}", testMethod.getName(), e);
            return "Retry config: Error reading configuration";
        }
    }
    
    /**
     * Validates retry configuration settings.
     * 
     * @return true if configuration is valid, false otherwise
     */
    public static boolean validateRetryConfiguration() {
        try {
            int retryCount = getGlobalRetryCount();
            boolean retryEnabled = isGlobalRetryEnabled();
            
            if (retryCount < 0) {
                logger.error("Invalid retry count: {}. Must be >= 0.", retryCount);
                return false;
            }
            
            if (retryEnabled && retryCount == 0) {
                logger.warn("Retry is enabled but retry count is 0. Tests will not be retried.");
            }
            
            logger.info("Retry configuration validation passed - Enabled: {}, Count: {}", retryEnabled, retryCount);
            return true;
        } catch (Exception e) {
            logger.error("Error validating retry configuration", e);
            return false;
        }
    }
} 