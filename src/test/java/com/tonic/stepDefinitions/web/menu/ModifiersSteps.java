package com.tonic.stepDefinitions.web.menu;

import com.microsoft.playwright.Page;
import com.tonic.actions.HomeActions;
import com.tonic.enums.BOHConstants;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.hooks.Hooks;
import com.tonic.pageObjects.web.menu.ItemsPage;
import com.tonic.pageObjects.web.menu.MenuLocators;
import com.tonic.pageObjects.web.menu.ModifiersPage;
import com.tonic.pageObjects.web.menu.PremodifiersPage;
import com.tonic.stepDefinitions.BaseStep;
import com.tonic.utils.ApplicationUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import java.util.List;
import static org.testng.Assert.*;

// Placeholder for ModifiersPage (to be implemented if not present)

public class ModifiersSteps extends BaseStep {
    // Use the correct constructor for ApplicationUtils
    private Page page = PlaywrightFactory.getPage();
    private final ModifiersPage modifiersPage = new ModifiersPage(page);
    ItemsPage itemsPage = new ItemsPage(page);
    private String modifierName = null;
    private String updatedmodifierName = null;

    HomeActions homeActions = new HomeActions(PlaywrightFactory.getPage());


    @Given("The user navigates to {string} > {string}")
    public void theUserNavigatesTo(String homePageHeaders, String pageName) {
        homeActions.navigateToSpecificPageFromHomePageHeaders(homePageHeaders, pageName);
    }

    @And("User is on the {string} page")
    public void userIsOnTheSpecificPage(String page){
        String cancelButton = modifiersPage.buildDynamicLocator(MenuLocators.PAGE_HEADER,page);
        boolean pageHeader = modifiersPage.isVisible(cancelButton,"Page Header"+ page);
        assertTrue(pageHeader, "Modifiers page is not visible");
        LOGGER.info("User is on the "+ page +" Page");
    }

    @And("The user should see a list of Modifiers configured in BoH")
    public void userShouldSeeListOfModifiers() {
        assertFalse(modifiersPage.isModifiersListVisible().isEmpty(), "Modifiers list is not visible");
        modifierName = modifiersPage.isModifiersListVisible().get(0);
    }

    @Then("Each row should have an Edit Icon visible under the Actions column")
    public void eachRowShouldHaveEditIcon() {
        List<Boolean> editIconsVisibility = modifiersPage.isEditIconVisibleForAllRows();
        Hooks.getSoftAssert().assertFalse(editIconsVisibility.contains(false), "Edit icon not visible for editable modifier:");
        assertAll();
    }

    @When("The user clicks on the Edit Icon under the Actions column")
    public void userClicksEditIcon() {
        modifiersPage.clickDynamicElement(MenuLocators.SPECIFIC_MODIFIER_EDIT, modifierName,2000);
    }

    @Then("The row becomes editable and {string} button is visible")
    public void theRowBecomesEditableAndButtonIsVisible(String cancel) {

        String cancelButton = modifiersPage.buildDynamicLocator(MenuLocators.TXT_BUTTON,cancel);
        boolean cancelIcon=modifiersPage.isVisible(cancelButton,cancel);
        assertTrue(cancelIcon,cancel+" Button is not visible");
    }

    @And("The icons should change to {string} and {string}")
    public void iconsShouldChangeToCheckAndX(String save, String cancel) {
        String saveTxt = ButtonConstants.fromKey(save).getValue();
        String saveButtonEle = modifiersPage.buildDynamicLocator(MenuLocators.TXT_BUTTON,saveTxt);
        boolean saveIcon=modifiersPage.isVisible(saveButtonEle,saveTxt);
        Hooks.getSoftAssert().assertTrue(saveIcon, save +" Button is not visible");
        String cancelCTA = ButtonConstants.fromKey(cancel).getValue();
        String cancelButtonEle = modifiersPage.buildDynamicLocator(MenuLocators.TXT_BUTTON,cancelCTA);
        boolean cancelIcon=modifiersPage.isVisible(cancelButtonEle,cancelCTA);
        Hooks.getSoftAssert().assertTrue(cancelIcon, cancel +" Button is not visible");
        assertAll();
    }

