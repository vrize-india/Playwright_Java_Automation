package com.tonic.driver;

import com.tonic.factory.DriverFactory;

import java.net.MalformedURLException;
import java.util.Objects;

public class Driver {

    private Driver() {
    }

    public static void initDriver(String device, String runMode, String testName) throws Exception {

        if (Objects.isNull(DriverManager.getDriver())) {
            try {
                DriverManager.setDriver(DriverFactory.getDriver(device, runMode, testName));
                System.out.println("AppiumDriver initialized for device: " + device);
            } catch (MalformedURLException e) {
                System.err.println("Error initializing AppiumDriver: " + e.getMessage());
                throw new Exception("Please check the capabilities of the device", e);
            }
        }
    }

    public static void quitDriver() {
        if (Objects.nonNull(DriverManager.getDriver())) {
            DriverManager.getDriver().quit();
            DriverManager.unload();
        }
    }
}
