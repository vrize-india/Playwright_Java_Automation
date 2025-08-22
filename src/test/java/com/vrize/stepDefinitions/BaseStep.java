package com.vrize.stepDefinitions;

import com.vrize.driver.MobileDriver;
import com.vrize.enums.WaitEnums;
import com.vrize.pageObjects.mobile.Login;
import com.vrize.hooks.Hooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.SoftAssert;
import io.appium.java_client.AppiumDriver;

public class BaseStep {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseStep.class);

    // Get the driver instance once and reuse it
    public static AppiumDriver driver;

    public static void assertAll() {
        SoftAssert softAssert = Hooks.getSoftAssert();
        if (softAssert != null) {
            softAssert.assertAll();
        }
    }

    public void softAssertTrue(boolean value,String elementName, String negativeMessage) {
        Hooks.getSoftAssert().assertTrue(value, elementName + ": " + negativeMessage);
    }


    public void softAssertFalse(boolean value, String elementName, String negativeMessage) {
        Hooks.getSoftAssert().assertFalse(value, elementName + ": " + negativeMessage);
    }

    public void softAssertFalse(boolean value, String negativeMessage) {
        Hooks.getSoftAssert().assertFalse(value,  negativeMessage);
    }

    /**
     * Performs a soft assertion to verify  condition.
     * @param value  The boolean value representing success condition to assert.
     * @param negativeMessage The message to display if the assertion fails.
     * The method performs a soft assertion, meaning test execution will continue even if the assertion fails.
     * Useful for checking multiple UI elements without halting the test immediately.
     */
    public void softAssertTrue(boolean value, String negativeMessage) {
        Hooks.getSoftAssert().assertTrue(value, negativeMessage);
    }

    /**
     * Launches and initializes the mobile application with comprehensive wait strategies.
     * This method can be reused across different step definition classes for mobile app initialization.
     * 
     * @return Login object initialized with the mobile driver
     */
    public Login launchMobileApplication() {
        LOGGER.info("Launching mobile application for HYBRID mode...");
        
        try {
            // Initialize mobile driver if not already initialized
            if (driver == null) {
                MobileDriver.initDriver("Mobile App Launch");
                LOGGER.info("Mobile driver initialized successfully");
                driver = MobileDriver.getDriver();
            }
            // Initialize the login object
            Login login = new Login(driver);
            LOGGER.info("Login object initialized successfully");
            
            // Wait for app to be ready with multiple wait strategies
            if (driver != null) {
                try {
                    LOGGER.info("Waiting for app to load completely...");
                    
                    // Wait for app launch (initial app loading)
                    login.mobileWait(WaitEnums.APP_LAUNCH, 15);
                    LOGGER.info("App launch wait completed");
                    
                    // Wait for UI to be interactive
                    login.mobileWait(WaitEnums.UI_INTERACTION, 8);
                    LOGGER.info("UI interaction wait completed");
                    
                    // Additional wait for navigation to complete
                    login.mobileWait(WaitEnums.NAVIGATION, 5);
                    LOGGER.info("Navigation wait completed");
                    
                    LOGGER.info("Mobile application launched and fully loaded successfully");
                    
                } catch (Exception e) {
                    LOGGER.warn("Some app launch waits failed, but continuing: " + e.getMessage());
                    // Even if some waits fail, give a final wait to ensure app is ready
                    try {
                        LOGGER.info("Applying fallback waits due to previous wait failures...");
                        // Try one more mobile wait
                        login.mobileWait(com.vrize.enums.WaitEnums.APP_LAUNCH, 5);
                        LOGGER.info("Fallback app launch wait completed");
                        
                    } catch (Exception fallbackException) {
                        LOGGER.warn("Fallback wait also failed: " + fallbackException.getMessage());
                    }
                }
            }
            
            return login;
            
        } catch (Exception e) {
            LOGGER.error("Error launching mobile application: " + e.getMessage());
            throw new RuntimeException("Failed to launch mobile application", e);
        }
    }
}
