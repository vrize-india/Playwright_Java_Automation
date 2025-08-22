package com.tonic.utils;

import com.tonic.enums.ConfigProperties;
import com.tonic.constants.XrayConstants;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized configuration management for Xray integration.
 * Provides consistent access to configuration properties with caching and fallback mechanisms.
 */
public class XrayConfigManager {
    private static final Properties configCache = new Properties();
    private static volatile boolean configLoaded = false;
    private static final Object configLock = new Object();
    
    /**
     * Gets the configuration properties, loading them if not already cached.
     * @return Properties object containing all configuration
     */
    public static synchronized Properties getConfig() {
        if (!configLoaded) {
            loadConfig();
        }
        return configCache;
    }
    
    /**
     * Gets a configuration property value.
     * @param key The configuration property key
     * @return The property value, or null if not found
     */
    public static String getProperty(ConfigProperties key) {
        return getProperty(key, null);
    }
    
    /**
     * Gets a configuration property value with a default fallback.
     * @param key The configuration property key
     * @param defaultValue The default value if property is not found
     * @return The property value, or defaultValue if not found
     */
    public static String getProperty(ConfigProperties key, String defaultValue) {
        Properties config = getConfig();
        String value = config.getProperty(key.getPropertyName());
        
        // If not found in config, try system properties
        if (value == null || value.trim().isEmpty()) {
            value = System.getProperty(key.getPropertyName());
        }
        
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Gets a boolean configuration property value.
     * @param key The configuration property key
     * @param defaultValue The default value if property is not found
     * @return The boolean property value, or defaultValue if not found
     */
    public static boolean getBooleanProperty(ConfigProperties key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Gets a configuration property value directly from system properties.
     * @param key The configuration property key
     * @return The system property value, or null if not found
     */
    public static String getSystemProperty(ConfigProperties key) {
        return System.getProperty(key.getPropertyName());
    }
    
    /**
     * Checks if a configuration property is set and non-empty.
     * @param key The configuration property key
     * @return true if the property is set and non-empty, false otherwise
     */
    public static boolean isPropertySet(ConfigProperties key) {
        String value = getProperty(key);
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Reloads the configuration from the source.
     * Useful for testing or when configuration changes at runtime.
     */
    public static synchronized void reloadConfig() {
        configLoaded = false;
        configCache.clear();
        loadConfig();
    }
    
    /**
     * Loads configuration from config.properties file.
     * Tries multiple locations for robustness.
     */
    private static void loadConfig() {
        synchronized (configLock) {
            if (configLoaded) {
                return;
            }
            
            boolean loaded = false;
            
            // Try classpath first (most reliable)
            try (InputStream input = XrayConfigManager.class.getClassLoader().getResourceAsStream(XrayConstants.CONFIG_FILE)) {
                if (input != null) {
                    configCache.load(input);
                    loaded = true;
                }
            } catch (Exception e) {
                // Log error but continue with fallback
                System.err.println("[XRAY_CONFIG] Error loading config from classpath: " + e.getMessage());
            }
            
            // Try file system fallbacks if classpath failed
            if (!loaded) {
                String[] fallbackPaths = {
                    "src/main/resources/config.properties",
                    "target/classes/config.properties",
                    "config.properties"
                };
                
                for (String path : fallbackPaths) {
                    try (InputStream input = new java.io.FileInputStream(path)) {
                        configCache.load(input);
                        loaded = true;
                        break;
                    } catch (Exception e) {
                        // Continue to next fallback path
                    }
                }
            }
            
            if (!loaded) {
                throw new RuntimeException("Failed to load configuration from any known location");
            }
            
            configLoaded = true;
        }
    }
    
    /**
     * Gets the secret key for decryption operations.
     * @return The secret key from config or default value
     */
    public static String getSecretKey() {
        String key = getProperty(ConfigProperties.JIRA_PROJECT_KEY);
        if (key != null && !key.trim().isEmpty()) {
            return key.trim();
        }
        return XrayConstants.DEFAULT_SECRET_KEY;
    }
}

