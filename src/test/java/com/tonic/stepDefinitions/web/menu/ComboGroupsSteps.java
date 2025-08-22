package com.tonic.stepDefinitions.web.menu;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tonic.enums.ButtonConstants;
import com.tonic.enums.KeyboardKey;
import com.tonic.pageObjects.web.menu.ComboGroupsPage;
import com.tonic.pageObjects.web.menu.MenuLocators;
import com.tonic.utils.ApplicationUtils;
import com.tonic.stepDefinitions.BaseStep;
import io.cucumber.java.en.*;
import com.tonic.factory.PlaywrightFactory;
import org.testng.Assert;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.*;

public class ComboGroupsSteps extends BaseStep {
    private ComboGroupsPage comboGroupsPage;
    private Page page;
    private String editedComboGroupName;
    private String newComboGroupName;
    private String inputFieldLocator;
    private String specificComboDeleteLocator;
    private List<String> comboGroupsList;


    public ComboGroupsSteps() {
        this.page = PlaywrightFactory.getPage();
        this.comboGroupsPage = new ComboGroupsPage(this.page);
    }
    private List<String> getComboGroupsList() {
        if (this.comboGroupsList == null) {
            this.comboGroupsList = comboGroupsPage.getComboGroupsList();
        }
        return this.comboGroupsList;
    }

