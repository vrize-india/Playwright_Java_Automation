package com.vrize.utils;

import com.vrize.constants.FrameworkConstants;
import com.vrize.enums.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for securely reading and updating password-related properties.
 * @author Gaurav Purwar
 */
public class PasswordManagerUtils {
    private static final Logger logger = LoggerFactory.getLogger(PasswordManagerUtils.class);
    private static final String PROPERTIES_FILE = FrameworkConstants.getPropertyFilePath();
    private static final String PASSWORD_KEY = "password";

    public static String getPassword(ConfigProperties key) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
            return props.getProperty(String.valueOf(key));
        } catch (IOException e) {
            logger.error("Failed to get password for key: {}", key, e);
            return null;
        }
    }

    public static void setPassword(ConfigProperties key, String newPassword) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
        } catch (IOException e) {
            logger.error("Failed to load properties file for setting password: {}", PROPERTIES_FILE, e);
        }
        props.setProperty(String.valueOf(key), newPassword);
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
            props.store(out, null);
        } catch (IOException e) {
            logger.error("Failed to store password for key: {}", key, e);
        }
    }
}
