package com.tonic.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manages a shared execId across all parallel test executions.
 * Ensures only one execId is created for the entire test suite.
 * 
 * @author Tonic Automation Team
 */
public class SharedExecIdManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SharedExecIdManager.class);
    
    // Thread-safe reference to store the shared execId
    private static final AtomicReference<String> sharedExecId = new AtomicReference<>();
    
    // Flag to track if execId has been initialized
    private static volatile boolean isInitialized = false;
    
    // Lock object for synchronization
    private static final Object lock = new Object();
    
    /**
     * Gets or creates the shared execId. Only one execId will be created
     * regardless of how many parallel tests are running.
     * 
     * @return The shared execId
     */
    public static String getOrCreateSharedExecId() {
        String currentExecId = sharedExecId.get();
        
        if (currentExecId != null) {
            logger.debug("Using existing shared execId: {}", currentExecId);
            return currentExecId;
        }
        
        // Synchronize to ensure only one execId is created
        synchronized (lock) {
            // Double-check after acquiring lock
            currentExecId = sharedExecId.get();
            if (currentExecId != null) {
                logger.debug("Using existing shared execId (after lock): {}", currentExecId);
                return currentExecId;
            }
            
            // Create new execId only if not already created
            if (!isInitialized) {
                logger.info("Creating shared execId for parallel test execution");
                String newExecId = XrayLogger.createNewTestExecution();
                
                if (newExecId != null) {
                    sharedExecId.set(newExecId);
                    isInitialized = true;
                    logger.info("Shared execId created successfully: {}", newExecId);
                    return newExecId;
                } else {
                    logger.error("Failed to create shared execId");
                    return null;
                }
            }
        }
        
        return sharedExecId.get();
    }
    
    /**
     * Gets the current shared execId without creating a new one.
     * 
     * @return The current shared execId, or null if not yet created
     */
    public static String getSharedExecId() {
        return sharedExecId.get();
    }
    
    /**
     * Sets the shared execId (useful for external execId injection).
     * 
     * @param execId The execId to set
     */
    public static void setSharedExecId(String execId) {
        if (execId != null && !execId.trim().isEmpty()) {
            sharedExecId.set(execId);
            isInitialized = true;
            logger.info("Shared execId set externally: {}", execId);
        }
    }
    
    /**
     * Checks if the shared execId has been initialized.
     * 
     * @return true if execId is initialized, false otherwise
     */
    public static boolean isInitialized() {
        return isInitialized;
    }
    
    /**
     * Resets the shared execId (useful for testing or cleanup).
     */
    public static void reset() {
        synchronized (lock) {
            sharedExecId.set(null);
            isInitialized = false;
            logger.info("Shared execId reset");
        }
    }
    
    /**
     * Clears the shared execId without resetting the initialization flag.
     */
    public static void clear() {
        sharedExecId.set(null);
        logger.debug("Shared execId cleared");
    }
    
    /**
     * Handles test suite completion and cleanup.
     * This should be called when all tests are finished.
     */
    public static void onTestSuiteComplete() {
        synchronized (lock) {
            String execId = sharedExecId.get();
            if (execId != null) {
                logger.info("Test suite completed. Shared execId used: {}", execId);
            }
            // Reset for next test suite
            reset();
        }
    }
    
    /**
     * Gets execution statistics for reporting.
     * 
     * @return String containing execution statistics
     */
    public static String getExecutionStats() {
        String execId = sharedExecId.get();
        if (execId != null) {
            return String.format("Shared ExecId: %s, Initialized: %s", execId, isInitialized);
        }
        return "No shared execId created";
    }
} 