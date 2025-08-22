package com.vrize.utils;

import com.vrize.enums.ConfigProperties;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Field;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class JiraServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(JiraServiceProvider.class);
    private static final String SECRET_KEY = "TonicAutomation";
    
    // Configuration properties
    private String jiraUrl;
    private String project;
    private String apiVersion;
    private String contentType;
    private String encoding;
    private int successResponseCode;
    private int linkCreatedResponseCode;
    private String linkTypesEndpoint;
    private String issueLinkEndpoint;
    
    private JiraClient jira;

    public JiraServiceProvider() {
        try {
            // Load configuration properties using PropertyBuilder with ConfigProperties enum
            this.jiraUrl = PropertyBuilder.getPropValue(ConfigProperties.JIRA_URL);
            this.project = PropertyBuilder.getPropValue(ConfigProperties.JIRA_PROJECT);
            this.apiVersion = PropertyBuilder.getPropValue(ConfigProperties.JIRA_API_VERSION, "2"); // Default to API v2
            this.contentType = PropertyBuilder.getPropValue(ConfigProperties.JIRA_CONTENT_TYPE, "application/json");
            this.encoding = PropertyBuilder.getPropValue(ConfigProperties.JIRA_ENCODING, "utf-8");
            this.successResponseCode = Integer.parseInt(PropertyBuilder.getPropValue(ConfigProperties.JIRA_SUCCESS_RESPONSE_CODE, "200"));
            this.linkCreatedResponseCode = Integer.parseInt(PropertyBuilder.getPropValue(ConfigProperties.JIRA_LINK_CREATED_RESPONSE_CODE, "201"));
            
            // Validate required configuration
            validateConfiguration();
            
            // Build API endpoints
            this.linkTypesEndpoint = jiraUrl + "rest/api/" + apiVersion + "/issueLinkType";
            this.issueLinkEndpoint = jiraUrl + "rest/api/" + apiVersion + "/issueLink";
            
            // Load and decrypt credentials
            String[] credentials = loadAndDecryptCredentials();
            String email = credentials[0];
            String token = credentials[1];
            
            BasicCredentials creds = new BasicCredentials(email, token);
            jira = new JiraClient(jiraUrl, creds);
            
            logger.info("JiraServiceProvider initialized successfully with URL: {}", jiraUrl);
        } catch (Exception e) {
            logger.error("Failed to initialize JiraServiceProvider with properties", e);
            throw new RuntimeException("Failed to initialize JiraServiceProvider with properties", e);
        }
    }

    /**
     * Validates that required configuration values are properly loaded.
     * @throws RuntimeException if required configuration is missing or invalid
     */
    private void validateConfiguration() {
        if (jiraUrl == null || jiraUrl.trim().isEmpty()) {
            throw new RuntimeException("Jira URL is required but not configured");
        }
        if (project == null || project.trim().isEmpty()) {
            throw new RuntimeException("Jira project is required but not configured");
        }
        if (apiVersion == null || apiVersion.trim().isEmpty()) {
            throw new RuntimeException("Jira API version is required but not configured");
        }
        
        // Validate URL format
        if (!jiraUrl.startsWith("http://") && !jiraUrl.startsWith("https://")) {
            throw new RuntimeException("Jira URL must start with http:// or https://");
        }
        
        // Ensure URL ends with slash for proper endpoint construction
        if (!jiraUrl.endsWith("/")) {
            jiraUrl = jiraUrl + "/";
        }
        
        logger.debug("Configuration validation passed - URL: {}, Project: {}, API Version: {}", jiraUrl, project, apiVersion);
    }

    /**
     * Gets the configurable parent key with priority order:
     * 1. System property (-Dparent.key="<Parent id>")
     * 2. Configuration file (jira_plan_id)
     * 
     * @return The parent key to use for linking
     */
    public static String getParentKey() {
        // Priority 1: System property override
        String systemParentKey = System.getProperty("parent.key");
        if (systemParentKey != null && !systemParentKey.trim().isEmpty()) {
            logger.info("Using parent key from system property: {}", systemParentKey);
            return systemParentKey.trim();
        }
        
        // Priority 2: Configuration file
        try {
            String configParentKey = PropertyBuilder.getPropValue(ConfigProperties.JIRA_PLAN_ID);
            if (configParentKey != null && !configParentKey.trim().isEmpty()) {
                logger.info("Using parent key from configuration: {}", configParentKey);
                return configParentKey.trim();
            }
        } catch (Exception e) {
            logger.warn("Could not read parent key from configuration, using default", e);
        }
        
        // No fallback available - throw exception
        throw new RuntimeException("No parent key available from system property or configuration file");
    }

    /**
     * Loads and decrypts Jira credentials from configuration.
     * @return String array containing [email, token]
     */
    private String[] loadAndDecryptCredentials() {
        String encryptedEmail = PropertyBuilder.getPropValue(ConfigProperties.JIRA_EMAIL_ENCRYPTED);
        String encryptedToken = PropertyBuilder.getPropValue(ConfigProperties.JIRA_TOKEN_ENCRYPTED);
        
        if (encryptedEmail == null || encryptedEmail.isEmpty() || encryptedToken == null || encryptedToken.isEmpty()) {
            throw new RuntimeException("Jira credentials not found in configuration");
        }
        
        String email = EncryptionUtils.decrypt(encryptedEmail, SECRET_KEY);
        String token = EncryptionUtils.decrypt(encryptedToken, SECRET_KEY);
        
        return new String[]{email, token};
    }

    public String createJiraTicket(String issueType, String summary, String description) {
        // Input validation
        if (issueType == null || issueType.trim().isEmpty()) {
            logger.error("Issue type cannot be null or empty");
            return null;
        }
        if (summary == null || summary.trim().isEmpty()) {
            logger.error("Summary cannot be null or empty");
            return null;
        }
        if (description == null) {
            description = ""; // Allow empty description
        }
        
        try {
            Issue.FluentCreate fleuntCreate = jira.createIssue(project, issueType)
                .field(Field.SUMMARY, summary)
                .field(Field.DESCRIPTION, description);
            
            Issue newIssue = fleuntCreate.execute();
            logger.info("New issue created in Jira with ID: {}", newIssue.getKey());
            return newIssue.getKey();
        } catch (JiraException e) {
            logger.error("Failed to create Jira ticket with summary: {}", summary, e);
            return null;
        }
    }

    /**
     * Gets available issue link types from Jira.
     * @return List of available link type names
     */
    public List<String> getAvailableLinkTypes() {
        List<String> linkTypes = new ArrayList<>();
        try {
            URL url = new URL(linkTypesEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            // Use Basic authentication
            String[] credentials = loadAndDecryptCredentials();
            String email = credentials[0];
            String token = credentials[1];
            String basicAuth = Base64.getEncoder().encodeToString((email + ":" + token).getBytes(encoding));
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            
            int status = conn.getResponseCode();

            // Read the response (success or error)
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            (status == successResponseCode ? conn.getInputStream() : conn.getErrorStream()),
                            encoding))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
            String body = sb.toString();

            if (status == successResponseCode && !body.isBlank()) {
                // JSON looks like: {"issueLinkTypes":[{"name":"Relates"}, {"name":"Blocks"}]}
                JSONObject root = new JSONObject(body);
                JSONArray types = root.optJSONArray("issueLinkTypes");
                if (types != null) {
                    for (int i = 0; i < types.length(); i++) {
                        String name = types.getJSONObject(i).optString("name", "");
                        if (!name.isEmpty()) {
                            linkTypes.add(name);
                            logger.info("Found link type: {}", name);
                        }
                    }
                }
                
                // Also check for "values" array structure (Jira API v2 format)
                if (linkTypes.isEmpty()) {
                    JSONArray values = root.optJSONArray("values");
                    if (values != null) {
                        for (int i = 0; i < values.length(); i++) {
                            String name = values.getJSONObject(i).optString("name", "");
                            if (!name.isEmpty()) {
                                linkTypes.add(name);
                                logger.info("Found link type: {}", name);
                            }
                        }
                    }
                }
                
                // Handle case where response is directly an array
                if (linkTypes.isEmpty() && root.length() == 0) {
                    try {
                        JSONArray directArray = new JSONArray(body);
                        for (int i = 0; i < directArray.length(); i++) {
                            String name = directArray.getJSONObject(i).optString("name", "");
                            if (!name.isEmpty()) {
                                linkTypes.add(name);
                                logger.info("Found link type: {}", name);
                            }
                        }
                    } catch (Exception e) {
                        logger.debug("Response is not a direct array format");
                    }
                }
                
                logger.info("Successfully parsed {} link types from Jira API", linkTypes.size());
            } else {
                logger.warn("Failed to fetch link types (status {}). Body: {}", status, body);
                
                // Use fallback types for non-success responses
                return getFallbackLinkTypes();
            }
            
        } catch (Exception e) {
            logger.error("Exception occurred while fetching available link types", e);
            // Don't crash - return fallback types
        }
        
        // If no link types were found from API, use fallback types
        if (linkTypes.isEmpty()) {
            logger.warn("No link types found from API, using fallback types");
            return getFallbackLinkTypes();
        }
        
        return linkTypes;
    }
    
    /**
     * Provides fallback link types when API call fails or returns no results.
     * @return List of common fallback link types
     */
    private List<String> getFallbackLinkTypes() {
        List<String> fallbackTypes = new ArrayList<>();
        fallbackTypes.add("Parent-Child");
        fallbackTypes.add("Relates");
        fallbackTypes.add("is child of");
        fallbackTypes.add("AgileTest");
        fallbackTypes.add("Blocks");
        fallbackTypes.add("is blocked by");
        fallbackTypes.add("Duplicate");
        fallbackTypes.add("is duplicated by");
        logger.info("Using {} fallback link types", fallbackTypes.size());
        return fallbackTypes;
    }

    public void linkIssues(String inwardKey, String outwardKey, String linkType) {
        // Input validation
        if (inwardKey == null || inwardKey.trim().isEmpty()) {
            logger.error("Inward issue key cannot be null or empty");
            return;
        }
        if (outwardKey == null || outwardKey.trim().isEmpty()) {
            logger.error("Outward issue key cannot be null or empty");
            return;
        }
        if (linkType == null || linkType.trim().isEmpty()) {
            logger.error("Link type cannot be null or empty");
            return;
        }

        try {
            // Build JSON payload safely using JSONObject
            JSONObject payload = new JSONObject();
            JSONObject typeObj = new JSONObject();
            typeObj.put("name", linkType);
            payload.put("type", typeObj);
            
            JSONObject inwardIssue = new JSONObject();
            inwardIssue.put("key", inwardKey);
            payload.put("inwardIssue", inwardIssue);
            
            JSONObject outwardIssue = new JSONObject();
            outwardIssue.put("key", outwardKey);
            payload.put("outwardIssue", outwardIssue);
            
            String json = payload.toString();

            URL url = new URL(issueLinkEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", contentType);
            
            // Use Basic authentication with already decrypted credentials
            String[] credentials = loadAndDecryptCredentials();
            String email = credentials[0];
            String token = credentials[1];
            String basicAuth = Base64.getEncoder().encodeToString((email + ":" + token).getBytes(encoding));
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(encoding);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != linkCreatedResponseCode) {
                // Read error response for better debugging
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        errorResponse.append(inputLine);
                    }
                }
                logger.error("Failed to link issues: HTTP {} - {}", responseCode, errorResponse.toString());
                return; // Return early instead of throwing exception
            }
            
            logger.info("Successfully linked {} to {} with link type: {}", inwardKey, outwardKey, linkType);
        } catch (Exception e) {
            logger.error("Failed to link issues: inwardKey={}, outwardKey={}, linkType={}", inwardKey, outwardKey, linkType, e);
            // Don't throw exception, just log the error
        }
    }
}
