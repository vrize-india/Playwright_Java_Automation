package com.vrize.constants;

public final class XrayConstants {
    // API Endpoints
    public static final String XRAY_GRAPHQL_API = "https://xray.cloud.getxray.app/api/v1/graphql";
    public static final String XRAY_ATTACHMENT_API = "https://xray.cloud.getxray.app/api/v2/issue/{}/attachments";
    public static final String XRAY_AUTH_ENDPOINT = "https://xray.cloud.getxray.app/api/oauth/token";
    public static final String XRAY_EXECUTION_ENDPOINT = "https://xray.cloud.getxray.app/api/v2/import/execution";
    public static final String XRAY_TESTRUN_API = "https://xray.cloud.getxray.app/api/v2/testrun";
    public static final String XRAY_TESTRUN_ATTACHMENT_API = "https://xray.cloud.getxray.app/api/v2/testrun/{}/attachments";
    
    // HTTP Headers
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary={}";
    public static final String ACCEPT_JSON = "application/json";
    public static final String X_ATLASSIAN_TOKEN = "X-Atlassian-Token";
    public static final String X_ATLASSIAN_TOKEN_VALUE = "no-check";
    
    // Log Prefixes
    public static final String LOG_PREFIX_XRAY = "[XRAY]";
    public static final String LOG_PREFIX_JIRA = "[JIRA]";
    public static final String LOG_PREFIX_DEBUG = "[DEBUG]";
    public static final String LOG_PREFIX_ERROR = "[ERROR]";
    public static final String LOG_PREFIX_WARN = "[WARN]";
    
    // File Operations
    public static final String LINE_FEED = "\r\n";
    public static final String BOUNDARY_PREFIX = "----WebKitFormBoundary";
    
    // Default Values
    public static final String DEFAULT_SECRET_KEY = "TonicAutomation";
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    public static final String VIDEO_CONTENT_TYPE = "video/webm";
    
    // Jira API
    public static final String JIRA_REST_API_V3 = "rest/api/3/issue/";
    public static final String JIRA_ATTACHMENTS_ENDPOINT = "/attachments";
    
    // Xray REST API
    public static final String XRAY_REST_API = "rest/raven/1.0/api/testplan/";
    public static final String XRAY_TEST_ENDPOINT = "/test";
    
    // GraphQL
    public static final String GRAPHQL_MUTATION_TEMPLATE = "mutation { addTestExecutionsToTestPlan(issueId: \"{}\", testExecIssueIds: [\"{}\"]) { addedTestExecutions warning } }";
    
    // File Paths
    public static final String TEST_EXECUTION_KEY_FILE = "target/testexecution.key";
    public static final String CONFIG_FILE = "config.properties";
    
    // Link Types
    public static final String[] PREFERRED_LINK_TYPES = {"Parent-Child", "Relates", "is child of", "AgileTest"};
    
    private XrayConstants() {
        // Utility class, prevent instantiation
    }
}

