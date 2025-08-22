package com.vrize.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Used if Allure Reports are prioritised
 * Utility class for taking screenshots and attaching them to Allure reports
 */
public class AllureScreenshotUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(AllureScreenshotUtils.class);
    private static final String SCREENSHOTS_DIR = System.getProperty("user.dir") + "/screenshots/";
    
    /**
     * Takes a screenshot using Playwright and attaches it to the Allure report
     * 
     * @param page The Playwright page object
     * @param name Name of the screenshot
     * @return byte array of the screenshot
     */
    @Attachment(value = "{name}", type = "image/png")
    public static byte[] takeScreenshot(Page page, String name) {
        try {
            // Create a unique filename for the screenshot
            String screenshotName = name.replaceAll("[^a-zA-Z0-9]", "_") + "_" + 
                                    UUID.randomUUID().toString() + ".png";
            
            // Create the path where the screenshot will be saved
            String screenshotPath = SCREENSHOTS_DIR + screenshotName;
            
            // Create screenshots directory if it doesn't exist
            Path screenshotsDir = Paths.get(SCREENSHOTS_DIR);
            if (!Files.exists(screenshotsDir)) {
                Files.createDirectories(screenshotsDir);
            }
            
            // Take the screenshot using Playwright
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(screenshotPath))
                    .setFullPage(true));
            
            System.out.println("Screenshot taken: " + name);
            return screenshot;
        } catch (Exception e) {
            logger.error("Error taking screenshot: {}", name, e);
            return new byte[0];
        }
    }
    
    /**
     * Overloaded method to take a screenshot of a specific element
     * 
     * @param page The Playwright page object
     * @param selector CSS selector for the element to screenshot
     * @param name Name of the screenshot
     * @return byte array of the screenshot
     */
    @Attachment(value = "{name} - Element Screenshot", type = "image/png")
    public static byte[] takeElementScreenshot(Page page, String selector, String name) {
        try {
            // Create a unique filename for the screenshot
            String screenshotName = "element_" + name.replaceAll("[^a-zA-Z0-9]", "_") + "_" + 
                                    UUID.randomUUID().toString() + ".png";
            
            // Create the path where the screenshot will be saved
            String screenshotPath = SCREENSHOTS_DIR + screenshotName;
            
            // Create screenshots directory if it doesn't exist
            Path screenshotsDir = Paths.get(SCREENSHOTS_DIR);
            if (!Files.exists(screenshotsDir)) {
                Files.createDirectories(screenshotsDir);
            }
            
            // Take the screenshot of the specific element using Playwright
            Locator elementLocator = page.locator(selector);
            byte[] screenshot = elementLocator.screenshot(new Locator.ScreenshotOptions()
                    .setPath(Paths.get(screenshotPath)));
            
            System.out.println("Element screenshot taken: " + name);
            return screenshot;
        } catch (Exception e) {
            logger.error("Error taking element screenshot: {} with selector: {}", name, selector, e);
            return new byte[0];
        }
    }
} 