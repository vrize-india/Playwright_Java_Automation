package com.tonic.stepDefinitions.web.menu;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tonic.enums.ButtonConstants;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.pageObjects.web.menu.MenuLocators;
import com.tonic.pageObjects.web.menu.SalesAndParentGroupsPage;
import com.tonic.utils.ApplicationUtils;
import com.tonic.stepDefinitions.BaseStep;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import java.util.List;
import static org.testng.Assert.*;


public class SalesAndParentGroupsSteps extends BaseStep {
    private Page page =PlaywrightFactory.getPage();
    SalesAndParentGroupsPage salesAndParentGroupsPage = new SalesAndParentGroupsPage(PlaywrightFactory.getPage());
    private String salesGroupName;
    private String randomParentGroup;
    private String editedSalesGroupName;
    private String validateDeletedSalesGroup;
    List<String> salesGroupList = salesAndParentGroupsPage.isSalesAndParentGroupsVisible();

    @And("User should see a list of Sales And Parent Groups displayed")
    public void userShouldSeeListOfSalesAndParentGroups() {
        assertFalse(salesGroupList.isEmpty(), "Sales And Parent Group list is not visible");
    }

    @Then("User should see the inline editor enabled to add information")
    public void inlineEditorEnabled() {
        salesAndParentGroupsPage.waitForSelector(MenuLocators.INLINE_EDITOR, 10000);
        assertTrue(page.isVisible(MenuLocators.INLINE_EDITOR), "Inline editor is not visible");
    }

