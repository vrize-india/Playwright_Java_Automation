package com.vrize.listeners;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Aggregates test results across multiple runs including retries.
 * Maintains final status of each test considering all retry attempts.
 */
public class TestResultAggregator {
    
    private static final String RESULTS_FILE = "target/aggregated-results.json";
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    
    // In-memory storage of test results
    private static final ConcurrentHashMap<String, TestResult> aggregatedResults = new ConcurrentHashMap<>();
    
    public static class TestResult {
        public String testKey;
        public String testName;
        public String finalStatus;
        public int totalAttempts;
        public int failedAttempts;
        public String firstFailureMessage;
        public String lastFailureMessage;
        public String screenshotPath;
        public long duration;
        public List<String> tags = new ArrayList<>();
        public long timestamp = System.currentTimeMillis();
        
        public TestResult() {}
        
        public TestResult(String testKey, String testName, String status) {
            this.testKey = testKey;
            this.testName = testName;
            this.finalStatus = status;
            this.totalAttempts = 1;
            this.failedAttempts = status.equals("FAILED") ? 1 : 0;
        }
    }
    
    /**
     * Records a test result, updating the aggregated data
     */
    public static void recordTestResult(String testKey, String testName, String status, 
                                      String errorMessage, String screenshotPath, long duration) {
        aggregatedResults.compute(testKey != null ? testKey : testName, (key, existing) -> {
            if (existing == null) {
                TestResult result = new TestResult(testKey, testName, status);
                result.duration = duration;
                if (status.equals("FAILED")) {
                    result.firstFailureMessage = errorMessage;
                    result.lastFailureMessage = errorMessage;
                    result.screenshotPath = screenshotPath;
                }
                return result;
            } else {
                // Update existing result
                existing.totalAttempts++;
                existing.duration += duration;
                
                if (status.equals("FAILED")) {
                    existing.failedAttempts++;
                    if (existing.firstFailureMessage == null) {
                        existing.firstFailureMessage = errorMessage;
                    }
                    existing.lastFailureMessage = errorMessage;
                    if (screenshotPath != null) {
                        existing.screenshotPath = screenshotPath;
                    }
                    existing.finalStatus = "FAILED";
                } else if (status.equals("PASSED")) {
                    // Only update to PASSED if not already marked as FAILED in final attempt
                    if (existing.totalAttempts <= getMaxRetryCount() + 1) {
                        existing.finalStatus = "PASSED";
                    }
                }
                
                return existing;
            }
        });
        
        // Persist to file after each update
        saveResults();
    }
    
    /**
     * Adds tags to a test result
     */
    public static void addTagsToTest(String testKey, List<String> tags) {
        TestResult result = aggregatedResults.get(testKey);
        if (result != null && tags != null) {
            result.tags.addAll(tags);
            saveResults();
        }
    }
    
    /**
     * Gets the final status of all tests
     */
    public static Map<String, TestResult> getFinalResults() {
        return new HashMap<>(aggregatedResults);
    }
    
    /**
     * Saves results to JSON file
     */
    private static void saveResults() {
        try {
            Files.createDirectories(Paths.get("target"));
            mapper.writeValue(new File(RESULTS_FILE), aggregatedResults);
        } catch (IOException e) {
            System.err.println("[TestResultAggregator] Failed to save results: " + e.getMessage());
        }
    }
    
    /**
     * Loads results from JSON file
     */
    public static void loadResults() {
        File file = new File(RESULTS_FILE);
        if (file.exists()) {
            try {
                Map<String, TestResult> loaded = mapper.readValue(file, 
                    mapper.getTypeFactory().constructMapType(HashMap.class, String.class, TestResult.class));
                aggregatedResults.putAll(loaded);
            } catch (IOException e) {
                System.err.println("[TestResultAggregator] Failed to load results: " + e.getMessage());
            }
        }
    }
    
    /**
     * Clears all results
     */
    public static void clearResults() {
        aggregatedResults.clear();
        try {
            Files.deleteIfExists(Paths.get(RESULTS_FILE));
        } catch (IOException e) {
            // Ignore
        }
    }
    
    /**
     * Gets max retry count from configuration
     */
    private static int getMaxRetryCount() {
        try {
            return com.vrize.utils.ConfigManager.getIntConfigValue("retry.count", "retry.count", 3);
        } catch (Exception e) {
            return 3;
        }
    }
    
    /**
     * Generates a summary report
     */
    public static String generateSummaryReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n=== Test Execution Summary ===\n");
        
        int totalTests = aggregatedResults.size();
        int passedTests = 0;
        int failedTests = 0;
        int retriedTests = 0;
        
        for (TestResult result : aggregatedResults.values()) {
            if (result.finalStatus.equals("PASSED")) {
                passedTests++;
                if (result.totalAttempts > 1) {
                    retriedTests++;
                }
            } else {
                failedTests++;
            }
        }
        
        report.append(String.format("Total Tests: %d\n", totalTests));
        report.append(String.format("Passed: %d (including %d after retries)\n", passedTests, retriedTests));
        report.append(String.format("Failed: %d\n", failedTests));
        report.append("\n=== Detailed Results ===\n");
        
        // Sort by test key/name
        List<TestResult> sortedResults = new ArrayList<>(aggregatedResults.values());
        sortedResults.sort((a, b) -> {
            String keyA = a.testKey != null ? a.testKey : a.testName;
            String keyB = b.testKey != null ? b.testKey : b.testName;
            return keyA.compareTo(keyB);
        });
        
        for (TestResult result : sortedResults) {
            report.append(String.format("\n%s - %s\n", 
                result.testKey != null ? result.testKey : result.testName, 
                result.finalStatus));
            report.append(String.format("  Test Name: %s\n", result.testName));
            report.append(String.format("  Attempts: %d (Failed: %d)\n", 
                result.totalAttempts, result.failedAttempts));
            
            if (result.finalStatus.equals("FAILED")) {
                report.append(String.format("  Error: %s\n", result.lastFailureMessage));
                if (result.screenshotPath != null) {
                    report.append(String.format("  Screenshot: %s\n", result.screenshotPath));
                }
            }
        }
        
        return report.toString();
    }
    
    /**
     * Checks if this is a single-run scenario (no retries)
     */
    public static boolean isSingleRunScenario() {
        try {
            // If no aggregated results file exists, this might be a single run
            if (!Files.exists(Paths.get(RESULTS_FILE))) {
                return true;
            }
            
            // Load the latest results from file
            loadResults();
            
            // Check if we have any results with multiple attempts
            for (TestResult result : aggregatedResults.values()) {
                if (result.totalAttempts > 1) {
                    return false; // This is a retry scenario
                }
            }
            
            return true; // All tests had only one attempt
        } catch (Exception e) {
            return true; // Default to single run if we can't determine
        }
    }
}
