package com.tonic.stepDefinitions.web.menu;

import com.microsoft.playwright.Locator;
import com.tonic.driver.MobileDriver;
import com.tonic.enums.BOHConstants;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.pageObjects.mobile.Login;
import com.tonic.pageObjects.web.MenuConfigurationPage;
import com.tonic.pageObjects.web.menu.MenuLocators;
import com.tonic.stepDefinitions.BaseStep;
import com.tonic.hooks.Hooks;
import com.tonic.utils.ApplicationUtils;
import io.cucumber.java.en.*;
import org.testng.Assert;

import java.util.List;

public class MenuConfigurationSteps extends BaseStep {
    private final MenuConfigurationPage menuConfigurationPage = new MenuConfigurationPage(PlaywrightFactory.getPage());
    private String menuCategoriesSize;
    private String updatedCategoryName;
    private Login login;

    @When("The user clicks on plus icon to {string}")
    public void theUserClicksOnPlusIconToAddCategory(String addNewCategory) {
        menuCategoriesSize = menuConfigurationPage.getCategoriesCount();
        System.out.println("Categories list size: " + menuCategoriesSize);
        menuConfigurationPage.clickElement(MenuLocators.ADD_NEW_CATEGORY_BUTTON, 1000);
        String addNewCategoryHeader = menuConfigurationPage.buildDynamicLocator(MenuLocators.ACTION_BUTTON, addNewCategory);
        boolean pageHeader = menuConfigurationPage.isVisible(addNewCategoryHeader, "Header" + addNewCategory);
        Assert.assertTrue(pageHeader, "Header " + addNewCategory + " is not visible");
    }

