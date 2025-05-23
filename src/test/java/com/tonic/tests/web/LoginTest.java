package com.tonic.tests.web;

import com.tonic.utils.AllureScreenshotUtil;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.tonic.factory.PlaywrightFactory.takeScreenshot;

@Epic("Terminal Management System")
@Feature("Terminal Management Features")
public class LoginTest extends BaseTest {

    @Test(priority = 1)
    @Story("User Authentication")
    @Description("Verify user can login with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void loginWithValidCredentials() {
        // Create test entry in ExtentReports
        test = extent.createTest("Login With Valid Credentials", "Verify user can login with valid credentials");
        
        try {
            enterCredentials();
            verifyDashboard();
            
            // Add screenshot to the report
            test.pass("Login successful", MediaEntityBuilder.createScreenCaptureFromBase64String(takeScreenshot(), "Login success").build());
        } catch (Exception e) {
            test.fail("Login failed", MediaEntityBuilder.createScreenCaptureFromBase64String(takeScreenshot(), "Login failure").build());
            test.fail(e);
            throw e;
        }
    }
    
    @Step("Enter login credentials and submit")
    private void enterCredentials() {
        // Take screenshot before login
        AllureScreenshotUtil.takeScreenshot(page, "Before login");
        
        // Perform login action
        loginPage.doLogin(prop.getProperty("username"), prop.getProperty("password"));
        
        // Take screenshot after login
        AllureScreenshotUtil.takeScreenshot(page, "After login submission");
    }
    
    @Step("Verify dashboard is loaded")
    private void verifyDashboard() {
        // Take screenshot of dashboard
        AllureScreenshotUtil.takeScreenshot(page, "Dashboard verification");
        
        // Verify dashboard is loaded
        Assert.assertTrue(adminDashboardPage.isDashboardLoaded());
        
        // Take another screenshot after verification
        AllureScreenshotUtil.takeScreenshot(page, "Final dashboard state");
    }

    @Test(priority = 2)
    @Story("Terminal Navigation")
    @Description("Verify user can navigate to terminals page")
    @Severity(SeverityLevel.NORMAL)
    public void navigateToTerminalsPage() {
        // Create test entry in ExtentReports
        test = extent.createTest("Navigate To Terminals Page", "Verify user can navigate to terminals page");
        
        try {
            // Take initial screenshot
            AllureScreenshotUtil.takeScreenshot(page, "Before login for terminal navigation");
            
            // Login step
            loginPage.doLogin(prop.getProperty("username"), prop.getProperty("password"));
            AllureScreenshotUtil.takeScreenshot(page, "After login for terminal navigation");
            
            // Navigate to configuration
            adminDashboardPage.goToConfiguration();
            AllureScreenshotUtil.takeScreenshot(page, "After navigation to configuration");
            
            // Verify configuration loaded
            Assert.assertTrue(configurationPage.isConfigurationLoaded());
            
            // Navigate to terminals
            configurationPage.goToTerminals();
            AllureScreenshotUtil.takeScreenshot(page, "After navigation to terminals");
            
            // Verify terminals page loaded
            Assert.assertTrue(terminalsPage.isAddTerminalButtonPresent());
            AllureScreenshotUtil.takeScreenshot(page, "Final terminals page state");
            
            // Add screenshot to the report
            test.pass("Navigation successful", MediaEntityBuilder.createScreenCaptureFromBase64String(takeScreenshot(), "Navigation success").build());
        } catch (Exception e) {
            test.fail("Navigation failed", MediaEntityBuilder.createScreenCaptureFromBase64String(takeScreenshot(), "Navigation failure").build());
            test.fail(e);
            throw e;
        }
    }

    @Test(priority = 3)
    public void openAddTerminalDialog() {
        test = extent.createTest("Open Add Terminal Dialog");
        try {
            loginPage.doLogin(prop.getProperty("username"), prop.getProperty("password"));
            adminDashboardPage.goToConfiguration();
            configurationPage.goToTerminals();
            terminalsPage.clickAddTerminalButton();
            Assert.assertTrue(terminalsPage.isAddTerminalDialogVisible());
            test.pass("Add terminal dialog opened successfully", MediaEntityBuilder.createScreenCaptureFromBase64String(takeScreenshot()).build());
        } catch (Exception e) {
            test.fail("Failed to open add terminal dialog", MediaEntityBuilder.createScreenCaptureFromBase64String(takeScreenshot()).build());
            test.fail(e);
            throw e;
        }
    }
} 