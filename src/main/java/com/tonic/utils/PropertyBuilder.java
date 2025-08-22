package com.tonic.utils;

import com.tonic.constants.FrameworkConstants;
import com.tonic.enums.ConfigProperties;
import com.tonic.exceptions.PropertyFileHandleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Builds and provides access to configuration properties from the framework config file.
 * Uses lazy initialization to avoid System.exit() in static blocks.
 * @author Gaurav Purwar
 */
public final class PropertyBuilder {

	private static final Logger logger = LoggerFactory.getLogger(PropertyBuilder.class);
	private PropertyBuilder() {
	}

	private static Properties property = new Properties();
	private static Map<String, String> CONFIG_MAP; // Will be made immutable after loading
	private static volatile boolean isInitialized = false;
	private static final Object INIT_LOCK = new Object();
	
	// Environment properties caching
	private static Map<String, String> ENV_CONFIG_MAP; // Will be made immutable after loading
	private static volatile boolean isEnvInitialized = false;
	private static final Object ENV_INIT_LOCK = new Object();
	
	// Cached current environment to avoid repeated lookups
	private static volatile String cachedEnvironment = null;
	private static final Object ENV_CACHE_LOCK = new Object();

	/**
	 * Ensures the property file is loaded before accessing properties.
	 * Uses double-checked locking for thread safety.
	 */
	private static void ensureInitialized() {
		if (!isInitialized) {
			synchronized (INIT_LOCK) {
				if (!isInitialized) {
					initializeProperties();
					isInitialized = true;
				}
			}
		}
	}

	/**
	 * Ensures the environment properties file is loaded before accessing environment properties.
	 * Uses double-checked locking for thread safety.
	 */
	private static void ensureEnvInitialized() {
		if (!isEnvInitialized) {
			synchronized (ENV_INIT_LOCK) {
				if (!isEnvInitialized) {
					initializeEnvironmentProperties();
					isEnvInitialized = true;
				}
			}
		}
	}

