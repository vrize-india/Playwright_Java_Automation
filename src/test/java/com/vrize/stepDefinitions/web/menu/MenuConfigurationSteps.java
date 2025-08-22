package com.vrize.stepDefinitions.web.menu;

import com.microsoft.playwright.Locator;
import com.vrize.driver.MobileDriver;
import com.vrize.enums.BOHConstants;
import com.vrize.enums.BOHDataConstants;
import com.vrize.enums.ButtonConstants;
import com.vrize.factory.PlaywrightFactory;
import com.vrize.pageObjects.mobile.Login;
import com.vrize.pageObjects.web.menu.MenuConfigurationPage;
import com.vrize.stepDefinitions.BaseStep;
import com.vrize.hooks.Hooks;
import com.vrize.utils.ApplicationUtils;
import io.cucumber.java.en.*;
import org.testng.Assert;

import java.util.List;

public class MenuConfigurationSteps extends BaseStep {
    private final MenuConfigurationPage menuConfigurationPage = new MenuConfigurationPage(PlaywrightFactory.getPage());

    private Login login;


    @When("The user launches the mobile application with resource id {string}")
    public void theUserLaunchesTheMobileApplication(String resourceId) throws Exception {
        LOGGER.info("Launching mobile application with resource ID: {}", resourceId);
        login = launchMobileApplication();
        login.performResourceLoginWithValidation(resourceId);
        LOGGER.info("Mobile application launch and login completed successfully");
    }

    @And("Creates a new order by clicking on QS")
    public void createANewOrderByClickingOnQS() throws InterruptedException {
        login.clickQSIcon();
    }

    @When("The user clicks on Swipe Right icon on mobile")
    public void tapOnSwipeRightIcon() throws InterruptedException {
        login.clickSwipeIcon();
    }

    @When("The user relaunches the mobile application")
    public void theUserRelaunchesTheMobileApplication() throws Exception {
        LOGGER.info("Relaunching mobile application...");
        
        // Stop the current mobile driver
        if (MobileDriver.getDriver() != null) {
            try {
                MobileDriver.getDriver().quit();
                LOGGER.info("Mobile driver stopped successfully");
            } catch (Exception e) {
                LOGGER.warn("Error stopping mobile driver: " + e.getMessage());
            }
        }
        
        // Stop Appium server
        try {
            Hooks.stopAppiumServer();
            LOGGER.info("Appium server stopped successfully");
        } catch (Exception e) {
            LOGGER.warn("Error stopping Appium server: " + e.getMessage());
        }
        
        // Wait for cleanup using proper wait mechanism
        try {
            Thread.sleep(1000); // Minimal wait for port cleanup
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Restart Appium server and reinitialize mobile driver
        try {
            Hooks.startAppiumServer();
            LOGGER.info("Appium server restarted successfully");
            
            // Reinitialize mobile driver
            MobileDriver.initDriver("Mobile App Relaunch");
            LOGGER.info("Mobile driver reinitialized successfully");
            
            // Reinitialize the login object with the new driver
            // Create a new Login object that will get the fresh driver
            login = new Login(MobileDriver.getDriver());
            LOGGER.info("Login object reinitialized successfully");
            
            // Wait for app to be ready using mobile wait
            if (MobileDriver.getDriver() != null) {
                try {
                    // Use mobile wait for app launch
                    login.mobileWait(com.vrize.enums.WaitEnums.APP_LAUNCH, 5);
                    LOGGER.info("Mobile application relaunch completed successfully");
                } catch (Exception e) {
                    LOGGER.warn("App launch wait failed, continuing anyway: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("Error relaunching mobile application: " + e.getMessage());
            throw new RuntimeException("Failed to relaunch mobile application", e);
        }
    }

    @And("The {string} dialog should open for adding items to the category")
    public void validateAddMenuItemsDialogIsOpenedForAddingItemsToTheCategory(String dialogContent) {
        menuConfigurationPage.getElementByText(dialogContent).waitFor(new Locator.WaitForOptions().setTimeout(5000));
        Assert.assertTrue(menuConfigurationPage.getElementByText(dialogContent).isVisible(),"The Add Menu Items dialog is not visible ");
        LOGGER.info("Add Menu Item Dialog is visible");
    }


}
