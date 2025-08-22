package com.tonic.listeners;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestStepStarted;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.Status;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Cucumber plugin to extract Xray keys from feature files and track test execution.
 * This plugin works in conjunction with XrayListener to provide proper Xray integration.
 */
public class XrayCucumberPlugin implements ConcurrentEventListener {
    
    private static final Map<String, String> testCaseToXrayKey = new HashMap<>();
    private static final Pattern XRAY_KEY_PATTERN = Pattern.compile("@XrayKey=([A-Z]+-\\d+)");
    
    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestRunStarted.class, this::onTestRunStarted);
        publisher.registerHandlerFor(TestCaseStarted.class, this::onTestCaseStarted);
        publisher.registerHandlerFor(TestCaseFinished.class, this::onTestCaseFinished);
        publisher.registerHandlerFor(TestRunFinished.class, this::onTestRunFinished);
    }
    
    private void onTestRunStarted(TestRunStarted event) {
        System.out.println("[XRAY] Test run started");
        testCaseToXrayKey.clear();
        // Load previous results if this is a retry run
        TestResultAggregator.loadResults();
    }
    
    private void onTestCaseStarted(TestCaseStarted event) {
        String testCaseName = event.getTestCase().getName();
        String xrayKey = extractXrayKeyFromTestCase(event.getTestCase());
        
        if (xrayKey != null) {
            testCaseToXrayKey.put(testCaseName, xrayKey);
            System.out.println("[XRAY] Test case started: " + testCaseName + " | Xray Key: " + xrayKey);
        } else {
            System.out.println("[XRAY] Test case started: " + testCaseName + " | No Xray Key found");
        }
    }
    
    private void onTestCaseFinished(TestCaseFinished event) {
        String testCaseName = event.getTestCase().getName();
        String xrayKey = testCaseToXrayKey.get(testCaseName);
        Status status = event.getResult().getStatus();
        
        // Record result in aggregator
        String xrayStatus = convertCucumberStatusToXrayStatus(status);
        String errorMessage = null;
        String screenshotPath = null;
        long duration = event.getResult().getDuration().toMillis();
        
        if (status == Status.FAILED && event.getResult().getError() != null) {
            errorMessage = event.getResult().getError().getMessage();
            // Screenshot path would be captured by screenshot utils
            screenshotPath = getLatestScreenshotPath(testCaseName);
        }
        
        // Record in aggregator
        TestResultAggregator.recordTestResult(xrayKey, testCaseName, xrayStatus, 
            errorMessage, screenshotPath, duration);
        
        // Add tags
        List<String> tags = new ArrayList<>(event.getTestCase().getTags());
        TestResultAggregator.addTagsToTest(xrayKey != null ? xrayKey : testCaseName, tags);
        
        if (xrayKey != null) {
            System.out.println("[XRAY] Test case finished: " + testCaseName + " | Xray Key: " + xrayKey + " | Status: " + xrayStatus);
            XrayLogger.logTestExecution(xrayKey, xrayStatus, errorMessage);
        } else {
            System.out.println("[XRAY] Test case finished: " + testCaseName + " | No Xray Key found | Status: " + status);
        }
    }
    
    private String getLatestScreenshotPath(String testName) {
        // This would integrate with your screenshot utilities
        // For now, return null - can be enhanced later
        return null;
    }
    
    private void onTestRunFinished(TestRunFinished event) {
        System.out.println("[XRAY] Test run finished");
        testCaseToXrayKey.clear();
    }
    
    /**
     * Extracts Xray key from test case tags.
     */
    private String extractXrayKeyFromTestCase(io.cucumber.plugin.event.TestCase testCase) {
        for (String tag : testCase.getTags()) {
            Matcher matcher = XRAY_KEY_PATTERN.matcher(tag);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
    
    /**
     * Converts Cucumber status to Xray status.
     */
    private String convertCucumberStatusToXrayStatus(Status cucumberStatus) {
        switch (cucumberStatus) {
            case PASSED:
                return "PASSED";
            case FAILED:
                return "FAILED";
            case SKIPPED:
                return "SKIPPED";
            case PENDING:
                return "SKIPPED";
            case UNDEFINED:
                return "SKIPPED";
            case AMBIGUOUS:
                return "FAILED";
            default:
                return "SKIPPED";
        }
    }
    
    /**
     * Gets the Xray key for a test case name.
     * This method can be used by other components to get the Xray key.
     */
    public static String getXrayKeyForTestCase(String testCaseName) {
        return testCaseToXrayKey.get(testCaseName);
    }
} 