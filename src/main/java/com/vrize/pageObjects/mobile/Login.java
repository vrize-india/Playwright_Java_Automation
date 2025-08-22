package com.vrize.pageObjects.mobile;

import com.vrize.common.mobile.BasePage;
import com.vrize.enums.WaitEnums;
import io.appium.java_client.AppiumDriver;

import static com.vrize.utils.PropertyBuilder.getEnvPropValue;

public class Login extends BasePage {

    public Login(AppiumDriver driver) {
        super(driver);
    }

    /**
     * Performs store login with custom credentials using coordinate-based interactions
     * @param hostUrl the host URL for the application
     * @param storeId the store identification number
     * @param authCode the authentication code
     * @throws InterruptedException if the thread is interrupted during wait operations
     */
    public void performStoreLogin(String hostUrl, String storeId, String authCode) throws InterruptedException {
        // Wait for app to load and be ready
        mobileWait(WaitEnums.APP_LAUNCH, 3);
        // Enter login credentials
        clearAndEnterText(HOST_URL, DEFAULT_CLEAR_CHARS, hostUrl);
        clearAndEnterText(STORE_ID, DEFAULT_CLEAR_CHARS, storeId);
        clearAndEnterText(AUTH_CODE, DEFAULT_CLEAR_CHARS, authCode);
        // Wait for UI to settle before submitting
        mobileWait(WaitEnums.UI_INTERACTION, 2);
        // Submit login
        tap(ENTER_BTN);
        // Wait for login processing and navigation
        mobileWait(WaitEnums.NAVIGATION, 10);
        LOGGER.info("Store login completed successfully!");
    }

    /**
     * Performs resource login with input validation and comprehensive error handling
     * @param resourceId the three-digit resource ID as a single string
     * @throws Exception if login fails or application launch fails
     */
    public void performResourceLoginWithValidation(String resourceId) throws Exception {
        try {
            // Validate input parameters first
            validateResourceId(resourceId);

            // Extract individual digits from the resource ID
            String firstDigit = String.valueOf(resourceId.charAt(0));
            String secondDigit = String.valueOf(resourceId.charAt(1));
            String thirdDigit = String.valueOf(resourceId.charAt(2));
            
            // Perform the actual resource login
            performResourceLogin(firstDigit, secondDigit, thirdDigit);
            LOGGER.debug("Resource login completed for resource ID: {}", resourceId);
            
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid resource ID: {}", e.getMessage());
            throw e; // Re-throw validation errors as-is
        } catch (Exception e) {
            LOGGER.error("Failed to perform resource login with resource ID {}: {}", resourceId, e.getMessage());
            throw new RuntimeException("Resource login failed: " + e.getMessage(), e);
        }
    }

    /**
     * Validates that the resource ID is a three-digit string
     * @param resourceId the three-digit resource ID as a single string
     * @throws IllegalArgumentException if the resource ID is invalid
     */
    private void validateResourceId(String resourceId) {
        if (resourceId == null || resourceId.length() != 3 || !resourceId.matches("\\d{3}")) {
            throw new IllegalArgumentException("Resource ID must be a three-digit string (e.g., '123'). Got: " + resourceId);
        }
    }

    public void performResourceLogin(String firstDigit, String secondDigit, String thirdDigit) throws InterruptedException {
        LOGGER.info("Starting resource login with digits: [{}, {}, {}]", firstDigit, secondDigit, thirdDigit);
        
        try {
            // Wait for app to load and be ready
            String firstScreen = captureScreenshotAndExtractText("first_screen");
            LOGGER.info("First screen content: {}", firstScreen);

            // Check if store login is required first
            if (firstScreen.toLowerCase().contains("host url")) {
                LOGGER.info("Store login required, performing store login first");
                try {
                    // Log current environment being used
                    String currentEnv = getCurrentEnvironment();
                    LOGGER.info("Current environment: {}", currentEnv);
                    
                    // Fetch host URL and store ID from environment configuration
                    String hostUrl = getFohHostUrl();
                    String storeId = getFohStoreId();
                    String authCode = getFohAuthCode();
                    
                    LOGGER.info("Using FOH configuration - Host: {}, Store ID: {}, Auth Code: {}", hostUrl, storeId, authCode);
                    performStoreLogin(hostUrl, storeId, authCode);
                    LOGGER.info("Store login completed successfully");
                } catch (RuntimeException e) {
                    LOGGER.error("Configuration error during store login: {}", e.getMessage());
                    throw new RuntimeException("Store login failed due to configuration issue. " + e.getMessage(), e);
                } catch (Exception e) {
                    LOGGER.error("Store login failed: {}", e.getMessage());
                    throw new RuntimeException("Store login failed, cannot proceed with resource login", e);
                }
            }

            // Wait for resource login screen to be ready
            mobileWait(WaitEnums.APP_LAUNCH, 2);
            
            String resourceLoginScreen = captureScreenshotAndExtractText("resource_login_screen");
            LOGGER.info("Resource login screen content: {}", resourceLoginScreen);

            // Enter resource login credentials using parameters
            enterResourceDigits(firstDigit, secondDigit, thirdDigit);

            // Wait for UI to settle before submitting
            mobileWait(WaitEnums.UI_INTERACTION, 2);

            // Submit login
            tap(LOGIN_BTN);
            LOGGER.debug("Login button tapped");

            // Wait for login processing and navigation
            mobileWait(WaitEnums.NAVIGATION, 5);

            LOGGER.info("Resource login completed successfully for digits: [{}, {}, {}]", firstDigit, secondDigit, thirdDigit);

        } catch (Exception e) {
            LOGGER.error("Resource login failed for digits [{}, {}, {}]: {}", firstDigit, secondDigit, thirdDigit, e.getMessage());
            throw new RuntimeException("Resource login failed", e);
        }
    }

