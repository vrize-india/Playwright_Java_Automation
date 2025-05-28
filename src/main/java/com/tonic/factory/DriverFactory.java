package com.tonic.factory;

import com.tonic.constants.FrameworkConstants;
import com.tonic.enums.ConfigProperties;
import com.tonic.utils.PropertyBuilder;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.MutableCapabilities;

import java.net.URI;

/**
 * Handles dynamic Appium driver instantiation for Android/iOS in local or remote (i.e. Sauce Labs) environments.
 * @author : Gaurav Purwar
 */
public class DriverFactory {

    public static AppiumDriver getDriver(String device, String runMode, String testName) throws Exception {
        AppiumDriver driver = null;

        if (runMode.equalsIgnoreCase("remote")) {
            if (device.equalsIgnoreCase("ios")) {
                MutableCapabilities caps = new MutableCapabilities();
                caps.setCapability("platformName", PropertyBuilder.getPropValue(ConfigProperties.PLATFORMNAMEIOS));
                caps.setCapability("appium:app", PropertyBuilder.getPropValue(ConfigProperties.APPIOS));  // The filename of the mobile app
                caps.setCapability("appium:deviceName", PropertyBuilder.getPropValue(ConfigProperties.DEVICENAMEIOS));
                caps.setCapability("appium:automationName", PropertyBuilder.getPropValue(ConfigProperties.AUTOMATIONNAMEIOS));
                MutableCapabilities sauceOptions = new MutableCapabilities();
                sauceOptions.setCapability("username", PropertyBuilder.getPropValue(ConfigProperties.USERNAME));
                sauceOptions.setCapability("accessKey", PropertyBuilder.getPropValue(ConfigProperties.ACCESSKEY));
                sauceOptions.setCapability("build", PropertyBuilder.getPropValue(ConfigProperties.APPIUMBUILD));
                sauceOptions.setCapability("name", testName);
                caps.setCapability("sauce:options", sauceOptions);
                caps.setCapability("appium:autoAcceptAlerts", true);
                caps.setCapability("appium:maxTypingFrequency", 10);
                URI uri = new URI("https://ondemand.us-west-1.saucelabs.com:443/wd/hub");
                driver = new IOSDriver(uri.toURL(), caps);
                driver.setSetting("acceptAlertButtonSelector", "**/XCUIElementTypeButton[`label == 'Allow While Using App'`]");
            } else {
                MutableCapabilities caps = new MutableCapabilities();
                caps.setCapability("platformName", PropertyBuilder.getPropValue(ConfigProperties.PLATFORMNAMEANDROID));
                caps.setCapability("appium:app", PropertyBuilder.getPropValue(ConfigProperties.APPANDROID));  // The filename of the mobile app
                caps.setCapability("appium:deviceName", PropertyBuilder.getPropValue(ConfigProperties.DEVICENAMEANDROID));
                caps.setCapability("appium:platformVersion", PropertyBuilder.getPropValue(ConfigProperties.PLATFORMVERSION));
                caps.setCapability("appium:automationName", PropertyBuilder.getPropValue(ConfigProperties.AUTOMATIONNAMEANDROID));
                MutableCapabilities sauceOptions = new MutableCapabilities();
                sauceOptions.setCapability("username", PropertyBuilder.getPropValue(ConfigProperties.USERNAME));
                sauceOptions.setCapability("accessKey", PropertyBuilder.getPropValue(ConfigProperties.ACCESSKEY));
                sauceOptions.setCapability("build", PropertyBuilder.getPropValue(ConfigProperties.APPIUMBUILD));
                sauceOptions.setCapability("name", testName);
                sauceOptions.setCapability("deviceOrientation", PropertyBuilder.getPropValue(ConfigProperties.DEVICEORIENTATION));
                sauceOptions.setCapability("appiumVersion", PropertyBuilder.getPropValue(ConfigProperties.APPIUMVERSION));
                caps.setCapability("appium:autoGrantPermissions", true);
                caps.setCapability("sauce:options", sauceOptions);

                URI uri = new URI("https://ondemand.us-west-1.saucelabs.com:443/wd/hub");
                driver = new AndroidDriver(uri.toURL(), caps);            }
        } else {
            if (device.equalsIgnoreCase("android")) {
                UiAutomator2Options options = new UiAutomator2Options();
                options.setDeviceName(PropertyBuilder.getPropValue(ConfigProperties.ANDROIDSIMULATORNAME));
                options.setApp(FrameworkConstants.getAppFilePath());
                options.autoGrantPermissions();
                URI uri = new URI("http://127.0.0.1:" + String.valueOf(FrameworkConstants.getAndroidPort()));

                driver = new AndroidDriver(uri.toURL(), options);
            } else {
                XCUITestOptions options = new XCUITestOptions();
                options.setDeviceName(PropertyBuilder.getPropValue(ConfigProperties.IOSSIMULATORNAME));
                options.setApp(FrameworkConstants.getAppIosFilePath());
                options.autoAcceptAlerts();
                options.setMaxTypingFrequency(10);
                URI uri = new URI("http://127.0.0.1:" + String.valueOf(FrameworkConstants.getIosPort()));

                driver = new IOSDriver(uri.toURL(), options);
                driver.setSetting("acceptAlertButtonSelector", "**/XCUIElementTypeButton[`label == 'Allow While Using App'`]");
            }
        }
        return driver;
    }
}
