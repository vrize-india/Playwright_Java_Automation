package com.vrize.listeners;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for retry-related functionality.
 * Provides Xray reporting control and retry tracking methods.
 * Can be used by both TestNG retry and native Cucumber retry.
 * 
 * @author Tonic Automation Team
 */
public class RetryUtility {
    
    private static final Logger logger = LoggerFactory.getLogger(RetryUtility.class);
    
    // Thread-safe map to track if Xray reporting should be suppressed for each test/scenario
    private static final ConcurrentHashMap<String, Boolean> suppressXrayReportingMap = new ConcurrentHashMap<>();
    
    // Thread-safe map to track retry attempts for each test/scenario
    private static final ConcurrentHashMap<String, AtomicInteger> retryCountMap = new ConcurrentHashMap<>();
    
    /**
     * Suppresses Xray reporting for a specific test/scenario.
     * This prevents duplicate Xray reports during retry attempts.
     * 
     * @param key Unique identifier for the test/scenario
     */
    public static void suppressXrayReporting(String key) {
        if (key != null && !key.trim().isEmpty()) {
            suppressXrayReportingMap.put(key, true);
            logger.debug("Xray reporting suppressed for: {}", key);
        }
    }
    
    /**
     * Allows Xray reporting for a specific test/scenario.
     * This enables Xray reporting when retries are exhausted or test passes.
     * 
     * @param key Unique identifier for the test/scenario
     */
    public static void allowXrayReporting(String key) {
        if (key != null && !key.trim().isEmpty()) {
            suppressXrayReportingMap.put(key, false);
            logger.debug("Xray reporting allowed for: {}", key);
        }
    }
    
    /**
     * Checks if Xray reporting should be suppressed for a specific test/scenario.
     * 
     * @param key Unique identifier for the test/scenario
     * @return true if Xray reporting should be suppressed, false otherwise
     */
    public static boolean shouldSuppressXrayReporting(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        Boolean suppress = suppressXrayReportingMap.get(key);
        return suppress != null && suppress;
    }
    
    /**
     * Gets the current retry count for a specific test/scenario.
     * 
     * @param key Unique identifier for the test/scenario
     * @return The current retry count, 0 if no retries attempted
     */
    public static int getCurrentRetryCount(String key) {
        if (key == null || key.trim().isEmpty()) {
            return 0;
        }
        AtomicInteger retryCount = retryCountMap.get(key);
        return retryCount != null ? retryCount.get() : 0;
    }
    
    /**
     * Increments the retry count for a specific test/scenario.
     * 
     * @param key Unique identifier for the test/scenario
     * @return The new retry count after increment
     */
    public static int incrementRetryCount(String key) {
        if (key == null || key.trim().isEmpty()) {
            return 0;
        }
        AtomicInteger retryCount = retryCountMap.computeIfAbsent(key, k -> new AtomicInteger(0));
        int newCount = retryCount.incrementAndGet();
        logger.debug("Retry count incremented for {}: {}", key, newCount);
        return newCount;
    }
    
    /**
     * Sets the retry count for a specific test/scenario.
     * 
     * @param key Unique identifier for the test/scenario
     * @param count The retry count to set
     */
    public static void setRetryCount(String key, int count) {
        if (key != null && !key.trim().isEmpty() && count >= 0) {
            retryCountMap.put(key, new AtomicInteger(count));
            logger.debug("Retry count set for {}: {}", key, count);
        }
    }
    
    /**
     * Clears retry data for a specific test/scenario.
     * 
     * @param key Unique identifier for the test/scenario
     */
    public static void clearRetryData(String key) {
        if (key != null && !key.trim().isEmpty()) {
            retryCountMap.remove(key);
            suppressXrayReportingMap.remove(key);
            logger.debug("Retry data cleared for: {}", key);
        }
    }
    
    /**
     * Clears all retry data for all tests/scenarios.
     * Useful for cleanup between test runs.
     */
    public static void clearAllRetryData() {
        int retryCount = retryCountMap.size();
        int suppressCount = suppressXrayReportingMap.size();
        
        retryCountMap.clear();
        suppressXrayReportingMap.clear();
        
        logger.info("Cleared all retry data: {} retry entries, {} suppress entries", retryCount, suppressCount);
    }
    
    /**
     * Gets the total number of tests/scenarios currently being tracked for retries.
     * 
     * @return The number of tracked tests/scenarios
     */
    public static int getTrackedTestCount() {
        return retryCountMap.size();
    }
    
    /**
     * Gets the total number of tests/scenarios with suppressed Xray reporting.
     * 
     * @return The number of tests/scenarios with suppressed Xray reporting
     */
    public static int getSuppressedXrayCount() {
        return (int) suppressXrayReportingMap.values().stream()
                .filter(suppress -> suppress != null && suppress)
                .count();
    }
    
    /**
     * Checks if a test/scenario is currently being tracked for retries.
     * 
     * @param key Unique identifier for the test/scenario
     * @return true if the test/scenario is being tracked, false otherwise
     */
    public static boolean isTracked(String key) {
        return key != null && !key.trim().isEmpty() && retryCountMap.containsKey(key);
    }
    
    /**
     * Gets a summary of retry statistics.
     * 
     * @return A string containing retry statistics
     */
    public static String getRetryStatistics() {
        int totalTracked = getTrackedTestCount();
        int suppressedXray = getSuppressedXrayCount();
        
        StringBuilder stats = new StringBuilder();
        stats.append("Retry Statistics:\n");
        stats.append("  Total Tracked Tests: ").append(totalTracked).append("\n");
        stats.append("  Suppressed Xray Reports: ").append(suppressedXray).append("\n");
        
        if (totalTracked > 0) {
            stats.append("  Tracked Tests:\n");
            retryCountMap.forEach((key, count) -> {
                stats.append("    ").append(key).append(": ").append(count.get()).append(" retries\n");
            });
        }
        
        return stats.toString();
    }
    
    /**
     * Resets retry count for a specific test/scenario to 0.
     * 
     * @param key Unique identifier for the test/scenario
     */
    public static void resetRetryCount(String key) {
        if (key != null && !key.trim().isEmpty()) {
            retryCountMap.put(key, new AtomicInteger(0));
            logger.debug("Retry count reset for {}: 0", key);
        }
    }
    
    /**
     * Checks if retry is in progress for a specific test/scenario.
     * 
     * @param key Unique identifier for the test/scenario
     * @return true if retry is in progress, false otherwise
     */
    public static boolean isRetryInProgress(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        AtomicInteger retryCount = retryCountMap.get(key);
        return retryCount != null && retryCount.get() > 0;
    }
}
