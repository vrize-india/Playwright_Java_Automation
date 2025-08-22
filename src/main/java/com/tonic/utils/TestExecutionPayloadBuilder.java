package com.tonic.utils;

import com.tonic.constants.XrayConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for constructing test execution payloads for Xray Cloud API.
 * Improves readability and maintainability of payload construction.
 */
public class TestExecutionPayloadBuilder {
    private String testExecutionKey;
    private final List<TestResult> tests = new ArrayList<>();
    private final List<String> defects = new ArrayList<>();
    
    /**
     * Sets the test execution key for the payload.
     * @param key The test execution key
     * @return This builder instance for method chaining
     */
    public TestExecutionPayloadBuilder withTestExecutionKey(String key) {
        this.testExecutionKey = key;
        return this;
    }
    
    /**
     * Adds a test result to the payload.
     * @param testKey The Xray test key (e.g., "CALC-123")
     * @param status The test status ("PASSED" or "FAILED")
     * @param comment Optional comment or error message
     * @return This builder instance for method chaining
     */
    public TestExecutionPayloadBuilder addTest(String testKey, String status, String comment) {
        tests.add(new TestResult(testKey, status, comment));
        return this;
    }
    
    /**
     * Adds a test result to the payload without comment.
     * @param testKey The Xray test key
     * @param status The test status
     * @return This builder instance for method chaining
     */
    public TestExecutionPayloadBuilder addTest(String testKey, String status) {
        return addTest(testKey, status, null);
    }
    
    /**
     * Adds a defect key to the payload.
     * @param defectKey The Jira bug key (e.g., "TONIC-9999")
     * @return This builder instance for method chaining
     */
    public TestExecutionPayloadBuilder addDefect(String defectKey) {
        if (defectKey != null && !defectKey.trim().isEmpty()) {
            defects.add(defectKey);
        }
        return this;
    }
    
    /**
     * Adds multiple defect keys to the payload.
     * @param defectKeys List of defect keys
     * @return This builder instance for method chaining
     */
    public TestExecutionPayloadBuilder addDefects(List<String> defectKeys) {
        if (defectKeys != null) {
            for (String defectKey : defectKeys) {
                addDefect(defectKey);
            }
        }
        return this;
    }
    
    /**
     * Builds the final JSON payload for Xray Cloud API.
     * @return JSONObject containing the complete payload
     * @throws IllegalStateException if required fields are missing
     */
    public JSONObject build() {
        if (testExecutionKey == null || testExecutionKey.trim().isEmpty()) {
            throw new IllegalStateException("Test execution key is required");
        }
        
        if (tests.isEmpty()) {
            throw new IllegalStateException("At least one test result is required");
        }
        
        JSONObject payload = new JSONObject();
        payload.put("testExecutionKey", testExecutionKey);
        
        // Build tests array
        JSONArray testsArray = new JSONArray();
        for (TestResult test : tests) {
            JSONObject testObj = new JSONObject();
            testObj.put("testKey", test.testKey);
            testObj.put("status", test.status);
            
            if (test.comment != null && !test.comment.trim().isEmpty()) {
                testObj.put("comment", test.comment);
            }
            
            // Add defects if any
            if (!defects.isEmpty()) {
                JSONArray defectsArray = new JSONArray();
                for (String defect : defects) {
                    defectsArray.put(defect);
                }
                testObj.put("defects", defectsArray);
            }
            
            testsArray.put(testObj);
        }
        
        payload.put("tests", testsArray);
        return payload;
    }
    
    /**
     * Builds the payload as a JSON string.
     * @return JSON string representation of the payload
     */
    public String buildAsString() {
        return build().toString();
    }
    
    /**
     * Builds the payload as a formatted JSON string.
     * @param indentFactor Number of spaces for indentation
     * @return Formatted JSON string
     */
    public String buildAsFormattedString(int indentFactor) {
        return build().toString(indentFactor);
    }
    
    /**
     * Internal class representing a test result.
     */
    private static class TestResult {
        private final String testKey;
        private final String status;
        private final String comment;
        
        TestResult(String testKey, String status, String comment) {
            this.testKey = testKey;
            this.status = status;
            this.comment = comment;
        }
    }
    
    /**
     * Creates a new builder instance.
     * @return New TestExecutionPayloadBuilder instance
     */
    public static TestExecutionPayloadBuilder newBuilder() {
        return new TestExecutionPayloadBuilder();
    }
}

