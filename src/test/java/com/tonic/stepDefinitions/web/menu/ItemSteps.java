package com.tonic.stepDefinitions.web.menu;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tonic.actions.HomeActions;
import com.tonic.enums.*;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.pageObjects.web.menu.ItemsPage;
import com.tonic.pageObjects.web.menu.MenuLocators;
import com.tonic.stepDefinitions.BaseStep;
import com.tonic.utils.ApplicationUtils;
import com.tonic.utils.PropertyBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.*;

public class ItemSteps extends BaseStep {
    private Page page = PlaywrightFactory.getPage();

    HomeActions homeActions = new HomeActions(page);
    ItemsPage itemsPage = new ItemsPage(page);

    private String itemName;
    private String salesGroup;
    private String specificItemDeleteLocator;
    private String inputFieldLocator;


    @Then("The user is on the {string} screen")
    public void userIsOnTheItemScreen(String itemScreen) {
        itemsPage.waitForSelector(itemsPage.buildDynamicLocator(MenuLocators.PAGE_HEADER, itemScreen),5000);
        assertTrue(itemsPage.getElementByText(MenuLocators.PAGE_HEADER, itemScreen,10000).isVisible(),"Items screen is not visible");
        itemsPage.reloadPage();
    }

    @When("The user clicks on the {string} CTA")
    public void userClicksOnAddNewCTA(String addNewButton) {
        itemsPage.clickDynamicElement(MenuLocators.ADD_NEW_CTA, addNewButton, 5000);
    }

    @Then("A new blank row should be added at the top of the table")
    public void newBlankRowIsAdded() {
        assertTrue(itemsPage.isVisible(MenuLocators.NEW_BLANK_ROW, "New Blank Row"), "New blank row not added at top");
    }

    @Then("The {string} CTA button should be disabled")
    public void addNewCtaButtonShouldBeDisabled(String addNewButton) {
        assertTrue(itemsPage.getElementByText(MenuLocators.ADD_NEW_CTA, addNewButton,10000).isDisabled(), "Add New CTA button is not disabled");
    }

