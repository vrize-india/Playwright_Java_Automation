package com.tonic.tests.mobile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import com.tonic.constants.FrameworkConstants;
import com.tonic.enums.ConfigProperties;
import com.tonic.utils.PropertyBuilder;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.*;

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
    public HashMap<String, HashMap<String, String>> testClassData = null;
    public HashMap<String, String> tcData = null;
    public HashMap<String, String> configurationData = null;
    AppiumDriverLocalService androidService = null;
    AppiumDriverLocalService iosService = null;

    protected ExtentTest test;
    protected static ExtentReports extent;
    protected String device;
    public String platform;
    public String runmode;

    @BeforeSuite
    @Parameters({"device"})
    public void suiteSetUp(@Optional("android") String device) {
        this.device=device;
        if (device.equalsIgnoreCase("android")){
            AppiumServiceBuilder builder = new AppiumServiceBuilder().withAppiumJS(new File(FrameworkConstants.getAppiumPath())).withIPAddress(FrameworkConstants.getIpAddress()).usingPort(FrameworkConstants.getAndroidPort()).withTimeout(Duration.ofSeconds(Integer.parseInt(PropertyBuilder.getPropValue(ConfigProperties.TIMEOUT))));
            androidService = AppiumDriverLocalService.buildService(builder);
            androidService.start();}

        if (device.equalsIgnoreCase("ios")){
            AppiumServiceBuilder builderIOS = new AppiumServiceBuilder().withAppiumJS(new File(FrameworkConstants.getAppiumPath())).withIPAddress(FrameworkConstants.getIpAddress()).usingPort(FrameworkConstants.getIosPort()).withTimeout(Duration.ofSeconds(Integer.parseInt(PropertyBuilder.getPropValue(ConfigProperties.TIMEOUT))));
            iosService = AppiumDriverLocalService.buildService(builderIOS);
            iosService.start();}
    }
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
    @Parameters({"platform"})
    public void setUpPlatform(@Optional("mobile") String platform) {
        this.platform = platform;
        // You can add logic here to initialize things based on the platform
        System.out.println("Running tests on platform: " + platform);
    }
    @BeforeClass
    @Parameters({"platform"})
    public void setUpRunmode(@Optional("local") String runmode) {
        this.runmode = runmode;
        System.out.println("Running tests on platform: " + runmode);
    }
    @BeforeClass
    @Parameters({"device"})
    public void set(@Optional("device") String platform){
        //this.platform=platform;
        configurationSetup();
    }
    public void configurationSetup() {
        {
            try {
                String inputJsonFilePath = FrameworkConstants.getInputJsonPath();
                Object obj = new JSONParser().parse(new FileReader(inputJsonFilePath + "configuration.json"));
                JSONObject jo = (JSONObject) obj;
                configurationData = (HashMap<String, String>) jo.get("configuration");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
   @BeforeMethod
    public void setUp(Method method) throws Exception {
        if (device.equalsIgnoreCase("android")){
            Driver.initDriver("android", runmode, method.getName());

        }
        else if (device.equalsIgnoreCase("ios")){
            Driver.initDriver("ios", runmode, method.getName());

        }
        configurationSetup();
        dataSetup(method);
    }
    @AfterMethod
    public void tearDown() {
       try{ Driver.quitDriver();
        System.out.println("Appium driver closed");
    } catch (Exception e) {
        System.err.println("Error closing Appium driver: " + e.getMessage());
    }
        System.out.println("Test cleanup complete");
    }

    @AfterSuite
    @Parameters({"device"})
    public void suiteTearDown() {
        if(device.equalsIgnoreCase("android")){
            androidService.stop();
        }
        else if(device.equalsIgnoreCase("ios")){
            iosService.stop();
        }
    }

    @AfterSuite
    public void tearDownReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("Mobile ExtentReports saved successfully!");
        }
    }

    public void dataSetup(Method method) {
        {
            String tcMethodName = method.getName();
            String tcClassName = this.getClass().getSimpleName();
            String tcPackageFullName = this.getClass().getPackageName();
            String tcPakageName = tcPackageFullName.substring(tcPackageFullName.lastIndexOf(".") + 1);

            Object obj = null;
            try {
                String inputJsonFilePath;
                inputJsonFilePath = FrameworkConstants.getInputJsonPath();
                obj = new JSONParser().parse(new FileReader(inputJsonFilePath + tcPakageName + ".json"));
                JSONObject jo = (JSONObject) obj;
                testClassData = (HashMap<String, HashMap<String, String>>) jo.get(tcClassName);
                tcData = (HashMap<String, String>) testClassData.get(tcMethodName);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
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