    @When("User enters the new sales group in the {string} field")
    public void enterSalesGroupName(String salesGroupFieldName)  {
        salesGroupName = ApplicationUtils.getRandomString(4);
        salesAndParentGroupsPage.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD,salesGroupFieldName,10000).fill(salesGroupName);
    }

    @And("User chooses a Parent Group from the {string} dropdown")
    public void chooseSpecificParentGroup(String parentGroupFieldName) {
        salesAndParentGroupsPage.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD,parentGroupFieldName,10000).click();
        List<String> ParentGroupList = salesAndParentGroupsPage.getVisibleElementsText(MenuLocators.PARENT_GROUP_DROPDOWN_OPTIONS_LIST, 2000);
        randomParentGroup = ApplicationUtils.getRandomElementFromList(ParentGroupList);
        salesAndParentGroupsPage.getElementByText(MenuLocators.PARENT_GROUP_DROPDOWN_OPTION,randomParentGroup, 10000).click();
    }

    @And("User checks the dialog box for Use for Seat Count")
    public void checkDialogBoxForSeatCount() {
        page.locator(MenuLocators.SEAT_COUNT_CHECKBOX).first().check();
    }

    @And("User clicks on Save CTA")
    public void clickOnSaveCTA() {
        page.click(MenuLocators.SAVE_BUTTON);
    }

    @Then("User should see that the new sales group is saved successfully and validated using the {string} field")
    public void newSalesGroupAddedSuccessfully(String inputField) {
        String searchInputEle = salesAndParentGroupsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, inputField);
        salesAndParentGroupsPage.enterText(searchInputEle, salesGroupName, 10000);
        salesAndParentGroupsPage.waitUntilElementIsVisible(MenuLocators.SPECIFIC_ELE, salesGroupName, 5000);
        List<String> updatedSalesGroupList = salesAndParentGroupsPage.getVisibleElementsText(MenuLocators.SALES_GROUP_LIST, 10000);
        assertTrue(updatedSalesGroupList.contains(salesGroupName), "Sales Group not found in the visible list");
    }

    @Then("User should see the Edit Icon under the Actions column for each row")
    public void eachRowShouldHaveEditIcon() {
        List<Boolean> visibilityList = salesAndParentGroupsPage.getIconsVisibility(salesGroupList, MenuLocators.SPECIFIC_SALES_GROUP_EDIT, "Edit Icon");
        softAssertFalse(visibilityList.contains(false), "Edit icon is not visible for the selected Sales group");
        assertAll();

    }

    @When("User clicks on the edit icon")
    public void userClicksEditIcon() {
        salesAndParentGroupsPage.getElementByText(MenuLocators.SPECIFIC_SALES_GROUP_EDIT,ApplicationUtils.getRandomElementFromList(salesGroupList),5000).first().click();
    }

    @And("User edit the existing sales group name in {string} field")
    public void userEditsFieldValues(String inputField) {
        editedSalesGroupName = ApplicationUtils.getRandomString(5);
        salesAndParentGroupsPage.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD,inputField,7000).fill(editedSalesGroupName);
    }

    @And("User receives the message {string}")
    public void validateSalesGroupAlertMessageIsVisible(String updatedSuccessMessage){
        salesAndParentGroupsPage.getElementByText(updatedSuccessMessage).waitFor(new Locator.WaitForOptions().setTimeout(5000));
        assertTrue(salesAndParentGroupsPage.getElementByText(updatedSuccessMessage).isVisible(), "Updated Sales group added message not displayed");
    }

    @Then("User should see edited sales group is visible")
    public void userVerifyEditedSalesGroup() {
        salesAndParentGroupsPage.reloadPage();
        LOGGER.info("Searching for Sales Group  "+editedSalesGroupName);
        salesAndParentGroupsPage.enterText(MenuLocators.SEARCH_BAR,editedSalesGroupName,5000);
        salesAndParentGroupsPage.waitUntilElementIsVisible(MenuLocators.SPECIFIC_ELE,editedSalesGroupName,5000);
        String specificSalesGroupName=salesAndParentGroupsPage.getTextOfElement(MenuLocators.SALES_GROUP_LIST,1000);
        Assert.assertEquals(specificSalesGroupName,editedSalesGroupName,"Sales Group is Not added");
    }

    @And("User should see the existing values visible in input field {string},{string},Use for Seat Count")
    public void existingValuesShouldBeVisibleInInputField(String salesGroupNameString, String parentGroupNameString) {
        String salesGroupNameEle = salesAndParentGroupsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,salesGroupNameString);
        boolean salesGroupInputFieldIsVisible=salesAndParentGroupsPage.isVisible(salesGroupNameEle,salesGroupNameString);
        softAssertTrue(salesGroupInputFieldIsVisible, salesGroupNameString +" Input text Box is not visible");
        String parentGroupNameEle = salesAndParentGroupsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,parentGroupNameString);
        boolean parentGroupInputFieldIsVisible=salesAndParentGroupsPage.isVisible(parentGroupNameEle,parentGroupNameString);
        softAssertTrue(parentGroupInputFieldIsVisible, parentGroupNameString +" Input text Box is not visible");
        salesAndParentGroupsPage.waitForSelector(MenuLocators.SEAT_COUNT_CHECKBOX, 10000);
        softAssertTrue(page.isVisible(MenuLocators.SEAT_COUNT_CHECKBOX), "Seat Count checkbox is not visible");
        assertAll();
    }

    @And("User should see the icons change to Save and Cancel")
    public void iconsChangeToSaveAndCancel() {
        salesAndParentGroupsPage.waitForSelector(MenuLocators.SAVE_BUTTON, 10000);
        softAssertTrue(page.isVisible(MenuLocators.SAVE_BUTTON), "Save button is not visible");
        salesAndParentGroupsPage.waitForSelector(MenuLocators.CANCEL_BUTTON, 10000);
        softAssertTrue(page.isVisible(MenuLocators.CANCEL_BUTTON), "Cancel button is not visible");
        assertAll();
    }

    @When("User clicks on the Cancel icon should close the inline editor")
    public void clickOnCancelIcon() {
        salesAndParentGroupsPage.click(MenuLocators.CANCEL_BUTTON, 10000);

    }

    @Then("User should see the Delete Icon under the Actions column for each row")
    public void eachSalesGroupShouldHaveDeleteIcon() {
        List<Boolean> deleteIconVisibilityList = salesAndParentGroupsPage.getIconsVisibility(salesGroupList, MenuLocators.SPECIFIC_SALES_GROUP_DELETE, "Delete Icon");
        softAssertFalse(deleteIconVisibilityList.contains(false), "Delete icon is not visible for the selected sales group");
        assertAll();
    }

    @When("User clicks on Delete Icon")
    public void userClicksOnDeleteIcon() {
        salesAndParentGroupsPage.getElementByText(MenuLocators.SPECIFIC_SALES_GROUP_DELETE,ApplicationUtils.getRandomElementFromList(salesGroupList),5000).first().click();
    }

    @Then("User should see a Delete confirmation popup")
    public void isDeleteConfirmationPopup(){
        assertTrue(page.isVisible(MenuLocators.DELETE_MODAL_POPUP),"Delete Confirmation modal is not visible");
    }

    @And("User should see the delete confirmation popup containing the {string} icon {string} icon and Confirmation Text")
    public void validateDeleteConfirmationPopup(String closeIcon, String trashIcon){
        String closeTxt = ButtonConstants.fromKey(closeIcon).getValue();
        String closeIconEle = salesAndParentGroupsPage.buildDynamicLocator(MenuLocators.ICON_WITH_DYNAMIC_VALUE, closeTxt);
        boolean closeIconTxt =salesAndParentGroupsPage.isVisible(closeIconEle, closeTxt);
        softAssertTrue(closeIconTxt, closeIcon + "is not visible");
        String trashTxt = ButtonConstants.fromKey(trashIcon).getValue();
        String trashIconEle = salesAndParentGroupsPage.buildDynamicLocator(MenuLocators.ICON_WITH_DYNAMIC_VALUE, trashTxt);
        boolean trashIconTxt =salesAndParentGroupsPage.isVisible(trashIconEle, trashTxt);
        softAssertTrue(trashIconTxt, trashIcon + "is not visible");
        softAssertTrue(page.isVisible(MenuLocators.DELETE_MODAL_CONTENT),"The Delete Content Message Visible");
        assertAll();
    }

    @And("User should see the delete popup containing {string} CTA and {string} CTA")
    public void validateDeleteConfirmationModalCTA(String yesButton,String cancelButton){
        boolean yesButtonIsVisible = salesAndParentGroupsPage.getElementByText(yesButton).isVisible();
        softAssertTrue(yesButtonIsVisible, yesButton +"Button is not visible");
        boolean cancelButtonIsVisible = salesAndParentGroupsPage.getElementByText(cancelButton).isVisible();
        softAssertTrue(cancelButtonIsVisible, cancelButton +"Button is not visible");
        assertAll();
    }

    @When("User deletes the newly added sales group from the list")
    public void userDeletesNewlyAddedSalesGroup() {
        validateDeletedSalesGroup=salesGroupName;
        salesAndParentGroupsPage.clickDynamicElement(MenuLocators.SPECIFIC_SALES_GROUP_DELETE,salesGroupName,5000);
    }

    @When("User clicks on {string} CTA")
    public void clickOnYesButton(String yesButton){
        salesAndParentGroupsPage.getElementByText(yesButton).click();
    }

    @And("User should see a display message {string}")
    public void validateSalesGroupDeletedMessage(String deletedSuccessMessage)  {
        salesAndParentGroupsPage.getElementByText(deletedSuccessMessage).waitFor(new Locator.WaitForOptions().setTimeout(5000));
        assertTrue(salesAndParentGroupsPage.getElementByText(deletedSuccessMessage).isVisible(), "Sales Group Deleted message not displayed");
    }

    @Then("User should see that the respective Sales Group is deleted from the system")
    public void validateDeletedSalesGroup() {
        LOGGER.info("Searching for Sales Group  " + validateDeletedSalesGroup);
        page.waitForSelector(salesAndParentGroupsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, validateDeletedSalesGroup), new Page.WaitForSelectorOptions().setTimeout(15000).setState(WaitForSelectorState.DETACHED));
        salesAndParentGroupsPage.reloadPage();
        salesAndParentGroupsPage.enterText(MenuLocators.SEARCH_BAR, validateDeletedSalesGroup, 5000);
        assertFalse(page.locator(salesAndParentGroupsPage.buildDynamicLocator(MenuLocators.SALES_GROUP_LIST, validateDeletedSalesGroup)).isVisible(), "Combo is Not Deleted");
    }

    @When("User clicks on {string} CTA in delete popup")
    public void clickOnDeleteCancelCTA(String cancelButton){
        salesAndParentGroupsPage.getElementByText(cancelButton).click();
    }
}
