package com.tonic.tests.web;

import java.util.Properties;
import java.util.Date;
import java.io.File;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.microsoft.playwright.Page;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.pages.HomePage;
import com.tonic.pages.LoginPage;
import com.tonic.pages.AdminDashboardPage;
import com.tonic.pages.ConfigurationPage;
import com.tonic.pages.TerminalsPage;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import static com.tonic.factory.PlaywrightFactory.takeScreenshot;

/**
 * Base Test class for all web tests
 * Provides common setup and teardown functionality
 */
public class BaseTest {

	protected PlaywrightFactory pf;
	public Page page;
	protected Properties prop;

	protected HomePage homePage;
	protected LoginPage loginPage;
	protected AdminDashboardPage adminDashboardPage;
	protected ConfigurationPage configurationPage;
	protected TerminalsPage terminalsPage;
    
    // ExtentReports setup 
    protected static ExtentReports extent;
    protected ExtentTest test;

	@Parameters({ "browser" })
	@BeforeTest
	public void setup(@Optional("chrome") String browserName) {
		pf = new PlaywrightFactory();
		prop = pf.init_prop();

		if (browserName != null) {
			prop.setProperty("browser", browserName);
		}

		page = pf.initBrowser(prop);
		homePage = new HomePage(page);
		loginPage = new LoginPage(page);
	}
	
	/**
	 * Initialize all page objects
	 */
	protected void initPageObjects() {
		adminDashboardPage = new AdminDashboardPage(page);
		configurationPage = new ConfigurationPage(page);
		terminalsPage = new TerminalsPage(page);
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
        // Initialize page objects for each test
        // Use the page already initialized in the BeforeTest
        adminDashboardPage = new AdminDashboardPage(page);
        configurationPage = new ConfigurationPage(page);
        terminalsPage = new TerminalsPage(page);
        loginPage = new LoginPage(page);
        
        System.out.println("Using existing browser session for test method");
    }

	@AfterMethod
    public void tearDownMethod() {
        // Don't close browser here - let AfterTest handle it
        System.out.println("Test method completed");
    }

	@AfterTest
	public void tearDown() {
		if (page != null) {
			page.context().browser().close();
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
		return page;
	}
}
