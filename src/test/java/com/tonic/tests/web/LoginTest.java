package com.tonic.tests.web;

import com.tonic.annotations.TonicAnnotation;
import com.tonic.enums.CategoryType;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.utils.AllureScreenshotUtil;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.tonic.utils.JiraPolicy;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.tonic.factory.PlaywrightFactory.takeScreenshot;

@Epic("Terminal Management System")
@Feature("Terminal Management Features")
public class LoginTest extends BaseTest {

    @TonicAnnotation(category = {CategoryType.WEB})
    @JiraPolicy(logTicketReady=false)
    @Test(priority = 1,description = "Verify user can login with valid credentials")
    @Story("User Authentication")
    @Description("Verify user can login with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void loginWithValidCredentials() {
        test = com.tonic.utils.ExtentManager.getExtentTest();
        try {
            Step1_EnterCredentials();
            Step2_VerifyDashboard();
            // Add screenshot to the report
            String screenshot = takeScreenshot();
            if (screenshot != null && !screenshot.isEmpty()) {
                test.pass("Login successful", MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot, "Login success").build());
            } else {
                test.pass("Login successful (screenshot not available)");
            }
        } catch (Exception e) {
            String screenshot = takeScreenshot();
            if (screenshot != null && !screenshot.isEmpty()) {
                test.fail("Login failed", MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot, "Login failure").build());
            } else {
                test.fail("Login failed (screenshot not available)");
            }
            test.fail(e);
            throw e;
        }
    }

    @Step("Enter login credentials and submit")
    private void Step1_EnterCredentials() {
        AllureScreenshotUtil.takeScreenshot(PlaywrightFactory.getPage(), "Before login");
        loginPage.doLogin(prop.getProperty("username"), prop.getProperty("password"));
        AllureScreenshotUtil.takeScreenshot(PlaywrightFactory.getPage(), "After login submission");
    }

    @Step("Verify dashboard is loaded")
    private void Step2_VerifyDashboard() {
        AllureScreenshotUtil.takeScreenshot(PlaywrightFactory.getPage(), "Dashboard verification");
        Assert.assertTrue(adminDashboardPage.isDashboardLoaded());
        AllureScreenshotUtil.takeScreenshot(PlaywrightFactory.getPage(), "Final dashboard state");
    }

    @TonicAnnotation(category = {CategoryType.WEB})
    @JiraPolicy(logTicketReady=false)
    @Test(priority = 2,description = "Verify user is able to navigate to terminal")
    @Story("Terminal Navigation")
    @Description("Verify user can navigate to terminals page")
    @Severity(SeverityLevel.NORMAL)
    public void navigateToTerminalsPage() {
        test = com.tonic.utils.ExtentManager.getExtentTest();
        try {
            AllureScreenshotUtil.takeScreenshot(PlaywrightFactory.getPage(), "Before login for terminal navigation");
            loginPage.doLogin(prop.getProperty("username"), prop.getProperty("password"));
            AllureScreenshotUtil.takeScreenshot(PlaywrightFactory.getPage(), "After login for terminal navigation");
            adminDashboardPage.goToConfiguration();
            AllureScreenshotUtil.takeScreenshot(PlaywrightFactory.getPage(), "After navigation to configuration");
            Assert.assertTrue(configurationPage.isConfigurationLoaded());
            configurationPage.goToTerminals();
            AllureScreenshotUtil.takeScreenshot(PlaywrightFactory.getPage(), "After navigation to terminals");
            Assert.assertTrue(terminalsPage.isAddTerminalButtonPresent());
            AllureScreenshotUtil.takeScreenshot(PlaywrightFactory.getPage(), "Final terminals page state");
            String screenshot = takeScreenshot();
            if (screenshot != null && !screenshot.isEmpty()) {
                test.pass("Navigation successful", MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot, "Navigation success").build());
            } else {
                test.pass("Navigation successful (screenshot not available)");
            }
        } catch (Exception e) {
            String screenshot = takeScreenshot();
            if (screenshot != null && !screenshot.isEmpty()) {
                test.fail("Navigation failed", MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot, "Navigation failure").build());
            } else {
                test.fail("Navigation failed (screenshot not available)");
            }
            test.fail(e);
            throw e;
        }
    }

    @TonicAnnotation(category = {CategoryType.WEB})
    @JiraPolicy(logTicketReady=false)
    @Test(priority = 3,description = "Verify user is able to add terminal")
    public void openAddTerminalDialog() {
        test = com.tonic.utils.ExtentManager.getExtentTest();
        try {
            loginPage.doLogin(prop.getProperty("username"), prop.getProperty("password"));
            adminDashboardPage.goToConfiguration();
            configurationPage.goToTerminals();
            terminalsPage.clickAddTerminalButton();
            Assert.assertTrue(terminalsPage.isAddTerminalDialogVisible());
            String screenshot = takeScreenshot();
            if (screenshot != null && !screenshot.isEmpty()) {
                test.pass("Add terminal dialog opened successfully", MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot).build());
            } else {
                test.pass("Add terminal dialog opened successfully (screenshot not available)");
            }
        } catch (Exception e) {
            String screenshot = takeScreenshot();
            if (screenshot != null && !screenshot.isEmpty()) {
                test.fail("Failed to open add terminal dialog", MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot).build());
            } else {
                test.fail("Failed to open add terminal dialog (screenshot not available)");
            }
            test.fail(e);
            throw e;
        }
    }
}

