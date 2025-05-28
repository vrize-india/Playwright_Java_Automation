package com.tonic.driver;

import com.tonic.factory.DriverFactory;
import io.appium.java_client.AppiumDriver;

import java.net.MalformedURLException;
import java.util.Objects;

/**
 * The Driver class is a thread-safe utility for initializing, accessing,
 * and managing the lifecycle of the Appium driver instance in your automation framework.
 * @author Gaurav Purwar
 */
public class Driver {

    private Driver() {
    }

    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    public static AppiumDriver getDriver() {
        return driver.get();
    }

    private static void setDriver(AppiumDriver ad) {
        if (Objects.nonNull(ad)) {
            driver.set(ad);
        }
    }

    private static void unload() {
        driver.remove();
    }

    public static void initDriver(String device, String runMode, String testName) throws Exception {
        if (Objects.isNull(getDriver())) {
            try {
                setDriver(DriverFactory.getDriver(device, runMode, testName));
                System.out.println("AppiumDriver initialized for device: " + device);
            } catch (MalformedURLException e) {
                System.err.println("Error initializing AppiumDriver: " + e.getMessage());
                throw new Exception("Please check the capabilities of the device", e);
            }
        }
    }

    public static void quitDriver() {
        if (Objects.nonNull(getDriver())) {
            getDriver().quit();
            unload();
        }
    }
}
