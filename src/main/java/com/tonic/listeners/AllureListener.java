package com.tonic.listeners;

import com.microsoft.playwright.Page;
import com.tonic.constants.FrameworkConstants;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * TestNG listener implementation for integrating Allure reporting with Playwright.
 * Captures screenshots, browser logs, environment details, and page source for enhanced test reporting.
 *
 * @author Gaurav Purwar
 */

public class AllureListener implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(AllureListener.class);
    private static final String ALLURE_RESULTS_DIR = System.getProperty("allure.results.directory", "target/allure-results");
    
    private static String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }
    
    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println("Starting test execution: " + iTestContext.getName());
        
        // Add environment information to Allure report
        addEnvironmentInfo();
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        System.out.println("Finishing test execution: " + iTestContext.getName());
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        System.out.println("Starting test method: " + getTestMethodName(iTestResult));
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        System.out.println("Test succeeded: " + getTestMethodName(iTestResult));
        // Get the page object from the test instance if available
        Page page = getPageFromTestInstance(iTestResult);
        if (page != null) {
            takeScreenshot(page, "Test PASSED: " + getTestMethodName(iTestResult));
            
            // Collect and attach browser logs if available
            attachBrowserLogs(iTestResult);
        }
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        System.out.println("Test failed: " + getTestMethodName(iTestResult));
        // Get the page object from the test instance if available
        Page page = getPageFromTestInstance(iTestResult);
        if (page != null) {
            takeScreenshot(page, "Test FAILED: " + getTestMethodName(iTestResult));
            
            // Collect and attach browser logs if available
            attachBrowserLogs(iTestResult);
            
            // Capture and attach HTML source of the page
            attachPageSource(page, iTestResult);
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        System.out.println("Test skipped: " + getTestMethodName(iTestResult));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("Test failed but within success percentage: " + getTestMethodName(iTestResult));
    }
    
    /**
     * Helper method to get the Page object from test instance
     */
    private Page getPageFromTestInstance(ITestResult iTestResult) {
        try {
            Object testInstance = iTestResult.getInstance();
            // Check if the test instance has a 'page' field
            java.lang.reflect.Field pageField = testInstance.getClass().getDeclaredField("page");
            pageField.setAccessible(true);
            return (Page) pageField.get(testInstance);
        } catch (Exception e) {
            System.err.println("Could not get page from test instance: " + e.getMessage());
            return null;
        }
    }

    @Attachment(value = "Screenshot on {1}", type = "image/png")
    public byte[] takeScreenshot(Page page, String methodName) {
        try {
            String screenshotName = methodName + "_" + UUID.randomUUID().toString() + ".png";
            String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + screenshotName;
            
            // Create screenshots directory if it doesn't exist
            Path screenshotsDir = Paths.get(System.getProperty("user.dir") + "/screenshots/");
            if (!Files.exists(screenshotsDir)) {
                Files.createDirectories(screenshotsDir);
            }
            
            // Take screenshot using Playwright
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(screenshotPath))
                    .setFullPage(true));
            
            System.out.println("Screenshot saved to: " + screenshotPath);
            return screenshot;
        } catch (Exception e) {
            logger.error("Error taking screenshot for method: {}", methodName, e);
            return null;
        }
    }
    
    /**
     * Add environment information to the Allure report
     */
    private void addEnvironmentInfo() {
        try {
            Map<String, String> env = new HashMap<>();
            env.put("Java Version", System.getProperty("java.version"));
            env.put("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            env.put("Browser", FrameworkConstants.getBrowser());
            env.put("Playwright Version", "1.44.0");
            
            // Convert the map to properties format
            StringBuilder envContent = new StringBuilder();
            for (Map.Entry<String, String> entry : env.entrySet()) {
                envContent.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            
            // Write to file
            Path envPropsPath = Paths.get(ALLURE_RESULTS_DIR + "/environment.properties");
            Files.createDirectories(envPropsPath.getParent());
            Files.write(envPropsPath, envContent.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Error creating environment properties: " + e.getMessage());
        }
    }
    
    /**
     * Attach browser logs to the Allure report
     */
    private void attachBrowserLogs(ITestResult iTestResult) {
        try {
            String logs = "Browser logs would be captured here if available";
            Allure.addAttachment("Browser Logs", "text/plain", new ByteArrayInputStream(logs.getBytes(StandardCharsets.UTF_8)), ".txt");
        } catch (Exception e) {
            System.err.println("Error attaching browser logs: " + e.getMessage());
        }
    }
    
    /**
     * Capture and attach the page source
     */
    private void attachPageSource(Page page, ITestResult iTestResult) {
        try {
            String pageSource = page.content();
            Allure.addAttachment("Page Source", "text/html", new ByteArrayInputStream(pageSource.getBytes(StandardCharsets.UTF_8)), ".html");
        } catch (Exception e) {
            System.err.println("Error attaching page source: " + e.getMessage());
        }
    }
} 