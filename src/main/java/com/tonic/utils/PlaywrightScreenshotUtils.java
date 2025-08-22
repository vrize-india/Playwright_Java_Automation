package com.tonic.utils;

import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for taking screenshots using Playwright with dynamic naming and path management.
 * Supports test-specific naming, timestamp-based naming, and custom directory structures.
 * Uses System.currentTimeMillis() for precise timestamps and includes testName for better traceability.
 * Ensures target/ and screenshots/ directories exist to prevent runtime failures.
 * Supports Base64 encoding for reporting frameworks that prefer embedded screenshots.
 * Implements defensive coding with null checks to prevent NullPointerExceptions.
 * Enhanced logging and exception handling for better diagnostics during screenshot issues.
 * 
 * @author Gaurav Purwar
 */
public class PlaywrightScreenshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(PlaywrightScreenshotUtils.class);
    private static final String DEFAULT_SCREENSHOTS_DIR = System.getProperty("user.dir") + "/screenshots/";
    private static final String TARGET_DIR = System.getProperty("user.dir") + "/target/";
    private static final String TARGET_SCREENSHOTS_DIR = TARGET_DIR + "screenshots/";
    private static final String DEFAULT_FILENAME_PREFIX = "screenshot";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    // Configuration for exception handling
    private static final boolean THROW_EXCEPTIONS_ON_FAILURE = Boolean.parseBoolean(
        System.getProperty("screenshot.throw.exceptions", "false"));
    
    // Static initialization to ensure directories exist
    static {
        ensureDirectoriesExist();
    }
    
    private PlaywrightScreenshotUtils() {}

    /**
     * Validates that the page parameter is not null.
     * Implements defensive coding to prevent NullPointerExceptions.
     * 
     * @param page Playwright page instance to validate
     * @param methodName Name of the calling method for logging purposes
     * @return true if page is valid, false if null
     */
    private static boolean validatePage(Page page, String methodName) {
        if (page == null) {
            String errorMsg = String.format("Page parameter is null in method: %s. Cannot take screenshot.", methodName);
            logger.error(errorMsg);
            
            if (THROW_EXCEPTIONS_ON_FAILURE) {
                throw new IllegalArgumentException(errorMsg);
            }
            return false;
        }
        return true;
    }

    /**
     * Handles screenshot failures with enhanced logging and optional exception throwing.
     * 
     * @param errorMessage The error message to log
     * @param exception The exception that occurred (can be null)
     * @param context Additional context information
     */
    private static void handleScreenshotFailure(String errorMessage, Exception exception, String context) {
        String fullErrorMessage = String.format("Screenshot failure - %s. Context: %s", errorMessage, context);
        
        if (exception != null) {
            logger.error(fullErrorMessage, exception);
            
            if (THROW_EXCEPTIONS_ON_FAILURE) {
                throw new RuntimeException(fullErrorMessage, exception);
            }
        } else {
            logger.error(fullErrorMessage);
            
            if (THROW_EXCEPTIONS_ON_FAILURE) {
                throw new RuntimeException(fullErrorMessage);
            }
        }
    }

    /**
     * Logs successful screenshot operations with detailed information.
     * 
     * @param screenshotPath Path where screenshot was saved
     * @param screenshotSize Size of the screenshot in bytes
     * @param methodName Name of the method that took the screenshot
     */
    private static void logScreenshotSuccess(Path screenshotPath, int screenshotSize, String methodName) {
        logger.info("Screenshot taken successfully - Method: {}, Path: {}, Size: {} bytes", 
                   methodName, screenshotPath, screenshotSize);
    }

    /**
     * Ensures that all required directories exist to prevent runtime failures.
     * Creates target/, screenshots/, and target/screenshots/ directories if they don't exist.
     */
    private static void ensureDirectoriesExist() {
        try {
            // Ensure target directory exists
            createDirectoryIfNotExists(TARGET_DIR);
            
            // Ensure screenshots directory exists
            createDirectoryIfNotExists(DEFAULT_SCREENSHOTS_DIR);
            
            // Ensure target/screenshots directory exists
            createDirectoryIfNotExists(TARGET_SCREENSHOTS_DIR);
            
            logger.debug("All required directories verified/created successfully");
        } catch (Exception e) {
            String errorMsg = "Failed to ensure directories exist during initialization";
            logger.error(errorMsg, e);
            
            if (THROW_EXCEPTIONS_ON_FAILURE) {
                throw new RuntimeException(errorMsg, e);
            }
        }
    }

    /**
     * Takes a screenshot with a custom name and saves it to the default screenshots directory.
     * 
     * @param page Playwright page instance
     * @param name Custom name for the screenshot (will be sanitized)
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshot(Page page, String name) {
        if (!validatePage(page, "takeScreenshot(Page, String)")) {
            return new byte[0];
        }
        logger.debug("Taking screenshot with name: '{}'", name);
        return takeScreenshot(page, name, DEFAULT_SCREENSHOTS_DIR);
    }

    /**
     * Takes a screenshot with a custom name and saves it to the default screenshots directory.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param name Custom name for the screenshot (will be sanitized)
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotAsBase64(Page page, String name) {
        if (!validatePage(page, "takeScreenshotAsBase64(Page, String)")) {
            return "";
        }
        logger.debug("Taking Base64 screenshot with name: '{}'", name);
        byte[] screenshot = takeScreenshot(page, name, DEFAULT_SCREENSHOTS_DIR);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot with a custom name and saves it to a specified directory.
     * 
     * @param page Playwright page instance
     * @param name Custom name for the screenshot (will be sanitized)
     * @param directory Directory path where to save the screenshot
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshot(Page page, String name, String directory) {
        if (!validatePage(page, "takeScreenshot(Page, String, String)")) {
            return new byte[0];
        }
        
        String methodName = "takeScreenshot(Page, String, String)";
        logger.debug("Taking screenshot - Name: '{}', Directory: '{}'", name, directory);
        
        try {
            // Ensure directory exists before taking screenshot
            createDirectoryIfNotExists(directory);
            
            // Sanitize the name to remove invalid characters
            String sanitizedName = sanitizeFilename(name);
            logger.debug("Sanitized filename: '{}' -> '{}'", name, sanitizedName);
            
            // Create unique filename with System.currentTimeMillis() for precise timestamp and UUID
            long currentTimeMillis = System.currentTimeMillis();
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            String filename = String.format("%s_%s_%d_%s.png", sanitizedName, timestamp, currentTimeMillis, uniqueId);
            
            // Create the full path
            Path screenshotPath = Paths.get(directory, filename);
            logger.debug("Screenshot path: {}", screenshotPath);
            
            // Take screenshot
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));
            
            logScreenshotSuccess(screenshotPath, screenshot.length, methodName);
            return screenshot;
            
        } catch (Exception e) {
            String context = String.format("Name: '%s', Directory: '%s'", name, directory);
            handleScreenshotFailure("Failed to take screenshot", e, context);
            return new byte[0];
        }
    }

    /**
     * Takes a screenshot with a custom name and saves it to a specified directory.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param name Custom name for the screenshot (will be sanitized)
     * @param directory Directory path where to save the screenshot
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotAsBase64(Page page, String name, String directory) {
        if (!validatePage(page, "takeScreenshotAsBase64(Page, String, String)")) {
            return "";
        }
        logger.debug("Taking Base64 screenshot - Name: '{}', Directory: '{}'", name, directory);
        byte[] screenshot = takeScreenshot(page, name, directory);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot with timestamp-based naming (backward compatibility).
     * 
     * @param page Playwright page instance
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshot(Page page) {
        if (!validatePage(page, "takeScreenshot(Page)")) {
            return new byte[0];
        }
        long currentTimeMillis = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String screenshotName = DEFAULT_FILENAME_PREFIX + "_" + timestamp + "_" + currentTimeMillis;
        logger.debug("Taking timestamp-based screenshot: '{}'", screenshotName);
        return takeScreenshot(page, screenshotName);
    }

    /**
     * Takes a screenshot with timestamp-based naming and returns Base64 encoded string.
     * 
     * @param page Playwright page instance
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotAsBase64(Page page) {
        if (!validatePage(page, "takeScreenshotAsBase64(Page)")) {
            return "";
        }
        logger.debug("Taking timestamp-based Base64 screenshot");
        byte[] screenshot = takeScreenshot(page);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot for a specific test scenario with test-specific naming.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario
     * @param stepName Name of the test step (optional)
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotForTest(Page page, String testName, String stepName) {
        if (!validatePage(page, "takeScreenshotForTest(Page, String, String)")) {
            return new byte[0];
        }
        String screenshotName = stepName != null && !stepName.trim().isEmpty() 
            ? String.format("%s_%s", testName, stepName)
            : testName;
        logger.debug("Taking test-specific screenshot - Test: '{}', Step: '{}', Name: '{}'", 
                    testName, stepName, screenshotName);
        return takeScreenshot(page, screenshotName);
    }

    /**
     * Takes a screenshot for a specific test scenario with test-specific naming.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario
     * @param stepName Name of the test step (optional)
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotForTestAsBase64(Page page, String testName, String stepName) {
        if (!validatePage(page, "takeScreenshotForTestAsBase64(Page, String, String)")) {
            return "";
        }
        logger.debug("Taking test-specific Base64 screenshot - Test: '{}', Step: '{}'", testName, stepName);
        byte[] screenshot = takeScreenshotForTest(page, testName, stepName);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot for a specific test scenario with test-specific naming.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotForTest(Page page, String testName) {
        if (!validatePage(page, "takeScreenshotForTest(Page, String)")) {
            return new byte[0];
        }
        logger.debug("Taking test-specific screenshot - Test: '{}'", testName);
        return takeScreenshotForTest(page, testName, null);
    }

    /**
     * Takes a screenshot for a specific test scenario with test-specific naming.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotForTestAsBase64(Page page, String testName) {
        if (!validatePage(page, "takeScreenshotForTestAsBase64(Page, String)")) {
            return "";
        }
        logger.debug("Taking test-specific Base64 screenshot - Test: '{}'", testName);
        byte[] screenshot = takeScreenshotForTest(page, testName);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot with custom directory structure (e.g., organized by date).
     * 
     * @param page Playwright page instance
     * @param name Custom name for the screenshot
     * @param subDirectory Subdirectory within the screenshots directory
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotInSubdirectory(Page page, String name, String subDirectory) {
        if (!validatePage(page, "takeScreenshotInSubdirectory(Page, String, String)")) {
            return new byte[0];
        }
        String fullDirectory = DEFAULT_SCREENSHOTS_DIR + subDirectory + "/";
        logger.debug("Taking subdirectory screenshot - Name: '{}', Subdirectory: '{}', Full path: '{}'", 
                    name, subDirectory, fullDirectory);
        return takeScreenshot(page, name, fullDirectory);
    }

    /**
     * Takes a screenshot with custom directory structure and returns Base64 encoded string.
     * 
     * @param page Playwright page instance
     * @param name Custom name for the screenshot
     * @param subDirectory Subdirectory within the screenshots directory
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotInSubdirectoryAsBase64(Page page, String name, String subDirectory) {
        if (!validatePage(page, "takeScreenshotInSubdirectoryAsBase64(Page, String, String)")) {
            return "";
        }
        logger.debug("Taking subdirectory Base64 screenshot - Name: '{}', Subdirectory: '{}'", name, subDirectory);
        byte[] screenshot = takeScreenshotInSubdirectory(page, name, subDirectory);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot and saves it to the target/screenshots directory.
     * Useful for Maven builds where target/ is the standard output directory.
     * 
     * @param page Playwright page instance
     * @param name Custom name for the screenshot
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotToTarget(Page page, String name) {
        if (!validatePage(page, "takeScreenshotToTarget(Page, String)")) {
            return new byte[0];
        }
        logger.debug("Taking target screenshot - Name: '{}', Target directory: '{}'", name, TARGET_SCREENSHOTS_DIR);
        return takeScreenshot(page, name, TARGET_SCREENSHOTS_DIR);
    }

    /**
     * Takes a screenshot and saves it to the target/screenshots directory.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param name Custom name for the screenshot
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotToTargetAsBase64(Page page, String name) {
        if (!validatePage(page, "takeScreenshotToTargetAsBase64(Page, String)")) {
            return "";
        }
        logger.debug("Taking target Base64 screenshot - Name: '{}'", name);
        byte[] screenshot = takeScreenshotToTarget(page, name);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot and saves it to the target/screenshots directory with test-specific naming.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario
     * @param stepName Name of the test step (optional)
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotToTargetForTest(Page page, String testName, String stepName) {
        if (!validatePage(page, "takeScreenshotToTargetForTest(Page, String, String)")) {
            return new byte[0];
        }
        String screenshotName = stepName != null && !stepName.trim().isEmpty() 
            ? String.format("%s_%s", testName, stepName)
            : testName;
        logger.debug("Taking target test-specific screenshot - Test: '{}', Step: '{}', Name: '{}'", 
                    testName, stepName, screenshotName);
        return takeScreenshotToTarget(page, screenshotName);
    }

    /**
     * Takes a screenshot and saves it to the target/screenshots directory with test-specific naming.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario
     * @param stepName Name of the test step (optional)
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotToTargetForTestAsBase64(Page page, String testName, String stepName) {
        if (!validatePage(page, "takeScreenshotToTargetForTestAsBase64(Page, String, String)")) {
            return "";
        }
        logger.debug("Taking target test-specific Base64 screenshot - Test: '{}', Step: '{}'", testName, stepName);
        byte[] screenshot = takeScreenshotToTargetForTest(page, testName, stepName);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot and saves it to the target/screenshots directory with test-specific naming.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotToTargetForTest(Page page, String testName) {
        if (!validatePage(page, "takeScreenshotToTargetForTest(Page, String)")) {
            return new byte[0];
        }
        logger.debug("Taking target test-specific screenshot - Test: '{}'", testName);
        return takeScreenshotToTargetForTest(page, testName, null);
    }

    /**
     * Takes a screenshot and saves it to the target/screenshots directory with test-specific naming.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotToTargetForTestAsBase64(Page page, String testName) {
        if (!validatePage(page, "takeScreenshotToTargetForTestAsBase64(Page, String)")) {
            return "";
        }
        logger.debug("Taking target test-specific Base64 screenshot - Test: '{}'", testName);
        byte[] screenshot = takeScreenshotToTargetForTest(page, testName);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot and returns only the byte array without saving to file.
     * Useful when you only need the screenshot data for reporting.
     * 
     * @param page Playwright page instance
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotBytesOnly(Page page) {
        if (!validatePage(page, "takeScreenshotBytesOnly(Page)")) {
            return new byte[0];
        }
        
        String methodName = "takeScreenshotBytesOnly(Page)";
        logger.debug("Taking screenshot (bytes only)");
        
        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true));
            
            logger.debug("Screenshot taken successfully (bytes only) - Size: {} bytes", screenshot.length);
            return screenshot;
        } catch (Exception e) {
            handleScreenshotFailure("Failed to take screenshot (bytes only)", e, "Method: " + methodName);
            return new byte[0];
        }
    }

    /**
     * Takes a screenshot and returns only the Base64 encoded string without saving to file.
     * Useful for reporting frameworks that prefer embedded Base64 screenshots.
     * 
     * @param page Playwright page instance
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotBase64Only(Page page) {
        if (!validatePage(page, "takeScreenshotBase64Only(Page)")) {
            return "";
        }
        
        String methodName = "takeScreenshotBase64Only(Page)";
        logger.debug("Taking screenshot (Base64 only)");
        
        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true));
            String base64String = Base64.getEncoder().encodeToString(screenshot);
            
            logger.debug("Screenshot taken successfully (Base64 only) - Size: {} bytes, Base64 length: {} chars", 
                        screenshot.length, base64String.length());
            return base64String;
        } catch (Exception e) {
            handleScreenshotFailure("Failed to take screenshot (Base64 only)", e, "Method: " + methodName);
            return "";
        }
    }

    /**
     * Takes a screenshot with enhanced traceability using testName and precise timestamp.
     * This method ensures maximum traceability and prevents any possibility of overwriting.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @param stepName Name of the test step (optional)
     * @param additionalInfo Additional information to include in filename (optional)
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotWithFullTraceability(Page page, String testName, String stepName, String additionalInfo) {
        if (!validatePage(page, "takeScreenshotWithFullTraceability(Page, String, String, String)")) {
            return new byte[0];
        }
        
        String methodName = "takeScreenshotWithFullTraceability(Page, String, String, String)";
        logger.debug("Taking full traceability screenshot - Test: '{}', Step: '{}', Additional: '{}'", 
                    testName, stepName, additionalInfo);
        
        if (testName == null || testName.trim().isEmpty()) {
            logger.warn("Test name is required for full traceability. Using default naming.");
            return takeScreenshot(page);
        }
        
        long currentTimeMillis = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        
        StringBuilder filenameBuilder = new StringBuilder();
        filenameBuilder.append(sanitizeFilename(testName));
        
        if (stepName != null && !stepName.trim().isEmpty()) {
            filenameBuilder.append("_").append(sanitizeFilename(stepName));
        }
        
        if (additionalInfo != null && !additionalInfo.trim().isEmpty()) {
            filenameBuilder.append("_").append(sanitizeFilename(additionalInfo));
        }
        
        filenameBuilder.append("_").append(timestamp);
        filenameBuilder.append("_").append(currentTimeMillis);
        filenameBuilder.append("_").append(uniqueId);
        filenameBuilder.append(".png");
        
        String filename = filenameBuilder.toString();
        Path screenshotPath = Paths.get(DEFAULT_SCREENSHOTS_DIR, filename);
        
        logger.debug("Full traceability filename: '{}'", filename);
        
        try {
            // Ensure directory exists
            createDirectoryIfNotExists(DEFAULT_SCREENSHOTS_DIR);
            
            // Take screenshot
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));
            
            logScreenshotSuccess(screenshotPath, screenshot.length, methodName);
            return screenshot;
            
        } catch (Exception e) {
            String context = String.format("Test: '%s', Step: '%s', Additional: '%s', Filename: '%s'", 
                                         testName, stepName, additionalInfo, filename);
            handleScreenshotFailure("Failed to take screenshot with full traceability", e, context);
            return new byte[0];
        }
    }

    /**
     * Takes a screenshot with enhanced traceability using testName and precise timestamp.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @param stepName Name of the test step (optional)
     * @param additionalInfo Additional information to include in filename (optional)
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotWithFullTraceabilityAsBase64(Page page, String testName, String stepName, String additionalInfo) {
        if (!validatePage(page, "takeScreenshotWithFullTraceabilityAsBase64(Page, String, String, String)")) {
            return "";
        }
        logger.debug("Taking full traceability Base64 screenshot - Test: '{}', Step: '{}', Additional: '{}'", 
                    testName, stepName, additionalInfo);
        byte[] screenshot = takeScreenshotWithFullTraceability(page, testName, stepName, additionalInfo);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot with enhanced traceability using testName and precise timestamp.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @param stepName Name of the test step (optional)
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotWithFullTraceability(Page page, String testName, String stepName) {
        if (!validatePage(page, "takeScreenshotWithFullTraceability(Page, String, String)")) {
            return new byte[0];
        }
        logger.debug("Taking full traceability screenshot - Test: '{}', Step: '{}'", testName, stepName);
        return takeScreenshotWithFullTraceability(page, testName, stepName, null);
    }

    /**
     * Takes a screenshot with enhanced traceability using testName and precise timestamp.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @param stepName Name of the test step (optional)
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotWithFullTraceabilityAsBase64(Page page, String testName, String stepName) {
        if (!validatePage(page, "takeScreenshotWithFullTraceabilityAsBase64(Page, String, String)")) {
            return "";
        }
        logger.debug("Taking full traceability Base64 screenshot - Test: '{}', Step: '{}'", testName, stepName);
        byte[] screenshot = takeScreenshotWithFullTraceability(page, testName, stepName);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot with enhanced traceability using testName and precise timestamp.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotWithFullTraceability(Page page, String testName) {
        if (!validatePage(page, "takeScreenshotWithFullTraceability(Page, String)")) {
            return new byte[0];
        }
        logger.debug("Taking full traceability screenshot - Test: '{}'", testName);
        return takeScreenshotWithFullTraceability(page, testName, null, null);
    }

    /**
     * Takes a screenshot with enhanced traceability using testName and precise timestamp.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotWithFullTraceabilityAsBase64(Page page, String testName) {
        if (!validatePage(page, "takeScreenshotWithFullTraceabilityAsBase64(Page, String)")) {
            return "";
        }
        logger.debug("Taking full traceability Base64 screenshot - Test: '{}'", testName);
        byte[] screenshot = takeScreenshotWithFullTraceability(page, testName);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot with enhanced traceability and saves it to the target directory.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @param stepName Name of the test step (optional)
     * @param additionalInfo Additional information to include in filename (optional)
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotWithFullTraceabilityToTarget(Page page, String testName, String stepName, String additionalInfo) {
        if (!validatePage(page, "takeScreenshotWithFullTraceabilityToTarget(Page, String, String, String)")) {
            return new byte[0];
        }
        
        String methodName = "takeScreenshotWithFullTraceabilityToTarget(Page, String, String, String)";
        logger.debug("Taking full traceability target screenshot - Test: '{}', Step: '{}', Additional: '{}'", 
                    testName, stepName, additionalInfo);
        
        if (testName == null || testName.trim().isEmpty()) {
            logger.warn("Test name is required for full traceability. Using default naming.");
            return takeScreenshotToTarget(page, DEFAULT_FILENAME_PREFIX);
        }
        
        long currentTimeMillis = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        
        StringBuilder filenameBuilder = new StringBuilder();
        filenameBuilder.append(sanitizeFilename(testName));
        
        if (stepName != null && !stepName.trim().isEmpty()) {
            filenameBuilder.append("_").append(sanitizeFilename(stepName));
        }
        
        if (additionalInfo != null && !additionalInfo.trim().isEmpty()) {
            filenameBuilder.append("_").append(sanitizeFilename(additionalInfo));
        }
        
        filenameBuilder.append("_").append(timestamp);
        filenameBuilder.append("_").append(currentTimeMillis);
        filenameBuilder.append("_").append(uniqueId);
        filenameBuilder.append(".png");
        
        String filename = filenameBuilder.toString();
        Path screenshotPath = Paths.get(TARGET_SCREENSHOTS_DIR, filename);
        
        logger.debug("Full traceability target filename: '{}'", filename);
        
        try {
            // Ensure directory exists
            createDirectoryIfNotExists(TARGET_SCREENSHOTS_DIR);
            
            // Take screenshot
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));
            
            logScreenshotSuccess(screenshotPath, screenshot.length, methodName);
            return screenshot;
            
        } catch (Exception e) {
            String context = String.format("Test: '%s', Step: '%s', Additional: '%s', Filename: '%s', Target: '%s'", 
                                         testName, stepName, additionalInfo, filename, TARGET_SCREENSHOTS_DIR);
            handleScreenshotFailure("Failed to take screenshot with full traceability to target", e, context);
            return new byte[0];
        }
    }

    /**
     * Takes a screenshot with enhanced traceability and saves it to the target directory.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @param stepName Name of the test step (optional)
     * @param additionalInfo Additional information to include in filename (optional)
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotWithFullTraceabilityToTargetAsBase64(Page page, String testName, String stepName, String additionalInfo) {
        if (!validatePage(page, "takeScreenshotWithFullTraceabilityToTargetAsBase64(Page, String, String, String)")) {
            return "";
        }
        logger.debug("Taking full traceability target Base64 screenshot - Test: '{}', Step: '{}', Additional: '{}'", 
                    testName, stepName, additionalInfo);
        byte[] screenshot = takeScreenshotWithFullTraceabilityToTarget(page, testName, stepName, additionalInfo);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot with enhanced traceability and saves it to the target directory.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @param stepName Name of the test step (optional)
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotWithFullTraceabilityToTarget(Page page, String testName, String stepName) {
        if (!validatePage(page, "takeScreenshotWithFullTraceabilityToTarget(Page, String, String)")) {
            return new byte[0];
        }
        logger.debug("Taking full traceability target screenshot - Test: '{}', Step: '{}'", testName, stepName);
        return takeScreenshotWithFullTraceabilityToTarget(page, testName, stepName, null);
    }

    /**
     * Takes a screenshot with enhanced traceability and saves it to the target directory.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @param stepName Name of the test step (optional)
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotWithFullTraceabilityToTargetAsBase64(Page page, String testName, String stepName) {
        if (!validatePage(page, "takeScreenshotWithFullTraceabilityToTargetAsBase64(Page, String, String)")) {
            return "";
        }
        logger.debug("Taking full traceability target Base64 screenshot - Test: '{}', Step: '{}'", testName, stepName);
        byte[] screenshot = takeScreenshotWithFullTraceabilityToTarget(page, testName, stepName);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Takes a screenshot with enhanced traceability and saves it to the target directory.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @return byte array of the screenshot
     */
    public static byte[] takeScreenshotWithFullTraceabilityToTarget(Page page, String testName) {
        if (!validatePage(page, "takeScreenshotWithFullTraceabilityToTarget(Page, String)")) {
            return new byte[0];
        }
        logger.debug("Taking full traceability target screenshot - Test: '{}'", testName);
        return takeScreenshotWithFullTraceabilityToTarget(page, testName, null, null);
    }

    /**
     * Takes a screenshot with enhanced traceability and saves it to the target directory.
     * Returns Base64 encoded string for reporting frameworks.
     * 
     * @param page Playwright page instance
     * @param testName Name of the test/scenario (required for traceability)
     * @return Base64 encoded string of the screenshot
     */
    public static String takeScreenshotWithFullTraceabilityToTargetAsBase64(Page page, String testName) {
        if (!validatePage(page, "takeScreenshotWithFullTraceabilityToTargetAsBase64(Page, String)")) {
            return "";
        }
        logger.debug("Taking full traceability target Base64 screenshot - Test: '{}'", testName);
        byte[] screenshot = takeScreenshotWithFullTraceabilityToTarget(page, testName);
        return Base64.getEncoder().encodeToString(screenshot);
    }

    /**
     * Converts a byte array to Base64 encoded string.
     * Utility method for converting existing byte arrays to Base64 for reporting.
     * 
     * @param screenshotBytes Byte array of the screenshot
     * @return Base64 encoded string
     */
    public static String convertToBase64(byte[] screenshotBytes) {
        if (screenshotBytes == null || screenshotBytes.length == 0) {
            logger.warn("Screenshot bytes are null or empty, returning empty string");
            return "";
        }
        String base64String = Base64.getEncoder().encodeToString(screenshotBytes);
        logger.debug("Converted {} bytes to Base64 string of length {}", screenshotBytes.length, base64String.length());
        return base64String;
    }

    /**
     * Sanitizes a filename by removing or replacing invalid characters.
     * 
     * @param filename Original filename
     * @return Sanitized filename
     */
    private static String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return DEFAULT_FILENAME_PREFIX;
        }
        
        // Replace invalid characters with underscores
        String sanitized = filename.replaceAll("[^a-zA-Z0-9._-]", "_")
                      .replaceAll("_+", "_")  // Replace multiple underscores with single
                      .replaceAll("^_|_$", ""); // Remove leading/trailing underscores
        
        if (!sanitized.equals(filename)) {
            logger.debug("Filename sanitized: '{}' -> '{}'", filename, sanitized);
        }
        
        return sanitized;
    }

    /**
     * Creates a directory if it doesn't exist.
     * Enhanced to handle nested directory creation and provide better error handling.
     * 
     * @param directoryPath Path to the directory
     */
    private static void createDirectoryIfNotExists(String directoryPath) {
        try {
            Path directory = Paths.get(directoryPath);
            if (!Files.exists(directory)) {
                // Create all parent directories as well
                Files.createDirectories(directory);
                logger.debug("Created directory structure: {}", directoryPath);
            } else if (!Files.isDirectory(directory)) {
                String errorMsg = "Path exists but is not a directory: " + directoryPath;
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Failed to create directory: " + directoryPath;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Verifies that all required directories exist and are writable.
     * Can be called to check directory status before taking screenshots.
     * 
     * @return true if all directories are ready, false otherwise
     */
    public static boolean verifyDirectoriesReady() {
        try {
            ensureDirectoriesExist();
            
            // Check if directories are writable
            Path screenshotsDir = Paths.get(DEFAULT_SCREENSHOTS_DIR);
            Path targetScreenshotsDir = Paths.get(TARGET_SCREENSHOTS_DIR);
            
            if (!Files.isWritable(screenshotsDir)) {
                logger.error("Screenshots directory is not writable: {}", DEFAULT_SCREENSHOTS_DIR);
                return false;
            }
            
            if (!Files.isWritable(targetScreenshotsDir)) {
                logger.error("Target screenshots directory is not writable: {}", TARGET_SCREENSHOTS_DIR);
                return false;
            }
            
            logger.debug("All directories verified and ready for screenshots");
            return true;
            
        } catch (Exception e) {
            logger.error("Directory verification failed", e);
            return false;
        }
    }
} 