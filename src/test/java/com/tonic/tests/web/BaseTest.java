package com.tonic.tests.web;

import java.util.Properties;
import java.util.Date;
import java.io.File;

import com.tonic.listeners.AllureListener;
import com.tonic.listeners.JiraListener;
import com.tonic.listeners.Listener;
import org.testng.annotations.*;

import com.microsoft.playwright.Page;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.pages.web.HomePage;
import com.tonic.pages.web.LoginPage;
import com.tonic.pages.web.AdminDashboardPage;
import com.tonic.pages.web.ConfigurationPage;
import com.tonic.pages.web.TerminalsPage;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/**
 * Base Test class for all web tests
 * Provides common setup and teardown functionality
 */
@Listeners({Listener.class,JiraListener.class, AllureListener.class})
public class BaseTest {

    protected PlaywrightFactory pf;
    protected Properties prop;
    protected String platform;
    protected HomePage homePage;
    protected LoginPage loginPage;
    protected AdminDashboardPage adminDashboardPage;
    protected ConfigurationPage configurationPage;
    protected TerminalsPage terminalsPage;

    // ExtentReports setup
    protected static ExtentReports extent;
    protected ExtentTest test;

    @Parameters({ "browser" })
    @BeforeMethod
    public void setup(@Optional("chrome") String browserName) {
        pf = new PlaywrightFactory();
        prop = pf.init_prop();

        if (browserName != null) {
            prop.setProperty("browser", browserName);
        }

        homePage = new HomePage(PlaywrightFactory.getPage());
        loginPage = new LoginPage(PlaywrightFactory.getPage());
    }

    /**
     * Initialize all page objects
     */
    protected void initPageObjects() {
        adminDashboardPage = new AdminDashboardPage(PlaywrightFactory.getPage());
        configurationPage = new ConfigurationPage(PlaywrightFactory.getPage());
        terminalsPage = new TerminalsPage(PlaywrightFactory.getPage());
    }

    @BeforeClass
    @Parameters({"platform"})
    public void setUpPlatform(@Optional("web") String platform) {
        this.platform = platform;
        // You can add logic here to initialize things based on the platform
        System.out.println("Running tests on platform: " + platform);
    }
    @BeforeClass
    public void setupReport() {
        // Setup ExtentReports directly in the test class
        String outputFolder = System.getProperty("user.dir") + File.separator + "build" + File.separator;
        File outputDir = new File(outputFolder);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
            System.out.println("Created output directory: " + outputFolder);
        }

        String reportFile = outputFolder + "TestExecutionReport" + System.currentTimeMillis() + ".html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(reportFile);
        reporter.config().setReportName("Terminal Management Test Results");
        reporter.config().setDocumentTitle("Test Execution Report");

        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Environment", "Test");
        extent.setSystemInfo("User", "Automation Tester");
        extent.setSystemInfo("Timestamp", new Date().toString());

        System.out.println("ExtentReports initialized. Report will be saved to: " + reportFile);

        // Initialize all page objects
        initPageObjects();
    }

    @BeforeMethod
    public void setupMethod() {
        pf = new PlaywrightFactory();
        prop = pf.init_prop();
        pf.initBrowser(prop); // This sets the ThreadLocal for the current thread
        adminDashboardPage = new AdminDashboardPage(PlaywrightFactory.getPage());
        configurationPage = new ConfigurationPage(PlaywrightFactory.getPage());
        terminalsPage = new TerminalsPage(PlaywrightFactory.getPage());
        loginPage = new LoginPage(PlaywrightFactory.getPage());
        homePage = new HomePage(PlaywrightFactory.getPage());
        System.out.println("Using existing browser session for test method");
    }

    @AfterMethod
    public void tearDownMethod() {
        // Don't close browser here - let AfterTest handle it
        System.out.println("Test method completed");
    }

    @AfterTest
    public void tearDown() {
        if (PlaywrightFactory.getPage() != null) {
            PlaywrightFactory.getPage().context().browser().close();
            System.out.println("Browser closed after test");
        }
    }

    @AfterClass
    public void tearDownReport() {
        // Generate the report
        if (extent != null) {
            extent.flush();
            System.out.println("ExtentReports saved successfully!");
        }
    }

    public Page getPage() {
        return PlaywrightFactory.getPage();
    }
}