    /**
     * Enters the three resource digits by tapping the appropriate buttons
     * @param firstDigit first resource digit
     * @param secondDigit second resource digit
     * @param thirdDigit third resource digit
     */
    private void enterResourceDigits(String firstDigit, String secondDigit, String thirdDigit) {
        LOGGER.debug("Entering resource digits: [{}, {}, {}]", firstDigit, secondDigit, thirdDigit);
        
        // Map digits to button constants and tap them
        tapDigitButton(firstDigit);
        tapDigitButton(secondDigit);
        tapDigitButton(thirdDigit);
    }

    /**
     * Taps the button corresponding to the given digit
     * @param digit the digit to enter
     */
    private void tapDigitButton(String digit) {
        try {
            switch (digit) {
                case "0":
                    tap(ZERO_BTN);
                    break;
                case "1":
                    tap(ONE_BTN);
                    break;
                case "2":
                    tap(TWO_BTN);
                    break;
                case "3":
                    tap(THREE_BTN);
                    break;
                case "4":
                    tap(FOUR_BTN);
                    break;
                case "5":
                    tap(FIVE_BTN);
                    break;
                case "6":
                    tap(SIX_BTN);
                    break;
                case "7":
                    tap(SEVEN_BTN);
                    break;
                case "8":
                    tap(EIGHT_BTN);
                    break;
                case "9":
                    tap(NINE_BTN);
                    break;
                default:
                    LOGGER.warn("Invalid digit '{}' provided for resource login", digit);
                    throw new IllegalArgumentException("Invalid digit: " + digit + ". Must be 0-9.");
            }
            LOGGER.debug("Tapped button for digit: {}", digit);
        } catch (Exception e) {
            LOGGER.error("Failed to tap button for digit '{}': {}", digit, e.getMessage());
            throw new RuntimeException("Failed to enter digit: " + digit, e);
        }
    }

    /**
     * Gets the FOH host URL from environment configuration
     * @return the FOH host URL
     * @throws RuntimeException if the FOH host URL is not configured
     */
    private String getFohHostUrl() {
        try {
            String hostUrl = getEnvPropValue("foh.host.url");
            LOGGER.debug("Retrieved FOH host URL from environment config: {}", hostUrl);
            return hostUrl;
        } catch (Exception e) {
            String errorMsg = "FOH host URL is not configured in environment properties. " +
                            "Please ensure '{env}.foh.host.url' is set in environment.properties file. " +
                            "Error: " + e.getMessage();
            LOGGER.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Gets the FOH store ID from environment configuration
     * @return the FOH store ID
     * @throws RuntimeException if the FOH store ID is not configured
     */
    private String getFohStoreId() {
        try {
            String storeId = getEnvPropValue("foh.store.id");
            LOGGER.debug("Retrieved FOH store ID from environment config: {}", storeId);
            return storeId;
        } catch (Exception e) {
            String errorMsg = "FOH store ID is not configured in environment properties. " +
                            "Please ensure '{env}.foh.store.id' is set in environment.properties file. " +
                            "Error: " + e.getMessage();
            LOGGER.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Gets the FOH authentication code from environment configuration
     * @return the FOH authentication code
     * @throws RuntimeException if the FOH authentication code is not configured
     */
    private String getFohAuthCode() {
        try {
            String authCode = getEnvPropValue("foh.code");
            LOGGER.debug("Retrieved FOH auth code from environment config: {}", authCode);
            return authCode;
        } catch (Exception e) {
            String errorMsg = "FOH authentication code is not configured in environment properties. " +
                            "Please ensure '{env}.foh.code' is set in environment.properties file. " +
                            "Error: " + e.getMessage();
            LOGGER.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Gets the current environment being used
     * @return the current environment name
     * @throws RuntimeException if the current environment cannot be determined
     */
    private String getCurrentEnvironment() {
        try {
            return com.vrize.utils.PropertyBuilder.getCurrentEnvironment();
        } catch (Exception e) {
            String errorMsg = "Current environment cannot be determined. " +
                            "Please ensure 'env' property is set in config.properties or use -Denv parameter. " +
                            "Error: " + e.getMessage();
            LOGGER.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    public void clickQSIcon() throws InterruptedException {
        // Capture screenshot before clicking
        String extractedText = captureScreenshotAndExtractText("Before Clicking on QS");
        if(!extractedText.toLowerCase().contains("TipName")){
            // Wait for app to load and be ready
            mobileWait(WaitEnums.APP_LAUNCH, 1);
        }

        tap(QS_BTN);
        // Wait for login processing and navigation
        mobileWait(WaitEnums.NAVIGATION, 3);
        LOGGER.info("QS icon clicked!");
    }

    /**
     * Clicks the swipe icon and captures screenshot with OCR text extraction
     * @throws InterruptedException if the thread is interrupted during wait operations
     */
    public void clickSwipeIcon() throws InterruptedException {
        LOGGER.info("Starting swipe icon click operation");

        // Capture screenshot before clicking
        String extractedText = captureScreenshotAndExtractText("Before_Swipe");
        if(!extractedText.toLowerCase().contains("tallboys")){
            // Wait for app to load and be ready
            mobileWait(WaitEnums.APP_LAUNCH, 1);
        }
        
        // Click swipe button
        for(int i=0; i<5;i++){
            tap(Swipe_BTN);
        }
        LOGGER.info("Swipe Icon clicked!");
        
        // Wait for navigation to complete
        mobileWait(WaitEnums.NAVIGATION, 3);
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








