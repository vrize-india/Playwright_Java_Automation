package com.tonic.utils;

import com.tonic.constants.FrameworkConstants;
import com.tonic.enums.ConfigProperties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PasswordManagerUtils {
    private static final String PROPERTIES_FILE = FrameworkConstants.getPropertyFilePath();
    private static final String PASSWORD_KEY = "password";

    public static String getPassword(ConfigProperties key) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
            return props.getProperty(String.valueOf(key));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setPassword(ConfigProperties key, String newPassword) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        props.setProperty(String.valueOf(key), newPassword);
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
