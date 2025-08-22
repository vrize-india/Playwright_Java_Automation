package com.vrize.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.vrize.annotations.XrayKey;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TestNG listener to report all test results to Xray via XrayLogger.
 */
public class XrayListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        // No action needed on test start
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Check if Xray reporting should be suppressed due to retry attempts
        String testKey = getTestKey(result);
        if (RetryUtility.shouldSuppressXrayReporting(testKey)) {
            System.out.println("[XRAY] Suppressing Xray reporting for retry attempt: " + result.getName());
            return;
        }
        
        String xrayKey = getXrayTestKey(result);
        if (xrayKey != null && !xrayKey.isEmpty()) {
            System.out.println("[XRAY] Test PASSED: " + result.getName() + " | Xray Key: " + xrayKey);
            XrayLogger.logTestExecution(xrayKey, "PASSED", null);
        } else {
            System.out.println("[XRAY] Test PASSED: " + result.getName() + " | No Xray Key found");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Check if Xray reporting should be suppressed due to retry attempts
        String testKey = getTestKey(result);
        if (RetryUtility.shouldSuppressXrayReporting(testKey)) {
            System.out.println("[XRAY] Suppressing Xray reporting for retry attempt: " + result.getName());
            return;
        }
        
        String xrayKey = getXrayTestKey(result);
        if (xrayKey != null && !xrayKey.isEmpty()) {
            System.out.println("[XRAY] Test FAILED: " + result.getName() + " | Xray Key: " + xrayKey);
            String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "Test failed";
            XrayLogger.logTestExecution(testKey, "FAILED", errorMessage);
        } else {
            System.out.println("[XRAY] Test FAILED: " + result.getName() + " | No Xray Key found");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Check if Xray reporting should be suppressed due to retry attempts
        String testKey = getTestKey(result);
        if (RetryUtility.shouldSuppressXrayReporting(testKey)) {
            System.out.println("[XRAY] Suppressing Xray reporting for retry attempt: " + result.getName());
            return;
        }
        
        String xrayKey = getXrayTestKey(result);
        if (xrayKey != null && !xrayKey.isEmpty()) {
            System.out.println("[XRAY] Test SKIPPED: " + result.getName() + " | Xray Key: " + xrayKey);
            XrayLogger.logTestExecution(xrayKey, "SKIPPED", null);
        } else {
            System.out.println("[XRAY] Test SKIPPED: " + result.getName() + " | No Xray Key found");
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not used
    }

    @Override
    public void onStart(ITestContext context) {
        // Check if this is a retry attempt by looking at the test context
        // If we're in a retry, we should not create a new execId
        boolean isRetryRun = isRetryExecution(context);
        
        if (isRetryRun) {
            System.out.println("[XRAY] Suppressing execId creation for retry attempt");
            // On final attempt, reuse persisted execId so Xray logging can occur
            boolean isFinalAttempt = Boolean.parseBoolean(System.getProperty("final.attempt", "false"));
            if (isFinalAttempt) {
                String persisted = XrayLogger.readTestExecutionKeyFromFile();
                if (persisted != null && !persisted.trim().isEmpty()) {
                    XrayLogger.setTestExecutionKey(persisted);
                    System.out.println("[XRAY] Final retry run detected. Initialized testExecutionKey from persisted file: " + persisted);
                }
            }
            return;
        }
        
        XrayLogger.initializeTestExecutionKey();
    }
    
    /**
     * Generates a unique key for a test result to track retry attempts.
     *
     * @param result The test result
     * @return A unique string identifier for the test
     */
    private String getTestKey(ITestResult result) {
        String testName = result.getName();
        String className = result.getTestClass().getName();
        String methodName = result.getMethod().getMethodName();

        // Include parameters if available to make the key more unique
        Object[] parameters = result.getParameters();
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(className).append(".").append(methodName);

        if (parameters != null && parameters.length > 0) {
            keyBuilder.append("(");
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) keyBuilder.append(",");
                keyBuilder.append(parameters[i] != null ? parameters[i].toString() : "null");
            }
            keyBuilder.append(")");
        }

        return keyBuilder.toString();
    }

    /**
     * Determines if this is a retry execution by checking various indicators.
     */
    private boolean isRetryExecution(ITestContext context) {
        // Check if this is a rerun execution by looking at system properties or context
        String rerunFile = System.getProperty("cucumber.rerun.file");
        if (rerunFile != null && rerunFile.contains("rerun.txt")) {
            return true;
        }
        
        // Check if we're running from a rerun file
        String cucumberFeatures = System.getProperty("cucumber.features");
        if (cucumberFeatures != null && cucumberFeatures.contains("@target/rerun.txt")) {
            return true;
        }
        
        // Check if this is a retry attempt by looking at the retry attempt system property
        String retryAttempt = System.getProperty("cucumber.retry.attempt");
        if (retryAttempt != null && !retryAttempt.trim().isEmpty()) {
            return true;
        }
        
        // Check if this is a retry attempt by looking at the test context name
        String testName = context.getName();
        if (testName != null && testName.toLowerCase().contains("retry")) {
            return true;
        }
        
        return false;
    }

    @Override
    public void onFinish(ITestContext context) {
        // Log test suite completion statistics
        System.out.println("[XRAY] Test suite finished. " + SharedExecIdManager.getExecutionStats());
        
        // Only reset execId if this is not a retry scenario
        // For retry scenarios, we want to keep the same execId across all attempts
        boolean isRetryRun = isRetryExecution(context);
        if (!isRetryRun) {
            // Cleanup shared execId for next test suite
            SharedExecIdManager.onTestSuiteComplete();
        } else {
            System.out.println("[XRAY] Retry scenario detected - keeping execId for next attempt");
        }
    }

    /**
     * Extracts Xray test key from test method annotations or test name.
     * Looks for @XrayKey annotation or extracts from test name pattern.
     */
    private String getXrayTestKey(ITestResult result) {
        try {
            // First, try to get from XrayCucumberPlugin if available
            try {
                String cucumberXrayKey = XrayCucumberPlugin.getXrayKeyForTestCase(result.getName());
                if (cucumberXrayKey != null && !cucumberXrayKey.isEmpty()) {
                    return cucumberXrayKey;
                }
            } catch (Exception e) {
                // XrayCucumberPlugin might not be available, continue with other methods
            }
            
            // Try to get the test method
            Method testMethod = result.getMethod().getConstructorOrMethod().getMethod();
            if (testMethod != null) {
                // Check for @XrayKey annotation on the method
                if (testMethod.isAnnotationPresent(XrayKey.class)) {
                    XrayKey xrayKeyAnnotation = testMethod.getAnnotation(XrayKey.class);
                    return xrayKeyAnnotation.value();
                }
                
                // Check for @XrayKey annotation on the class
                Class<?> testClass = testMethod.getDeclaringClass();
                if (testClass.isAnnotationPresent(XrayKey.class)) {
                    XrayKey xrayKeyAnnotation = testClass.getAnnotation(XrayKey.class);
                    return xrayKeyAnnotation.value();
                }
            }
            
            // If no annotation found, try to extract from test name or description
            String testName = result.getName();
            String description = result.getMethod().getDescription();
            
            // Look for Xray key pattern (e.g., TONIC-12345) in test name or description
            String xrayKey = extractXrayKeyFromText(testName);
            if (xrayKey != null) {
                return xrayKey;
            }
            
            xrayKey = extractXrayKeyFromText(description);
            if (xrayKey != null) {
                return xrayKey;
            }
            
            // Try to get from test parameters or attributes
            Object[] parameters = result.getParameters();
            if (parameters != null) {
                for (Object param : parameters) {
                    if (param instanceof String) {
                        String paramStr = (String) param;
                        xrayKey = extractXrayKeyFromText(paramStr);
                        if (xrayKey != null) {
                            return xrayKey;
                        }
                    }
                }
            }
            
            // For Cucumber tests, try to extract from the test instance
            Object testInstance = result.getInstance();
            if (testInstance != null) {
                // Try to get Xray key from the test instance class
                Class<?> instanceClass = testInstance.getClass();
                if (instanceClass.isAnnotationPresent(XrayKey.class)) {
                    XrayKey xrayKeyAnnotation = instanceClass.getAnnotation(XrayKey.class);
                    return xrayKeyAnnotation.value();
                }
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("[XRAY] Error extracting Xray test key: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Extracts Xray key from text using regex pattern.
     * Looks for patterns like TONIC-12345, CALC-123, etc.
     */
    private String extractXrayKeyFromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        // Pattern to match Xray keys (e.g., TONIC-12345, CALC-123)
        Pattern pattern = Pattern.compile("([A-Z]+)-\\d+");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            return matcher.group(1) + "-" + matcher.group(2);
        }
        
        return null;
    }
} 