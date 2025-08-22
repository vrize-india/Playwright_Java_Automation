package com.tonic.constants;

import java.nio.file.Path;
import java.nio.file.Paths;
import com.tonic.enums.ConfigProperties;
import com.tonic.utils.PropertyBuilder;
import com.tonic.utils.ConfigManager;

/**
 * Framework constants class containing only static final values and configuration getters.
 * This class provides a centralized location for accessing framework configuration values
 * through a consistent API. All dynamic configuration logic has been moved to ConfigManager.
 * 
 * <p>This class follows the principle of separation of concerns by keeping only true constants
 * and delegating dynamic configuration retrieval to appropriate utility classes.</p>
 * 
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Platform-neutral path handling using {@link java.nio.file.Paths}</li>
 *   <li>Consistent configuration access through {@link ConfigProperties} enum</li>
 *   <li>Type-safe property retrieval with automatic conversion</li>
 *   <li>Centralized configuration management</li>
 * </ul>
 * 
 * <p><strong>Usage Examples:</strong></p>
 * <pre>{@code
 * // Get environment configuration
 * String env = FrameworkConstants.getEnvironment();
 * 
 * // Get browser configuration
 * String browser = FrameworkConstants.getBrowser();
 * 
 * // Get timeout values
 * int timeout = FrameworkConstants.getExplicitWait();
 * 
 * // Get boolean configurations
 * boolean headless = FrameworkConstants.isHeadlessMode();
 * }</pre>
 * 
 * @author Gaurav Purwar
 * @since 1.0
 * @see ConfigManager
 * @see PropertyBuilder
 * @see ConfigProperties
 */
