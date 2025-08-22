package com.vrize.listeners;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import static com.vrize.utils.EncryptionUtils.*;

import java.util.List;
import java.util.Properties;

import com.vrize.enums.ConfigProperties;
import com.vrize.constants.XrayConstants;
import com.vrize.utils.XrayConfigManager;
import com.vrize.utils.XrayHttpClient;
import com.vrize.utils.TestExecutionPayloadBuilder;
import com.vrize.utils.XrayValidationUtils;
import com.vrize.exceptions.XrayException;
import com.vrize.exceptions.ErrorCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.vrize.utils.JiraServiceProvider;
import java.io.InputStream;
import com.vrize.utils.EncryptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized Xray integration service for TONIC AI Automation Framework.
 *
 * <p>This class provides comprehensive integration with Xray Cloud API for:</p>
 * <ul>
 *   <li>Test execution result logging</li>
 *   <li>Test plan association and management</li>
 *   <li>Report and video attachment</li>
 *   <li>Automatic linking between test executions and test plans</li>
 * </ul>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Automatic authentication with Xray Cloud</li>
 *   <li>Dynamic configuration management with system property overrides</li>
 *   <li>Comprehensive error handling and logging</li>
 *   <li>Fallback mechanisms for failed operations</li>
 *   <li>Integration with Jira REST API for enhanced functionality</li>
 * </ul>
 *
 * <p>Configuration properties:</p>
 * <ul>
 *   <li>{@code xray_enabled} - Enable/disable Xray integration</li>
 *   <li>{@code xray.clientId} - Xray Cloud client ID</li>
 *   <li>{@code xray.clientSecret} - Xray Cloud client secret</li>
 *   <li>{@code xray.authEndpoint} - Xray authentication endpoint</li>
 *   <li>{@code xray.executionEndpoint} - Xray execution endpoint</li>
 *   <li>{@code execId} - Test execution key</li>
 *   <li>{@code test.plan.key} - Test plan key</li>
 * </p>
 *
 * @author TONIC AI Automation Team
 * @version 2.0.0
 * @since 1.0.0
 */
public class XrayLogger {
    private static final Logger logger = LoggerFactory.getLogger(XrayLogger.class);
    private static final String CONFIG_FILE = "config.properties";
    private static String clientId;
    private static String clientSecret;
    private static String authEndpoint;
    private static String executionEndpoint;
    private static boolean xrayEnabled = false;
    private static String testExecutionKey = null;
    private static final String TEST_EXECUTION_KEY_FILE = "target/testexecution.key";

    static {
        try {
            // Use XrayConfigManager for consistent config loading
            clientId = decrypt(XrayConfigManager.getProperty(ConfigProperties.XRAY_CLIENT_ID));
            clientSecret = decrypt(XrayConfigManager.getProperty(ConfigProperties.XRAY_CLIENT_SECRET));
            authEndpoint = XrayConfigManager.getProperty(ConfigProperties.XRAY_AUTH_ENDPOINT);
            executionEndpoint = XrayConfigManager.getProperty(ConfigProperties.XRAY_EXECUTION_ENDPOINT);

            // Validate required configuration
            if (clientId == null || clientId.trim().isEmpty()) {
                throw new XrayException(ErrorCode.CONFIGURATION_ERROR, "Static Initialization", "Xray client ID is not configured");
            }

            if (clientSecret == null || clientSecret.trim().isEmpty()) {
                throw new XrayException(ErrorCode.CONFIGURATION_ERROR, "Static Initialization", "Xray client secret is not configured");
            }

            if (authEndpoint == null || authEndpoint.trim().isEmpty()) {
                throw new XrayException(ErrorCode.CONFIGURATION_ERROR, "Static Initialization", "Xray auth endpoint is not configured");
            }

            if (executionEndpoint == null || executionEndpoint.trim().isEmpty()) {
                throw new XrayException(ErrorCode.CONFIGURATION_ERROR, "Static Initialization", "Xray execution endpoint is not configured");
            }

            // Check system property first, then config
            String sysProp = System.getProperty("xray_enabled");
            if (sysProp != null) {
                xrayEnabled = Boolean.parseBoolean(sysProp);
            } else {
                xrayEnabled = XrayConfigManager.getBooleanProperty(ConfigProperties.XRAY_ENABLED, false);
            }
        } catch (Exception e) {
            if (e instanceof XrayException) {
                throw e;
            }
            throw new XrayException(ErrorCode.CONFIGURATION_ERROR, "Static Initialization", "Failed to load Xray config properties", e);
        }
    }

    /**
     * Sets the test execution key for the current test run.
     *
     * <p>This method:</p>
     * <ul>
     *   <li>Validates the provided key format</li>
     *   <li>Stores the key in memory for the current session</li>
     *   <li>Persists the key to a file for cross-session persistence</li>
     *   <li>Logs configuration details for debugging</li>
     * </ul>
     *
     * <p>The key is used to identify which test execution in Xray/Jira should receive the test results.</p>
     *
     * @param key The test execution key (e.g., "TONIC-14647")
     * @throws XrayException if the key is null, empty, or invalid format
     */
    public static void setTestExecutionKey(String key) {
        // Input validation
        XrayValidationUtils.requireNonBlank(key, "key");

        testExecutionKey = key;
        logger.debug("setTestExecutionKey called. testExecutionKey set to: {}", testExecutionKey);
        // Debug: print encrypted values and config path used for test execution creation
        try {
            String encryptedEmail = XrayConfigManager.getProperty(ConfigProperties.JIRA_EMAIL_ENCRYPTED);
            String encryptedToken = XrayConfigManager.getProperty(ConfigProperties.JIRA_TOKEN_ENCRYPTED);

            // Validate configuration
            if (encryptedEmail == null || encryptedEmail.trim().isEmpty()) {
                logger.warn("(setTestExecutionKey) jira.email.encrypted is not configured");
            }

            if (encryptedToken == null || encryptedToken.trim().isEmpty()) {
                logger.warn("(setTestExecutionKey) jira.token.encrypted is not configured");
            }

            logger.debug("(setTestExecutionKey) Loaded config.properties from XrayConfigManager");
            logger.debug("(setTestExecutionKey) jira.email.encrypted={}", encryptedEmail);
            logger.debug("(setTestExecutionKey) jira.token.encrypted={}", encryptedToken);

            // Debug decryption here as well
            String SECRET_KEY = XrayConstants.DEFAULT_SECRET_KEY;
            if (encryptedEmail != null && !encryptedEmail.trim().isEmpty()) {
                debugDecrypt(encryptedEmail, SECRET_KEY, "setTestExecutionKey - email");
            }
            if (encryptedToken != null && !encryptedToken.trim().isEmpty()) {
                debugDecrypt(encryptedToken, SECRET_KEY, "setTestExecutionKey - token");
            } else {
                System.out.println("[XRAY][DEBUG] (setTestExecutionKey) Could not load config.properties from classpath");
            }
        } catch (Exception e) {
            logger.error("(setTestExecutionKey) Error loading config.properties: {}", e.getMessage());
        }
        // Persist to file
        try (java.io.FileWriter fw = new java.io.FileWriter(XrayConstants.TEST_EXECUTION_KEY_FILE, false)) {
            fw.write(key);
        } catch (Exception e) {
            logger.error("Could not persist test execution key: {}", e.getMessage());
        }
    }

