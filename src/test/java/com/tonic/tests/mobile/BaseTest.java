package com.tonic.tests.mobile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.microsoft.playwright.Page;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.driver.Driver;
import com.tonic.driver.DriverManager;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * BaseTest class for mobile tests
 * Contains common setup and teardown methods
 */
public class BaseTest {

    protected PlaywrightFactory pf;
    protected Page page;
    protected Properties prop;
    protected ExtentTest test;
    protected static ExtentReports extent;
    protected String device = "android"; // Default device
    
    @BeforeSuite
    public void setupReport() {
        // Setup ExtentReports
        String outputFolder = System.getProperty("user.dir") + File.separator + "build" + File.separator;
        File outputDir = new File(outputFolder);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
            System.out.println("Created output directory: " + outputFolder);
        }
        
        String reportFile = outputFolder + "MobileTestReport" + System.currentTimeMillis() + ".html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(reportFile);
        reporter.config().setReportName("Mobile Test Results");
        reporter.config().setDocumentTitle("Mobile Test Execution Report");
        
        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Environment", "Test");
        extent.setSystemInfo("User", "Mobile Test User");
        extent.setSystemInfo("Timestamp", new Date().toString());
        
        System.out.println("Mobile ExtentReports initialized. Report will be saved to: " + reportFile);
    }
    
    @BeforeClass
    @Parameters({"device"})
    public void setUpAppium(String device) {
        if (device != null) {
            this.device = device;
        }
        System.out.println("Setting up test on device: " + this.device);
        
        try {
            // Initialize Appium driver
            Driver.initDriver(this.device, "local", "Mobile App Test");
            System.out.println("Appium driver initialized successfully");
            
            // Allow time for app to load
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("Failed to initialize Appium driver: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @BeforeMethod
    public void setup() {
        // Initialize Playwright for web view testing if needed
        pf = new PlaywrightFactory();
        prop = pf.init_prop();
        page = pf.initBrowser(prop);
        System.out.println("Mobile browser session started");
    }
    
    @AfterMethod
    public void tearDown() {
        if (page != null) {
            page.context().browser().close();
            System.out.println("Mobile browser closed");
        }
    }
    
    @AfterClass
    public void tearDownAppium() {
        try {
            // Clean up Appium driver
            Driver.quitDriver();
            System.out.println("Appium driver closed");
        } catch (Exception e) {
            System.err.println("Error closing Appium driver: " + e.getMessage());
        }
        System.out.println("Test cleanup complete");
    }
    
    @AfterSuite
    public void tearDownReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("Mobile ExtentReports saved successfully!");
        }
    }
    
    /**
     * Take screenshot with Playwright
     */
    protected String takeScreenshot() {
        String path = System.getProperty("user.dir") + "/screenshots/mobile_" + System.currentTimeMillis() + ".png";
        
        // Create the directory if it doesn't exist
        Path screenshotDir = Paths.get(System.getProperty("user.dir") + "/screenshots/");
        try {
            if (!Files.exists(screenshotDir)) {
                Files.createDirectories(screenshotDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        byte[] buffer = page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(path))
                .setFullPage(true));
        
        return java.util.Base64.getEncoder().encodeToString(buffer);
    }
    
    /**
     * Take screenshot with Appium
     */
    protected String takeAppiumScreenshot() {
        try {
            return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            System.err.println("Failed to take Appium screenshot: " + e.getMessage());
            return null;
        }
    }
} 