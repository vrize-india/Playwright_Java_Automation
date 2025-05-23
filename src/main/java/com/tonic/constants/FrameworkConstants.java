package com.tonic.constants;

import com.tonic.enums.ConfigProperties;
import com.tonic.utils.PropertyBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FrameworkConstants {

    private FrameworkConstants() {
    }
    public static String ENV = System.getProperty("env") == null ? "staging" : System.getProperty("env");
    public static String DEVICE = System.getProperty("device") == null ? "android" : System.getProperty("device");
    private static final String RESOURCES_PATH = System.getProperty("user.dir") + "/src/main/resources";
    public static final String PROPERTY_FILE_PATH = RESOURCES_PATH + "/config.properties";
    public static final String APP_FILE_PATH = RESOURCES_PATH + "/app/app-release.apk";
    public static final String APP_IOS_FILE_PATH = RESOURCES_PATH + "/app/TSC.app";
    private static final String INPUT_JSON_PATH = RESOURCES_PATH + "/data/";
    private static final int ANDROID_PORT = 4723;
    private static final int IOS_PORT = 4725;
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final String APPIUM_PATH = "appium/node_modules/appium/build/lib/main.js";
    private static final int EXPLICIT_WAIT = 30;
    private static final int MEDIUM_WAIT = 50;
    private static final int LONG_WAIT = 80;
    private static final String EXTENT_REPORT_FOLDER_PATH = System.getProperty("user.dir") + "/extent-test-output/";
    private static final String EXTENT_REPORT_SCREENSHOTS_FOLDER_PATH = EXTENT_REPORT_FOLDER_PATH + "screenshots/";
    private static String extendReportFilePath = "";
    public static final String ERROR_LOG_FILE_PATH = System.getProperty("user.dir") + "\\extent-test-output\\errorLog" + FrameworkConstants.DEVICE.toUpperCase() + "_" + new SimpleDateFormat("MMMdd_HHmm").format(new Date()) + ".txt";

    public static String getPropertyFilePath() {
        return PROPERTY_FILE_PATH;
    }
    public static String getAppFilePath() {
        return APP_FILE_PATH;
    }

    public static String getAppIosFilePath() {
        return APP_IOS_FILE_PATH;
    }

    public static int getAndroidPort() {
        return ANDROID_PORT;
    }
    public static int getIosPort() {
        return IOS_PORT;
    }
    public static String getIpAddress() {
        return IP_ADDRESS;
    }
    public static String getAppiumPath() {
        return APPIUM_PATH;
    }
    public static int getExplicitWait() {
        return EXPLICIT_WAIT;
    }
    public static String getExtentReportFolderPath() {
        return EXTENT_REPORT_FOLDER_PATH;
    }
    public static String getExtentReportScreenshotsFolderPath() {
        return EXTENT_REPORT_SCREENSHOTS_FOLDER_PATH;
    }
    public static String getInputJsonPath() {
        return INPUT_JSON_PATH;
    }

    public static int mediumWait() {
        return MEDIUM_WAIT;
    }
    public static int longWait() {
        return LONG_WAIT;
    }

    private static String createReportPath() {
        if (PropertyBuilder.getPropValue(ConfigProperties.OVERRIDEREPORTS).equalsIgnoreCase("no")) {
            return EXTENT_REPORT_FOLDER_PATH + System.currentTimeMillis() + "/index.html";
        } else {
            System.out.println(new Date());
            System.out.println(EXTENT_REPORT_FOLDER_PATH + "/" + new SimpleDateFormat("MMMdd_HHmm").format(new Date()) + "_" + DEVICE + "_" + ENV.toUpperCase() + "_Reports.html");
            return EXTENT_REPORT_FOLDER_PATH + "/" +new SimpleDateFormat("MMMdd_HHmm").format(new Date()) + "_" + DEVICE + "_" + ENV.toUpperCase() + "_Reports.html";
        }
    }

    public static String getExtentReportFilePath() {
        if (extendReportFilePath.isEmpty()) {
            extendReportFilePath = createReportPath();
        }
        return extendReportFilePath;
    }
}