    /**
     * Reads the test execution key from the persistent storage file.
     *
     * <p>This method attempts to read the test execution key from the file system.
     * If the file exists and contains a valid key, it validates the format and returns it.
     * If any errors occur during reading, they are logged and null is returned.</p>
     *
     * <p>The file path is defined in {@link XrayConstants#TEST_EXECUTION_KEY_FILE}.</p>
     *
     * @return The test execution key if successfully read and validated, null otherwise
     */
    public static String readTestExecutionKeyFromFile() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(XrayConstants.TEST_EXECUTION_KEY_FILE);
            if (java.nio.file.Files.exists(path)) {
                String key = new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8).trim();
                if (key != null && !key.trim().isEmpty()) {
                    XrayValidationUtils.requireValidJiraKey(key, "test execution key from file");
                }
                return key;
            }
        } catch (IOException e) {
            logger.error("I/O error reading test execution key from file: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error reading test execution key from file: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Gets the currently stored test execution key.
     *
     * <p>This method returns the in-memory test execution key that was set during the current session.
     * It does not read from the file system or validate the key format.</p>
     *
     * @return The current test execution key, or null if not set
     */
    public static String getTestExecutionKey() {
        // No input validation needed for getter method
        return testExecutionKey;
    }

    /**
     * Checks if Xray integration is currently enabled.
     *
     * <p>This method returns the current state of Xray integration based on the configuration
     * loaded during static initialization. The value can be overridden at runtime using
     * the system property {@code xray_enabled}.</p>
     *
     * @return true if Xray integration is enabled, false otherwise
     */
    public static boolean isXrayEnabled() {
        // No input validation needed for getter method
        return xrayEnabled;
    }

    /**
     * Determines if this is a retry execution by checking system properties.
     */
    private static boolean isRetryExecution() {
        // Check if this is a rerun execution by looking at system properties
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

        return false;
    }

    /**
     * Initializes the test execution key for the current test run.
     *
     * <p>This method follows a priority order for determining the test execution key:</p>
     * <ol>
     *   <li>System property {@code execId} (highest priority)</li>
     *   <li>Configuration property {@code jira.exec.id}</li>
     *   <li>Create a new test execution in Jira (lowest priority)</li>
     * </ol>
     *
     * <p>If a key is provided via system property or config, it validates the format and uses it.
     * If no key is provided, it creates a new test execution in Jira and links it to the test plan.</p>
     *
     * <p>This method should be called during test setup (e.g., in {@code @Before} hooks) to ensure
     * the test execution key is properly initialized before test execution begins.</p>
     *
     * @throws XrayException if Xray integration is disabled or configuration is invalid
     */
    public static void initializeTestExecutionKey() {
        if (!xrayEnabled) {
            logger.info("Xray integration is disabled (xray_enabled=false). Skipping Test Execution initialization.");
            return;
        }

        // Check if this is a retry attempt before initializing Xray
        if (isRetryExecution()) {
            System.out.println("[XRAY] Suppressing execId creation for retry attempt in initializeTestExecutionKey");
            return;
        }

        // First check system property (for backward compatibility and runtime override)
        String execId = System.getProperty("execId");
        if (execId == null || execId.trim().isEmpty()) {
            // If not provided via system property, check config properties
            execId = com.vrize.utils.PropertyBuilder.getPropValue(ConfigProperties.JIRA_EXEC_ID);
        }
        
        logger.debug("System property execId: {}", System.getProperty("execId"));
        logger.debug("Config property execId: {}", com.vrize.utils.PropertyBuilder.getPropValue(ConfigProperties.JIRA_EXEC_ID));
        logger.debug("Final execId value: {}", execId);
        
        if (execId != null && !execId.trim().isEmpty()) {
            // Validate the execId before setting it
            XrayValidationUtils.requireValidJiraKey(execId, "execId");
            setTestExecutionKey(execId);
            SharedExecIdManager.setSharedExecId(execId);
            System.out.println("[XRAY] Using provided Test Execution ID: " + execId + " - NO NEW EXECUTION WILL BE CREATED");
            logger.info("Using provided Test Execution ID: {} - NO NEW EXECUTION WILL BE CREATED", execId);
        } else {
            // Use shared execId manager to ensure only one execId is created
            String sharedExecId = SharedExecIdManager.getOrCreateSharedExecId();
            if (sharedExecId != null) {
                setTestExecutionKey(sharedExecId);
                System.out.println("[XRAY] Using shared Test Execution ID: " + sharedExecId);
            } else {
                logger.error("Failed to create new Test Execution");
            }
        }
    }

    /**
     * Creates a new test execution in Jira and links it to the test plan.
     *
     * <p>This method creates a new test execution issue in Jira when no execution
     * key is provided via configuration or system properties. It automatically
     * links the new execution to the configured test plan.</p>
     *
     * <p>The creation process:</p>
     * <ul>
     *   <li>Generates a unique summary with timestamp</li>
     *   <li>Retrieves the parent test plan key from configuration</li>
     *   <li>Creates a new Jira issue of type "Test Execution"</li>
     *   <li>Attempts to link the execution to the test plan</li>
     *   <li>Returns the new execution key for use</li>
     * </ul>
     *
     * <p>This method is called automatically when {@link #logTestExecution(String, String, String, String)}
     * is invoked without a pre-configured execution key.</p>
     *
     * @return The newly created test execution key, or null if creation fails
     */
    public static String createNewTestExecution() {
        try {
            String summary = "Automated Test Execution - " + System.currentTimeMillis();
            String parentKey = JiraServiceProvider.getParentKey();

            // Input validation
            XrayValidationUtils.requireValidJiraKey(parentKey, "parentKey");

            String description = "Automated test execution created by XrayLogger - linked to test plan " + parentKey;
            
            // Create a new test execution in Jira
            JiraServiceProvider jiraSp = new JiraServiceProvider();
            String executionKey = jiraSp.createJiraTicket("Test Execution", summary, description);

            // Validate new execution key format
            XrayValidationUtils.requireValidJiraKey(executionKey, "executionKey");

            logger.info("[XRAY] New Test Execution created in Jira with ID: {}", executionKey);

            // Try to link the new test execution to the configurable parent key with different link types
            boolean linked = linkExecutionToTestPlan(executionKey, parentKey);
            if (linked) {
                logger.info("[XRAY] Successfully linked test execution {} to test plan {}", executionKey, parentKey);
            } else {
                logger.warn("[XRAY] Failed to link test execution {} to test plan {} with any available link type", executionKey, parentKey);
            }

            return executionKey;
        } catch (Exception e) {
            logger.error("Failed to create new test execution: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Automatically links the current test execution to the current test plan.
     *
     * <p>This method establishes a Parent-Child relationship between the test execution and test plan
     * in Jira, which allows Xray to properly associate test results with test plans.</p>
     *
     * <p>The method:</p>
     * <ul>
     *   <li>Retrieves the current test execution key and test plan key</li>
     *   <li>Validates both keys are in proper Jira format</li>
     *   <li>Attempts to create a Parent-Child link in Jira</li>
     *   <li>Logs the success or failure of the linking operation</li>
     * </ul>
     *
     * <p>This method should be called during test setup (e.g., in {@code @Before} hooks) after
     * the test execution key has been initialized.</p>
     *
     * @throws XrayException if the execution ID or plan ID is invalid or missing
     */
    public static void addExecutionIdToPlanIdAutomatically() {
        String executionId = getTestExecutionKey();
        String planId = getCurrentTestPlanKey();

        // Input validation
        XrayValidationUtils.requireValidJiraKey(executionId, "executionId");
        XrayValidationUtils.requireValidJiraKey(planId, "planId");

        logger.debug("addExecutionIdToPlanIdAutomatically called");
        logger.debug("Execution ID: {}, Plan ID: {}", executionId, planId);

        boolean linked = linkExecutionToTestPlan(executionId, planId);
        if (linked) {
            logger.info("Successfully linked execution ID {} to test plan {}", executionId, planId);
        } else {
            logger.error("Failed to link execution ID {} to test plan {}", executionId, planId);
        }
    }

    /**
     * Attempts to link a test execution to the test plan using various link types.
     *
     * <p>This method attempts to create a relationship between a test execution and test plan
     * in Jira. It tries multiple link types in order of preference to maximize the chance
     * of successful linking.</p>
     *
     * <p>The linking strategy:</p>
     * <ul>
     *   <li>First tries preferred link types (Parent-Child, Relates, etc.)</li>
     *   <li>Then queries Jira for available link types</li>
     *   <li>Tries each available link type in both directions</li>
     *   <li>Returns true on first successful link</li>
     * </ul>
     *
     * <p>Preferred link types are defined in {@link XrayConstants#PREFERRED_LINK_TYPES}
     * and include common relationship types like "Parent-Child" and "Relates".</p>
     *
     * @param executionKey The test execution key to link
     * @param testPlanKey The test plan key to link to
     * @return true if successfully linked, false otherwise
     */
    private static boolean linkExecutionToTestPlan(String executionKey, String testPlanKey) {
        // Input validation
        XrayValidationUtils.requireValidJiraKey(executionKey, "executionKey");
        XrayValidationUtils.requireValidJiraKey(testPlanKey, "testPlanKey");

        JiraServiceProvider jira = new JiraServiceProvider();

        // Try preferred types first
        XrayValidationUtils.requireNonEmpty(XrayConstants.PREFERRED_LINK_TYPES, "PREFERRED_LINK_TYPES");
        for (String type : XrayConstants.PREFERRED_LINK_TYPES) {
            XrayValidationUtils.requireNonBlank(type, "preferred link type");
            if (tryLink(jira, testPlanKey, executionKey, type) || tryLink(jira, executionKey, testPlanKey, type)) {
                logger.info("[XRAY] Linked using preferred type: {}", type);
                return true;
            }
        }

        // Try whatever Jira reports as available
        try {
            List<String> available = jira.getAvailableLinkTypes();
            logger.debug("[XRAY] Jira link types: {}", available);
            if (available != null) {
                // Validate available link types list
                XrayValidationUtils.requireNonEmpty(available, "available link types");
                for (String type : available) {
                    if (type == null || type.isBlank()) continue;
                    // Validate link type before trying
                    XrayValidationUtils.requireNonBlank(type, "linkType");
                    if (tryLink(jira, testPlanKey, executionKey, type) || tryLink(jira, executionKey, testPlanKey, type)) {
                        logger.info("[XRAY] Linked using Jira type: {}", type);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("[XRAY] Could not fetch Jira link types; skipping: {}", e.getMessage());
        }

        logger.warn("[XRAY] Could not link Test Plan {} and Test Execution {}", testPlanKey, executionKey);
        return false;
    }

    /**
     * Helper method to attempt one link and report success/failure.
     *
     * <p>This method attempts to create a single link between two Jira issues using
     * the specified link type. It handles exceptions gracefully and logs the results
     * for debugging purposes.</p>
     *
     * <p>The method validates all input parameters before attempting the link operation
     * to ensure data integrity and prevent invalid operations.</p>
     *
     * @param jira The JiraServiceProvider instance for Jira operations
     * @param fromKey The source issue key (e.g., "TONIC-14630")
     * @param toKey The target issue key (e.g., "TONIC-14647")
     * @param linkType The type of link to create (e.g., "Parent-Child", "Relates")
     * @return true if the link was successfully created, false otherwise
     */
    private static boolean tryLink(JiraServiceProvider jira, String fromKey, String toKey, String linkType) {
        // Input validation
        XrayValidationUtils.requireNonNull(jira, "jira");
        XrayValidationUtils.requireValidJiraKey(fromKey, "fromKey");
        XrayValidationUtils.requireValidJiraKey(toKey, "toKey");
        XrayValidationUtils.requireNonBlank(linkType, "linkType");

        try {
            jira.linkIssues(fromKey, toKey, linkType);
            return true;
        } catch (Exception e) {
            logger.debug("[XRAY] Link failed ({} -> {}, type: {}): {}", fromKey, toKey, linkType, e.getMessage());
            return false;
        }
    }

    /**
     * Authenticates with Xray Cloud API and returns a bearer token.
     *
     * <p>This method performs OAuth2 client credentials flow authentication with Xray Cloud.
     * It uses the configured client ID and client secret to obtain an access token.</p>
     *
     * <p>The authentication process:</p>
     * <ul>
     *   <li>Validates that client credentials are properly configured</li>
     *   <li>Sends a POST request to the Xray authentication endpoint</li>
     *   <li>Returns the bearer token for subsequent API calls</li>
     *   <li>Throws IOException if authentication fails</li>
     * </ul>
     *
     * <p>The returned token should be used in the Authorization header for all subsequent
     * Xray API calls as "Bearer {token}".</p>
     *
     * @return A bearer token string for Xray API authentication
     * @throws IOException if the authentication request fails or returns an error
     * @throws XrayException if the client credentials are not properly configured
     */
    public static String authenticate() throws IOException {
                // Input validation
        XrayValidationUtils.requireNonBlank(clientId, "clientId");
        XrayValidationUtils.requireNonBlank(clientSecret, "clientSecret");
        XrayValidationUtils.requireValidUrl(authEndpoint, "authEndpoint");

        JSONObject body = new JSONObject();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);

        XrayHttpClient httpClient = new XrayHttpClient(authEndpoint, "", "None");
        XrayHttpClient.HttpResponse response = httpClient.post("", body);

        logger.debug("Auth response code: {}", response.getStatusCode());
        logger.debug("Auth response body: {}", response.getBody());

        if (response.getStatusCode() != 200) {
            throw new IOException("Failed to authenticate with Xray: HTTP " + response.getStatusCode());
        }

        // Token is returned as a quoted string, e.g. "token..."
        return response.getBody().replaceAll("\"", "");
    }

    /**
     * Sends a test execution result to Xray Cloud.
     *
     * <p>This is a convenience method that calls {@link #logTestExecution(String, String, String, String)}
     * with a null defect key.</p>
     *
     * @param testKey The Xray test key (e.g., "CALC-123")
     * @param status The test status ("PASSED" or "FAILED")
     * @param comment Optional comment or error message
     */
    public static void logTestExecution(String testKey, String status, String comment) {
        logTestExecution(testKey, status, comment, null);
    }

    /**
     * Sends a test execution result to Xray Cloud.
     *
     * <p>This method is the core method for reporting test results to Xray. It:</p>
     * <ul>
     *   <li>Validates the test key and status parameters</li>
     *   <li>Determines the appropriate test execution key to use</li>
     *   <li>Creates a new test execution if none exists</li>
     *   <li>Builds the Xray Cloud API payload using {@link TestExecutionPayloadBuilder}</li>
     *   <li>Sends the result to Xray via HTTP POST</li>
     *   <li>Handles errors gracefully with detailed logging</li>
     * </ul>
     *
     * <p>The method automatically handles test execution key management:
     * - Uses the currently set key if available
     * - Falls back to system property {@code execId} if set
     * - Falls back to configuration property {@code jira.exec.id} if set
     * - Creates a new test execution if no key is available</p>
     *
     * @param testKey The Xray test key (e.g., "CALC-123")
     * @param status The test status ("PASSED" or "FAILED")
     * @param comment Optional comment or error message
     * @param defectKey Optional Jira bug key to be added under Defects (e.g., "TONIC-9999")
     * @throws XrayException if the test key or status is invalid
     */
    public static void logTestExecution(String testKey, String status, String comment, String defectKey) {
        // Input validation
        XrayValidationUtils.requireNonBlank(testKey, "testKey");
        XrayValidationUtils.requireNonBlank(status, "status");

        if (!xrayEnabled) {
            logger.debug("Xray integration is disabled (xray_enabled=false). Skipping Xray reporting for testKey: {}", testKey);
            return;
        }

        try {
            String token = authenticate();
            String execKey = determineExecutionKey();

            if (execKey == null) {
                logger.error("Failed to determine test execution key for testKey: {}", testKey);
                return;
            }

            JSONObject payload = buildTestExecutionPayload(execKey, testKey, status, comment, defectKey);
            sendTestExecutionToXray(payload, token);

        } catch (IOException e) {
            logger.error("Network error while logging test execution for testKey: {}, status: {}", testKey, status, e);
        } catch (JSONException e) {
            logger.error("JSON formatting error while logging test execution for testKey: {}, status: {}", testKey, status, e);
        } catch (Exception e) {
            logger.error("Unexpected error while logging test execution for testKey: {}, status: {}", testKey, status, e);
        }
    }

    /**
     * Determines the appropriate test execution key to use.
     *
     * @return The test execution key, or null if none can be determined
     */
    private static String determineExecutionKey() {
        // First check if we already have a key set
        String execKey = testExecutionKey;
        if (execKey != null && !execKey.trim().isEmpty()) {
            return execKey;
        }

        // Check if execId was provided via system property or config properties
        String execId = getExecIdFromSystemOrConfig();
        if (execId != null && !execId.trim().isEmpty()) {
            logger.warn("testExecutionKey is null but execId was provided: {}. Using provided execId.", execId);
            setTestExecutionKey(execId);
            return execId;
        }

        // Create a new test execution if no key is available
        logger.warn("testExecutionKey is null or empty, creating new test execution");
        String newExecutionKey = createNewTestExecution();
        if (newExecutionKey != null) {
            setTestExecutionKey(newExecutionKey);
            return newExecutionKey;
        }

        logger.error("Failed to create new test execution");
        return null;
    }

    /**
     * Gets the execId from system properties or configuration.
     *
     * @return The execId value, or null if not found
     */
    private static String getExecIdFromSystemOrConfig() {
        // Check system property first (highest priority)
        String execId = System.getProperty("execId");
        if (execId != null && !execId.trim().isEmpty()) {
            return execId;
        }

        // Then check config properties
        return com.vrize.utils.PropertyBuilder.getPropValue(ConfigProperties.JIRA_EXEC_ID);
    }

    /**
     * Builds the test execution payload for Xray Cloud API.
     *
     * @param execKey The test execution key
     * @param testKey The test key
     * @param status The test status
     * @param comment Optional comment
     * @param defectKey Optional defect key
     * @return JSONObject containing the payload
     */
    private static JSONObject buildTestExecutionPayload(String execKey, String testKey, String status, String comment, String defectKey) {
        TestExecutionPayloadBuilder payloadBuilder = TestExecutionPayloadBuilder.newBuilder();
        return payloadBuilder
            .withTestExecutionKey(execKey)
            .addTest(testKey, status, comment)
            .addDefect(defectKey)
            .build();
    }

    /**
     * Sends the test execution payload to Xray Cloud.
     *
     * @param payload The JSON payload to send
     * @param token The authentication token
     * @throws IOException if the request fails
     */
    private static void sendTestExecutionToXray(JSONObject payload, String token) throws IOException {
        // Validate execution endpoint
        XrayValidationUtils.requireValidUrl(executionEndpoint, "executionEndpoint");

        // Use XrayHttpClient for HTTP operations
        XrayHttpClient httpClient = new XrayHttpClient(executionEndpoint, token, "Bearer");
        XrayHttpClient.HttpResponse response = httpClient.post("", payload);

        logger.debug("Log test execution response code: {}", response.getStatusCode());
        logger.debug("Log test execution response body: {}", response.getBody());

        if (!response.isSuccess()) {
            throw new IOException("Failed to log test execution to Xray: HTTP " + response.getStatusCode());
        }
    }

    /**
     * Associates tests with the current test plan using Xray REST API.
     *
     * <p>This method automatically uses the current test plan ID from configuration
     * or system properties and delegates to {@link #associateTestsWithTestPlan(String, List, List)}.</p>
     *
     * <p>The method retrieves the current test plan key using the same priority system
     * as other methods: system properties first, then configuration.</p>
     *
     * <p>Based on Xray REST API documentation:
     * <a href="https://docs.getxray.app/space/XRAY/301474100/Test+Plans+-+REST">Test Plans - REST</a></p>
     *
     * @param testsToAdd List of test keys to add to the current test plan
     * @param testsToRemove List of test keys to remove from the current test plan
     * @return true if successful, false otherwise
     */
    public static boolean associateTestsWithCurrentTestPlan(List<String> testsToAdd, List<String> testsToRemove) {
        // Get the current test plan ID from config or system properties
        String currentTestPlanKey = com.vrize.utils.PropertyBuilder.getPropValue(com.vrize.enums.ConfigProperties.JIRA_PLAN_ID);

        // Input validation
        XrayValidationUtils.requireValidJiraKey(currentTestPlanKey, "currentTestPlanKey");

        logger.debug("Using current test plan ID: {}", currentTestPlanKey);
        return associateTestsWithTestPlan(currentTestPlanKey, testsToAdd, testsToRemove);
    }

    /**
     * Associates tests with a specific test plan using Xray REST API.
     * Based on: https://docs.getxray.app/space/XRAY/301474100/Test+Plans+-+REST
     *
     * @param testPlanKey The key of the test plan (e.g., "TEST-123")
     * @param testsToAdd List of test keys to add to the test plan (e.g., ["CALC-14", "CALC-29"])
     * @param testsToRemove List of test keys to remove from the test plan (e.g., ["CALC-15", "CALC-50"])
     * @return true if successful, false otherwise
     */
    /**
     * Associates tests with a specific test plan using Xray REST API.
     *
     * <p>This method uses the Xray REST API to add or remove tests from a test plan.
     * It supports both adding new tests and removing existing ones in a single operation.</p>
     *
     * <p>The method:</p>
     * <ul>
     *   <li>Validates all input parameters including Jira key formats</li>
     *   <li>Loads Jira credentials from configuration</li>
     *   <li>Constructs the Xray REST API endpoint</li>
     *   <li>Creates a JSON payload with add/remove operations</li>
     *   <li>Uses Basic Authentication for Jira REST API calls</li>
     *   <li>Handles various response scenarios including warnings</li>
     * </ul>
     *
     * <p>Based on Xray REST API documentation:
     * <a href="https://docs.getxray.app/space/XRAY/301474100/Test+Plans+-+REST">Test Plans - REST</a></p>
     *
     * @param testPlanKey The key of the test plan (e.g., "TONIC-14630")
     * @param testsToAdd List of test keys to add to the test plan
     * @param testsToRemove List of test keys to remove from the test plan
     * @return true if successful, false otherwise
     */
    public static boolean associateTestsWithTestPlan(String testPlanKey, List<String> testsToAdd, List<String> testsToRemove) {
        // Input validation
        XrayValidationUtils.requireValidJiraKey(testPlanKey, "testPlanKey");

        if (testsToAdd != null) {
            for (String testKey : testsToAdd) {
                if (testKey != null && !testKey.trim().isEmpty()) {
                    XrayValidationUtils.requireValidJiraKey(testKey, "testKey in testsToAdd");
                }
            }
        }

        if (testsToRemove != null) {
            for (String testKey : testsToRemove) {
                if (testKey != null && !testKey.trim().isEmpty()) {
                    XrayValidationUtils.requireValidJiraKey(testKey, "testKey in testsToRemove");
                }
            }
        }

        try {
            // Load Jira credentials using PropertyBuilder
            String jiraUrl = com.vrize.utils.PropertyBuilder.getPropValue(com.vrize.enums.ConfigProperties.JIRA_URL);
            String encryptedEmail = com.vrize.utils.PropertyBuilder.getPropValue(com.vrize.enums.ConfigProperties.JIRA_EMAIL_ENCRYPTED);
            String encryptedToken = com.vrize.utils.PropertyBuilder.getPropValue(com.vrize.enums.ConfigProperties.JIRA_TOKEN_ENCRYPTED);

            // Validate configuration
            XrayValidationUtils.requireNonBlank(jiraUrl, "jiraUrl");
            XrayValidationUtils.requireNonBlank(encryptedEmail, "encryptedEmail");
            XrayValidationUtils.requireNonBlank(encryptedToken, "encryptedToken");

            // Decrypt credentials
            String email = com.vrize.utils.EncryptionUtils.decrypt(encryptedEmail);
            String token = com.vrize.utils.EncryptionUtils.decrypt(encryptedToken);

            // Construct the Xray API endpoint
            if (!jiraUrl.endsWith("/")) {
                jiraUrl += "/";
            }
            String apiUrl = jiraUrl + "rest/raven/1.0/api/testplan/" + testPlanKey + "/test";

            // Create JSON payload
            JSONObject payload = new JSONObject();

            if (testsToAdd != null && !testsToAdd.isEmpty()) {
                JSONArray addArray = new JSONArray();
                for (String testKey : testsToAdd) {
                    addArray.put(testKey);
                }
                payload.put("add", addArray);
            }

            if (testsToRemove != null && !testsToRemove.isEmpty()) {
                JSONArray removeArray = new JSONArray();
                for (String testKey : testsToRemove) {
                    removeArray.put(testKey);
                }
                payload.put("remove", removeArray);
            }

            logger.debug("Associating tests with test plan: {}", testPlanKey);
            logger.debug("Payload: {}", payload.toString(2));

                        // Validate Jira URL
            XrayValidationUtils.requireValidUrl(jiraUrl, "jiraUrl");

            // Validate Xray REST API endpoint
            String apiEndpoint = XrayConstants.XRAY_REST_API + testPlanKey + XrayConstants.XRAY_TEST_ENDPOINT;
            XrayValidationUtils.requireValidUrl(apiEndpoint, "apiEndpoint");

            // Use XrayHttpClient for HTTP operations
            String auth = java.util.Base64.getEncoder().encodeToString((email + ":" + token).getBytes(StandardCharsets.UTF_8));
            XrayHttpClient httpClient = new XrayHttpClient(jiraUrl, auth, "Basic");
            XrayHttpClient.HttpResponse response = httpClient.post(apiEndpoint, payload);

            logger.debug("Associate tests response code: {}", response.getStatusCode());
            logger.debug("Associate tests response body: {}", response.getBody());

            if (response.getStatusCode() == 200) {
                String responseBody = response.getBody();
                if (responseBody.trim().isEmpty() || responseBody.equals("[]")) {
                    logger.info("Successfully associated tests with test plan {}", testPlanKey);
                    return true;
                } else {
                    logger.warn("Tests association completed with warnings: {}", responseBody);
                    return true; // Still consider it successful even with warnings
                }
            } else {
                logger.error("Failed to associate tests with test plan: HTTP {}", response.getStatusCode());
                return false;
            }

        } catch (IOException e) {
            logger.error("Network error while associating tests with test plan: {}", e.getMessage(), e);
            return false;
        } catch (JSONException e) {
            logger.error("JSON formatting error while associating tests with test plan: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while associating tests with test plan: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Convenience method to add a single test to the current test plan.
     *
     * <p>This method automatically uses the current test plan ID from configuration
     * or system properties and delegates to {@link #associateTestsWithCurrentTestPlan(List, List)}
     * with the test to add and no tests to remove.</p>
     *
     * <p>This is a convenience wrapper for the common case of adding a single test
     * to the current test plan without removing any existing tests.</p>
     *
     * @param testKey The key of the test to add (e.g., "TONIC-7360")
     * @return true if successful, false otherwise
     */
    public static boolean addTestToCurrentTestPlan(String testKey) {
        // Input validation
        XrayValidationUtils.requireValidJiraKey(testKey, "testKey");
        return associateTestsWithCurrentTestPlan(java.util.Arrays.asList(testKey), null);
    }

    /**
     * Convenience method to remove a single test from the current test plan.
     *
     * <p>This method automatically uses the current test plan ID from configuration
     * or system properties and delegates to {@link #associateTestsWithCurrentTestPlan(List, List)}
     * with no tests to add and the test to remove.</p>
     *
     * <p>This is a convenience wrapper for the common case of removing a single test
     * from the current test plan without adding any new tests.</p>
     *
     * @param testKey The key of the test to remove (e.g., "TONIC-7360")
     * @return true if successful, false otherwise
     */
    public static boolean removeTestFromCurrentTestPlan(String testKey) {
        // Input validation
        XrayValidationUtils.requireValidJiraKey(testKey, "testKey");
        return associateTestsWithCurrentTestPlan(null, java.util.Arrays.asList(testKey));
    }

    /**
     * Convenience method to add a single test to a specific test plan.
     *
     * <p>This method delegates to {@link #associateTestsWithTestPlan(String, List, List)}
     * with the test to add and no tests to remove.</p>
     *
     * <p>This is a convenience wrapper for the common case of adding a single test
     * to a specific test plan without removing any existing tests.</p>
     *
     * @param testPlanKey The key of the test plan (e.g., "TONIC-14630")
     * @param testKey The key of the test to add (e.g., "TONIC-7360")
     * @return true if successful, false otherwise
     */
    public static boolean addTestToTestPlan(String testPlanKey, String testKey) {
        // Input validation
        XrayValidationUtils.requireValidJiraKey(testPlanKey, "testPlanKey");
        XrayValidationUtils.requireValidJiraKey(testKey, "testKey");
        return associateTestsWithTestPlan(testPlanKey, java.util.Arrays.asList(testKey), null);
    }

    /**
     * Convenience method to remove a single test from a specific test plan.
     *
     * <p>This method delegates to {@link #associateTestsWithTestPlan(String, List, List)}
     * with no tests to add and the test to remove.</p>
     *
     * <p>This is a convenience wrapper for the common case of removing a single test
     * from a specific test plan without adding any new tests.</p>
     *
     * @param testPlanKey The key of the test plan (e.g., "TONIC-14630")
     * @param testKey The key of the test to remove (e.g., "TONIC-7360")
     * @return true if successful, false otherwise
     */
    public static boolean removeTestFromTestPlan(String testPlanKey, String testKey) {
        // Input validation
        XrayValidationUtils.requireValidJiraKey(testPlanKey, "testPlanKey");
        XrayValidationUtils.requireValidJiraKey(testKey, "testKey");
        return associateTestsWithTestPlan(testPlanKey, null, java.util.Arrays.asList(testKey));
    }

    /**
     * Automatically associates the current test execution with the current test plan using Xray Cloud GraphQL API.
     *
     * <p>This method establishes a relationship between the current test execution and test plan in Xray,
     * allowing test results to be properly associated with test plans for reporting and analysis.</p>
     *
     * <p>The association process:</p>
     * <ul>
     *   <li>Retrieves the current test execution and test plan keys</li>
     *   <li>Fetches internal Jira issue IDs using Jira REST API</li>
     *   <li>Uses Xray Cloud GraphQL API v1 to create the association</li>
     *   <li>Handles GraphQL responses and validates success</li>
     * </ul>
     *
     * <p>This method should be called during test setup (e.g., in {@code @Before} hooks) after
     * both the test execution and test plan keys have been initialized.</p>
     *
     * <p>Note: This method uses the Xray Cloud GraphQL API which requires internal Jira issue IDs,
     * not the human-readable issue keys.</p>
     *
     * @return true if successfully associated, false otherwise
     * @throws XrayException if the execution or plan keys are invalid or missing
     */
    public static boolean associateCurrentTestExecutionWithCurrentTestPlan() {
        String currentExecutionKey = getTestExecutionKey();
        String currentTestPlanKey = getCurrentTestPlanKey();

        // Input validation
        XrayValidationUtils.requireValidJiraKey(currentExecutionKey, "currentExecutionKey");
        XrayValidationUtils.requireValidJiraKey(currentTestPlanKey, "currentTestPlanKey");

        logger.debug("Associating current test execution {} with current test plan {} via Xray Cloud API v2", currentExecutionKey, currentTestPlanKey);

        try {
            // Authenticate with Xray Cloud to get Bearer token
            String bearerToken = authenticate();
            if (bearerToken == null || bearerToken.trim().isEmpty()) {
                logger.error("Failed to authenticate with Xray Cloud");
                return false;
            }

            // First, get the internal Jira issue IDs using Jira REST API
            String testPlanInternalId = getJiraIssueInternalId(currentTestPlanKey, bearerToken);
            String testExecutionInternalId = getJiraIssueInternalId(currentExecutionKey, bearerToken);

            // Validate internal IDs
            XrayValidationUtils.requireNonBlank(testPlanInternalId, "testPlanInternalId");
            XrayValidationUtils.requireNonBlank(testExecutionInternalId, "testExecutionInternalId");

            logger.debug("Internal IDs - Test Plan: {} (key: {}), Test Execution: {} (key: {})", testPlanInternalId, currentTestPlanKey, testExecutionInternalId, currentExecutionKey);

            // Use Xray Cloud GraphQL API v1 for associating test executions with test plans
            String apiUrl = XrayConstants.XRAY_GRAPHQL_API;

            // Validate GraphQL API URL
            XrayValidationUtils.requireValidUrl(apiUrl, "apiUrl");

            // Create GraphQL mutation payload using internal IDs
            JSONObject payload = new JSONObject();
            String graphqlQuery = XrayConstants.GRAPHQL_MUTATION_TEMPLATE.replace("{}", testPlanInternalId).replace("{}", testExecutionInternalId);
            payload.put("query", graphqlQuery);

            // Validate GraphQL query
            XrayValidationUtils.requireNonBlank(graphqlQuery, "graphqlQuery");

            logger.debug("Xray Cloud GraphQL API URL: {}", apiUrl);
            logger.debug("GraphQL Query: {}", graphqlQuery);
            logger.debug("Payload: {}", payload.toString(2));

            // Use XrayHttpClient for HTTP operations
            XrayHttpClient httpClient = new XrayHttpClient("", bearerToken, "Bearer");
            XrayHttpClient.HttpResponse response = httpClient.post(apiUrl, payload);

            logger.debug("Xray Cloud GraphQL API response code: {}", response.getStatusCode());
            logger.debug("Xray Cloud GraphQL API response body: {}", response.getBody());

            if (response.isSuccess()) {
                // Parse GraphQL response to check for success
                try {
                    JSONObject responseJson = new JSONObject(response.getBody());
                    if (responseJson.has("data") && responseJson.getJSONObject("data").has("addTestExecutionsToTestPlan")) {
                        JSONObject result = responseJson.getJSONObject("data").getJSONObject("addTestExecutionsToTestPlan");
                        if (result.has("addedTestExecutions")) {
                            logger.info("Successfully associated test execution {} with test plan {} via Xray Cloud GraphQL API", currentExecutionKey, currentTestPlanKey);
                            if (result.has("warning") && !result.isNull("warning")) {
                                logger.warn("GraphQL response contains warnings: {}", result.get("warning"));
                            }
                            return true;
                        }
                    } else if (responseJson.has("errors")) {
                        logger.error("GraphQL errors: {}", responseJson.getJSONArray("errors").toString());
                        return false;
                    }
                } catch (JSONException parseException) {
                    logger.warn("Could not parse GraphQL response, but HTTP status is successful: {}", parseException.getMessage());
                    return true; // Assume success if HTTP is 200/201
                }
                return true;
            } else {
                logger.error("Failed to associate test execution with test plan via Xray Cloud GraphQL API: HTTP {}", response.getStatusCode());
                logger.error("Response: {}", response.getBody());
                return false;
            }

        } catch (IOException e) {
            logger.error("Network error while associating test execution with test plan via Xray Cloud GraphQL API: {}", e.getMessage(), e);
            return false;
        } catch (JSONException e) {
            logger.error("JSON formatting error while associating test execution with test plan via Xray Cloud GraphQL API: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while associating test execution with test plan via Xray Cloud GraphQL API: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gets the internal Jira issue ID using Jira REST API.
     *
     * <p>This method is required because Xray GraphQL API requires internal numeric IDs,
     * not the human-readable issue keys. It fetches the internal ID by making a REST API
     * call to Jira and parsing the response.</p>
     *
     * <p>The method:</p>
     * <ul>
     *   <li>Loads Jira credentials from configuration</li>
     *   <li>Constructs the Jira REST API endpoint for the issue</li>
     *   <li>Uses Basic Authentication with Jira credentials</li>
     *   <li>Parses the response to extract the internal ID</li>
     *   <li>Returns the numeric ID as a string</li>
     * </ul>
     *
     * <p>Note: The bearerToken parameter is not used for Jira REST API calls as Jira
     * requires its own authentication mechanism.</p>
     *
     * @param issueKey The Jira issue key (e.g., "TONIC-14630")
     * @param bearerToken The Bearer token (not used for Jira REST API)
     * @return The internal Jira issue ID as a string, or null if failed
     */
    private static String getJiraIssueInternalId(String issueKey, String bearerToken) {
        // Input validation
        XrayValidationUtils.requireValidJiraKey(issueKey, "issueKey");
        XrayValidationUtils.requireNonBlank(bearerToken, "bearerToken");

        try {
            // Load Jira credentials from config using XrayConfigManager
            Properties props = XrayConfigManager.getConfig();

            String jiraBaseUrl = props.getProperty("jira.url");
            String encryptedEmail = props.getProperty("jira.email.encrypted");
            String encryptedToken = props.getProperty("jira.token.encrypted");

            // Validate configuration
            XrayValidationUtils.requireNonBlank(jiraBaseUrl, "jiraBaseUrl");
            XrayValidationUtils.requireNonBlank(encryptedEmail, "encryptedEmail");
            XrayValidationUtils.requireNonBlank(encryptedToken, "encryptedToken");

            if (!jiraBaseUrl.endsWith("/")) {
                jiraBaseUrl += "/";
            }

            // Decrypt Jira credentials using the same approach as working methods
            String secretKey = getSecretKey(props);
            String email = com.vrize.utils.EncryptionUtils.debugDecrypt(encryptedEmail, secretKey, "getJiraIssueInternalId - email");
            String token = com.vrize.utils.EncryptionUtils.debugDecrypt(encryptedToken, secretKey, "getJiraIssueInternalId - token");

            // Validate Jira base URL
            XrayValidationUtils.requireValidUrl(jiraBaseUrl, "jiraBaseUrl");

            // Use Jira REST API v3 to get issue details
            String jiraApiUrl = jiraBaseUrl + XrayConstants.JIRA_REST_API_V3 + issueKey;

            // Validate Jira API URL
            XrayValidationUtils.requireValidUrl(jiraApiUrl, "jiraApiUrl");

            logger.debug("Getting internal ID for issue {} from: {}", issueKey, jiraApiUrl);

            // Use XrayHttpClient for HTTP operations
            String auth = java.util.Base64.getEncoder().encodeToString((email + ":" + token).getBytes(StandardCharsets.UTF_8));
            XrayHttpClient httpClient = new XrayHttpClient(jiraBaseUrl, auth, "Basic");
            XrayHttpClient.HttpResponse response = httpClient.get(XrayConstants.JIRA_REST_API_V3 + issueKey);

            logger.debug("Jira REST API response code: {}", response.getStatusCode());

            if (response.getStatusCode() == 200) {
                try {
                    JSONObject responseJson = new JSONObject(response.getBody());
                    String internalId = responseJson.getString("id");
                    logger.debug("Successfully got internal ID {} for issue {}", internalId, issueKey);
                    return internalId;
                } catch (JSONException parseException) {
                    logger.error("Failed to parse Jira REST API response: {}", parseException.getMessage());
                    return null;
                }
            } else {
                logger.error("Failed to get internal ID for issue {}: HTTP {}", issueKey, response.getStatusCode());
                logger.error("Response: {}", response.getBody());
                return null;
            }

        } catch (IOException e) {
            logger.error("Network error while getting internal Jira issue ID for {}: {}", issueKey, e.getMessage(), e);
            return null;
        } catch (JSONException e) {
            logger.error("JSON parsing error while getting internal Jira issue ID for {}: {}", issueKey, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error while getting internal Jira issue ID for {}: {}", issueKey, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets the current test execution key with priority-based fallback.
     *
     * <p>This method retrieves the test execution key following a specific priority order:</p>
     * <ol>
     *   <li>System property {@code execId} (highest priority)</li>
     *   <li>Configuration property {@code jira.exec.id}</li>
     *   <li>Stored in-memory value from current session</li>
     * </ol>
     *
     * <p>This priority system allows runtime overrides via system properties while maintaining
     * backward compatibility with configuration files and session state.</p>
     *
     * <p>All retrieved keys are validated to ensure they conform to Jira issue key format
     * (e.g., "TONIC-123").</p>
     *
     * @return The current test execution key, or null if not found in any source
     */
    public static String getCurrentTestExecutionKey() {
        // First check system property (highest priority)
        String execId = System.getProperty("execId");
        if (execId != null && !execId.trim().isEmpty()) {
            XrayValidationUtils.requireValidJiraKey(execId, "execId from system property");
            logger.debug("Using test execution key from system property: {}", execId);
            return execId;
        }

        // Then check config properties
        String configExecId = com.vrize.utils.PropertyBuilder.getPropValue(ConfigProperties.JIRA_EXEC_ID);
        if (configExecId != null && !configExecId.trim().isEmpty()) {
            XrayValidationUtils.requireValidJiraKey(configExecId, "execId from config");
            logger.debug("Using test execution key from config: {}", configExecId);
            return configExecId;
        }

        // Finally check stored value
        if (testExecutionKey != null && !testExecutionKey.trim().isEmpty()) {
            XrayValidationUtils.requireValidJiraKey(testExecutionKey, "stored testExecutionKey");
            logger.debug("Using stored test execution key: {}", testExecutionKey);
            return testExecutionKey;
        }

        logger.warn("No test execution key found in system properties, config, or stored value");
        return null;
    }

    /**
     * Gets the current test plan key with priority-based fallback.
     *
     * <p>This method retrieves the test plan key following a specific priority order:</p>
     * <ol>
     *   <li>System property {@code test.plan.key} (highest priority)</li>
     *   <li>Configuration property {@code jira.plan.id}</li>
     * </ol>
     *
     * <p>This priority system allows runtime overrides via system properties while maintaining
     * backward compatibility with configuration files.</p>
     *
     * <p>All retrieved keys are validated to ensure they conform to Jira issue key format
     * (e.g., "TONIC-123").</p>
     *
     * @return The current test plan key, or null if not found in any source
     */
    public static String getCurrentTestPlanKey() {
        // First check system property (highest priority)
        String planKey = System.getProperty("test.plan.key");
        if (planKey != null && !planKey.trim().isEmpty()) {
            XrayValidationUtils.requireValidJiraKey(planKey, "test.plan.key from system property");
            logger.debug("Using test plan key from system property: {}", planKey);
            return planKey;
        }

        // Then check config properties
        String configPlanKey = com.vrize.utils.PropertyBuilder.getPropValue(ConfigProperties.JIRA_PLAN_ID);
        if (configPlanKey != null && !configPlanKey.trim().isEmpty()) {
            XrayValidationUtils.requireValidJiraKey(configPlanKey, "jira.plan.id from config");
            logger.debug("Using test plan key from config: {}", configPlanKey);
            return configPlanKey;
        }

        logger.warn("No test plan key found in system properties or config");
        return null;
    }

    /**
     * Attaches a file (e.g., HTML report) to the current test execution in Xray Cloud.
     *
     * <p>This method uploads a file to the current test execution, making it available
     * as evidence in Xray test reports. It first attempts to use the Xray Cloud attachment API,
     * and falls back to Jira REST API if the Xray endpoint is not available.</p>
     *
     * <p>The attachment process:</p>
     * <ul>
     *   <li>Validates the file exists and is readable</li>
     *   <li>Retrieves the current test execution key</li>
     *   <li>Loads Jira credentials for authentication</li>
     *   <li>Attempts Xray Cloud attachment first</li>
     *   <li>Falls back to Jira REST API if Xray fails</li>
     *   <li>Logs success or failure with detailed information</li>
     * </ul>
     *
     * <p>Supported file types include HTML reports, screenshots, logs, and other test artifacts.</p>
     *
     * @param filePath Path to the file to attach (must exist and be readable)
     * @throws XrayException if the file path is invalid or file does not exist
     */
    public static void attachReportToTestExecution(String filePath) {
        // Input validation
        XrayValidationUtils.requireFileExists(filePath, "filePath");

        if (!xrayEnabled) {
            logger.debug("Xray integration is disabled (xray_enabled=false). Skipping report attachment.");
            return;
        }

        // Get execution key and validate
        String execKey = getExecutionKeyForAttachment();
        if (execKey == null) {
            logger.error("Test execution key is not set. Cannot attach report.");
            return;
        }
        
        try {
            // Attempt Xray Cloud attachment first
            attachToXray(execKey, filePath);
        } catch (Exception e) {
            logger.warn("Xray attachment failed, trying Jira fallback: {}", e.getMessage());
            // Fallback to Jira REST API
            attachToJiraFallback(execKey, filePath);
        }
    }

    /**
     * Gets the execution key for attachment operations.
     *
     * @return The test execution key, or null if not available
     */
    private static String getExecutionKeyForAttachment() {
        // Always read the key from file for robustness
        return readTestExecutionKeyFromFile();
    }

    /**
     * Attempts to attach a file to Xray Cloud using the Xray attachment API.
     *
     * @param execKey The test execution key
     * @param filePath Path to the file to attach
     * @throws IOException if the attachment fails
     */
    private static void attachToXray(String execKey, String filePath) throws IOException {
        // Load Jira credentials for authentication
        JiraCredentials credentials = loadJiraCredentials();

        // Validate Xray attachment API URL
        String attachmentUrl = XrayConstants.XRAY_ATTACHMENT_API.replace("{}", execKey);
        XrayValidationUtils.requireValidUrl(attachmentUrl, "attachmentUrl");

        // Validate file
        java.io.File file = new java.io.File(filePath);
        XrayValidationUtils.requireFileExists(filePath, "filePath");

        // Use XrayHttpClient for multipart file upload
        String boundary = XrayHttpClient.generateBoundary();
        String basicAuth = createBasicAuth(credentials.email, credentials.token);
        XrayHttpClient httpClient = new XrayHttpClient("", basicAuth, "Basic");
        XrayHttpClient.HttpResponse response = httpClient.postMultipart(
            attachmentUrl,
            file,
            boundary
        );

        logger.debug("Attach report response code: {}", response.getStatusCode());
        logger.debug("Attach report response body: {}", response.getBody());

        if (response.isSuccess()) {
            logger.info("Report attached to test execution {}", execKey);
        } else if (response.getStatusCode() == 404) {
            throw new IOException("Xray attachment endpoint returned 404, will try Jira fallback");
        } else {
            throw new IOException("Failed to attach report to Xray: HTTP " + response.getStatusCode());
        }
    }

    /**
     * Fallback method to attach report to Jira issue using Jira REST API.
     *
     * @param execKey The test execution key
     * @param filePath Path to the file to attach
     */
    private static void attachToJiraFallback(String execKey, String filePath) {
        try {
            JiraCredentials credentials = loadJiraCredentials();
            attachReportToJiraIssue(execKey, filePath, credentials.email, credentials.token);
        } catch (Exception e) {
            logger.error("Jira fallback attachment also failed for execution {}: {}", execKey, e.getMessage(), e);
        }
    }

    /**
     * Loads Jira credentials from configuration.
     *
     * @return JiraCredentials object containing email and token
     * @throws RuntimeException if credentials cannot be loaded
     */
    private static JiraCredentials loadJiraCredentials() {
        Properties props = XrayConfigManager.getConfig();
        if (props.isEmpty()) {
            throw new RuntimeException("Could not load config.properties for Jira credentials");
        }

        String encryptedEmail = props.getProperty("jira.email.encrypted");
        String encryptedToken = props.getProperty("jira.token.encrypted");
        String secretKey = getSecretKey(props);

        // Validate configuration
        XrayValidationUtils.requireNonBlank(encryptedEmail, "encryptedEmail");
        XrayValidationUtils.requireNonBlank(encryptedToken, "encryptedToken");
        XrayValidationUtils.requireNonBlank(secretKey, "secretKey");

        String email = com.vrize.utils.EncryptionUtils.debugDecrypt(encryptedEmail, secretKey, "attachReportToTestExecution - email");
        String token = com.vrize.utils.EncryptionUtils.debugDecrypt(encryptedToken, secretKey, "attachReportToTestExecution - token");

        return new JiraCredentials(email, token);
    }

    /**
     * Creates Basic Authentication header value.
     *
     * @param email The Jira user email
     * @param token The Jira API token
     * @return Base64 encoded Basic Auth string
     */
    private static String createBasicAuth(String email, String token) {
        return java.util.Base64.getEncoder().encodeToString((email + ":" + token).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Helper class to hold Jira credentials.
     */
    private static class JiraCredentials {
        final String email;
        final String token;

        JiraCredentials(String email, String token) {
            this.email = email;
            this.token = token;
        }
    }

    /**
     * Attaches a video file to a specific test run in Xray Cloud.
     *
     * <p>This method uploads a video recording to a specific test run within a test execution,
     * providing visual evidence of test execution for debugging and analysis purposes.</p>
     *
     * <p>The video attachment process:</p>
     * <ul>
     *   <li>Validates the test execution key, test key, and video file path</li>
     *   <li>Retrieves the test run ID using Xray Cloud API</li>
     *   <li>Uploads the video file to the test run</li>
     *   <li>Falls back to Jira REST API if Xray attachment fails</li>
     *   <li>Handles various error conditions gracefully</li>
     * </ul>
     *
     * <p>Video attachments are particularly useful for UI automation tests where visual
     * confirmation of test execution is valuable for debugging failures.</p>
     *
     * @param testExecKey The test execution key (e.g., TONIC-14378)
     * @param testKey The test key (e.g., TONIC-11579)
     * @param videoPath The path to the video file (must exist and be readable)
     * @throws XrayException if any of the parameters are invalid or missing
     */
    public static void attachVideoToTestRun(String testExecKey, String testKey, String videoPath) {
        // Input validation
        XrayValidationUtils.requireValidJiraKey(testExecKey, "testExecKey");
        XrayValidationUtils.requireValidJiraKey(testKey, "testKey");
        XrayValidationUtils.requireFileExists(videoPath, "videoPath");

        try {
            // 1. Get testRunId for the testExecKey and testKey
            String testRunId = getTestRunId(testExecKey, testKey);
            if (testRunId == null) {
                logger.error("No testRunId found for testExecKey={}, testKey={}", testExecKey, testKey);
                return;
            }

            logger.debug("testRunId for {} in {}: {}", testKey, testExecKey, testRunId);

            // 2. Upload video as attachment to test run
            boolean uploaded = uploadVideoToTestRun(testRunId, videoPath);
            if (!uploaded) {
                // Fallback: Use Jira REST API to attach to test issue
                logger.warn("Xray Cloud test run attachment failed. Trying Jira REST API fallback...");
                uploadVideoToJiraFallback(testKey, videoPath);
            }

        } catch (Exception e) {
            logger.error("Error uploading video to test run: testExecKey={}, testKey={}, videoPath={}", testExecKey, testKey, videoPath, e);
        }
    }

    /**
     * Gets the test run ID for a specific test execution and test key.
     *
     * @param testExecKey The test execution key
     * @param testKey The test key
     * @return The test run ID, or null if not found
     */
    private static String getTestRunId(String testExecKey, String testKey) {
        try {
            String apiUrl = XrayConstants.XRAY_TESTRUN_API + "?testExecKey=" + testExecKey + "&testKey=" + testKey;
            XrayValidationUtils.requireValidUrl(apiUrl, "apiUrl");

            String bearerToken = authenticate();
            XrayHttpClient httpClient = new XrayHttpClient("", bearerToken, "Bearer");
            XrayHttpClient.HttpResponse response = httpClient.get(apiUrl);

            if (!response.isSuccess()) {
                logger.error("Failed to get testRunId: {} - {}", response.getStatusCode(), response.getBody());
                return null;
            }

            return parseTestRunIdFromResponse(response.getBody());

        } catch (Exception e) {
            logger.error("Error getting testRunId: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parses the test run ID from the API response.
     *
     * @param responseBody The response body from the API
     * @return The test run ID, or null if parsing fails
     */
    private static String parseTestRunIdFromResponse(String responseBody) {
        try {
            org.json.JSONArray arr = new org.json.JSONArray(responseBody);
            if (arr.length() > 0) {
                org.json.JSONObject obj = arr.getJSONObject(0);
                return obj.getString("id");
            }
        } catch (Exception e) {
            logger.error("Error parsing testRunId: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Uploads a video file to a test run in Xray Cloud.
     *
     * @param testRunId The test run ID
     * @param videoPath The path to the video file
     * @return true if successful, false otherwise
     */
    private static boolean uploadVideoToTestRun(String testRunId, String videoPath) {
        try {
            String attachUrl = XrayConstants.XRAY_TESTRUN_ATTACHMENT_API.replace("{}", testRunId);
            XrayValidationUtils.requireValidUrl(attachUrl, "attachUrl");

            // Validate video file
            java.io.File videoFile = new java.io.File(videoPath);
            XrayValidationUtils.requireFileExists(videoPath, "videoPath");

            String boundary = XrayHttpClient.generateBoundary();
            String bearerToken = authenticate();
            XrayHttpClient uploadClient = new XrayHttpClient("", bearerToken, "Bearer");
            XrayHttpClient.HttpResponse uploadResponse = uploadClient.postVideoMultipart(attachUrl, videoFile, boundary);

            logger.debug("Attach video to test run response code: {}", uploadResponse.getStatusCode());
            logger.debug("Attach video to test run response body: {}", uploadResponse.getBody());

            if (uploadResponse.isSuccess()) {
                logger.info("Video evidence uploaded to test run {}", testRunId);
                return true;
            } else if (uploadResponse.getStatusCode() == 404) {
                logger.warn("Xray Cloud test run attachment endpoint returned 404");
                return false;
            } else {
                logger.error("Failed to upload video evidence to test run: {}", uploadResponse.getStatusCode());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error uploading video to test run: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Fallback method to upload video to Jira issue using Jira REST API.
     *
     * @param testKey The test key
     * @param videoPath The path to the video file
     */
    private static void uploadVideoToJiraFallback(String testKey, String videoPath) {
        try {
            JiraCredentials credentials = loadJiraCredentials();
            Properties props = XrayConfigManager.getConfig();
            String jiraBaseUrl = props.getProperty("jira.url");

            // Validate configuration
            XrayValidationUtils.requireNonBlank(jiraBaseUrl, "jiraBaseUrl");

            if (!jiraBaseUrl.endsWith("/")) {
                jiraBaseUrl += "/";
            }

            // Validate Jira attachment URL
            String jiraAttachUrl = XrayConstants.JIRA_REST_API_V3 + testKey + XrayConstants.JIRA_ATTACHMENTS_ENDPOINT;
            XrayValidationUtils.requireValidUrl(jiraAttachUrl, "jiraAttachUrl");

            // Use XrayHttpClient for Jira fallback
            java.io.File videoFile = new java.io.File(videoPath);
            String boundary = XrayHttpClient.generateBoundary();
            String auth = createBasicAuth(credentials.email, credentials.token);
            XrayHttpClient jiraClient = new XrayHttpClient(jiraBaseUrl, auth, "Basic");
            XrayHttpClient.HttpResponse jiraResponse = jiraClient.postVideoMultipart(
                jiraAttachUrl,
                videoFile,
                boundary
            );

            logger.debug("[JIRA] Attach video to test issue response code: {}", jiraResponse.getStatusCode());
            logger.debug("[JIRA] Attach video to test issue response body: {}", jiraResponse.getBody());

            if (jiraResponse.isSuccess()) {
                logger.info("[JIRA] Video evidence uploaded to test issue {}", testKey);
            } else {
                logger.error("[JIRA] Failed to upload video evidence to test issue: {}", jiraResponse.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error uploading video to test issue: testKey={}, videoPath={}", testKey, videoPath, e);
        }
    }

    /**
     * Gets the secret key for decryption operations.
     *
     * <p>This method retrieves the secret key used for decrypting sensitive configuration
     * values like Jira credentials. It prioritizes the project key from configuration
     * and falls back to a default value if not available.</p>
     *
     * <p>The secret key is used by the encryption utilities to decrypt sensitive
     * configuration values stored in encrypted format.</p>
     *
     * @param props The configuration properties object
     * @return The secret key for decryption, never null
     */
    private static String getSecretKey(Properties props) {
        XrayValidationUtils.requireNonNull(props, "props");

        String key = props.getProperty("jira.project.key");
        if (key != null && !key.trim().isEmpty()) {
            return key.trim();
        }
        return XrayConstants.DEFAULT_SECRET_KEY;
    }

    /**
     * Fallback method to attach report to Jira issue using Jira REST API.
     *
     * <p>This method is used as a fallback when the Xray Cloud attachment endpoint
     * returns a 404 error or is otherwise unavailable. It provides an alternative
     * way to attach files to Jira issues for evidence and documentation purposes.</p>
     *
     * <p>The attachment process:</p>
     * <ul>
     *   <li>Loads Jira base URL from configuration</li>
     *   <li>Constructs the Jira REST API attachment endpoint</li>
     *   <li>Uses Basic Authentication with provided credentials</li>
     *   <li>Uploads the file using multipart form data</li>
     *   <li>Logs the success or failure of the operation</li>
     * </ul>
     *
     * <p>This method ensures that file attachments are not lost even when
     * Xray Cloud integration has issues.</p>
     *
     * @param issueKey The Jira issue key (e.g., TONIC-14423)
     * @param filePath The path to the file to attach
     * @param email The Jira user email for authentication
     * @param token The Jira API token for authentication
     */
    private static void attachReportToJiraIssue(String issueKey, String filePath, String email, String token) {
        // Input validation
        XrayValidationUtils.requireValidJiraKey(issueKey, "issueKey");
        XrayValidationUtils.requireFileExists(filePath, "filePath");
        XrayValidationUtils.requireNonBlank(email, "email");
        XrayValidationUtils.requireNonBlank(token, "token");

        try {
            // Load Jira base URL from config using XrayConfigManager
            Properties props = XrayConfigManager.getConfig();
            
            String jiraBaseUrl = props.getProperty("jira.url");

            // Validate configuration
            XrayValidationUtils.requireNonBlank(jiraBaseUrl, "jiraBaseUrl");
            
            if (!jiraBaseUrl.endsWith("/")) {
                jiraBaseUrl += "/";
            }
            
            // Use XrayHttpClient for Jira REST API
            String jiraAttachUrl = jiraBaseUrl + XrayConstants.JIRA_REST_API_V3 + issueKey + XrayConstants.JIRA_ATTACHMENTS_ENDPOINT;
            XrayValidationUtils.requireValidUrl(jiraAttachUrl, "jiraAttachUrl");

            String auth = java.util.Base64.getEncoder().encodeToString((email + ":" + token).getBytes(java.nio.charset.StandardCharsets.UTF_8));

            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                logger.error("[JIRA] File not found: {}", filePath);
                return;
            }
            
            // Use XrayHttpClient for multipart file upload
            String boundary = XrayHttpClient.generateBoundary();
            XrayHttpClient jiraClient = new XrayHttpClient(jiraBaseUrl, auth, "Basic");
            XrayHttpClient.HttpResponse response = jiraClient.postMultipart(
                XrayConstants.JIRA_REST_API_V3 + issueKey + XrayConstants.JIRA_ATTACHMENTS_ENDPOINT,
                file,
                boundary
            );
            
            logger.debug("[JIRA] Attach report response code: {}", response.getStatusCode());
            logger.debug("[JIRA] Attach report response body: {}", response.getBody());
            
            if (response.isSuccess()) {
                logger.info("[JIRA] Report successfully attached to Jira issue {} via REST API", issueKey);
            } else {
                logger.error("[JIRA] Failed to attach report to Jira issue: HTTP {}", response.getStatusCode());
            }
            
        } catch (IOException e) {
            logger.error("Network error while attaching report to Jira issue via REST API: issueKey={}, filePath={}", issueKey, filePath, e);
        } catch (Exception e) {
            logger.error("Unexpected error while attaching report to Jira issue via REST API: issueKey={}, filePath={}", issueKey, filePath, e);
        }
    }

    // TODO: Add helper methods for mapping your test methods to Xray test keys if needed.

    /**
     * Debug method for testing decryption functionality.
     *
     * <p>This method is used for debugging purposes to verify that encrypted
     * configuration values can be properly decrypted using the configured secret key.</p>
     *
     * @param encryptedText The encrypted text to decrypt
     * @param secretKey The secret key to use for decryption
     * @param context The context for logging (e.g., method name)
     */
    private static void debugDecrypt(String encryptedText, String secretKey, String context) {
        try {
            String decrypted = com.vrize.utils.EncryptionUtils.debugDecrypt(encryptedText, secretKey, context);
            logger.debug("({}) Decryption successful: {}", context, decrypted);
        } catch (Exception e) {
            logger.warn("({}) Decryption failed: {}", context, e.getMessage());
        }
    }
}