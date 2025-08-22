package com.vrize.factory;

import com.vrize.constants.FrameworkConstants;
import com.vrize.enums.ConfigProperties;
import com.vrize.utils.PropertyBuilder;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.MutableCapabilities;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to create and return AppiumDriver instances based on device type and run mode (local or remote).
 * All configuration values are fetched from config.properties
 *
 * @author Gaurav Purwar
 */

public class MobileDriverFactory {

    public static AppiumDriver driver;
    // Cache for property values to avoid repeated lookups
    private static final Map<ConfigProperties, String> propertyCache = new HashMap<>();

    public static AppiumDriver getDriver(String testName) throws Exception {
        // Read device and runMode from config properties
        String device = getCachedProperty(ConfigProperties.DEFAULT_DEVICE);
        String runMode = getCachedProperty(ConfigProperties.DEFAULT_RUN_MODE);

        System.out.println("[DRIVER_FACTORY] Using device: " + device + ", runMode: " + runMode + " from config properties");

        // Get run mode and device values from config properties
        String remoteRunMode = getCachedProperty(ConfigProperties.REMOTE_RUN_MODE);
        String localRunMode = getCachedProperty(ConfigProperties.LOCAL_RUN_MODE);
        String iosDevice = getCachedProperty(ConfigProperties.IOS);
        String androidDevice = getCachedProperty(ConfigProperties.ANDROID);

        if (runMode.equalsIgnoreCase(remoteRunMode)) {
            return createRemoteDriver(device, iosDevice, androidDevice, testName);
        } else {
            return createLocalDriver(device, iosDevice, androidDevice);
        }
    }

    /**
     * Gets property value from cache or loads it if not cached
     */
    private static String getCachedProperty(ConfigProperties property) {
        return propertyCache.computeIfAbsent(property, PropertyBuilder::getPropValue);
    }

    /**
     * Clears the property cache (useful for testing or when config changes)
     */
    public static void clearPropertyCache() {
        propertyCache.clear();
    }

    /**
     * Creates a remote AppiumDriver for cloud-based testing
     */
    private static AppiumDriver createRemoteDriver(String device, String iosDevice, String androidDevice, String testName) throws Exception {
        if (device.equalsIgnoreCase(iosDevice)) {
            return createRemoteIOSDriver(testName);
        } else {
            return createRemoteAndroidDriver(testName);
        }
    }

    /**
     * Creates a local AppiumDriver for local testing
     */
    private static AppiumDriver createLocalDriver(String device, String iosDevice, String androidDevice) throws Exception {
        if (device.equalsIgnoreCase(androidDevice)) {
            return createLocalAndroidDriver();
        } else {
            return createLocalIOSDriver();
        }
    }

    /**
     * Creates a remote iOS driver with Sauce Labs configuration
     */
    private static AppiumDriver createRemoteIOSDriver(String testName) throws Exception {
        MutableCapabilities caps = createBaseCapabilities();
        addIOSPlatformCapabilities(caps);
        addSauceLabsOptions(caps, testName);
        addIOSCapabilities(caps);

        driver = new IOSDriver(createRemoteURI().toURL(), caps);

        // Alert selector from config
        String alertSelector = getCachedProperty(ConfigProperties.IOS_ALERT_SELECTOR);
        driver.setSetting("acceptAlertButtonSelector", alertSelector);

        return createRemoteDriver(caps, driver);
    }

    /**
     * Creates a remote Android driver with Sauce Labs configuration
     */
    private static AppiumDriver createRemoteAndroidDriver(String testName) throws Exception {
        MutableCapabilities caps = createBaseCapabilities();
        addAndroidPlatformCapabilities(caps);
        addSauceLabsOptions(caps, testName);
        addAndroidCapabilities(caps);

        return createRemoteDriver(caps, new AndroidDriver(createRemoteURI().toURL(), caps));
    }

    /**
     * Creates a local Android driver
     */
    private static AppiumDriver createLocalAndroidDriver() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName(getCachedProperty(ConfigProperties.ANDROID_SIMULATOR_NAME));
        options.setApp(FrameworkConstants.getAppAndroidFilePath());

        // Android specific options from config
        options.autoGrantPermissions();
        addAndroidLocalCapabilities(options);
        driver = new AndroidDriver(createLocalURI().toURL(), options);