    @And("The input fields {string} and {string} should be visible")
    public void theExistingValuesShouldBeVisibleInInputField(String addNewComboField, String comboOptions) {
        boolean addNewInputFieldIsVisible = comboGroupsPage.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD, addNewComboField, 5000).isVisible();
        softAssertTrue(addNewInputFieldIsVisible, addNewComboField, " Input text Box is not visible");
        boolean comboOptionInputFieldIsVisible = comboGroupsPage.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD, comboOptions, 5000).isVisible();
        softAssertTrue(comboOptionInputFieldIsVisible, comboOptions, " Input text Box is not visible");
        assertAll();
    }

    @When("User Enters the Combo Group Name in {string}")
    public void userAddComboName(String comboName) {
        newComboGroupName = ApplicationUtils.getRandomString(4);
        comboGroupsPage.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD, comboName, 7000).fill(newComboGroupName);
    }

    @And("User need to choose the Available Options using field {string}")
    public void addingAndEditingOptions(String options) {
        comboGroupsPage.clickDynamicElement(MenuLocators.TEXT_BOX_INPUT_FIELD, options, 5000);
        String OptionName = comboGroupsPage.getVisibleElementsText(MenuLocators.AVAILABLE_OPTIONS_LIST, 2000).get(0);
        comboGroupsPage.getElementByText(MenuLocators.ADD_OPTIONS, OptionName, 8000).first().click();
        try {
            comboGroupsPage.click(MenuLocators.OUTSIDE_AREA, 5000);
        } catch (Exception e) {
            comboGroupsPage.click(0, 0);
        }
    }

    @Then("User created combo group should be visible")
    public void userVerifyComboAndOptionsCreated() {
        LOGGER.info("Searching for Combo " + newComboGroupName);
        comboGroupsPage.enterText(MenuLocators.SEARCH_BAR, newComboGroupName, 5000);
        comboGroupsPage.waitUntilElementIsVisible(MenuLocators.SPECIFIC_ELE, newComboGroupName, 5000);
        String SearchedCombo = comboGroupsPage.getTextOfElement(MenuLocators.COMBO_LIST, 1000);
        Assert.assertEquals(SearchedCombo, newComboGroupName, "Combo Group is Not added");
    }

    @And("The user deletes the newly added combo using the {string} field")
    public void userDeletesTheNewlyCreatedOrEditedCombo(String inputField) {
        comboGroupsPage.clickDynamicElement(MenuLocators.SPECIFIC_COMBO_DELETE_BUTTON, newComboGroupName, 10000);
        comboGroupsPage.click(MenuLocators.POPUP_DELETE, 10000);
    }

    @And("The user deletes the updated combo using the {string} field")
    public void userDeletesTheEditedCombo(String inputField) {
        comboGroupsPage.clickDynamicElement(MenuLocators.SPECIFIC_COMBO_DELETE_BUTTON, editedComboGroupName, 10000);
        comboGroupsPage.click(MenuLocators.POPUP_DELETE, 10000);
    }

    @Then("Each combo row displays a {string} button")
    public void validateEachItemRowDisplaysAToggleButton(String speceficIcons) {
        Map<String, Boolean> toggleVisibilityMap = comboGroupsPage.getElementVisibilityMapForEachCombo(MenuLocators.SPECIFIC_DELETE_BUTTON, speceficIcons);
        for (Map.Entry<String, Boolean> entry : toggleVisibilityMap.entrySet()) {
            softAssertTrue(entry.getValue(), entry.getKey(), "Dont have any Delete Button");
        }
        assertAll();
    }

    @When("User clicks on the Combo Group Edit Icon under the Actions column")
    public void userClicksOnComboGroupEditIcon() {
        comboGroupsPage.getElementByText(MenuLocators.SPECIFIC_COMBO_EDIT_BUTTON, ApplicationUtils.getRandomElementFromList(getComboGroupsList()), 5000).first().click();
    }

    @And("User need to Edit Combo Group name in {string}")
    public void userEditsComboGroupName(String comboEditName) {
        editedComboGroupName = ApplicationUtils.getRandomString(6);
        comboGroupsPage.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD, comboEditName, 7000).fill(editedComboGroupName);
    }

    @Then("The user is able to search and view the updated  Combo using the {string} field")
    public void userAbleToSeeCreatedOrEditedItemName(String inputField) {
        comboGroupsPage.enterText(comboGroupsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField), editedComboGroupName, 5000);
        comboGroupsPage.isVisible(MenuLocators.SPECIFIC_ELE, editedComboGroupName, 5000);
        assertTrue(comboGroupsPage.getVisibleElementsText(MenuLocators.COMBO_LIST, 10000).contains(editedComboGroupName), "Item not found");
        LOGGER.info("searched for combo and its visible " + editedComboGroupName);
    }

    @Then("The user is able to search and view the newly added Combo using the {string} field")
    public void userAbleToSeeCreatedComboName(String inputField) {
        comboGroupsPage.enterText(comboGroupsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField), newComboGroupName, 5000);
        comboGroupsPage.isVisible(MenuLocators.SPECIFIC_ELE, newComboGroupName, 5000);
        assertTrue(comboGroupsPage.getVisibleElementsText(MenuLocators.COMBO_LIST, 10000).contains(newComboGroupName), "Item not found");
        LOGGER.info("searched for combo and its visible " + newComboGroupName);
    }

    @When("User clicks on the Combo Group Delete Icon under the Actions column")
    public void userClicksOnComboGroupDeleteIcon() {
        comboGroupsPage.getElementByText(MenuLocators.SPECIFIC_COMBO_DELETE_BUTTON, ApplicationUtils.getRandomElementFromList(getComboGroupsList()), 5000).first().click();
    }

    @And("The delete confirmation popup should contain {string} icon {string} icon and Confirmation Text")
    public void validateDeleteConfirmationModal(String closeIcon, String trashIcon) {
        String closeTxt = ButtonConstants.fromKey(closeIcon).getValue();
        String closeIconEle = comboGroupsPage.buildDynamicLocator(MenuLocators.ICON_WITH_DYNAMIC_VALUE, closeTxt);
        boolean saveIconTxt = comboGroupsPage.isVisible(closeIconEle, closeTxt);
        softAssertTrue(saveIconTxt, closeIcon + "is not Visible");
        String trashTxt = ButtonConstants.fromKey(trashIcon).getValue();
        String trashIconEle = comboGroupsPage.buildDynamicLocator(MenuLocators.ICON_WITH_DYNAMIC_VALUE, trashTxt);
        boolean trashIconTxt = comboGroupsPage.isVisible(trashIconEle, trashTxt);
        softAssertTrue(trashIconTxt, trashIcon + "is not Visible");
        softAssertTrue(page.isVisible(MenuLocators.DELETE_MODAL_POPUP), "The Delete Content Message Visible");
        assertAll();
    }

    @And("The delete popup should have {string} CTA and {string} CTA")
    public void validateDeleteConfirmationModalCta(String yesButton, String cancelButton) {
        boolean yesButtonIsVisible = comboGroupsPage.getElementByText(yesButton).isVisible();
        softAssertTrue(yesButtonIsVisible, yesButton + "Button is not visible");
        boolean cancelButtonIsVisible = comboGroupsPage.getElementByText(cancelButton).isVisible();
        softAssertTrue(cancelButtonIsVisible, cancelButton + "Button is not visible");
        assertAll();
    }

    @Then("The system should display a Delete confirmation popup")
    public void isDeleteConfirmationModalVisible() {
        assertTrue(page.isVisible(MenuLocators.DELETE_MODAL_POPUP), "Delete Confirmation modal is not visible");
    }

    @And("The system should display the message {string}")
    public void validateComboDeletedMessage(String sucessMessage) {
        comboGroupsPage.getElementByText(sucessMessage).waitFor(new Locator.WaitForOptions().setTimeout(5000));
        assertTrue(comboGroupsPage.getElementByText(sucessMessage).isVisible(), "Combo Deleted message not displayed");
    }

    @When("User clicks on {string} Icon")
    public void clickOnDeleteCloseIcon(String closeIcon) {
        String closeTxt = ButtonConstants.fromKey(closeIcon).getValue();
        comboGroupsPage.clickDynamicElement(MenuLocators.ICON_WITH_DYNAMIC_VALUE, closeTxt, 5000);
    }

    @Then("User should see popup get closed")
    public void userSeePopupGetsClosed() {
        comboGroupsPage.reloadPage();
        Assert.assertFalse(page.isVisible(MenuLocators.DELETE_MODAL_POPUP), "Delete Confirmation modal is still visible");
    }

    @When("User deletes the newly added combo from the list")
    public void userDeletesNewlyAddedCombo() {
        page.waitForSelector(comboGroupsPage.buildDynamicLocator(MenuLocators.SPECIFIC_COMBO_DELETE_BUTTON, newComboGroupName), new Page.WaitForSelectorOptions().setTimeout(15000).setState(WaitForSelectorState.DETACHED));
        comboGroupsPage.reloadPage();
        comboGroupsPage.enterText(MenuLocators.SEARCH_BAR, newComboGroupName, 5000);
        comboGroupsPage.waitUntilElementIsVisible(MenuLocators.SPECIFIC_ELE, newComboGroupName, 5000);
        comboGroupsPage.clickDynamicElement(MenuLocators.SPECIFIC_COMBO_DELETE_BUTTON, newComboGroupName, 5000);
    }

    @And("The deleted combo should not be visible when searched using the {string} field")
    public void validateComboIsNotVisible(String inputField) {
        inputFieldLocator = comboGroupsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField);
        specificComboDeleteLocator = comboGroupsPage.buildDynamicLocator(MenuLocators.SPECIFIC_COMBO_DELETE_BUTTON, newComboGroupName);
    }

    @And("User should see the list of Combo Groups")
    public void userShouldSeeListOfComboGroups() {
        List<String> comboGroupsList = getComboGroupsList();
        softAssertFalse(comboGroupsList.isEmpty(), "The list of Combo Groups is empty");
        assertAll();
    }

    @Then("A new blank row should be added at the top of the Combo table")
    public void newBlankRowIsAdded() {
        assertTrue(comboGroupsPage.isVisible(MenuLocators.NEW_BLANK_ROW, "New Blank Row"), "New blank row not added at top");
    }

    @When("User enters invalid {string} values in the {string} field")
    public void theUserEntersInvalidValuesInTheField(String invalidItem, String inputField) {
        comboGroupsPage.waitForSelector(MenuLocators.COMBO_LIST, 10000);
        String itemNameFieldLocator = comboGroupsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField);
        String invalidItemSpace = KeyboardKey.fromKey(invalidItem).getValue();
        comboGroupsPage.pressRequiredKeyInKeyBoard(itemNameFieldLocator, invalidItemSpace, 5000);
    }

    @And("User attempts to click on the Save button without entering any values")
    public void theUserAttemptsToClickOnTheSaveButtonWithoutEnteringAnyValues() {
        comboGroupsPage.forceClick(MenuLocators.SAVE_BUTTON, 5000);
    }
}