public class FrameworkConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FrameworkConstants() {
        // Utility class - prevent instantiation
    }

    /**
     * Static file path constant for the resources directory.
     * Uses platform-neutral path construction with {@link Paths#get(String, String...)}.
     */
    private static final Path RESOURCES_PATH = Paths.get(System.getProperty("user.dir"), "src", "main", "resources");

    // ==================== CONFIGURATION GETTERS ====================

    /**
     * Gets the current environment configuration value.
     * Priority: System property "env" > PropertyBuilder value
     * 
     * @return The environment value (e.g., "dev", "test", "prod")
     * @see ConfigManager#getEnvironment()
     */
    public static String getEnvironment() {
        return ConfigManager.getEnvironment();
    }

    /**
     * Gets the current device configuration value.
     * Priority: System property "device" > PropertyBuilder value
     * 
     * @return The device value (e.g., "android", "ios")
     * @see ConfigManager#getDevice()
     */
    public static String getDevice() {
        return ConfigManager.getDevice();
    }

    /**
     * Gets the current browser configuration value.
     * Priority: System property "browser" > PropertyBuilder value
     * 
     * @return The browser value (e.g., "chrome", "firefox", "safari")
     * @see ConfigManager#getBrowser()
     */
    public static String getBrowser() {
        return ConfigManager.getBrowser();
    }

    // ==================== APPLICATION FILE PATHS ====================

    /**
     * Gets the Android application file path from configuration.
     * 
     * @return The path to the Android application file (.apk)
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#APP_ANDROID
     */
    public static String getAppAndroidFilePath() {
        return getStringProperty(ConfigProperties.APP_ANDROID);
    }

    /**
     * Gets the iOS application file path from configuration.
     * 
     * @return The path to the iOS application file (.app or .ipa)
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#APP_IOS
     */
    public static String getAppIosFilePath() {
        return getStringProperty(ConfigProperties.APP_IOS);
    }

    // ==================== PORT CONFIGURATIONS ====================

    /**
     * Gets the Android device port from configuration.
     * 
     * @return The port number for Android device communication
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     * @see ConfigProperties#PORT
     */
    public static int getAndroidPort() {
        return getIntProperty(ConfigProperties.PORT);
    }

    /**
     * Gets the iOS device port from configuration.
     * 
     * @return The port number for iOS device communication
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     * @see ConfigProperties#IOS_PORT
     */
    public static int getIosPort() {
        return getIntProperty(ConfigProperties.IOS_PORT);
    }

    // ==================== NETWORK CONFIGURATIONS ====================

    /**
     * Gets the IP address from configuration.
     * 
     * @return The IP address for device/server communication
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#IP_ADDRESS
     */
    public static String getIpAddress() {
        return getStringProperty(ConfigProperties.IP_ADDRESS);
    }

    /**
     * Gets the Appium path from configuration.
     * 
     * @return The path to the Appium executable
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#APPIUM_PATH
     */
    public static String getAppiumPath() {
        return getStringProperty(ConfigProperties.APPIUM_PATH);
    }

    // ==================== WAIT CONFIGURATIONS ====================

    /**
     * Gets the explicit wait timeout from configuration.
     * 
     * @return The explicit wait timeout in milliseconds
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     * @see ConfigProperties#TIMEOUT
     */
    public static int getExplicitWait() {
        return getIntProperty(ConfigProperties.TIMEOUT);
    }

    /**
     * Gets the medium wait timeout from configuration.
     * 
     * @return The medium wait timeout in milliseconds
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     * @see ConfigProperties#WAIT_MEDIUM
     */
    public static int getMediumWait() {
        return getIntProperty(ConfigProperties.WAIT_MEDIUM);
    }

    /**
     * Gets the long wait timeout from configuration.
     * 
     * @return The long wait timeout in milliseconds
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     * @see ConfigProperties#WAIT_LONG
     */
    public static int getLongWait() {
        return getIntProperty(ConfigProperties.WAIT_LONG);
    }

    // ==================== APPIUM CONFIGURATIONS ====================

    /**
     * Gets the Appium startup timeout from configuration.
     * 
     * @return The Appium server startup timeout in milliseconds
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     * @see ConfigProperties#APPIUM_STARTUP_TIMEOUT
     */
    public static int getAppiumStartupTimeout() {
        return getIntProperty(ConfigProperties.APPIUM_STARTUP_TIMEOUT);
    }

    /**
     * Gets the Appium connection timeout from configuration.
     * 
     * @return The Appium connection timeout in milliseconds
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     * @see ConfigProperties#APPIUM_CONNECTION_TIMEOUT
     */
    public static int getAppiumConnectionTimeout() {
        return getIntProperty(ConfigProperties.APPIUM_CONNECTION_TIMEOUT);
    }

    /**
     * Gets the Appium read timeout from configuration.
     * 
     * @return The Appium read timeout in milliseconds
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     * @see ConfigProperties#APPIUM_READ_TIMEOUT
     */
    public static int getAppiumReadTimeout() {
        return getIntProperty(ConfigProperties.APPIUM_READ_TIMEOUT);
    }

    /**
     * Gets the Appium sleep interval from configuration.
     * 
     * @return The Appium sleep interval in milliseconds
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     * @see ConfigProperties#APPIUM_SLEEP_INTERVAL
     */
    public static int getAppiumSleepInterval() {
        return getIntProperty(ConfigProperties.APPIUM_SLEEP_INTERVAL);
    }

    // ==================== PLATFORM CONFIGURATIONS ====================

    /**
     * Gets the default platform from configuration.
     * 
     * @return The default platform value (e.g., "WEB", "MOBILE", "API")
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#DEFAULT_PLATFORM
     */
    public static String getDefaultPlatform() {
        return getStringProperty(ConfigProperties.DEFAULT_PLATFORM);
    }

    /**
     * Gets the default run mode from configuration.
     * 
     * @return The default run mode value (e.g., "local", "remote")
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#DEFAULT_RUN_MODE
     */
    public static String getDefaultRunMode() {
        return getStringProperty(ConfigProperties.DEFAULT_RUN_MODE);
    }

    // ==================== VIDEO CONFIGURATIONS ====================

    /**
     * Gets the video directory path from configuration.
     * 
     * @return The directory path for storing video recordings
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#VIDEO_DIRECTORY
     */
    public static String getVideoDirectory() {
        return getStringProperty(ConfigProperties.VIDEO_DIRECTORY);
    }

    /**
     * Gets the video delete after upload setting from configuration.
     * 
     * @return {@code true} if videos should be deleted after upload, {@code false} otherwise
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#VIDEO_DELETE_AFTER_UPLOAD
     */
    public static boolean isVideoDeleteAfterUpload() {
        return Boolean.parseBoolean(PropertyBuilder.getPropValue(ConfigProperties.VIDEO_DELETE_AFTER_UPLOAD));
    }

    // ==================== LOGGING CONFIGURATIONS ====================

    /**
     * Gets the log directory path from configuration.
     * 
     * @return The directory path for storing log files
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#LOG_DIRECTORY
     */
    public static String getLogDirectory() {
        return getStringProperty(ConfigProperties.LOG_DIRECTORY);
    }

    // ==================== MOBILE CONFIGURATIONS ====================

    /**
     * Gets the mobile screenshots enabled setting from configuration.
     * 
     * @return {@code true} if mobile screenshots are enabled, {@code false} otherwise
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#MOBILE_SCREENSHOTS_ENABLED
     */
    public static boolean isMobileScreenshotsEnabled() {
        return Boolean.parseBoolean(PropertyBuilder.getPropValue(ConfigProperties.MOBILE_SCREENSHOTS_ENABLED));
    }

    // ==================== SCREENSHOT CONFIGURATIONS ====================

    /**
     * Gets the attach all screenshots setting from configuration.
     * 
     * @return {@code true} if all screenshots should be attached, {@code false} otherwise
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#ATTACH_ALL_SCREENSHOTS
     */
    public static boolean isAttachAllScreenshots() {
        return Boolean.parseBoolean(PropertyBuilder.getPropValue(ConfigProperties.ATTACH_ALL_SCREENSHOTS));
    }

    /**
     * Gets the bug logging enabled setting from configuration.
     * 
     * @return {@code true} if bug logging is enabled, {@code false} otherwise
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#BUG_LOGGING_ENABLED
     */
    public static boolean isBugLoggingEnabled() {
        return Boolean.parseBoolean(PropertyBuilder.getPropValue(ConfigProperties.BUG_LOGGING_ENABLED));
    }

    // ==================== XRAY CONFIGURATIONS ====================

    /**
     * Gets the Xray integration enabled setting from configuration.
     * 
     * @return {@code true} if Xray integration is enabled, {@code false} otherwise
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#XRAY_ENABLED
     */
    public static boolean isXrayEnabled() {
        return Boolean.parseBoolean(PropertyBuilder.getPropValue(ConfigProperties.XRAY_ENABLED));
    }

    // ==================== HEADLESS MODE CONFIGURATIONS ====================

    /**
     * Gets the headless mode setting from configuration.
     * 
     * @return {@code true} if headless mode is enabled, {@code false} otherwise
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#HEADLESS_MODE
     */
    public static boolean isHeadlessMode() {
        return Boolean.parseBoolean(PropertyBuilder.getPropValue(ConfigProperties.HEADLESS_MODE));
    }

    // ==================== CONFIGURATION FILE PATHS ====================

    /**
     * Gets the main configuration file path from configuration.
     * 
     * @return The path to the main configuration file
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#CONFIG_FILE_PATH
     */
    public static String getPropertyFilePath() {
        return getStringProperty(ConfigProperties.CONFIG_FILE_PATH);
    }

    /**
     * Gets the environment configuration file path from configuration.
     * 
     * @return The path to the environment configuration file
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#ENV_FILE_PATH
     */
    public static String getEnvFilePath() {
        return getStringProperty(ConfigProperties.ENV_FILE_PATH);
    }

    /**
     * Gets the platform configuration file path from configuration.
     * 
     * @return The path to the platform configuration file
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is not found
     * @see ConfigProperties#PLATFORM_CONFIG_FILE_PATH
     */
    public static String getPlatformConfigFilePath() {
        return getStringProperty(ConfigProperties.PLATFORM_CONFIG_FILE_PATH);
    }

    // ==================== LEGACY METHODS ====================
    /**
     * Gets the resources path as a string.
     * This is a legacy method maintained for backward compatibility.
     * 
     * @return The resources path as a string
     * @deprecated Use {@link #RESOURCES_PATH} directly if you need the Path object,
     *             or this method if you specifically need a string representation
     */
    @Deprecated
    public static String getResourcesPath() {
        return RESOURCES_PATH.toString();
    }

    // ==================== Helper METHODS ====================

    /**
     * Retrieves a configuration value as a string for the given property key.
     *
     * @param key The configuration key defined in {@link ConfigProperties}
     * @return The string value of the property
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is missing or unreadable
     */
    private static String getStringProperty(ConfigProperties key) {
        return PropertyBuilder.getPropValue(key);
    }

    /**
     * Retrieves a configuration value as an integer for the given property key.
     *
     * @param key The configuration key defined in {@link ConfigProperties}
     * @return The integer value parsed from the property file
     * @throws NumberFormatException if the value is not a valid integer
     * @throws com.tonic.exceptions.PropertyFileHandleException if the property is missing or unreadable
     */
    private static int getIntProperty(ConfigProperties key) {
        return Integer.parseInt(PropertyBuilder.getPropValue(key));
    }
}