        return driver;
    }

    /**
     * Creates a local iOS driver
     */
    private static AppiumDriver createLocalIOSDriver() throws Exception {
        XCUITestOptions options = new XCUITestOptions();
        options.setDeviceName(getCachedProperty(ConfigProperties.IOS_SIMULATOR_NAME));
        options.setApp(FrameworkConstants.getAppIosFilePath());

        // iOS specific options from config
        options.autoAcceptAlerts();
        options.setMaxTypingFrequency(
            Integer.parseInt(getCachedProperty(ConfigProperties.IOS_MAX_TYPING_FREQUENCY)));

        addIOSLocalCapabilities(options);

        IOSDriver driver = new IOSDriver(createLocalIOSURI().toURL(), options);

        // Alert selector from config
        String alertSelector = getCachedProperty(ConfigProperties.IOS_ALERT_SELECTOR);
        driver.setSetting("acceptAlertButtonSelector", alertSelector);

        return driver;
    }

    /**
     * Creates base capabilities object
     */
    private static MutableCapabilities createBaseCapabilities() {
        return new MutableCapabilities();
    }

    /**
     * Adds iOS platform-specific capabilities
     */
    private static void addIOSPlatformCapabilities(MutableCapabilities caps) {
        caps.setCapability("platformName", getCachedProperty(ConfigProperties.PLATFORM_NAME_IOS));
        caps.setCapability("appium:app", getCachedProperty(ConfigProperties.APP_IOS));
        caps.setCapability("appium:deviceName", getCachedProperty(ConfigProperties.DEVICE_NAME_IOS));
        caps.setCapability("appium:automationName", getCachedProperty(ConfigProperties.AUTOMATION_NAME_IOS));
    }

    /**
     * Adds Android platform-specific capabilities
     */
    private static void addAndroidPlatformCapabilities(MutableCapabilities caps) {
        caps.setCapability("platformName", getCachedProperty(ConfigProperties.PLATFORM_NAME_ANDROID));
        caps.setCapability("appium:app", getCachedProperty(ConfigProperties.APP_ANDROID));
        caps.setCapability("appium:deviceName", getCachedProperty(ConfigProperties.DEVICE_NAME_ANDROID));
        caps.setCapability("appium:platformVersion", getCachedProperty(ConfigProperties.PLATFORM_VERSION));
        caps.setCapability("appium:automationName", getCachedProperty(ConfigProperties.AUTOMATION_NAME_ANDROID));
    }

    /**
     * Creates remote URI for cloud testing
     */
    private static URI createRemoteURI() throws Exception {
        String remoteServerUrl = getCachedProperty(ConfigProperties.REMOTE_SERVER_URL);
        return new URI(remoteServerUrl);
    }

    /**
     * Creates local URI for Android testing
     */
    private static URI createLocalURI() throws Exception {
        String ipAddress = getCachedProperty(ConfigProperties.IP_ADDRESS);
        int port = Integer.parseInt(getCachedProperty(ConfigProperties.PORT));
        return new URI("http://" + ipAddress + ":" + port);
    }

    /**
     * Creates local URI for iOS testing
     */
    private static URI createLocalIOSURI() throws Exception {
        String ipAddress = getCachedProperty(ConfigProperties.IP_ADDRESS);
        int port = Integer.parseInt(getCachedProperty(ConfigProperties.IOS_PORT));
        return new URI("http://" + ipAddress + ":" + port);
    }

    /**
     * Creates remote driver with common setup
     */
    private static AppiumDriver createRemoteDriver(MutableCapabilities caps, AppiumDriver driver) {
        // Common remote driver setup if needed
        return driver;
    }

    /**
     * Adds Sauce Labs specific options to capabilities
     */
    private static void addSauceLabsOptions(MutableCapabilities caps, String testName) {
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("username", getCachedProperty(ConfigProperties.USERNAME));
        sauceOptions.setCapability("accessKey", getCachedProperty(ConfigProperties.ACCESS_KEY));
        sauceOptions.setCapability("build", getCachedProperty(ConfigProperties.APPIUM_BUILD));
        sauceOptions.setCapability("name", testName);
        sauceOptions.setCapability("deviceOrientation", getCachedProperty(ConfigProperties.DEVICE_ORIENTATION));
        sauceOptions.setCapability("appiumVersion", getCachedProperty(ConfigProperties.APPIUM_VERSION));
        caps.setCapability("sauce:options", sauceOptions);
    }

    /**
     * Adds iOS specific capabilities
     */
    private static void addIOSCapabilities(MutableCapabilities caps) {
        caps.setCapability("appium:autoAcceptAlerts",
            Boolean.parseBoolean(getCachedProperty(ConfigProperties.IOS_AUTO_ACCEPT_ALERTS)));
        caps.setCapability("appium:maxTypingFrequency",
            Integer.parseInt(getCachedProperty(ConfigProperties.IOS_MAX_TYPING_FREQUENCY)));
    }

    /**
     * Adds Android specific capabilities
     */
    private static void addAndroidCapabilities(MutableCapabilities caps) {
        caps.setCapability("appium:autoGrantPermissions",
            Boolean.parseBoolean(getCachedProperty(ConfigProperties.ANDROID_AUTO_GRANT_PERMISSIONS)));
    }

    /**
     * Adds Android local specific capabilities
     */
    private static void addAndroidLocalCapabilities(UiAutomator2Options options) {
        String noReset = getCachedProperty(ConfigProperties.ANDROID_NO_RESET);
        if (noReset != null) {
            options.setCapability("appium:noReset", Boolean.parseBoolean(noReset));
        }

        String fullReset = getCachedProperty(ConfigProperties.ANDROID_FULL_RESET);
        if (fullReset != null) {
            options.setCapability("appium:fullReset", Boolean.parseBoolean(fullReset));
        }

        String systemPort = getCachedProperty(ConfigProperties.ANDROID_SYSTEM_PORT);
        if (systemPort != null && !systemPort.isEmpty()) {
            options.setCapability("appium:systemPort", systemPort);
        }
    }

    /**
     * Adds iOS local specific capabilities
     */
    private static void addIOSLocalCapabilities(XCUITestOptions options) {
        String noReset = getCachedProperty(ConfigProperties.IOS_NO_RESET);
        if (noReset != null) {
            options.setCapability("appium:noReset", Boolean.parseBoolean(noReset));
        }

        String fullReset = getCachedProperty(ConfigProperties.IOS_FULL_RESET);
        if (fullReset != null) {
            options.setCapability("appium:fullReset", Boolean.parseBoolean(fullReset));
        }

        String simulatorStartupTimeout = getCachedProperty(ConfigProperties.IOS_SIMULATOR_STARTUP_TIMEOUT);
        if (simulatorStartupTimeout != null) {
            options.setCapability("appium:simulatorStartupTimeout",
                Integer.parseInt(simulatorStartupTimeout));
        }
    }
}
