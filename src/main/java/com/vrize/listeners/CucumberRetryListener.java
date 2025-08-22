package com.vrize.listeners;

import com.vrize.utils.ConfigManager;
import com.vrize.listeners.RetryUtility;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Enhanced Cucumber retry listener that combines immediate retry detection with advanced test run lifecycle management.
 * This listener provides comprehensive retry functionality including:
 * - Immediate retry detection and tracking
 * - Advanced test run state management
 * - System property management for integration
 * - Enhanced configuration options
 * - User guidance and instructions
 * 
 * @author Tonic Automation Team
 */
public class CucumberRetryListener implements ConcurrentEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(CucumberRetryListener.class);
    
    // Thread-safe maps to track retry attempts and failed scenarios
    private static final ConcurrentHashMap<String, AtomicInteger> retryCountMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> failedScenarios = new ConcurrentHashMap<>();
    
    // List to track failed scenarios for rerun (immediate mode)
    private static final List<String> failedScenariosImmediate = new ArrayList<>();
    
    // Configuration keys
    private static final String RETRY_ENABLED_KEY = "retry.enabled";
    private static final String RETRY_COUNT_KEY = "retry.count";
    private static final String RERUN_FILE_PATH_KEY = "cucumber.rerun.file";
    private static final String IMMEDIATE_RERUN_KEY = "retry.immediate.rerun";
    
    // Default values
    private static final boolean DEFAULT_RETRY_ENABLED = true;
    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final String DEFAULT_RERUN_FILE_PATH = "target/rerun.txt";
    private static final boolean DEFAULT_IMMEDIATE_RERUN = true;
    
    // Test run state
    private static volatile boolean isRetryRun = false;
    private static volatile int currentRetryAttempt = 0;
    private static final List<String> rerunScenarios = new ArrayList<>();
    
    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestRunStarted.class, this::handleTestRunStarted);
        publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
        publisher.registerHandlerFor(TestRunFinished.class, this::handleTestRunFinished);
    }
    
    /**
     * Handles test run lifecycle events for advanced retry management.
     */
    private void handleTestRunStarted(TestRunStarted event) {
        logger.info("Cucumber test run started with enhanced retry configuration - Enabled: {}, Max Retries: {}, Immediate Rerun: {}",
                   isRetryEnabled(), getMaxRetryCount(), isImmediateRerunEnabled());

        if (isRetryRun) {
            currentRetryAttempt++;
            logger.info("Starting retry attempt #{} for failed scenarios", currentRetryAttempt);
            
            // Set system properties to indicate this is a retry run
            System.setProperty("cucumber.rerun.file", getRerunFilePath());
            System.setProperty("cucumber.retry.attempt", String.valueOf(currentRetryAttempt));
            System.setProperty("cucumber.retry.enabled", "true");
        } else {
            // Reset state for new test run
            retryCountMap.clear();
            failedScenarios.clear();
            failedScenariosImmediate.clear();
            rerunScenarios.clear();
            currentRetryAttempt = 0;
            
            // Clear retry indicators
            System.clearProperty("cucumber.rerun.file");
            System.clearProperty("cucumber.retry.attempt");
            System.clearProperty("cucumber.retry.enabled");
        }
    }
    
    /**
     * Handles individual test case start events for retry tracking.
     */
    private void handleTestCaseStarted(TestCaseStarted event) {
        String scenarioKey = getScenarioKey(event);
        String scenarioName = event.getTestCase().getName();
        
        if (!retryCountMap.containsKey(scenarioKey)) {
            // First time running this scenario
            retryCountMap.put(scenarioKey, new AtomicInteger(0));
            logger.debug("Cucumber scenario started: {} - Initial retry count set to 0", scenarioName);
        } else {
            // This is a retry attempt
            int currentRetryCount = retryCountMap.get(scenarioKey).get();
            logger.info("Cucumber scenario retry started: {} - Retry attempt {}", scenarioName, currentRetryCount);
            
            // Suppress Xray reporting for retry attempts
            RetryUtility.suppressXrayReporting(scenarioKey);
        }
    }
    
    /**
     * Handles individual test case finish events with comprehensive retry logic.
     */
    private void handleTestCaseFinished(TestCaseFinished event) {
        String scenarioKey = getScenarioKey(event);
        String scenarioName = event.getTestCase().getName();
        String scenarioLocation = getScenarioLocation(event);
        AtomicInteger retryCount = retryCountMap.get(scenarioKey);
        int attempts = retryCount != null ? retryCount.get() : 0;
        
        if (event.getResult().getStatus().name().equals("FAILED")) {
            if (attempts < getMaxRetryCount() && isRetryEnabled()) {
                // Scenario failed but can be retried
                retryCount.incrementAndGet();
                logger.info("Cucumber scenario FAILED (attempt {}/{}): {} - Will be retried", 
                    attempts + 1, getMaxRetryCount() + 1, scenarioName);
                
                // Suppress Xray reporting for retry attempts
                RetryUtility.suppressXrayReporting(scenarioKey);
                
                // Add to failed scenarios tracking
                failedScenarios.put(scenarioKey, scenarioLocation);
                rerunScenarios.add(scenarioLocation);
                
                // Handle immediate rerun file creation if enabled
                if (isImmediateRerunEnabled()) {
                    if (scenarioLocation != null && !failedScenariosImmediate.contains(scenarioLocation)) {
                        failedScenariosImmediate.add(scenarioLocation);
                        logger.info("Added scenario to immediate rerun list: {}", scenarioLocation);
                    }
                    // Create rerun file immediately
                    createRerunFile(failedScenariosImmediate, "immediate");
                }
                
            } else {
                // Final failure after all retries exhausted
                logger.error("Cucumber scenario FAILED after {} retry attempts: {}", attempts, scenarioName);
                retryCountMap.remove(scenarioKey);
                failedScenarios.remove(scenarioKey);
                
                // Allow Xray reporting for final failure
                RetryUtility.allowXrayReporting(scenarioKey);
            }
        } else if (event.getResult().getStatus().name().equals("PASSED")) {
            // Scenario passed
            if (attempts > 0) {
                logger.info("Cucumber scenario PASSED after {} retry attempts: {}", attempts, scenarioName);
            } else {
                logger.debug("Cucumber scenario PASSED on first attempt: {}", scenarioName);
            }
            
            // Clean up tracking data
            retryCountMap.remove(scenarioKey);
            failedScenarios.remove(scenarioKey);
            
            // Allow Xray reporting for successful scenarios
            RetryUtility.allowXrayReporting(scenarioKey);
        }
    }
    
    /**
     * Handles test run completion with comprehensive cleanup and rerun file creation.
     */
    private void handleTestRunFinished(TestRunFinished event) {
        if (!isRetryRun && !rerunScenarios.isEmpty() && isRetryEnabled()) {
            // First run finished with failures - create comprehensive rerun file
            createRerunFile(rerunScenarios, "comprehensive");
            
            logger.info("Test run finished. {} scenarios failed and added to rerun file: {}", 
                       rerunScenarios.size(), getRerunFilePath());
            
            // Provide comprehensive retry instructions
            logger.info("=== RETRY INSTRUCTIONS ===");
            logger.info("To retry failed scenarios using shell script:");
            logger.info("  ./run-native-cucumber-retry.sh \"mvn test\"");
            logger.info("To retry failed scenarios using Maven directly:");
            logger.info("  mvn test -Dcucumber.features=@{}", getRerunFilePath());
            logger.info("To retry with custom parameters:");
            logger.info("  mvn test -Dcucumber.features=@{} -Dbrowser=firefox -Dxray.enabled=true", getRerunFilePath());
            logger.info("========================");
            
        } else {
            // Final run finished
            logger.info("Test run finished. Final results:");
            logger.info("- Failed scenarios: {}", failedScenarios.size());
            logger.info("- Total retry attempts: {}", currentRetryAttempt);
            
            // Clean up all tracking data
            clearRetryData();
        }
    }
    
    /**
     * Creates a rerun file with the specified scenarios.
     */
    private void createRerunFile(List<String> scenarios, String mode) {
        if (scenarios.isEmpty()) {
            return;
        }
        
        try {
            Path rerunPath = Paths.get(getRerunFilePath());
            Files.createDirectories(rerunPath.getParent());
            
            try (FileWriter writer = new FileWriter(rerunPath.toFile())) {
                for (String scenario : scenarios) {
                    writer.write(scenario + "\n");
                }
            }
            
            logger.info("Created {} rerun file with {} failed scenarios: {}", mode, scenarios.size(), rerunPath);
        } catch (IOException e) {
            logger.error("Failed to create {} rerun file", mode, e);
        }
    }
    
    /**
     * Generates a unique key for a Cucumber scenario to track retry attempts.
     */
    private String getScenarioKey(TestCaseStarted event) {
        return event.getTestCase().getName() + "_" + event.getTestCase().getUri() + "_" + event.getTestCase().getLine();
    }
    
    /**
     * Generates a unique key for a Cucumber scenario to track retry attempts.
     */
    private String getScenarioKey(TestCaseFinished event) {
        return event.getTestCase().getName() + "_" + event.getTestCase().getUri() + "_" + event.getTestCase().getLine();
    }
    
    /**
     * Gets the scenario location for rerun file in Cucumber format (file:line).
     */
    private String getScenarioLocation(TestCaseFinished event) {
        try {
            String uri = event.getTestCase().getUri().toString();
            int line = event.getTestCase().getLine();
            
            // Convert URI to relative path if needed
            if (uri.startsWith("file:")) {
                uri = uri.substring(5); // Remove "file:" prefix
            }
            
            return uri + ":" + line;
        } catch (Exception e) {
            logger.warn("Could not get scenario location for rerun", e);
            return null;
        }
    }
    
    /**
     * Checks if retry functionality is enabled via configuration.
     */
    private boolean isRetryEnabled() {
        try {
            return ConfigManager.getBooleanConfigValue("retry.enabled", RETRY_ENABLED_KEY, DEFAULT_RETRY_ENABLED);
        } catch (Exception e) {
            logger.warn("Error reading retry.enabled configuration, using default: {}", DEFAULT_RETRY_ENABLED, e);
            return DEFAULT_RETRY_ENABLED;
        }
    }
    
    /**
     * Gets the maximum number of retry attempts from configuration.
     */
    private int getMaxRetryCount() {
        try {
            return ConfigManager.getIntConfigValue("retry.count", RETRY_COUNT_KEY, DEFAULT_RETRY_COUNT);
        } catch (Exception e) {
            logger.warn("Error reading retry.count configuration, using default: {}", DEFAULT_RETRY_COUNT, e);
            return DEFAULT_RETRY_COUNT;
        }
    }
    
    /**
     * Checks if immediate rerun file creation is enabled.
     */
    private boolean isImmediateRerunEnabled() {
        try {
            return ConfigManager.getBooleanConfigValue("retry.immediate.rerun", IMMEDIATE_RERUN_KEY, DEFAULT_IMMEDIATE_RERUN);
        } catch (Exception e) {
            logger.warn("Error reading retry.immediate.rerun configuration, using default: {}", DEFAULT_IMMEDIATE_RERUN, e);
            return DEFAULT_IMMEDIATE_RERUN;
        }
    }
    
    /**
     * Gets the rerun file path from configuration.
     */
    private String getRerunFilePath() {
        try {
            return ConfigManager.getConfigValue("cucumber.rerun.file", RERUN_FILE_PATH_KEY, DEFAULT_RERUN_FILE_PATH);
        } catch (Exception e) {
            logger.warn("Error reading cucumber.rerun.file configuration, using default: {}", DEFAULT_RERUN_FILE_PATH, e);
            return DEFAULT_RERUN_FILE_PATH;
        }
    }
    
    /**
     * Gets the current retry count for a specific scenario.
     */
    public static int getCurrentRetryCount(String scenarioKey) {
        AtomicInteger retryCount = retryCountMap.get(scenarioKey);
        return retryCount != null ? retryCount.get() : 0;
    }
    
    /**
     * Gets the list of failed scenarios.
     */
    public static ConcurrentHashMap<String, String> getFailedScenarios() {
        return new ConcurrentHashMap<>(failedScenarios);
    }
    
    /**
     * Gets the list of rerun scenarios.
     */
    public static List<String> getRerunScenarios() {
        return new ArrayList<>(rerunScenarios);
    }
    
    /**
     * Checks if a rerun file exists.
     */
    public static boolean hasRerunFile() {
        String rerunFilePath = getRerunFilePathStatic();
        return new File(rerunFilePath).exists();
    }
    
    /**
     * Gets the rerun file path (static method).
     */
    private static String getRerunFilePathStatic() {
        try {
            return ConfigManager.getConfigValue("cucumber.rerun.file", "cucumber.rerun.file", DEFAULT_RERUN_FILE_PATH);
        } catch (Exception e) {
            return DEFAULT_RERUN_FILE_PATH;
        }
    }
    
    /**
     * Clears all retry data. Useful for testing or manual cleanup.
     */
    public static void clearRetryData() {
        retryCountMap.clear();
        failedScenarios.clear();
        failedScenariosImmediate.clear();
        rerunScenarios.clear();
        isRetryRun = false;
        currentRetryAttempt = 0;
    }
} 