    @When("The user enters a value as {string} in {string} field")
    public void theUserEntersAValueAsInField(String modifierName, String inputField) {
        String randomString = ApplicationUtils.getRandomString(4);
        updatedmodifierName = modifierName.concat(randomString);
        String modifierNameEle = modifiersPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,inputField);
        boolean modifierInputFieldIsVisible=modifiersPage.isVisible(modifierNameEle,inputField);
        assertTrue(modifierInputFieldIsVisible,inputField+" Text Box is not visible");
        modifiersPage.fillField(modifierNameEle,updatedmodifierName,3000);
    }

    @Then("The system should validate the field values")
    public void systemShouldValidateFieldValues() {
        String errorMsgLocator = modifiersPage.buildDynamicLocator(MenuLocators.ERROR_LOCATOR,modifierName);
        int errorCount = modifiersPage.getElementCount(errorMsgLocator);
        Assert.assertEquals(errorCount, 0, "Error message is displayed");
    }

    @When("The user clicks on the {string} icon")
    public void theUserClicksOnTheIcon(String saveText) {

        modifiersPage.clickDynamicElement(MenuLocators.TXT_BUTTON,saveText,3000);
        modifiersPage.reloadPage();
    }

    @Then("The system should save the updated changes")
    public void systemShouldSaveUpdatedChanges() {
        modifiersPage.enterText(MenuLocators.SEARCH_BAR,updatedmodifierName,5000);
        List<String>newModifiersList= modifiersPage.isModifiersListVisible();
        // For now, just check that the change is saved (could be improved by storing the value)
        assertTrue(newModifiersList.contains(updatedmodifierName), "Modifiers Name Changes was not saved" + newModifiersList + updatedmodifierName);
    }

    @And("The existing values should be visible in input field {string},{string}, {string}, {string},")
    public void theExistingValuesShouldBeVisibleInInputField(String modfierNameString, String displayNameString, String onlineOrderingString, String multiplierString) {
        String modifierNameEle = modifiersPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,modfierNameString);
        boolean modifierInputFieldIsVisible=modifiersPage.isVisible(modifierNameEle,modfierNameString);
        Hooks.getSoftAssert().assertTrue(modifierInputFieldIsVisible, modfierNameString +" Input text Box is not visible");
        String displayNameEle = modifiersPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,displayNameString);
        boolean displayNameInputFieldIsVisible=modifiersPage.isVisible(displayNameEle,displayNameString);
        Hooks.getSoftAssert().assertTrue(displayNameInputFieldIsVisible, displayNameString +" Input text Box is not visible");
        String onlineOrderingEle = modifiersPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,onlineOrderingString);
        boolean onlineOrderingInputFieldIsVisible=modifiersPage.isVisible(onlineOrderingEle,onlineOrderingString);
        Hooks.getSoftAssert().assertTrue(onlineOrderingInputFieldIsVisible, onlineOrderingString +" Input text Box is not visible");
        String multiplierEle = modifiersPage.buildDynamicLocator(MenuLocators.NUMBER_INPUT_FIELD,multiplierString);
        boolean multiplierInputFieldIsVisible=modifiersPage.isVisible(multiplierEle,multiplierString);
        Hooks.getSoftAssert().assertTrue(multiplierInputFieldIsVisible, multiplierString +" Input text Box is not visible");
        BaseStep.assertAll();
    }

    @Then("The {string} CTA should be disabled")
    public void addNewShouldDisabled(String Addnew) {
        boolean isDisabled = modifiersPage.getElementByText(Addnew).isDisabled();
        assertTrue(isDisabled, "Add New button should be disabled after click");
    }

    @Then("The system should not save the updated changes")
    public void systemShouldNotSaveUpdatedChanges() throws InterruptedException {
        modifiersPage.waitForSelector(MenuLocators.MODIFIERS_LIST,5000);
        List<String>newModifiersList= modifiersPage.isModifiersListVisible();
        // For now, just check that the change is saved (could be improved by storing the value)
        assertFalse(newModifiersList.contains(updatedmodifierName), "Modifiers Name Changes was saved");
    }

    @Then("User new Modifier is added")
    public void userNewModifierIsAdded() {
        modifiersPage.enterText(MenuLocators.SEARCH_BAR,updatedmodifierName,10000);
        List<String>newModifiersList= modifiersPage.isModifiersListVisible();
        // For now, just check that the change is saved (could be improved by storing the value)
        assertTrue(newModifiersList.contains(updatedmodifierName), "Modifiers Name Changes was not saved");
    }

    @And("User must Enters {string} in {string} and Optional fields {string}  in {string} {string} as {string},{string} in {string} {string} in {string} and {string} in {string}")
    public void userMustEntersAllMandatoryFieldsOnAddModifiers (String modifierName, String modifierNameField, String displayName, String displayNameField, String onlineOdering, String onlineOrderingField, String multiplier, String multiplierField, String prepTime, String prepTimeField, String price, String priceField) {
        String randomString = ApplicationUtils.getRandomString(4);
        updatedmodifierName = modifierName.concat(randomString);
        modifiersPage.enterTheMandatoryDetailOnAddModifiers(updatedmodifierName,modifierNameField,displayName,displayNameField,onlineOdering,onlineOrderingField,multiplier,multiplierField,prepTime,prepTimeField,price,priceField);
    }

    @Then("The icon should revert to the Pencil icon")
    public void theIconShouldRevertToThePencilIcon() {
        boolean editIconForSpecificModifier = modifiersPage.isVisible(MenuLocators.SPECIFIC_MODIFIER_EDIT, updatedmodifierName,2000);
        assertTrue(editIconForSpecificModifier, "Edit icon is not visible for edited Modifier "+updatedmodifierName );
    }

    @Then("The user should be able to see the {string} CTA")
    public void theUserShouldBeAbleToSeeTheCTA(String addNewCTA) {
        String addNewEle = modifiersPage.buildDynamicLocator(MenuLocators.ADD_NEW_CANCEL_BUTTON,"add");
        boolean addNewButton=modifiersPage.isVisible(addNewEle,addNewCTA);
        assertTrue(addNewButton, "Add New CTA is not visible");
    }

    @And("The {string} CTA should be enabled by default")
    public void theCTAShouldBeEnabledByDefault(String addNew) {
        boolean addNewButtonIsEnabled = modifiersPage.getElementByText(addNew).isEnabled();
        assertTrue(addNewButtonIsEnabled, "Add New button should be disabled after click");
    }

    @When("User clicks {string} CTA")
    public void userClickOnAddNew(String addNewButton) {
        modifiersPage.getElementByText(addNewButton).click();
    }

    @When("The user search and add the modifier in the Modifiers page")
    public void theUserShouldSearchAndAddTheModifiers() {
        modifiersPage.theUserSearchAndAddTheModifiers();
        String expectedModifier = BOHDataConstants.fromKey("MODIFIER_NAME").getValue();
        List<String> modifiersList = modifiersPage.isModifiersListVisible();
        assertTrue(modifiersList.contains(expectedModifier), "Expected modifier '" + expectedModifier + "' to be present after save, but it was not found.");
    }

    @When("The user search and delete the modifier in the Modifiers page")
    public void theUserShouldSearchAndDeleteTheModifiers() {
        String modifierName = BOHDataConstants.fromKey("MODIFIER_NAME").getValue();
        modifiersPage.theUserSearchAndDeleteTheModifiers();
        modifiersPage.waitForSelector(modifiersPage.buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MODIFIERS").getValue()),10000);
        boolean isModifierStillPresent = modifiersPage.isModifiersVisibleAfterSearch();
        assertFalse(isModifierStillPresent, "Modifier '" + modifierName + "' should not be present after deletion.");
        LOGGER.info("Modifier '" + modifierName + "' successfully deleted and no longer present.");
    }
}
