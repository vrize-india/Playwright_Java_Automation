package com.tonic.utils;

import com.tonic.enums.ConfigProperties;
import com.tonic.utils.PropertyBuilder;

/**
 * Manages dynamic configuration values with system property fallback logic.
 * Handles the resolution of configuration values from system properties or default values.
 * 
 * @author Gaurav Purwar
 */
public final class ConfigManager {

    private ConfigManager() {
        // Utility class - prevent instantiation
    }

    /**
     * Gets the environment configuration value.
     * Priority: System property "env" > PropertyBuilder value
     * 
     * @return The environment value
     */
    public static String getEnvironment() {
        return System.getProperty("env") == null ? 
            PropertyBuilder.getPropValue(ConfigProperties.ENV) : 
            System.getProperty("env");
    }

    /**
     * Gets the device configuration value.
     * Priority: System property "device" > PropertyBuilder value
     * 
     * @return The device value
     */
    public static String getDevice() {
        return System.getProperty("device") == null ? 
            PropertyBuilder.getPropValue(ConfigProperties.ANDROID) : 
            System.getProperty("device");
    }

    /**
     * Gets the browser configuration value.
     * Priority: System property "browser" > PropertyBuilder value
     * 
     * @return The browser value
     */
    public static String getBrowser() {
        return System.getProperty("browser") == null ? 
            PropertyBuilder.getPropValue(ConfigProperties.BROWSER) : 
            System.getProperty("browser");
    }

    /**
     * Gets a configuration value with system property fallback.
     * Priority: System property > PropertyBuilder value > default value
     * 
     * @param systemPropertyKey The system property key to check first
     * @param configProperty The ConfigProperties enum to use as fallback
     * @return The resolved configuration value
     */
    public static String getConfigValue(String systemPropertyKey, ConfigProperties configProperty) {
        return System.getProperty(systemPropertyKey) == null ? 
            PropertyBuilder.getPropValue(configProperty) : 
            System.getProperty(systemPropertyKey);
    }

    /**
     * Gets a configuration value with system property fallback and default value.
     * Priority: System property > PropertyBuilder value > default value
     * 
     * @param systemPropertyKey The system property key to check first
     * @param configProperty The ConfigProperties enum to use as fallback
     * @param defaultValue The default value if neither system property nor config property exists
     * @return The resolved configuration value
     */
    public static String getConfigValue(String systemPropertyKey, ConfigProperties configProperty, String defaultValue) {
        String systemValue = System.getProperty(systemPropertyKey);
        if (systemValue != null) {
            return systemValue;
        }
        
        try {
            return PropertyBuilder.getPropValue(configProperty);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gets an integer configuration value with system property fallback.
     * Priority: System property > PropertyBuilder value > default value
     * 
     * @param systemPropertyKey The system property key to check first
     * @param configProperty The ConfigProperties enum to use as fallback
     * @param defaultValue The default value if neither system property nor config property exists
     * @return The resolved integer configuration value
     */
    public static int getIntConfigValue(String systemPropertyKey, ConfigProperties configProperty, int defaultValue) {
        String systemValue = System.getProperty(systemPropertyKey);
        if (systemValue != null) {
            try {
                return Integer.parseInt(systemValue);
            } catch (NumberFormatException e) {
                // Fall through to config property
            }
        }
        
        try {
            return Integer.parseInt(PropertyBuilder.getPropValue(configProperty));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gets a boolean configuration value with system property fallback.
     * Priority: System property > PropertyBuilder value > default value
     * 
     * @param systemPropertyKey The system property key to check first
     * @param configProperty The ConfigProperties enum to use as fallback
     * @param defaultValue The default value if neither system property nor config property exists
     * @return The resolved boolean configuration value
     */
    public static boolean getBooleanConfigValue(String systemPropertyKey, ConfigProperties configProperty, boolean defaultValue) {
        String systemValue = System.getProperty(systemPropertyKey);
        if (systemValue != null) {
            return Boolean.parseBoolean(systemValue);
        }
        
        try {
            return Boolean.parseBoolean(PropertyBuilder.getPropValue(configProperty));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gets a configuration value with system property fallback using string key.
     * Priority: System property > PropertyBuilder value > default value
     * 
     * @param systemPropertyKey The system property key to check first
     * @param configKey The configuration key to use as fallback
     * @param defaultValue The default value if neither system property nor config property exists
     * @return The resolved configuration value
     */
    public static String getConfigValue(String systemPropertyKey, String configKey, String defaultValue) {
        String systemValue = System.getProperty(systemPropertyKey);
        if (systemValue != null) {
            return systemValue;
        }
        
        return PropertyBuilder.getPropValue(configKey, defaultValue);
    }

    /**
     * Gets an integer configuration value with system property fallback using string key.
     * Priority: System property > PropertyBuilder value > default value
     * 
     * @param systemPropertyKey The system property key to check first
     * @param configKey The configuration key to use as fallback
     * @param defaultValue The default value if neither system property nor config property exists
     * @return The resolved integer configuration value
     */
    public static int getIntConfigValue(String systemPropertyKey, String configKey, int defaultValue) {
        String systemValue = System.getProperty(systemPropertyKey);
        if (systemValue != null) {
            try {
                return Integer.parseInt(systemValue);
            } catch (NumberFormatException e) {
                // Fall through to config property
            }
        }
        
        return PropertyBuilder.getIntProperty(configKey, defaultValue);
    }

    /**
     * Gets a boolean configuration value with system property fallback using string key.
     * Priority: System property > PropertyBuilder value > default value
     * 
     * @param systemPropertyKey The system property key to check first
     * @param configKey The configuration key to use as fallback
     * @param defaultValue The default value if neither system property nor config property exists
     * @return The resolved boolean configuration value
     */
    public static boolean getBooleanConfigValue(String systemPropertyKey, String configKey, boolean defaultValue) {
        String systemValue = System.getProperty(systemPropertyKey);
        if (systemValue != null) {
            return Boolean.parseBoolean(systemValue);
        }
        
        return PropertyBuilder.getBooleanProperty(configKey, defaultValue);
    }
} 