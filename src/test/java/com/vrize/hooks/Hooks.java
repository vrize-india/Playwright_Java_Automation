package com.vrize.hooks;

import com.microsoft.playwright.*;
import com.vrize.common.web.BasePage;
import com.vrize.constants.FrameworkConstants;
import com.vrize.driver.MobileDriver;
import com.vrize.enums.ConfigProperties;
import com.vrize.factory.PlaywrightFactory;
import com.vrize.listeners.XrayLogger;
import com.vrize.pageObjects.web.login.LoginPage;
import com.vrize.utils.MobileScreenshotUtils;
import com.vrize.utils.PlaywrightScreenshotUtils;
import com.vrize.utils.PropertyBuilder;
import io.cucumber.java.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

public class Hooks {

    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);

    private Playwright playwright;
    private Browser browser;
    private BrowserContext videoContext;
    private Page page;
    private static Process appiumProcess;

    private static final boolean attachAllScreenshots = FrameworkConstants.isAttachAllScreenshots();
    private static final boolean bugLoggingEnabled = FrameworkConstants.isBugLoggingEnabled();

    private static ThreadLocal<SoftAssert> softAssertThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<BasePage> basePageThreadLocal = new ThreadLocal<>();
    public static ThreadLocal<Playwright> apiPlaywright = new ThreadLocal<>();
    public static ThreadLocal<APIRequest> apiRequest = new ThreadLocal<>();
    public static ThreadLocal<APIRequestContext> apiRequestContext = new ThreadLocal<>();

    // ─── Appium Server Helpers ────────────────────────────────────

    public static void startAppiumServer() throws IOException, InterruptedException {
        if (appiumProcess == null || !appiumProcess.isAlive()) {
            String appiumMainJs = System.getenv("APPIUM_MAIN_JS");
            if (appiumMainJs == null || appiumMainJs.isEmpty()) {
                appiumMainJs = FrameworkConstants.getAppiumPath();
            }
            String device = System.getProperty(
                    "device",
                    PropertyBuilder.getPropValue(com.vrize.enums.ConfigProperties.ANDROID)
            );
            int port = "ios".equalsIgnoreCase(device)
                    ? FrameworkConstants.getIosPort()
                    : FrameworkConstants.getAndroidPort();
            String driverFlag = "ios".equalsIgnoreCase(device)
                    ? PropertyBuilder.getPropValue(com.vrize.enums.ConfigProperties.AUTOMATION_NAME_IOS)
                    : PropertyBuilder.getPropValue(com.vrize.enums.ConfigProperties.AUTOMATION_NAME_ANDROID);
            String ipAddress = FrameworkConstants.getIpAddress();

            // Kill any existing process on the port before starting
            try {
                ProcessBuilder killBuilder = new ProcessBuilder(
                        "sh", "-c", "lsof -ti:" + port + " | xargs kill -9 2>/dev/null || true"
                );
                Process killProcess = killBuilder.start();
                killProcess.waitFor();
                Thread.sleep(1000); // Give time for port to be released
            } catch (Exception e) {
                System.err.println("Warning: Could not kill existing process on port " + port + ": " + e.getMessage());
            }

            ProcessBuilder builder = new ProcessBuilder(
                    appiumMainJs,
                    "--port", String.valueOf(port),
                    "--use-driver", driverFlag,
                    "--address", ipAddress
            );
            String logDir = FrameworkConstants.getLogDirectory();
            new File(logDir).mkdirs();
            builder.redirectOutput(new File(logDir + "/appium-server.log"));
            builder.redirectError(new File(logDir + "/appium-server-error.log"));
            appiumProcess = builder.start();

            if (!waitForAppiumServer(FrameworkConstants.getAppiumStartupTimeout(), port)) {
                throw new RuntimeException("Appium server did not become ready in time");
            }
        }
    }

    private static boolean waitForAppiumServer(long timeoutMillis, int port) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMillis) {
            try {
                String ip = FrameworkConstants.getIpAddress();
                HttpURLConnection conn = (HttpURLConnection) new URL("http://" + ip + ":" + port + "/status")
                        .openConnection();
                conn.setConnectTimeout(FrameworkConstants.getAppiumConnectionTimeout());
                conn.setReadTimeout(FrameworkConstants.getAppiumReadTimeout());
                if (conn.getResponseCode() == 200) {
                    return true;
                }
            } catch (IOException ignored) {
            }
            Thread.sleep(FrameworkConstants.getAppiumSleepInterval());
        }
        return false;
    }

    public static void stopAppiumServer() {
        if (appiumProcess != null && appiumProcess.isAlive()) {
            appiumProcess.destroy();
        }
    }

    // ─── Cucumber Hooks ───────────────────────────────────────────

    @BeforeAll
    public static void beforeAll() {
        // Check if this is a retry attempt before initializing Xray
        boolean isRetryRun = isRetryExecution();
        
        if (isRetryRun) {
            System.out.println("[XRAY] Suppressing execId creation for retry attempt in Hooks");
            return;
        }
        
        XrayLogger.initializeTestExecutionKey();
    }
    
    /**
     * Determines if this is a retry execution by checking system properties.
     */
    private static boolean isRetryExecution() {
        // Check if this is a rerun execution by looking at system properties
        String rerunFile = System.getProperty("cucumber.rerun.file");
        if (rerunFile != null && rerunFile.contains("rerun.txt")) {
            return true;
        }
        
        // Check if we're running from a rerun file
        String cucumberFeatures = System.getProperty("cucumber.features");
        if (cucumberFeatures != null && cucumberFeatures.contains("@target/rerun.txt")) {
            return true;
        }
        
        // Check if this is a retry attempt by looking at the retry attempt system property
        String retryAttempt = System.getProperty("cucumber.retry.attempt");
        if (retryAttempt != null && !retryAttempt.trim().isEmpty()) {
            return true;
        }
        
        return false;
    }

    @Before(order = 0)
    public void setUp(Scenario scenario) throws Exception {
        // Add thread detection logs
        logger.info("=== HOOK EXECUTION TRACKING ===");
        logger.info("Thread ID: {}", Thread.currentThread().getId());
        logger.info("Thread Name: {}", Thread.currentThread().getName());
        logger.info("Scenario: {}", scenario.getName());
        
        String platform = System.getProperty("Platform", System.getProperty("platform", ""));
        if (platform.isEmpty()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(FrameworkConstants.getPlatformConfigFilePath())) {
                Properties p = new Properties();
                if (in != null) {
                    p.load(in);
                    platform = p.getProperty("platform", FrameworkConstants.getDefaultPlatform());
                } else {
                    platform = FrameworkConstants.getDefaultPlatform();
                }
            }
        }
        platform = platform.trim();
        softAssertThreadLocal.set(new SoftAssert());

        boolean isHybrid = "HYBRID".equalsIgnoreCase(platform);
        System.out.println("[HOOKS] Platform detected: " + platform + ", isHybrid: " + isHybrid);

        if ("MOBILE".equalsIgnoreCase(platform)) {
            startAppiumServer();
            MobileDriver.initDriver(scenario.getName());
            return;
        }

        // For HYBRID mode, only start Appium server but don't initialize driver yet
        if (isHybrid) {
            logger.info("[HOOKS] Starting Appium server for HYBRID mode (driver will be initialized later)...");
            startAppiumServer();
            logger.info("[HOOKS] Appium server started for HYBRID mode");
        }

        if (isHybrid || "API".equalsIgnoreCase(platform)) {
            apiPlaywright.set(Playwright.create());
            apiRequest.set(apiPlaywright.get().request());
            apiRequestContext.set(apiRequest.get().newContext());
            if ("API".equalsIgnoreCase(platform)) return;
        }

        // Use XrayLogger.isXrayEnabled() to check both system properties and config file
        boolean xrayEnabled = XrayLogger.isXrayEnabled();

        // Add execution ID to plan ID after initialization
        if (xrayEnabled) {
            XrayLogger.addExecutionIdToPlanIdAutomatically();

            // Associate current test execution with current test plan using Xray API
            boolean associated = XrayLogger.associateCurrentTestExecutionWithCurrentTestPlan();
            if (associated) {
                logger.info("[XRAY] Successfully associated current test execution with current test plan via Xray API");
            } else {
                logger.warn("[XRAY] Failed to associate current test execution with current test plan via Xray API");
            }
        }

        if (isHybrid || "WEB".equalsIgnoreCase(platform)) {
            if (xrayEnabled) {
                logger.info("=== XRAY ENABLED - LAUNCHING SINGLE BROWSER WITH VIDEO RECORDING ===");
                logger.info("Thread ID: {}", Thread.currentThread().getId());

                // Create single browser instance with video capabilities
                String browserName = PropertyBuilder.getPropValue(ConfigProperties.BROWSER).trim().toLowerCase();
                boolean isHeadless = Boolean.parseBoolean(PropertyBuilder.getPropValue(ConfigProperties.HEADLESS_MODE));

                playwright = Playwright.create();
                BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(isHeadless);

                switch (browserName) {
                    case "chromium":
                        browser = playwright.chromium().launch(options);
                        break;
                    case "chrome":
                        options.setChannel("chrome");
                        browser = playwright.chromium().launch(options);
                        break;
                    case "edge":
                        options.setChannel("msedge");
                        browser = playwright.chromium().launch(options);
                        break;
                    case "firefox":
                        browser = playwright.firefox().launch(options);
                        break;
                    case "safari":
                    case "webkit":
                        browser = playwright.webkit().launch(options);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported browser: " + browserName);
                }

                logger.info("=== CREATING VIDEO-ENABLED CONTEXT ===");
                logger.info("Thread ID: {}", Thread.currentThread().getId());

                // Create video-enabled context and page
                Path videoDirPath = Paths.get(System.getProperty("user.dir"), FrameworkConstants.getVideoDirectory());
                Files.createDirectories(videoDirPath);

                videoContext = browser.newContext(new Browser.NewContextOptions().setRecordVideoDir(videoDirPath));
                page = videoContext.newPage();

                // Set in ThreadLocal for PlaywrightFactory
                PlaywrightFactory.setPlaywright(playwright);
                PlaywrightFactory.setBrowser(browser);
                PlaywrightFactory.setPage(page);

                logger.info("=== SINGLE VIDEO-ENABLED BROWSER CONTEXT CREATED ===");
                logger.info("Thread ID: {}", Thread.currentThread().getId());
            } else {
                logger.info("=== XRAY DISABLED - LAUNCHING SINGLE BROWSER ===");
                logger.info("Thread ID: {}", Thread.currentThread().getId());

                PlaywrightFactory pwFactory = new PlaywrightFactory();
                Properties props = pwFactory.initProp();
                page = pwFactory.initBrowser(props);
                playwright = PlaywrightFactory.getPlaywright();
                browser = PlaywrightFactory.getBrowser();
            }
            
            basePageThreadLocal.set(new BasePage(page));
            
            // Perform login for WEB and HYBRID platforms
            if ("WEB".equalsIgnoreCase(platform) || isHybrid) {
                String url = PropertyBuilder.getEnvPropValue("url");
                String user = PropertyBuilder.getEnvPropValue("username");
                String pass = PropertyBuilder.getEnvPropValue("password");
                page.navigate(url);
                new LoginPage(page).performLogin(user, pass);
            }
        }

        basePageThreadLocal.set(new BasePage(page));

        if ("WEB".equalsIgnoreCase(platform)) {
            logger.info("=== PERFORMING LOGIN ===");
            logger.info("Thread ID: {}", Thread.currentThread().getId());

            String url = PropertyBuilder.getEnvPropValue("url");
            String user = PropertyBuilder.getEnvPropValue("username");
            String pass = PropertyBuilder.getEnvPropValue("password");
            page.navigate(url);
            new LoginPage(page).performLogin(user, pass);

            logger.info("=== LOGIN COMPLETED ===");
            logger.info("Thread ID: {}", Thread.currentThread().getId());
        }
    }

    @AfterStep
    public void attachScreenshot(Scenario scenario) {
        String platform = System.getProperty("Platform", System.getProperty("platform", ""));
        if ("MOBILE".equalsIgnoreCase(platform)) {
            if (FrameworkConstants.isMobileScreenshotsEnabled() && scenario.isFailed()) {
                String base64 = MobileScreenshotUtils.screenshotCapture();
                if (base64 != null) {
                    byte[] img = Base64.getDecoder().decode(base64);
                    scenario.attach(img, "image/png", "Mobile_Failure_Screenshot");
                }
            }
        } else if (!"API".equalsIgnoreCase(platform)) {
            if (attachAllScreenshots || scenario.isFailed()) {
                String base64 = PlaywrightScreenshotUtils.takeScreenshotWithFullTraceabilityAsBase64(page, scenario.getName());
                byte[] img = Base64.getDecoder().decode(base64);
                scenario.attach(img, "image/png", "Step_Screenshot");
            }
        }
    }

    @After(order = 0)
    public void createBugForFailedScenario(Scenario scenario) {
        if (!bugLoggingEnabled || !scenario.isFailed()) {
            return;
        }
        // TODO: Implement bug logging logic for failed scenarios
        logger.info("Bug logging would be implemented here for failed scenario: {}", scenario.getName());
    }


    @After(order = 1)
    public void tearDown(Scenario scenario) {
        String platform = System.getProperty("Platform", System.getProperty("platform", ""));
        if (platform.isEmpty()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(FrameworkConstants.getPlatformConfigFilePath())) {
                Properties p = new Properties();
                if (in != null) {
                    p.load(in);
                    platform = p.getProperty("platform", FrameworkConstants.getDefaultPlatform());
                } else {
                    platform = FrameworkConstants.getDefaultPlatform();
                }
            } catch (IOException e) {
                platform = FrameworkConstants.getDefaultPlatform();
            }
        }
        platform = platform.trim();
        boolean isHybrid = "HYBRID".equalsIgnoreCase(platform);

        if ("MOBILE".equalsIgnoreCase(platform) || isHybrid) {
            System.out.println("[HOOKS] Cleaning up mobile resources...");
            // Kill the mobile app if driver is initialized
            if (MobileDriver.isDriverInitialized()) {
                try {
                    if (MobileDriver.getDriver() instanceof io.appium.java_client.android.AndroidDriver) {
                        ((io.appium.java_client.android.AndroidDriver) MobileDriver.getDriver()).terminateApp("com.ordyx.one");
                        System.out.println("[HOOKS] Mobile app terminated successfully");
                    }
                } catch (Exception e) {
                    System.out.println("[HOOKS] Failed to terminate app: " + e.getMessage());
                }
            }

            // Quit mobile driver and unload it
            if (MobileDriver.isDriverInitialized()) {
                try {
                    MobileDriver.quitDriver();
                    System.out.println("[HOOKS] Mobile driver quit successfully");
                } catch (Exception e) {
                    System.err.println("[HOOKS] Error quitting mobile driver: " + e.getMessage());
                }
            } else {
                System.out.println("[HOOKS] No mobile driver to quit");
            }

            // Stop Appium server
            try {
                stopAppiumServer();
                System.out.println("[HOOKS] Appium server stopped successfully");
            } catch (Exception e) {
                System.err.println("[HOOKS] Error stopping Appium server: " + e.getMessage());
            }

            System.out.println("[HOOKS] Mobile cleanup completed");
        }
        if (isHybrid || "WEB".equalsIgnoreCase(platform)) {
            if (videoContext != null) videoContext.close();
            if (page != null) page.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        }
        if (isHybrid || "API".equalsIgnoreCase(platform)) {
            if (apiPlaywright.get() != null) apiPlaywright.get().close();
            apiPlaywright.remove();
            apiRequest.remove();
            apiRequestContext.remove();
        }
        softAssertThreadLocal.remove();
        basePageThreadLocal.remove();
    }

    // ─── Public Getters ────────────────────────────────────

    public static BasePage getBasePage() {
        return basePageThreadLocal.get();
    }

    public static SoftAssert getSoftAssert() {
        return softAssertThreadLocal.get();
    }
}
