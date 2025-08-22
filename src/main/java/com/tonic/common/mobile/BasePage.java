package com.tonic.common.mobile;


import com.google.common.collect.ImmutableList;
import com.tonic.constants.FrameworkConstants;
import com.tonic.driver.MobileDriver;
import com.tonic.enums.WaitEnums;
import com.tonic.pageObjects.mobile.LoginScreen;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.apache.commons.io.FileUtils;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.tonic.factory.MobileExplicitWaitFactory.waitForElement;

public class BasePage implements LoginScreen {

    private final AppiumDriver driver;
    public static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);

    public BasePage() {
        this(MobileDriver.getDriver());
    }

    public BasePage(AppiumDriver driver) {
        this.driver = driver;
        if (this.driver == null) {
            throw new IllegalStateException("Appium driver is not initialized! Make sure MobileDriver.initDriver() is called before creating page objects.");
        }
        LOGGER.info("Mobile BasePage initialized with driver: {}", driver.getClass().getSimpleName());
    }

    protected HashMap<String, String> androidXpath = new HashMap<>();
    protected HashMap<String, String> iosXpath = new HashMap<>();



    public void tap(By by, WaitEnums waitStrategy, String elementName) {
        LOGGER.debug("Attempting to tap element: {} using strategy: {}", elementName, waitStrategy);
        waitForElement(waitStrategy, by).ifPresentOrElse(
            element -> {
                element.click();
                LOGGER.info("Successfully tapped element: {}", elementName);
            },
            () -> LOGGER.warn("Failed to find element for tapping: {}", elementName)
        );
    }

    public String getText(By by, WaitEnums waitStrategy, String elementName) {
        LOGGER.debug("Getting text from element: {} using strategy: {}", elementName, waitStrategy);
        String text = waitForElement(waitStrategy, by).map(WebElement::getText).orElse("");
        if (!text.isEmpty()) {
            LOGGER.debug("Retrieved text from {}: '{}'", elementName, text);
        } else {
            LOGGER.warn("No text found for element: {}", elementName);
        }
        return text;
    }

    public String getAttribute(By by, WaitEnums waitStrategy, String elementName, String attr) {
        LOGGER.debug("Getting attribute '{}' from element: {} using strategy: {}", attr, elementName, waitStrategy);
        String attributeValue = waitForElement(waitStrategy, by).map(e -> e.getAttribute(attr)).orElse("");
        if (!attributeValue.isEmpty()) {
            LOGGER.debug("Retrieved attribute '{}' from {}: '{}'", attr, elementName, attributeValue);
        } else {
            LOGGER.warn("No value found for attribute '{}' in element: {}", attr, elementName);
        }
        return attributeValue;
    }

    public void tap(WebElement webElement, String elementName) {
        try {
            webElement.click();
            LOGGER.info("Successfully tapped WebElement: {}", elementName);
        } catch (Exception e) {
            LOGGER.error("Failed to tap WebElement: {} - Error: {}", elementName, e.getMessage());
            throw e;
        }
    }

    public void sendKeys(By by, CharSequence value, WaitEnums waitStrategy, String elementName, boolean shouldScroll) {
        if (shouldScroll || MobileDriver.getDriver() instanceof IOSDriver) {
            scrollTo(by, elementName);
        }
        waitForElement(waitStrategy, by).ifPresent(el -> el.sendKeys(value));
        LOGGER.info("{} is entered successfully in {} input field", value, elementName);
    }

    public static boolean isVisible(By by, String elementName) {
        try {
            WebElement element = MobileDriver.getDriver().findElement(by);
            boolean visible = element.isDisplayed();
            LOGGER.info("{} is {}", elementName, visible ? "visible" : "present but not visible");
            return visible;
        } catch (Exception e) {
            LOGGER.warn("{} is not present", elementName);
            return false;
        }
    }

    public boolean isVisible(By by, String elementName, int timeout) {
        try {
            waitForElement(WaitEnums.VISIBLE, by, timeout);
            return isVisible(by, elementName);
        } catch (Exception e) {
            LOGGER.warn("{} is not visible", elementName);
            return false;
        }
    }

    public static void scrollTo(By by, String elementName) {
        scrollTo(by, elementName, "down");
    }

    public static void scrollTo(By by, String elementName, String scrollDirection) {
        LOGGER.info("Starting scroll to find element: {} in direction: {}", elementName, scrollDirection);
        int scrollAttempts = 0;
        final int maxScrollAttempts = 10;
        
        while (!isVisible(by, elementName) && scrollAttempts < maxScrollAttempts) {
            scrollAttempts++;
            LOGGER.debug("Scroll attempt #{} for element: {}", scrollAttempts, elementName);
            
            Dimension size = MobileDriver.getDriver().manage().window().getSize();
            Point midPoint = new Point(size.width / 2, size.height / 2);
            int bottom = midPoint.y + (int) (midPoint.y * 0.2);
            int top = midPoint.y - (int) (midPoint.y * 0.2);
            int mid = midPoint.y - (int) (midPoint.y * 0.3);

            Point start, end;
            if (scrollDirection.equalsIgnoreCase("down")) {
                start = new Point(midPoint.x, bottom);
                end = new Point(midPoint.x, top);
            } else if (scrollDirection.equalsIgnoreCase("mid")) {
                start = new Point(midPoint.x, bottom);
                end = new Point(midPoint.x, mid);
            } else {
                start = new Point(midPoint.x, top);
                end = new Point(midPoint.x, bottom);
            }

            PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
            Sequence swipe = new Sequence(input, 0);
            swipe.addAction(input.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.x, start.y));
            swipe.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(input.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), end.x, end.y));
            swipe.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            MobileDriver.getDriver().perform(ImmutableList.of(swipe));

            // Brief wait for scroll animation to complete
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        
        if (isVisible(by, elementName)) {
            LOGGER.info("Successfully found element '{}' after {} scroll attempts", elementName, scrollAttempts);
        } else {
            LOGGER.warn("Element '{}' not found after {} scroll attempts", elementName, scrollAttempts);
        }
    }

    /**
     * @deprecated Use proper WebDriverWait methods instead of Thread.sleep
     */
    @Deprecated
    public static void waitForMillis(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ignored) {
        }
    }

    public boolean waitUntilElementIsVisible(By locator) {
        return waitUntilElementIsVisible(locator, FrameworkConstants.getExplicitWait());
    }

    public boolean waitUntilElementIsVisible(By locator, int waitTime) {
        LOGGER.debug("Waiting for element visibility: {} for max {} seconds", locator, waitTime);
        for (int i = 1; i <= waitTime; i++) {
            try {
                WebElement element = MobileDriver.getDriver().findElement(locator);
                if (element.isDisplayed() || element.isEnabled()) {
                    LOGGER.info("Element found and visible after {} attempts: {}", i, locator);
                    return true;
                }
            } catch (Exception e) {
                // Wait between element visibility checks using mobile wait strategy
                mobileWait(WaitEnums.UI_INTERACTION, 1);
                LOGGER.debug("Trial {}: Element not found - {}", i, locator);
            }
        }
        LOGGER.warn("Element not visible after {} attempts: {}", waitTime, locator);
        return false;
    }

    public void clearField(By by, WaitEnums waitStrategy, String elementName) {
        LOGGER.debug("Clearing field: {} using strategy: {}", elementName, waitStrategy);
        Assert.assertTrue(waitUntilElementIsVisible(by), elementName + " is not visible");
        waitForElement(waitStrategy, by).ifPresentOrElse(
            element -> {
                element.clear();
                LOGGER.info("Successfully cleared field: {}", elementName);
            },
            () -> LOGGER.error("Failed to find element for clearing: {}", elementName)
        );
    }

    public void waitForEl(By locator) {
        LOGGER.debug("Waiting for element to be visible: {}", locator);
        try {
            new WebDriverWait(MobileDriver.getDriver(), Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOfElementLocated(locator));
            LOGGER.debug("Element is now visible: {}", locator);
        } catch (Exception e) {
            LOGGER.error("Element did not become visible within timeout: {} - Error: {}", locator, e.getMessage());
            throw e;
        }
    }

    public void waitForEl(WebElement ele) {
        LOGGER.debug("Waiting for WebElement to be visible");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOf(ele));
            LOGGER.debug("WebElement is now visible");
        } catch (Exception e) {
            LOGGER.error("WebElement did not become visible within timeout - Error: {}", e.getMessage());
            throw e;
        }
    }

    public void clickElement(By locator, String val) {
        LOGGER.debug("Attempting to click element: {}", val);
        try {
            waitForEl(locator);
            driver.findElement(locator).click();
            LOGGER.info("Successfully clicked element: {}", val);
        } catch (Exception e) {
            LOGGER.error("Failed to click element: {} - Error: {}", val, e.getMessage());
            throw e;
        }
    }

    public void enterVal(By locator, String fieldName, String val) {
        LOGGER.debug("Entering value '{}' in field: {}", val, fieldName);
        try {
            waitForEl(locator);
            WebElement element = driver.findElement(locator);
            element.clear();
            element.sendKeys(val);
            LOGGER.info("Successfully entered value '{}' in field: {}", val, fieldName);
        } catch (Exception e) {
            LOGGER.error("Failed to enter value '{}' in field: {} - Error: {}", val, fieldName, e.getMessage());
            throw e;
        }
    }

    public void clickElement(WebElement ele, String val) {
        LOGGER.debug("Attempting to click WebElement: {}", val);
        try {
            waitForEl(ele);
            ele.click();
            LOGGER.info("Successfully clicked WebElement: {}", val);
        } catch (Exception e) {
            LOGGER.error("Failed to click WebElement: {} - Error: {}", val, e.getMessage());
            throw e;
        }
    }

    public void enterVal(WebElement ele, String fieldName, String val) {
        LOGGER.debug("Entering value '{}' in WebElement field: {}", val, fieldName);
        try {
            waitForEl(ele);
            ele.sendKeys(val);
            LOGGER.info("Successfully entered value '{}' in WebElement field: {}", val, fieldName);
        } catch (Exception e) {
            LOGGER.error("Failed to enter value '{}' in WebElement field: {} - Error: {}", val, fieldName, e.getMessage());
            throw e;
        }
    }

    public void implicitWait() {
        LOGGER.debug("Setting implicit wait to 15 seconds");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        LOGGER.debug("Implicit wait configured successfully");
    }

    // Function to tap on specific coordinates
    public void tap(int x, int y) {
        LOGGER.debug("Performing tap at coordinates: ({}, {})", x, y);
        try {
            final var finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            var tapPoint = new Point(x, y);

            var tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ofMillis(0),
                    PointerInput.Origin.viewport(), tapPoint.x, tapPoint.y));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(new Pause(finger, Duration.ofMillis(50)));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            driver.perform(Arrays.asList(tap));
            LOGGER.info("Successfully tapped at coordinates: ({}, {})", x, y);
            
            // Wait for UI to respond after tap
            mobileWait(WaitEnums.UI_INTERACTION, 1);
        } catch (Exception e) {
            LOGGER.error("Failed to tap at coordinates: ({}, {}) - Error: {}", x, y, e.getMessage());
            throw e;
        }
    }

    /**
     * Mobile-specific wait strategy using FluentWait and app state polling
     * @param waitType - type of wait needed (APP_LAUNCH, UI_INTERACTION, NAVIGATION)
     * @param maxWaitSeconds - maximum time to wait
     */
    public void mobileWait(WaitEnums waitType, int maxWaitSeconds) {
        LOGGER.debug("Starting mobile wait - Type: {}, Max seconds: {}", waitType, maxWaitSeconds);
        long startTime = System.currentTimeMillis();
        
        FluentWait<AppiumDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(maxWaitSeconds))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        try {
            switch (waitType) {
                case APP_LAUNCH:
                    wait.until(driver -> {
                        try {
                            // Check if app is responsive by getting current activity (Android) or page source
                            String source = driver.getPageSource();
                            return source != null && source.length() > 100; // App has loaded content
                        } catch (Exception e) {
                            return false;
                        }
                    });
                    LOGGER.info("Mobile app launch completed in {}ms", System.currentTimeMillis() - startTime);
                    break;

                case UI_INTERACTION:
                    wait.until(driver -> {
                        try {
                            // Poll for app responsiveness after interaction
                            return driver.getCurrentUrl() != null || driver.getPageSource() != null;
                        } catch (Exception e) {
                            return false;
                        }
                    });
                    LOGGER.debug("Mobile UI interaction settled in {}ms", System.currentTimeMillis() - startTime);
                    break;

                case NAVIGATION:
                    String initialSource = driver.getPageSource();
                    wait.until(driver -> {
                        try {
                            // Wait for page source to change indicating navigation
                            String currentSource = driver.getPageSource();
                            return !currentSource.equals(initialSource);
                        } catch (Exception e) {
                            return false;
                        }
                    });
                    LOGGER.info("Mobile navigation completed in {}ms", System.currentTimeMillis() - startTime);
                    break;

                default:
                    LOGGER.warn("Unknown mobile wait type: {}", waitType);
                    return;
            }
        } catch (Exception e) {
            LOGGER.warn("Mobile wait '{}' timed out after {}ms - Error: {}", waitType, System.currentTimeMillis() - startTime, e.getMessage());
        }
    }



    /**
     * @deprecated Use mobileWait(MobileWaitType, seconds) instead
     */
    @Deprecated
    public void waitForMilliseconds(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while waiting: " + e.getMessage());
        }
    }

    /**
     * Function to tap and enter text at specific coordinates
     * @param x
     * @param y
     * @throws InterruptedException
     */
    public void enterText(int x, int y, String text) throws InterruptedException {
        LOGGER.debug("Entering text '{}' at coordinates: ({}, {})", text, x, y);
        try {
            tap(x, y);
            new Actions(driver).sendKeys(text).perform();
            LOGGER.info("Successfully entered text '{}' at coordinates: ({}, {})", text, x, y);
        } catch (Exception e) {
            LOGGER.error("Failed to enter text '{}' at coordinates: ({}, {}) - Error: {}", text, x, y, e.getMessage());
            throw e;
        }
    }


    /**
     * Function to tap and enter text at specific coordinates
     * @param x
     * @param y
     * @throws InterruptedException
     */
    public void clearAndEnterText(int x, int y, int charCount, String text) throws InterruptedException {
        LOGGER.debug("Clearing {} characters and entering text '{}' at coordinates: ({}, {})", charCount, text, x, y);
        try {
            tap(x, y);
            clearField(x, y, charCount);
            new Actions(driver).sendKeys(text).perform();
            LOGGER.info("Successfully cleared {} characters and entered text '{}' at coordinates: ({}, {})", charCount, text, x, y);
        } catch (Exception e) {
            LOGGER.error("Failed to clear and enter text '{}' at coordinates: ({}, {}) - Error: {}", text, x, y, e.getMessage());
            throw e;
        }
    }


    /**
     * Function to clear text field by sending backspaces
     * @param x
     * @param y
     * @param charCount
     * @throws InterruptedException
     */
    public void clearField(int x, int y, int charCount) throws InterruptedException {
        LOGGER.debug("Clearing field with {} backspaces at coordinates: ({}, {})", charCount, x, y);
        try {
            tap(x, y); // Tap to focus on the input field
            Actions actions = new Actions(driver);
            for (int i = 0; i < charCount; i++) {
                actions.sendKeys(Keys.BACK_SPACE);
            }
            actions.build().perform();
            LOGGER.debug("Successfully cleared field with {} backspaces at coordinates: ({}, {})", charCount, x, y);
        } catch (Exception e) {
            LOGGER.error("Failed to clear field at coordinates: ({}, {}) - Error: {}", x, y, e.getMessage());
            throw e;
        }
    }

    /**
     * Helper method to extract X coordinate from coordinate string
     * @param coOrds coordinate string in format "x,y"
     * @return X coordinate
     */
    public int getX(String coOrds) {
        try {
            int x = Integer.parseInt(coOrds.split(",")[0].trim());
            LOGGER.debug("Extracted X coordinate: {} from coOrds: {}", x, coOrds);
            return x;
        } catch (Exception e) {
            LOGGER.error("Failed to extract X coordinate from coOrds: {} - Error: {}", coOrds, e.getMessage());
            throw new IllegalArgumentException("Invalid coordinate format: " + coOrds, e);
        }
    }

    /**
     * Helper method to extract Y coordinate from coordinate string  
     * @param coords coordinate string in format "x,y"
     * @return Y coordinate
     */
    public int getY(String coords) {
        try {
            int y = Integer.parseInt(coords.split(",")[1].trim());
            LOGGER.debug("Extracted Y coordinate: {} from coords: {}", y, coords);
            return y;
        } catch (Exception e) {
            LOGGER.error("Failed to extract Y coordinate from coords: {} - Error: {}", coords, e.getMessage());
            throw new IllegalArgumentException("Invalid coordinate format: " + coords, e);
        }
    }

    /**
     * Tap using coordinate string
     * @param coOrds coordinate string in format "x,y"
     */
    public void tap(String coOrds) {
        LOGGER.debug("Tapping using coordinate string: {}", coOrds);
        tap(getX(coOrds), getY(coOrds));
    }

    /**
     * Clear and enter text using coordinate string
     * @param coords coordinate string in format "x,y"
     * @param charCount number of characters to clear
     * @param text text to enter
     * @throws InterruptedException
     */
    public void clearAndEnterText(String coords, int charCount, String text) throws InterruptedException {
        LOGGER.debug("Clearing and entering text using coordinate string: {} - Text: '{}'", coords, text);
        clearAndEnterText(getX(coords), getY(coords), charCount, text);
    }

    /**
     * Clear and enter text using coordinate string
     * @param coords coordinate string in format "x,y"
     * @param text text to enter
     * @throws InterruptedException
     */

    public void enterText(String coords, String text) throws InterruptedException {
        LOGGER.debug("Entering text '{}' using coordinate string: {} ", coords, text);
        enterText(getX(coords), getY(coords),text);
    }

    /**
     * Takes a screenshot of the mobile app
     * @param screenshotName name prefix for the screenshot file
     * @return the file path of the captured screenshot
     */
    public String takeScreenshot(String screenshotName) {
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotsDir = new File("screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }

            // Generate timestamp for unique filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String fileName = screenshotName + "_" + timestamp + ".png";
            File screenshotFile = new File(screenshotsDir, fileName);

            // Take screenshot using Appium driver
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(sourceFile, screenshotFile);

            String absolutePath = screenshotFile.getAbsolutePath();
            LOGGER.info("Screenshot saved successfully: {}", absolutePath);
            return absolutePath;

        } catch (IOException e) {
            LOGGER.error("Failed to take screenshot: {}", e.getMessage(), e);
            throw new RuntimeException("Screenshot capture failed", e);
        }
    }

    /**
     * Extracts text from an image using OCR (Tesseract)
     * @param imagePath path to the image file
     * @return extracted text from the image
     */
    public String extractTextFromImage(String imagePath) {
        try {
            LOGGER.debug("Starting OCR text extraction from image: {}", imagePath);
            
            Tesseract tesseract = new Tesseract();
            
            // Try different Tesseract data paths based on OS
            String[] possiblePaths = {
                "src/main/resources/tessdata",  // Project-specific path
                "/opt/homebrew/share/tessdata", // macOS Homebrew path
                "/usr/local/share/tessdata",    // Alternative macOS path
                "/usr/share/tesseract-ocr/4.00/tessdata", // Linux path
                "/usr/share/tessdata",          // Alternative Linux path
                "C:\\Program Files\\Tesseract-OCR\\tessdata", // Windows path
                System.getProperty("user.home") + "/tessdata" // User home directory
            };
            
            boolean dataPathSet = false;
            for (String path : possiblePaths) {
                File tessDataDir = new File(path);
                if (tessDataDir.exists() && tessDataDir.isDirectory()) {
                    tesseract.setDatapath(path);
                    dataPathSet = true;
                    LOGGER.debug("Using Tesseract data path: {}", path);
                    break;
                }
            }
            
            if (!dataPathSet) {
                LOGGER.warn("No valid Tesseract data path found. Using default system path.");
                // Let Tesseract use its default system path
            }
            
            tesseract.setLanguage("eng"); // English language
            tesseract.setPageSegMode(1); // Automatic page segmentation with OSD
            tesseract.setOcrEngineMode(1); // Neural nets LSTM engine only

            // Perform OCR
            String extractedText = tesseract.doOCR(new File(imagePath));
            
            if (extractedText == null || extractedText.trim().isEmpty()) {
                LOGGER.warn("No text extracted from image: {}", imagePath);
                return "No text found in the image";
            }
            
            // Clean up the extracted text
            String cleanedText = extractedText.trim()
                    .replaceAll("\\s+", " ") // Replace multiple spaces with single space
                    .replaceAll("\\n\\s*\\n", "\n"); // Remove empty lines
            
            LOGGER.debug("OCR extraction completed. Extracted {} characters", cleanedText.length());
            return cleanedText;
            
        } catch (TesseractException e) {
            LOGGER.error("OCR extraction failed for image: {} - Error: {}", imagePath, e.getMessage(), e);
            return "OCR extraction failed: " + e.getMessage();
        } catch (UnsatisfiedLinkError e) {
            LOGGER.error("Tesseract library not found. Please install Tesseract OCR. See TESSERACT_SETUP.md for instructions.");
            return "ERROR: Tesseract OCR not installed. Please install Tesseract and try again.\n" +
                   "Installation guide: See TESSERACT_SETUP.md in project root\n" +
                   "macOS: brew install tesseract\n" +
                   "Screenshot saved at: " + imagePath;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during OCR extraction: {}", e.getMessage(), e);
            return "Unexpected error during text extraction: " + e.getMessage() + 
                   "\nScreenshot saved at: " + imagePath;
        }
    }


    /**
     * Captures screenshot and extracts all text using OCR
     * @param screenshotName name prefix for the screenshot file
     */
    public String captureScreenshotAndExtractText(String screenshotName) {
        try {
            // Take screenshot first (this will always work)
            String screenshotPath = takeScreenshot(screenshotName);
            LOGGER.info("Screenshot captured: {}", screenshotPath);

            // Try to extract text from screenshot using OCR
            String extractedText = extractTextFromImage(screenshotPath);

            // Print all extracted text
            LOGGER.info("=== TEXT EXTRACTION FROM SCREENSHOT: {} ===", screenshotName);
            LOGGER.info("Extracted Text:\n{}", extractedText);

            // Also print to console for immediate visibility
            LOGGER.debug("\n=== OCR TEXT FROM SCREENSHOT: " + screenshotName + " ===");
            LOGGER.debug("Screenshot Path: " + screenshotPath);
            LOGGER.debug(extractedText);
            LOGGER.debug("=== END OF OCR TEXT ===\n");
            return extractedText;

        } catch (Exception e) {
            LOGGER.error("Failed to capture screenshot and extract text: {}", e.getMessage(), e);
            System.out.println("ERROR: Failed to capture screenshot and extract text: " + e.getMessage());
            return "";
        }
    }

    public String extractTextFromCurrentScreen(){
        return captureScreenshotAndExtractText("On_Category_List");
    }

}