    @And("The user enters value as {string} in {string} field")
    public void userEntersAValueAsInField(String categoryName, String inputField) {
        String randomString = ApplicationUtils.getRandomString(4);
        updatedCategoryName = categoryName.concat(randomString);
        String categoryNamelocator = menuConfigurationPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField);
        boolean modifierInputFieldIsVisible = menuConfigurationPage.isVisible(categoryNamelocator, inputField);
        Assert.assertTrue(modifierInputFieldIsVisible, inputField + " Text Box is not visible");
        menuConfigurationPage.fillField(categoryNamelocator, updatedCategoryName, 3000);
    }

    @When("The user clicks on {string} icon")
    public void userClicksOnTheIcon(String text) {
        menuConfigurationPage.clickDynamicElement(MenuLocators.TXT_BUTTON, text, 3000);
    }

    @Then("{string} Pop up message should display")
    public void popUpMessageShouldDisplay(String string) {
        String popUpMessage = menuConfigurationPage.buildDynamicLocator(MenuLocators.POP_UP_MESSAGE, string);
        menuConfigurationPage.waitForSelector(popUpMessage, 20000);
        Assert.assertTrue(popUpMessage.contains(string), "Correct Pop up is not displayed");
    }

    @And("The user selects any item from the dropdown")
    public void userSelectsAnyItemFromTheDropdown() {
        menuConfigurationPage.selectRandomFromCustomDropdown(MenuLocators.DROPDOWNTOGGLE_SELECTOR, MenuLocators.OPTION_SELECTOR);
    }

    @When("The user clicks on the {string} button")
    public void userClicksOnTheButton(String button) {
        menuConfigurationPage.clickDynamicElement(MenuLocators.ADD_BUTTONS, button, 3000);
    }

    @When("The user clicks on delete icon for item")
    public void userClicksOnDeleteIconForItem() {
        menuConfigurationPage.clickDynamicElement(MenuLocators.ADD_BUTTONS, "item-delete-0", 3000);
    }

    @And("The user clicks on {string} CTA")
    public void userClicksOnCTA(String string) {
        menuConfigurationPage.clickDynamicElement(MenuLocators.POP_UP_MESSAGE, string, 3000);
    }

    @And("The user clicks on the close icon")
    public void userClicksOnTheCloseIcon() {
        menuConfigurationPage.clickElement(MenuLocators.CANCEL_ICON_ADD_NEW_CATEGORY_POPUP, 1000);
    }

    @When("The user selects the created category for deletion")
    public void userSelectsTheDesiredCategoryForDeletion() {
        boolean searchInputFieldIsVisible = menuConfigurationPage.isVisible(MenuLocators.SEARCH_HERE_TEXT_BOX, updatedCategoryName);
        Assert.assertTrue(searchInputFieldIsVisible, updatedCategoryName + " Text Box is not visible");
        menuConfigurationPage.fillField(MenuLocators.SEARCH_HERE_TEXT_BOX, updatedCategoryName, 3000);
        menuConfigurationPage.clickDynamicElement(MenuLocators.KEBAB_MENU_FOR_SPECIFIC_CATEGORY, updatedCategoryName, 3000);
        menuConfigurationPage.clickDynamicElement(MenuLocators.DELETE_CTA_FOR_SPECIFIC_CATEGORY, updatedCategoryName, 3000);
    }

    @Then("{string} confirmation modal should be displayed")
    public void confirmationModalShouldBeDisplayed(String string) {
        String deleteConfirmationWindow = menuConfigurationPage.buildDynamicLocator(MenuLocators.POP_UP_MESSAGE, string);
        boolean modifierInputFieldIsVisible = menuConfigurationPage.isVisible(deleteConfirmationWindow, "Confirmation window");
        Assert.assertTrue(modifierInputFieldIsVisible, "Confirmation window is not visible");
    }

    @When("The user correctly types category name in the confirmation text box")
    public void theUserCorrectlyTypesCategoryNameInTheConfirmationTextBox() {
        String textBoxInConfirmationModal = menuConfigurationPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, "to confirm");
        menuConfigurationPage.fillField(textBoxInConfirmationModal, updatedCategoryName, 10000);
    }

    @And("Deleted category should be removed from the Menu Configuration")
    public void deletedCategoryShouldBeRemovedFromTheMenuConfiguration() {
        String noResultslocator = menuConfigurationPage.buildDynamicLocator(MenuLocators.POP_UP_MESSAGE, "No Results");
        boolean noResultsTextIsVisible = menuConfigurationPage.isVisible(noResultslocator, "No Results");
        Assert.assertTrue(noResultsTextIsVisible, " No Results Text is not visible");
    }

    @And("Category Count should be same after create and delete of the category")
    public void categoryCountShouldBeSameAfterCreateAndDeleteOfTheCategory() {
        menuConfigurationPage.clearField(MenuLocators.SEARCH_HERE_TEXT_BOX, 5000);
        String menuCategoriesSizeAfterDelete = menuConfigurationPage.getCategoriesCount();
        Assert.assertEquals(menuCategoriesSize, menuCategoriesSizeAfterDelete, "The Category count is not matching");
    }

    @When("The user launches the mobile application with resource id {string}")
    public void theUserLaunchesTheMobileApplication(String resourceId) throws Exception {
        LOGGER.info("Launching mobile application with resource ID: {}", resourceId);
        login = launchMobileApplication();
        login.performResourceLoginWithValidation(resourceId);
        LOGGER.info("Mobile application launch and login completed successfully");
    }

    @And("Creates a new order by clicking on QS")
    public void createANewOrderByClickingOnQS() throws InterruptedException {
        login.clickQSIcon();
    }

    @When("The user clicks on Swipe Right icon on mobile")
    public void tapOnSwipeRightIcon() throws InterruptedException {
        login.clickSwipeIcon();
    }

    @Then("User should be able to see added category")
    public void validateCategory() {
         String expectedCategory = updatedCategoryName.trim();
         String extractedText = login.extractTextFromCurrentScreen();
         LOGGER.info("New Category: "+expectedCategory+" to match with actual Category list: {"+extractedText+"}");
         Assert.assertTrue(extractedText.trim().toLowerCase().contains(expectedCategory.toLowerCase()),"New Category: "+expectedCategory+" is not matched in FOH with actual Category list: {"+extractedText+"}");
    }

    @And("The user enables the {string}")
    public void theUserEnablesThe(String toggle) {
        menuConfigurationPage.clickDynamicElement(MenuLocators.STORE_TOGGLE, toggle, 5000);
    }

    @When("The user relaunches the mobile application")
    public void theUserRelaunchesTheMobileApplication() throws Exception {
        LOGGER.info("Relaunching mobile application...");
        
        // Stop the current mobile driver
        if (MobileDriver.getDriver() != null) {
            try {
                MobileDriver.getDriver().quit();
                LOGGER.info("Mobile driver stopped successfully");
            } catch (Exception e) {
                LOGGER.warn("Error stopping mobile driver: " + e.getMessage());
            }
        }
        
        // Stop Appium server
        try {
            Hooks.stopAppiumServer();
            LOGGER.info("Appium server stopped successfully");
        } catch (Exception e) {
            LOGGER.warn("Error stopping Appium server: " + e.getMessage());
        }
        
        // Wait for cleanup using proper wait mechanism
        try {
            Thread.sleep(1000); // Minimal wait for port cleanup
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Restart Appium server and reinitialize mobile driver
        try {
            Hooks.startAppiumServer();
            LOGGER.info("Appium server restarted successfully");
            
            // Reinitialize mobile driver
            MobileDriver.initDriver("Mobile App Relaunch");
            LOGGER.info("Mobile driver reinitialized successfully");
            
            // Reinitialize the login object with the new driver
            // Create a new Login object that will get the fresh driver
            login = new Login(MobileDriver.getDriver());
            LOGGER.info("Login object reinitialized successfully");
            
            // Wait for app to be ready using mobile wait
            if (MobileDriver.getDriver() != null) {
                try {
                    // Use mobile wait for app launch
                    login.mobileWait(com.tonic.enums.WaitEnums.APP_LAUNCH, 5);
                    LOGGER.info("Mobile application relaunch completed successfully");
                } catch (Exception e) {
                    LOGGER.warn("App launch wait failed, continuing anyway: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("Error relaunching mobile application: " + e.getMessage());
            throw new RuntimeException("Failed to relaunch mobile application", e);
        }
    }

    @And("The user enables the Store and Online ordering option")
    public void enableStoreAndOnlineOrderingToggle() {
        menuConfigurationPage.clickDynamicElement(MenuLocators.STORE_TOGGLE, ButtonConstants.fromKey("STORE_TOGGLE").getValue(), 7000);
        menuConfigurationPage.clickDynamicElement(MenuLocators.ONLINE_ORDER_TOGGLE, ButtonConstants.fromKey("ONLINE_ORDER_TOGGLE").getValue(), 7000);
        softAssertTrue( menuConfigurationPage.isChecked(MenuLocators.STORE_TOGGLE,10000),"STORE TOGGLE is not enabled");
        softAssertTrue( menuConfigurationPage.isChecked(MenuLocators.ONLINE_ORDER_TOGGLE,10000),"STORE TOGGLE is not enabled");
        LOGGER.info("Store and Online Ordering toggle ");
        assertAll();
    }

    @Then("The Column Headers should be visible")
    public void validateTheColumHeadersAreVisible() {
        List<String> expectedTexts = ApplicationUtils.convertCommaSeparatedStringToList(BOHConstants.fromKey("ADDITIONAL_CHARGE_CATEGORY_COLUMN_HEADERS").getValue());
        List<String> actualTexts = menuConfigurationPage.getVisibleElementsText(MenuLocators.COLUMN_HEADERS,10000);
        Assert.assertEquals(expectedTexts,actualTexts,"Column headers are not visible");
        LOGGER.info("Expected column headers are visible {}", expectedTexts);
    }

    @And("The {string},{string} CTA should be visible under the Action column")
    public void validateSaveAndCancelCtaAreVisibleUnderActionAreVisible(String save,String cancel) {
        String saveButtonEle = menuConfigurationPage.buildDynamicLocator(MenuLocators.TXT_BUTTON,ButtonConstants.fromKey(save).getValue());
        softAssertTrue( menuConfigurationPage.isVisible(saveButtonEle,save),"SAVE CTA is not visible");
        LOGGER.info("'{}' CTA is visible under the Action column", save);
        String cancelButtonEle = menuConfigurationPage.buildDynamicLocator(MenuLocators.TXT_BUTTON, ButtonConstants.fromKey(cancel).getValue());
        softAssertTrue( menuConfigurationPage.isVisible(cancelButtonEle,cancel),"CANCEL CTA is not visible");
        LOGGER.info("'{}' CTA is visible under the Action column", cancel);
        assertAll();
    }

    @And("The {string} dialog should open for adding items to the category")
    public void validateAddMenuItemsDialogIsOpenedForAddingItemsToTheCategory(String dialogContent) {
        menuConfigurationPage.getElementByText(dialogContent).waitFor(new Locator.WaitForOptions().setTimeout(5000));
        Assert.assertTrue(menuConfigurationPage.getElementByText(dialogContent).isVisible(),"The Add Menu Items dialog is not visible ");
        LOGGER.info("Add Menu Item Dialog is visible");
    }

    @And("The user checks and creates a Category")
    public void userChecksAndCreatesACategory() {
        menuConfigurationPage.userSearchesAndCreatesACategory();
        String categoryName = BOHDataConstants.fromKey("CATEGORY_NAME").getValue();
        Assert.assertTrue(menuConfigurationPage.isCategoryVisibleAfterSearch(), "Category " + categoryName + " was not created.");
        LOGGER.info("Category " + categoryName + " created successfully and is visible in the category list.");
    }

    @And("The user deletes the category")
    public void userDeletesTheCategory() {
        menuConfigurationPage.userChecksAndDeletesTheCategory();
        String categoryName = BOHDataConstants.fromKey("CATEGORY_NAME").getValue();
        String popUpMessage = menuConfigurationPage.buildDynamicLocator(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("DELETED").getValue());
        menuConfigurationPage.waitForSelector(popUpMessage, 10000);
        Assert.assertTrue(popUpMessage.contains(BOHConstants.fromKey("DELETED").getValue()), "Correct Pop up is not dispalyed");
        LOGGER.info("Menu " + categoryName + " deleted!");
    }

    @And("The user adds an item to the category")
    public void theUserAddsAnItemToTheCategory() {
        String categoryName = BOHDataConstants.fromKey("CATEGORY_NAME").getValue();
        String itemName = BOHDataConstants.fromKey("ITEM_NAME").getValue();
        menuConfigurationPage.theUserAddingAnItemToTheCategory();
        Assert.assertTrue(menuConfigurationPage.isVisible(menuConfigurationPage.buildDynamicLocator(MenuLocators.SPECIFIC_ITEM_CATEGORY, itemName), "Verifying item presence in category: " + itemName), "Item '" + itemName + "' was not added to the category '" + categoryName + "'.");
        LOGGER.info("Item " + itemName + " was successfully added to category " + categoryName + ".");
    }
}
