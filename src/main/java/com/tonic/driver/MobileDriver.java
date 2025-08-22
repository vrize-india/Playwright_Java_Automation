package com.tonic.driver;

import com.tonic.factory.MobileDriverFactory;
import io.appium.java_client.AppiumDriver;
import java.net.MalformedURLException;
import java.util.Objects;

/**
 * Manages ThreadLocal AppiumDriver instances for parallel test execution.
 *
 * @author Gaurav Purwar
 */
public class MobileDriver {

    private MobileDriver() {
    }

    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    /**
     * Returns the current thread-local AppiumDriver instance.
     *
     * @return AppiumDriver for the current thread
     */
    public static AppiumDriver getDriver() {
        return driver.get();
    }

    /**
     * Sets the AppiumDriver instance for the current thread.
     *
     * @param ad AppiumDriver instance to be set
     */
    public static void setDriver(AppiumDriver ad) {
        if (Objects.nonNull(ad) && !isDriverInitialized()) {
            driver.set(ad);
        }
    }

    /**
     * Removes the AppiumDriver instance from the current thread.
     */
    public static void unload() {
        driver.remove();
    }

    /**
     * Initializes the AppiumDriver if not already initialized.
     *
     * @param testName Name of the test for driver identification
     * @throws Exception if driver initialization fails
     */
    public static void initDriver(String testName) throws Exception {
        if (!isDriverInitialized()) {
            try {
                setDriver(MobileDriverFactory.getDriver(testName));
                System.out.println("AppiumDriver initialized using config properties");
            } catch (MalformedURLException e) {
                String message = "Failed to initialize AppiumDriver. Please verify capabilities or URL. Cause: " + e.getMessage();
                System.err.println(message);
                throw new Exception(message, e);
            }
        }
    }

    /**
     * Quits and unloads the AppiumDriver if initialized.
     */
    public static void quitDriver() {
        if (isDriverInitialized()) {
            getDriver().quit();
            unload();
        }
    }

    /**
     * Restarts the AppiumDriver by quitting and reinitializing.
     *
     * @param testName Name of the test for driver identification
     * @throws Exception if restart fails
     */
    public static void restartDriver(final String testName) throws Exception {
        quitDriver();
        initDriver(testName);
    }

    /**
     * Checks if the AppiumDriver is already initialized for the current thread.
     *
     * @return true if driver exists, false otherwise
     */
    public static boolean isDriverInitialized() {
        return Objects.nonNull(getDriver());
    }
}