	/**
	 * Initializes the properties from the config file.
	 * Throws PropertyFileHandleException instead of System.exit().
	 */
	private static void initializeProperties() {
		String propertyFilePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "config.properties").toString();
		readPropertyFile(propertyFilePath);
	}

	/**
	 * Initializes the environment properties from the environment properties file.
	 * Throws PropertyFileHandleException if file cannot be loaded.
	 */
	private static void initializeEnvironmentProperties() {
		String envPropertyFilePath = Paths.get("src", "test", "resources", "config", "environment.properties").toString();
		readEnvironmentPropertyFile(envPropertyFilePath);
	}

	/**
	 * Gets a configuration property value using ConfigProperties enum.
	 * This method requires the property to exist and will throw an exception if not found.
	 * 
	 * @param key The ConfigProperties enum key to retrieve
	 * @return The property value as a string
	 * @throws PropertyFileHandleException if the property is not found in configuration
	 * @throws IllegalStateException if PropertyBuilder has not been initialized
	 */
	public static String getPropValue(ConfigProperties key) {
		ensureInitialized();
		String propertyName = key.getPropertyName().toLowerCase();
		if (Objects.isNull(CONFIG_MAP.get(propertyName))){
			throw new PropertyFileHandleException("Property name "+ key + " is not found. Please check config Properties" );
		}
		return CONFIG_MAP.get(propertyName);
	}

	/**
	 * Gets a configuration property value using ConfigProperties enum with a default fallback.
	 * Returns the default value if the property is not found.
	 * 
	 * @param key The ConfigProperties enum key to retrieve
	 * @param defaultValue The default value to return if property not found
	 * @return The property value or the default value if not found
	 * @throws IllegalStateException if PropertyBuilder has not been initialized
	 */
	public static String getPropValue(ConfigProperties key, String defaultValue) {
		ensureInitialized();
		String propertyName = key.getPropertyName().toLowerCase();
		String value = CONFIG_MAP.get(propertyName);
		return Objects.isNull(value) ? defaultValue : value;
	}

	/**
	 * Gets a configuration property value using string key with a default fallback.
	 * Returns the default value if the property is not found.
	 * 
	 * @param key The property key as a string (case-insensitive)
	 * @param defaultValue The default value to return if property not found
	 * @return The property value or the default value if not found
	 * @throws IllegalStateException if PropertyBuilder has not been initialized
	 */
	public static String getPropValue(String key, String defaultValue) {
		ensureInitialized();
		String value = CONFIG_MAP.get(key.toLowerCase());
		return Objects.isNull(value) ? defaultValue : value;
	}

	private static void readPropertyFile(String path) {
		try (FileInputStream env_file = new FileInputStream(path)) {
			//load Global File Properties
			property.load(env_file);

			// Create a mutable map for loading
			Map<String, String> mutableConfigMap = new HashMap<>();
			
			for(Map.Entry<Object, Object> entry: property.entrySet()){
				// Store keys in lowercase for case-insensitive lookup
				mutableConfigMap.put(String.valueOf(entry.getKey()).toLowerCase(), String.valueOf(entry.getValue()).trim());
			}
			
			// Make the map immutable after loading
			CONFIG_MAP = Collections.unmodifiableMap(mutableConfigMap);
			logger.info("Successfully loaded {} properties from: {}", CONFIG_MAP.size(), path);
		} catch (IOException e) {
			String errorMessage = "Failed to read property file: " + path;
			logger.error(errorMessage, e);
			throw new PropertyFileHandleException(errorMessage + ". " + e.getMessage());
		}
	}

	/**
	 * Reads and caches environment properties from the specified file path.
	 * Uses try-with-resources for automatic resource management.
	 */
	private static void readEnvironmentPropertyFile(String path) {
		Properties envProps = new Properties();
		try (FileInputStream envFile = new FileInputStream(path)) {
			envProps.load(envFile);
			
			// Create a mutable map for loading
			Map<String, String> mutableEnvConfigMap = new HashMap<>();
			
			// Store all environment properties in the cache map
			for(Map.Entry<Object, Object> entry: envProps.entrySet()){
				// Store keys in lowercase for case-insensitive lookup
				mutableEnvConfigMap.put(String.valueOf(entry.getKey()).toLowerCase(), String.valueOf(entry.getValue()).trim());
			}
			
			// Make the map immutable after loading
			ENV_CONFIG_MAP = Collections.unmodifiableMap(mutableEnvConfigMap);
			logger.info("Successfully loaded {} environment properties from: {}", ENV_CONFIG_MAP.size(), path);
		} catch (IOException e) {
			String errorMessage = "Failed to read environment property file: " + path;
			logger.error(errorMessage, e);
			throw new PropertyFileHandleException(errorMessage + ". " + e.getMessage());
		}
	}

	/**
	 * Gets an environment-specific property value.
	 * This method requires the environment property to exist and will throw an exception if not found.
	 * The environment is determined by the "env" system property or defaults to "RC_Automation".
	 * 
	 * @param key The environment property key (will be prefixed with current environment)
	 * @return The environment property value as a string
	 * @throws PropertyFileHandleException if the environment property is not found
	 * @throws IllegalStateException if PropertyBuilder or environment properties have not been initialized
	 */
	public static String getEnvPropValue(String key) {
		ensureInitialized();
		ensureEnvInitialized();
		
		// Use cached environment for efficiency
		String env = getCachedEnvironment();
		String fullKey = (env + "." + key).toLowerCase();
		
		String value = ENV_CONFIG_MAP.get(fullKey);
		if (value == null) {
			throw new PropertyFileHandleException("Environment property '" + fullKey + "' not found in environment.properties");
		}
		return value;
	}

	/**
	 * Gets environment property value with a default fallback.
	 * Returns the default value if the environment property is not found.
	 * 
	 * @param key The environment property key
	 * @param defaultValue The default value to return if property not found
	 * @return The environment property value or default value
	 */
	public static String getEnvPropValue(String key, String defaultValue) {
		ensureInitialized();
		ensureEnvInitialized();
		
		// Use cached environment for efficiency
		String env = getCachedEnvironment();
		String fullKey = (env + "." + key).toLowerCase();
		
		String value = ENV_CONFIG_MAP.get(fullKey);
		return Objects.isNull(value) ? defaultValue : value;
	}

	/**
	 * Resets the initialization state for testing purposes.
	 * This method should only be used in test scenarios.
	 */
	public static void reset() {
		synchronized (INIT_LOCK) {
			isInitialized = false;
			CONFIG_MAP = null; // Reset to null since it's now immutable
			property.clear();
		}
		synchronized (ENV_INIT_LOCK) {
			isEnvInitialized = false;
			ENV_CONFIG_MAP = null; // Reset to null since it's now immutable
		}
		synchronized (ENV_CACHE_LOCK) {
			cachedEnvironment = null; // Reset cached environment
		}
	}

	/**
	 * Checks if the PropertyBuilder has been initialized.
	 * @return true if initialized, false otherwise
	 */
	public static boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * Checks if the environment properties have been initialized.
	 * @return true if initialized, false otherwise
	 */
	public static boolean isEnvInitialized() {
		return isEnvInitialized;
	}

	/**
	 * Checks if a property exists in the main configuration.
	 * 
	 * @param key The property key to check
	 * @return true if the property exists, false otherwise
	 */
	public static boolean hasProperty(String key) {
		ensureInitialized();
		return CONFIG_MAP.containsKey(key.toLowerCase());
	}

	/**
	 * Checks if a property exists in the main configuration using ConfigProperties enum.
	 * 
	 * @param key The ConfigProperties enum key to check
	 * @return true if the property exists, false otherwise
	 */
	public static boolean hasProperty(ConfigProperties key) {
		ensureInitialized();
		return CONFIG_MAP.containsKey(key.name().toLowerCase());
	}

	/**
	 * Checks if an environment property exists.
	 * 
	 * @param key The environment property key to check
	 * @return true if the environment property exists, false otherwise
	 */
	public static boolean hasEnvProperty(String key) {
		ensureInitialized();
		ensureEnvInitialized();
		
		String env = getCachedEnvironment();
		String fullKey = (env + "." + key).toLowerCase();
		return ENV_CONFIG_MAP.containsKey(fullKey);
	}

	/**
	 * Gets all main configuration properties as a map.
	 * Useful for debugging and monitoring purposes.
	 * The returned map is immutable and cannot be modified.
	 * 
	 * @return An immutable view of all configuration properties
	 */
	/**
	 * Gets all main configuration properties as a map.
	 * Useful for debugging and monitoring purposes.
	 * The returned map is immutable and cannot be modified.
	 * 
	 * @return An immutable view of all configuration properties
	 * @throws IllegalStateException if PropertyBuilder has not been initialized
	 */
	public static Map<String, String> getAllProperties() {
		ensureInitialized();
		return CONFIG_MAP; // Already immutable, no need for defensive copy
	}

	/**
	 * Gets all environment properties as a map.
	 * Useful for debugging and monitoring purposes.
	 * The returned map is immutable and cannot be modified.
	 * 
	 * @return An immutable view of all environment properties
	 */
	public static Map<String, String> getAllEnvProperties() {
		ensureInitialized();
		ensureEnvInitialized();
		return ENV_CONFIG_MAP; // Already immutable, no need for defensive copy
	}

	/**
	 * Gets the current environment name being used.
	 * 
	 * @return The current environment name
	 */
	public static String getCurrentEnvironment() {
		ensureInitialized();
		return getCachedEnvironment();
	}

	/**
	 * Gets the cached current environment name.
	 * Uses double-checked locking for thread safety and performance.
	 * 
	 * @return The current environment name
	 */
	private static String getCachedEnvironment() {
		if (cachedEnvironment == null) {
			synchronized (ENV_CACHE_LOCK) {
				if (cachedEnvironment == null) {
					cachedEnvironment = System.getProperty("env", getPropValue("env", "RC_Automation"));
				}
			}
		}
		return cachedEnvironment;
	}

	/**
	 * Validates that all required properties exist.
	 * Throws PropertyFileHandleException if any required property is missing.
	 * 
	 * @param requiredKeys Array of required property keys
	 * @throws PropertyFileHandleException if any required property is missing
	 */
	public static void validateRequiredProperties(String... requiredKeys) {
		ensureInitialized();
		for (String key : requiredKeys) {
			if (!hasProperty(key)) {
				throw new PropertyFileHandleException("Required property '" + key + "' is missing from configuration");
			}
		}
	}

	/**
	 * Validates that all required environment properties exist.
	 * Throws PropertyFileHandleException if any required environment property is missing.
	 * 
	 * @param requiredKeys Array of required environment property keys
	 * @throws PropertyFileHandleException if any required environment property is missing
	 */
	public static void validateRequiredEnvProperties(String... requiredKeys) {
		ensureInitialized();
		ensureEnvInitialized();
		
		String env = getCachedEnvironment();
		for (String key : requiredKeys) {
			if (!hasEnvProperty(key)) {
				throw new PropertyFileHandleException("Required environment property '" + env + "." + key + "' is missing from environment configuration");
			}
		}
	}

	/**
	 * Gets a property value as an integer.
	 * 
	 * @param key The property key (case-insensitive)
	 * @param defaultValue The default value if property is not found or not a valid integer
	 * @return The integer value of the property
	 * @throws IllegalStateException if PropertyBuilder has not been initialized
	 */
	public static int getIntProperty(String key, int defaultValue) {
		ensureInitialized();
		String value = CONFIG_MAP.get(key.toLowerCase());
		if (Objects.isNull(value)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			logger.warn("Property '{}' value '{}' is not a valid integer, using default: {}", key, value, defaultValue);
			return defaultValue;
		}
	}

	/**
	 * Gets a property value as a boolean.
	 * 
	 * @param key The property key (case-insensitive)
	 * @param defaultValue The default value if property is not found
	 * @return The boolean value of the property
	 * @throws IllegalStateException if PropertyBuilder has not been initialized
	 */
	public static boolean getBooleanProperty(String key, boolean defaultValue) {
		ensureInitialized();
		String value = CONFIG_MAP.get(key.toLowerCase());
		if (Objects.isNull(value)) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value.trim());
	}

	/**
	 * Gets an environment property value as an integer.
	 * 
	 * @param key The environment property key (will be prefixed with current environment)
	 * @param defaultValue The default value if property is not found or not a valid integer
	 * @return The integer value of the environment property
	 * @throws IllegalStateException if PropertyBuilder or environment properties have not been initialized
	 */
	public static int getIntEnvProperty(String key, int defaultValue) {
		ensureInitialized();
		ensureEnvInitialized();
		
		String env = getCachedEnvironment();
		String fullKey = (env + "." + key).toLowerCase();
		String value = ENV_CONFIG_MAP.get(fullKey);
		if (Objects.isNull(value)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			logger.warn("Environment property '{}' value '{}' is not a valid integer, using default: {}", fullKey, value, defaultValue);
			return defaultValue;
		}
	}

	/**
	 * Gets an environment property value as a boolean.
	 * 
	 * @param key The environment property key (will be prefixed with current environment)
	 * @param defaultValue The default value if property is not found
	 * @return The boolean value of the environment property
	 * @throws IllegalStateException if PropertyBuilder or environment properties have not been initialized
	 */
	public static boolean getBooleanEnvProperty(String key, boolean defaultValue) {
		ensureInitialized();
		ensureEnvInitialized();
		
		String env = getCachedEnvironment();
		String fullKey = (env + "." + key).toLowerCase();
		String value = ENV_CONFIG_MAP.get(fullKey);
		if (Objects.isNull(value) || value.trim().isEmpty()) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value.trim());
	}

	/**
	 * Gets the total number of main configuration properties.
	 * 
	 * @return The number of properties loaded
	 * @throws IllegalStateException if PropertyBuilder has not been initialized
	 */
	public static int getPropertyCount() {
		ensureInitialized();
		return CONFIG_MAP.size();
	}

	/**
	 * Gets the total number of environment properties.
	 * 
	 * @return The number of environment properties loaded
	 * @throws IllegalStateException if PropertyBuilder or environment properties have not been initialized
	 */
	public static int getEnvPropertyCount() {
		ensureInitialized();
		ensureEnvInitialized();
		return ENV_CONFIG_MAP.size();
	}
}
