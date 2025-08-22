package com.vrize.enums;

public enum ConfigProperties {

    // =================== DEVICE CONFIG ===================
    ANDROID("android"),
    IOS("ios"),
    DEFAULT_DEVICE("default_device"),
    DEVICE_NAME_ANDROID("devicenameandroid"),
    DEVICE_NAME_IOS("devicenameios"),
    PLATFORM_NAME_ANDROID("platformnameandroid"),
    PLATFORM_NAME_IOS("platformnameios"),
    PLATFORM_VERSION("platformversion"),
    DEVICE_ORIENTATION("deviceorientation"),
    TOGGLE_ENABLE("toggleenable"),

    // =================== SIMULATOR CONFIG ===================
    ANDROID_SIMULATOR_NAME("android_simulator_name"),
    IOS_SIMULATOR_NAME("ios_simulator_name"),

    // =================== CREDENTIALS ===================
    USERNAME("username"),
    ACCESS_KEY("accesskey"),
    PASSWORD_PREFIX("passwordprefix"),
    PASSWORD_IOS("password_ios"),
    PASSWORD_ANDROID("password_android"),

    // =================== BROWSER / PLATFORM ===================
    BROWSER("browser"),
    DEFAULT_BROWSER("default_browser"),
    ENV("env"),
    DEFAULT_PLATFORM("default_platform"),
    DEFAULT_RUN_MODE("default_run_mode"),
    REMOTE_RUN_MODE("remote_run_mode"),
    LOCAL_RUN_MODE("local_run_mode"),

    // =================== APPIUM CONFIG ===================
    APPIUM_BUILD("appiumbuild"),
    APPIUM_VERSION("appiumversion"),
    APPIUM_PATH("appium_path"),
    APPIUM_STARTUP_TIMEOUT("appium_startup_timeout"),
    APPIUM_CONNECTION_TIMEOUT("appium_connection_timeout"),
    APPIUM_READ_TIMEOUT("appium_read_timeout"),
    APPIUM_SLEEP_INTERVAL("appium_sleep_interval"),

    // =================== IOS CONFIG ===================
    AUTOMATION_NAME_IOS("automation_name_ios"),
    IOS_PORT("ios_port"),
    IOS_AUTO_ACCEPT_ALERTS("ios_auto_accept_alerts"),
    IOS_MAX_TYPING_FREQUENCY("ios_max_typing_frequency"),
    IOS_NO_RESET("ios_no_reset"),
    IOS_FULL_RESET("ios_full_reset"),
    IOS_SIMULATOR_STARTUP_TIMEOUT("ios_simulator_startup_timeout"),
    IOS_ALERT_SELECTOR("ios_alert_selector"),
    APP_IOS("app_ios"),

    // =================== ANDROID CONFIG ===================
    AUTOMATION_NAME_ANDROID("automation_name_android"),
    ANDROID_AUTO_GRANT_PERMISSIONS("android_auto_grant_permissions"),
    ANDROID_NO_RESET("android_no_reset"),
    ANDROID_FULL_RESET("android_full_reset"),
    ANDROID_SYSTEM_PORT("android_system_port"),
    APP_ANDROID("app_android"),

    // =================== NETWORK CONFIG ===================
    IP_ADDRESS("ip_address"),
    REMOTE_SERVER_URL("remote_server_url"),
    PORT("port"),

    // =================== WAIT / TIMEOUT CONFIG ===================
    TIMEOUT("timeout"),
    WAIT_MEDIUM("wait_medium"),
    WAIT_LONG("wait_long"),

    // =================== VIDEO CONFIG ===================
    VIDEO_DIRECTORY("video_directory"),
    VIDEO_DELETE_AFTER_UPLOAD("video_delete_after_upload"),

    // =================== SCREENSHOT CONFIG ===================
    MOBILE_SCREENSHOTS_ENABLED("mobile_screenshots_enabled"),
    ATTACH_ALL_SCREENSHOTS("attach_all_screenshots"),
    PASSED_STEPS_SCREENSHOTS("passedstepsscreenshots"),
    SNAPSHOT("snapshot"),

    // =================== LOGGING CONFIG ===================
    LOG_DIRECTORY("log_directory"),
    BUG_LOGGING_ENABLED("bug_logging_enabled"),
    OVERRIDE_REPORTS("overridereports"),

    // =================== XRAY CONFIG ===================
    XRAY_ENABLED("xray_enabled"),
    XRAY_CLIENT_ID("xray.clientId"),
    XRAY_CLIENT_SECRET("xray.clientSecret"),
    XRAY_AUTH_ENDPOINT("xray.authEndpoint"),
    XRAY_EXECUTION_ENDPOINT("xray.executionEndpoint"),
    JIRA_EXEC_ID("execId"),

    // =================== HEADLESS CONFIG ===================
    HEADLESS_MODE("headless_mode"),

    // =================== FILE PATH CONFIG ===================
    CONFIG_FILE_PATH("config_file_path"),
    ENV_FILE_PATH("env_file_path"),
    PLATFORM_CONFIG_FILE_PATH("platform_config_file_path"),

    // =================== URL CONFIG ===================
    URL_PROPERTY_NAME("url_property_name"),

    // =================== JIRA CONFIG ===================
    JIRA_URL("jira.url"),
    JIRA_EMAIL_ENCRYPTED("jira.email.encrypted"),
    JIRA_TOKEN_ENCRYPTED("jira.token.encrypted"),
    JIRA_PROJECT("jira.project"),
    JIRA_REPORTER("jira.reporter"),
    JIRA_PROJECT_KEY("jira.project.key"),
    JIRA_API_VERSION("jira_api_version"),
    JIRA_CONTENT_TYPE("jira_content_type"),
    JIRA_ENCODING("jira_encoding"),
    JIRA_SUCCESS_RESPONSE_CODE("jira_success_response_code"),
    JIRA_LINK_CREATED_RESPONSE_CODE("jira_link_created_response_code"),
    JIRA_PLAN_ID("jira_plan_id"),

    // =================== RETRY CONFIG ===================
    RETRY_COUNT("retry.count"),
    RETRY_ENABLED("retry.enabled");

    private final String propertyName;

    ConfigProperties(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