    @Then("The values should be visible in input fields {string},{string},{string}")
    public void theValuesShouldBeVisibleInInputField(String addNewField, String salesGroupField, String modifierSetField) {
        String addNewInputField = itemsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,addNewField);
        softAssertTrue(itemsPage.isVisible(addNewInputField,addNewField),addNewField," is not visible");
        String salesGroupInputField = itemsPage.buildDynamicLocator(MenuLocators.SALES_GROUP_INPUT_FIELD,salesGroupField);
        softAssertTrue(itemsPage.isVisible(salesGroupInputField,salesGroupField),salesGroupField," is not visible");
        String modifierSetInputField = itemsPage.buildDynamicLocator(MenuLocators.MODIFIERSET_INPUT_FIELD,modifierSetField);
        softAssertTrue(itemsPage.isVisible(modifierSetInputField,modifierSetField),modifierSetField," is not visible");
        assertAll();
    }


    @When("The user enters an item name in the {string} field")
    public void enterItemName(String inputField) {
        itemName = ApplicationUtils.getRandomString(4)+ System.currentTimeMillis();
        itemsPage.waitForSelector(MenuLocators.ITEM_LIST, 10000);
        String itemNameFieldLocator = itemsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField);
        itemsPage.enterText(itemNameFieldLocator, itemName, 1000);

    }

    @And("The user chooses a Sales Group")
    public void chooseSalesGroupFromDropdown() {
        page.waitForTimeout(2000);
        itemsPage.click(MenuLocators.SALES_GROUP_INPUT_FIELD,10000);
        List<String> salesGroupList = itemsPage.getVisibleElementsText(MenuLocators.LIST_OF_SALESGROUPS, 2000);
        salesGroup=ApplicationUtils.getRandomElementFromList(salesGroupList);
        itemsPage.getElementByText(MenuLocators.SPECIFIC_SALESGROUP,salesGroup,5000).first().click();
        }

    @And("The user chooses a Modifier Set")
    public void chooseModifierSetFromDropdown()  {
        itemsPage.click(MenuLocators.MODIFIERSET_INPUT_FIELD,10000);
        itemsPage.chooseModifierSet(MenuLocators.LIST_OF_MODIFIERSETS);

    }

    @And("The user clicks on Save at the top right corner")
    public void clickOnSave() {
        itemsPage.click(MenuLocators.SAVE_BUTTON, 5000);
    }

    @Then("The user receives the message {string}")
    public void validateItemAddedMessage(String sucessMessage) throws InterruptedException {
        itemsPage.getElementByText(sucessMessage).waitFor(new Locator.WaitForOptions().setTimeout(5000));
        assertTrue(itemsPage.getElementByText(sucessMessage).isVisible(), "Item Added message not displayed");
    }

    @Then("The user is able to see the list of items")
    public void userIsAbleToSeeListOfItems() {
        assertTrue(itemsPage.getVisibleElementsText(MenuLocators.ITEM_LIST, 10000).size() > 0, "Items Not Visible in Item page");
    }

    @And("The user searches for an item using the {string} field and clicks the edit icon")
    public void theUserClicksTheEditIconOnAnItemItemNameUnderTheColumn(String inputField) {
        itemsPage.reloadPage();
        itemsPage.enterText(itemsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField), itemName, 5000);
        itemsPage.isVisible(MenuLocators.SPECIFIC_ITEM_NAME, itemName, 5000);
        itemsPage.clickDynamicElement(MenuLocators.SPECIFIC_ITEM_EDIT_BUTTON, itemName, 10000);
    }

    @Then("The user is able to search and view the updated or newly added item using the {string} field")
    public void userAbleToSeeCreatedOrEditedItemName(String inputField) {
        itemsPage.enterText(itemsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField), itemName, 5000);
        itemsPage.isVisible(MenuLocators.SPECIFIC_ITEM_NAME, itemName, 5000);
        assertTrue(itemsPage.getVisibleElementsText(MenuLocators.ITEM_LIST, 10000).contains(itemName), "Item not found");
        LOGGER.info("searched for item and its visible " + itemName);
    }

    @And("The user deletes the updated or newly added item using the {string} field")
    public void userDeletesTheNewlyCreatedOrEditedItem(String inputField) {
        itemsPage.clickDynamicElement(MenuLocators.SPECIFIC_DELETE_BUTTON, itemName, 10000);
        itemsPage.click(MenuLocators.POPUP_DELETE, 10000);
        inputFieldLocator= itemsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField);
        specificItemDeleteLocator= itemsPage.buildDynamicLocator(MenuLocators.SPECIFIC_DELETE_BUTTON, itemName);
        page.waitForSelector(specificItemDeleteLocator, new Page.WaitForSelectorOptions().setTimeout(15000).setState(WaitForSelectorState.DETACHED));
        boolean isItemVisible=itemsPage.isElementVisibleAfterSearch(inputFieldLocator,specificItemDeleteLocator,itemName,10000);
        Assert.assertFalse(isItemVisible, "Item Not Deleted");
    }

    @And("The user edits the Sales Group")
    public void youWillNeedToEditASalesGroup() {
        page.waitForTimeout(2000);
        itemsPage.click(MenuLocators.SALES_GROUP_INPUT_FIELD,10000);
        List<String> salesGroupNames=itemsPage.getVisibleElementsText(MenuLocators.LIST_OF_SALESGROUPS, 2000);
        String editingSalesGroup=ApplicationUtils.getRandomElementExcludingSpecificValue(salesGroup,salesGroupNames);
        itemsPage.getElementByText(MenuLocators.SPECIFIC_SALESGROUP,editingSalesGroup,5000).first().click();
    }

    @And("The user edits the Modifier Set")
    public void youWillNeedToEditAModifierSets() {
        itemsPage.click(MenuLocators.MODIFIERSET_INPUT_FIELD,10000);
        itemsPage.chooseModifierSet(MenuLocators.AVAILABLE_MODIFIER_SETS);
    }

    @When("The user enters invalid {string} values in the {string} field")
    public void theUserEntersInvalidValuesInTheField(String invalidItem,String inputField) {
        itemsPage.waitForSelector(MenuLocators.ITEM_LIST, 10000);
        String itemNameFieldLocator = itemsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField);
        String invalidItemSpace = KeyboardKey.fromKey(invalidItem).getValue();
        itemsPage.pressRequiredKeyInKeyBoard(itemNameFieldLocator,invalidItemSpace,5000);
    }

    @Then("An error message {string} should be displayed below the field")
    public void anErrorMessageShouldBeDisplayedBelowTheField(String errorMessage) {
        itemsPage.getElementByText(errorMessage).waitFor(new Locator.WaitForOptions().setTimeout(5000));
        assertTrue(itemsPage.getElementByText(errorMessage).isVisible(), errorMessage+" not displayed");
    }

    @When("The user does not select any dropdown values in the {string} field")
    public void theUserDoesNotSelectAnyDropdownValuesInTheField(String errorMessage) {
        page.waitForTimeout(2000);
        itemsPage.click(MenuLocators.SALES_GROUP_INPUT_FIELD,10000);
        try {
            itemsPage.click(MenuLocators.OUTSIDE_AREA, 1500);
        } catch (Exception e) {
            itemsPage.click(0, 0);
        }
    }

    @And("The user attempts to click on the Save button without entering any values")
    public void theUserAttemptsToClickOnTheSaveButtonWithoutEnteringAnyValues() {
        itemsPage.forceClick(MenuLocators.SAVE_BUTTON,5000);
    }
    @Then("Each item row displays a {string} button")
    public void validateEachItemRowDisplaysAToggleButton(String toggle) {
        Map<String, Boolean> toggleVisibilityMap = itemsPage.getElementVisibilityMapForEachItem(MenuLocators.SPECIFIC_ITEM_TOGGLE_BUTTON,toggle);
        for (Map.Entry<String, Boolean> entry : toggleVisibilityMap.entrySet()) {
            softAssertTrue(entry.getValue(),  entry.getKey(),"Dont have any Toggle Button");
        }
        assertAll();
    }

    @And("The toggle should be disabled")
    public void validateIsSpecificItemToggleButtonDisabled() {
        String specificItemToggleDisabledEle=itemsPage.buildDynamicLocator(MenuLocators.SPECIFIC_ITEM_TOGGLE_BUTTON,itemName);
        assertFalse( itemsPage.isChecked(specificItemToggleDisabledEle,5000),"The toggle is not disabled");
    }

    @Then("The toggle should be enabled")
    public void validateIsSpecificItemToggleButtonEnabled() {
        String specificItemToggleEnabledEle=itemsPage.buildDynamicLocator(MenuLocators.SPECIFIC_ITEM_TOGGLE_BUTTON,itemName);
        assertTrue( itemsPage.isChecked(specificItemToggleEnabledEle,5000),"The toggle is disabled");
    }

    @When("The user toggles the button from {string} to {string} during edit")
    public void theUserTogglesTheButtonFromEnabledToDisabledDuringEdit(String fromState, String toState) {
        boolean expectedState = toState.equalsIgnoreCase(PropertyBuilder.getPropValue(ConfigProperties.TOGGLE_ENABLE));
        String specificTItemToggleEle=itemsPage.buildDynamicLocator(MenuLocators.SPECIFIC_ITEM_TOGGLE_BUTTON,itemName);
        itemsPage.click(specificTItemToggleEle,10000);
        boolean toggleSuccess = itemsPage.validateItemToggleState(expectedState,specificTItemToggleEle , 15000);
        assertTrue(toggleSuccess, "Toggle state mismatch. Expected: " + expectedState);
    }

    @Then("The toggle should be disabled before saving the item")
    public void validateItemToggleStatus() {
        assertFalse(itemsPage.isChecked(MenuLocators.NEWLY_CREATED_ITEM_TOGGLE,5000),"Toggle is not disabled by default");
    }

    @When("User clicks on the Item Delete Icon using the {string} field")
    public void userClicksOnTheSpecificItemDeleteIconBySearchingIt(String inputField) {
        itemsPage.isElementVisibleAfterSearch(itemsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField),MenuLocators.SPECIFIC_ITEM_NAME, itemName,5000);
        itemsPage.clickDynamicElement(MenuLocators.SPECIFIC_DELETE_BUTTON, itemName, 7000);
    }

    @Then("The system should display a Delete confirmation popup with title {string}")
    public void validateDeleteConfirmationPopUpContains(String deleteTitle) {
        itemsPage.getElementByText(deleteTitle).waitFor(new Locator.WaitForOptions().setTimeout(5000));
        assertTrue(itemsPage.getElementByText(deleteTitle).isVisible(),"The Delete Pop up is not visible");
    }

    @And("The delete confirmation popup should contain Item Name with title {string}, Trash Icon and Confirmation text {string}")
    public void theDeleteConfirmationPopupShouldContainItemNameWithTitleDeleteItemTrashIconAndConfirmationTextDoYouReallyWantToDeleteThisRecord(String deleteTitle,String confirmationText) {
        String deleteItemName= itemsPage.getElementByText(deleteTitle).textContent();
        softAssertTrue(deleteItemName.contains(itemName),itemName," is not Visible");
        softAssertTrue(page.isVisible(MenuLocators.DELETE_ICON),"Trash Icon"," is Not Visible");
        softAssertTrue(itemsPage.getElementByText(confirmationText).isVisible(),confirmationText," is not visible");
        assertAll();
    }

    @Then("{string} CTA and {string} CTA should be enabled")
    public void validateYesAndCancelCtaAreEnabled(String yesDeleteCta, String cancelCta) {
        boolean isYesDeleteEnabled=itemsPage.getElementByText(MenuLocators.ACTION_BUTTON,yesDeleteCta,5000).isEnabled();
        softAssertTrue(isYesDeleteEnabled,yesDeleteCta," not enabled");
        boolean isCancelEnabled=itemsPage.getElementByText(MenuLocators.ACTION_BUTTON,cancelCta,5000).isEnabled();
        softAssertTrue(isCancelEnabled,cancelCta,"is not enabled");
        assertAll();
    }

    @When("The user navigates to {string} screen")
    public void navigateToHomeScreen(String homeScreen) {
        homeActions.navigateToHomeScreen(homeScreen);
        itemsPage.reloadPage();
    }

    @And("The deleted item should not be visible when searched using the {string} field")
    public void validateItemIsNotVisible(String inputField) {
        inputFieldLocator= itemsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField);
        specificItemDeleteLocator= itemsPage.buildDynamicLocator(MenuLocators.SPECIFIC_DELETE_BUTTON, itemName);
        page.waitForSelector(specificItemDeleteLocator, new Page.WaitForSelectorOptions().setTimeout(15000).setState(WaitForSelectorState.DETACHED));
        boolean isItemVisible=itemsPage.isElementVisibleAfterSearch(inputFieldLocator,specificItemDeleteLocator,itemName,10000);
        assertFalse(isItemVisible, "Item Not Deleted");
    }

    @Then("The delete confirmation popup should close")
    public void validateDeleteConfirmationPopUpIsNotVisible() {
        page.waitForSelector(MenuLocators.POPUP_DELETE, new Page.WaitForSelectorOptions().setTimeout(10000).setState(WaitForSelectorState.DETACHED));
        assertFalse((itemsPage.isVisible(MenuLocators.POPUP_DELETE,"Delete popup")),"Delete COnfirmation popup is visible");
    }

    @And("The delete popup for items have {string} CTA and {string} CTA")
    public void theDeletePopupForItemsHaveCTAAndCTA(String yesButton, String cancelButton) {
        boolean yesButtonIsVisible = itemsPage.getElementByText(yesButton).isVisible();
        softAssertTrue(yesButtonIsVisible, yesButton +"Button is not visible");
        boolean cancelButtonIsVisible = itemsPage.getElementByText(cancelButton).isVisible();
        softAssertTrue(cancelButtonIsVisible, cancelButton +"Button is not visible");
        assertAll();
    }

    @And("The user is adding an Item")
    public void theUserIsAddingAnItem() {
           itemsPage.theUserAddsAnItem();
           String itemSet = BOHDataConstants.fromKey("ITEM_NAME").getValue();
            itemsPage.waitForSelector(itemsPage.buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("ITEMS").getValue()),5000);
            assertTrue(itemsPage.getVisibleElementsText(MenuLocators.ITEM_LIST, 10000).contains(itemSet), "Item not found");
            LOGGER.info("searched for item and its visible " + itemSet);
            itemsPage.waitForSelector(itemsPage.buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("ITEMS").getValue()),5000);
    }

    @And("The user is deleting an Item")
    public void theUserIsDeletingAnItem() throws InterruptedException {
        String itemSet = BOHDataConstants.fromKey("ITEM_NAME").getValue();
        itemsPage.theUserDeletesAnItem();
        itemsPage.reloadPage();
        itemsPage.waitForSelector(itemsPage.buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("ITEMS").getValue()),20000);
        boolean isItemStillPresent = itemsPage.isItemVisibleAfterSearch();
        assertFalse(isItemStillPresent, "Item " + itemSet + " should not be present after deletion.");
        LOGGER.info("Item '" + itemSet + "' successfully deleted and no longer present.");
    }

    @And("The user is adding Modifier Sets To an item")
    public void theUserIsAddingModifierSetsToAnItem() {
        String itemSet = BOHDataConstants.fromKey("ITEM_NAME").getValue();
        String ModifiersSet = BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue();
        itemsPage.theUserAddsModifierSetsToAnItem();
        assertTrue(itemsPage.isVisible(itemsPage.buildDynamicLocator(MenuLocators.SPECIFIC_MODIFIERS_SPECIFIC_ITEM, itemSet, ModifiersSet), BOHConstants.fromKey("SPECIFIC_MODIFIER_ELEMENT").getValue()), "Modifier set '" + ModifiersSet + "' was not added to the item '" + itemSet + "'.");
        LOGGER.info("Modifier set" + ModifiersSet + "was successfully added to the item" + itemSet + ".");
    }

}
