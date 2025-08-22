package com.tonic.stepDefinitions.web.menu;


import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tonic.actions.HomeActions;
import com.tonic.enums.BOHConstants;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.hooks.Hooks;
import com.tonic.pageObjects.web.menu.MenuLocators;
import com.tonic.pageObjects.web.menu.PremodifiersPage;
import com.tonic.pageObjects.web.payment.TypesPage;
import com.tonic.utils.ApplicationUtils;
import com.tonic.stepDefinitions.BaseStep;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class PremodifiersSteps extends BaseStep {
    private Page page = PlaywrightFactory.getPage();
    private final PremodifiersPage premodifiers = new PremodifiersPage(page);
    private TypesPage typesPage = new TypesPage(page);
    private String preGroupName;
    private  String preModifierName;
    HomeActions nav = new HomeActions(page);

    @Before(order = 1, value = "@DualPricingEnabled")
    public void setupDualPricingEnabled() {
        nav.navigateToSpecificPageFromHomePageHeaders(ButtonConstants.fromKey("CONFIGURATION").getValue(), ButtonConstants.fromKey("TYPES").getValue());
        String option = System.getProperty(ButtonConstants.fromKey("DUAL_PRICING_OPTION").getValue(), ButtonConstants.fromKey("ENABLED").getValue());
        // Click icon, select option and save
        typesPage.configureDualPricing(option);
    }

    @Before(order = 1, value = "@DualPricingDisabled")
    public void setupDualPricingDisabled() {
        nav.navigateToSpecificPageFromHomePageHeaders(ButtonConstants.fromKey("CONFIGURATION").getValue(), ButtonConstants.fromKey("TYPES").getValue());
        String option = System.getProperty(ButtonConstants.fromKey("DUAL_PRICING_OPTION").getValue(), ButtonConstants.fromKey("DISABLED").getValue());
        // Click icon, select option and save
        typesPage.configureDualPricing(option);
    }

    @And("The user click on {string} CTA and verifies {string} Text is Visible")
    public void userClicksOnAddNewCta(String addNew, String configurePreModifierText) {
        premodifiers.clickDynamicElement(MenuLocators.ACTION_BUTTON, addNew, 5000);
        assertTrue(premodifiers.isVisible(MenuLocators.CONFIGURE_PRE_MODIFIER_GROUP_TEXT,configurePreModifierText));
        LOGGER.info("User is able to see"+ configurePreModifierText + "text");
    }

    @When("The user Adds {string} and click on {string} CTA in premodifiers modal")
    public void userClicksOnAddNewCtaInPremodifiersModal(String premodifierGroupName,String addnewButton){
        preGroupName = ApplicationUtils.getRandomString(8);
        premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,premodifierGroupName),preGroupName, 7000 );
        premodifiers.clickDynamicElement(MenuLocators.ADD_NEW_CTA_IN_PREMODIFIER_MODAL,addnewButton,1000);
    }

    @And("The user added {string} new premodifiers and applied {string}, {string}, {string}, {string}, {string} make the changes to {string}, {string},{string} fields and {string} the premodifiers")
    public void userAddsNewPremodifiersAndMakeTheChangesToFields(String field1,String none,String addAmount, String ForceAmount,String percentage,String multiplier,String premodifiername,String onlineOrdername, String kitchenName,String save) {
        int count = Integer.parseInt(field1);
        List<String> options = Arrays.asList(none,addAmount,ForceAmount,percentage,multiplier);

        // Get pricing buttons from the pricing type options list
        List<Locator> buttonList = premodifiers.getAllElements(MenuLocators.PRICING_TYPE_LIST,5000);
        int buttonsToUse = Math.min(Integer.parseInt(field1), buttonList.size());

        for (int i = 0; i < buttonsToUse && i < count; i++) {
            // Filling all required fields with random strings
            preModifierName = ApplicationUtils.getRandomString(8);

            premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,premodifiername),preModifierName, 7000 );

            String orderName = ApplicationUtils.getRandomString(7);
            premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,onlineOrdername), orderName, 7000);
            String kitchename = ApplicationUtils.getRandomString(7);
            premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,kitchenName), kitchename, 7000);

            buttonList.get(i).click();
            // Select a different option for each iteration
            String optionToClick = options.get(i % options.size());
            String dynamicOptionLocator = premodifiers.buildDynamicLocator(MenuLocators.PRICING_TYPES, optionToClick);
            premodifiers.clickDynamicElement(dynamicOptionLocator,optionToClick,1000);
            premodifiers.clickDynamicElement(MenuLocators.PRE_MODIFIER_SAVE_BUTTON,save,1000);
        }
    }

    @When("The user click on {string} button")
    public void userClicksOnSaveButton(String saveButton){
        premodifiers.clickDynamicElement(MenuLocators.SAVE_BUTTON_IN_PREMODIFIER_MODAL,saveButton,10000);
    }

    @Then("The user searches for the newly created group using the {string} field that is saved successfully")
    public void userSearchesForTheNewlyCreatedGroupUsingSearchFieldThatIsSavedSuccessfully(String search) {
        premodifiers.waitForSelector(premodifiers.buildDynamicLocator(MenuLocators.PAGE_HEADER,  ButtonConstants.fromKey("PREMODIFIERS").getValue()),5000);
        premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, search), preGroupName, 5000);
        premodifiers.waitUntilElementIsVisible(MenuLocators.SPECIFIC_PREMODIFIER_NAME,preGroupName,5000);
        assertTrue(premodifiers.getVisibleElementsText(MenuLocators.PRE_MODIFIER_GROUP_LIST, 10000).contains(preGroupName), "PreModifier Group not found");
        System.out.println("searched for Premodifier Group and its visible " + preGroupName);
    }

    @And("The user makes changes to fields {string}, {string} and {string} and {string} the premodifiers")
    public void userMakesChangesToFieldsAnd(String premodifiername,String onlineOrdername, String kitchenName, String save) {
        preModifierName = ApplicationUtils.getRandomString(8);
        premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,premodifiername),preModifierName, 7000 );
        String orderName = ApplicationUtils.getRandomString(7);
        premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,onlineOrdername),orderName, 7000 );
        String kitchename = ApplicationUtils.getRandomString(7);
        premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,kitchenName),kitchename, 7000 );
        premodifiers.clickDynamicElement(MenuLocators.PRE_MODIFIER_SAVE_BUTTON,save,1000);
    }

    @And("The user clicks on {string} button")
    public void userClicksOnCancelButton(String cancel) {
        premodifiers.clickDynamicElement(MenuLocators.ADD_NEW_CANCEL_BUTTON,cancel,10000);
    }

    @Then("The user enters the cancelled pre-modifier group name into the {string} field to search and verifies that {string}")
    public void userEntersTheCancelledPreModifierGroupNameIntoTheFieldToSearch(String search,String noResultFound) {
        premodifiers.waitForSelector(premodifiers.buildDynamicLocator(MenuLocators.PAGE_HEADER,  ButtonConstants.fromKey("PREMODIFIERS").getValue()),5000);
        premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, search), preGroupName, 5000);
        assertTrue(premodifiers.isVisible(MenuLocators.NO_RESULTS_FOUND_TEXT,noResultFound));
        LOGGER.info("searched for Premodifier Group and its Not Visible " + preGroupName);

    }

        @And("The user enters the premodifiers name {string} in the field")
        public void userEntersThePremodifiersNameInTheField (String premodifiername) {
            preModifierName = ApplicationUtils.getRandomString(8);
            premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, premodifiername), preModifierName, 7000);
        }

        @And("The user selects {string} under Pre or Post field")
        public void userSelectsUnderPreOrPostField (String postOption){
            premodifiers.click(MenuLocators.PRE_POST_DROPDOWN, 5000);
            String prePostEle = premodifiers.buildDynamicLocator(MenuLocators.PRE_POST_SELECTION, postOption);
            premodifiers.clickDynamicElement(prePostEle, postOption, 1000);
        }

        @And("The user disables the checkbox under the Online Ordering header")
        public void userDisablesTheCheckboxUnderTheOnlineOrderingHeader () {
            Locator checkbox = premodifiers.getElement(MenuLocators.ONLINE_ORDERING_CHECKBOX, 1000);

            // Ensuring it's visible
            assertTrue(premodifiers.isVisible(MenuLocators.ONLINE_ORDERING_CHECKBOX, ""));
            LOGGER.info("Online Ordering Checkbox is Visible");

            // Uncheck only if it's currently checked
            if (checkbox.isChecked()) {
                checkbox.uncheck();
            }
        }

      @And("The user chooses the {string} icon under the Pricing Type header")
      public void userChoosesThePricingTypeIconUnderThePricingTypeHeader (String pricingType){
            String optionLocator = premodifiers.buildDynamicLocator(MenuLocators.PRICING_TYPES, pricingType);
            premodifiers.clickDynamicElement(optionLocator, pricingType, 6000);
        }

        @And("The user enters {string} in the Percentage field and {string} the premodifiers")
        public void userEntersInThePercentageField (String percentageValue, String save){
            premodifiers.fillField(MenuLocators.PERCENTAGE_INPUT, percentageValue, 2000);
            premodifiers.clickDynamicElement(MenuLocators.PRE_MODIFIER_SAVE_BUTTON, save, 1000);
        }

        @Then("The user should be able to see two fields: {string} and {string}")
        public void userShouldBeAbleToSeeTwoFields(String cashPrice, String cardPrice) {
            softAssertTrue(premodifiers.getElementByText(MenuLocators.NUMBER_INPUT_FIELD, cashPrice, 2000).isVisible(), "Cash Price textbox is not visible");
            softAssertTrue(premodifiers.getElementByText(MenuLocators.NUMBER_INPUT_FIELD, cardPrice, 2000).isVisible(), "Card Price textbox is not visible");
            assertAll();
    }

    @Then("The user should be presented with columns Pre-Modifiers, Online Ordering Name, Kitchen Name , Pre or Post, {string}, Pricing Type and actions-header")
    public void userShouldBePresentedWithColumnsPreModifiersOnlineOrderingNameKitchenNamePreOrPostOnlineOrderingPricingTypeAndActionsHeader(String onlineOrderingCheckboxHeader) {
        List<Locator> premodifierColumnHeaderList = premodifiers.getAllElements(MenuLocators.PRE_MODIFIERS_HEADER_LIST,5000);
        int count = premodifierColumnHeaderList.size();
        LOGGER.info("Total headers found: " + count);

        for (int i = 0; i < count; i++) {
            Locator header = premodifierColumnHeaderList.get(i);
            boolean isVisible = header.isVisible();
            softAssertTrue(isVisible, "Header at index " + i + " is not visible");

        }

        boolean checkboxHeaderVisible = premodifiers.getElementByText(MenuLocators.NUMBER_INPUT_FIELD, onlineOrderingCheckboxHeader, 2000).isVisible();
        softAssertTrue(checkboxHeaderVisible, onlineOrderingCheckboxHeader +"Button is not visible");
        assertAll();

    }

    @And("The user observes the field {string}")
    public void theUserObservesTheField(String onlineOrdername) {
        assertTrue(premodifiers.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD,onlineOrdername,2000).isVisible(), "online ordering Name Textbox is not visible");
        LOGGER.info("Textbox" + onlineOrdername+ "is visible");
    }


    @Then("The field {string} should have attribute {string} and it should not be {string} so that it verifies field is optional")
    public void theFieldShouldBeOptional(String onlineOrdername,String ariarequired,String required) {
        Locator field = premodifiers.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD,onlineOrdername,2000);
        String ariaRequired = field.getAttribute(ariarequired);
        boolean isRequired = required.equalsIgnoreCase(ariaRequired);
        softAssertFalse(isRequired, "Field '" + onlineOrdername + "' is marked as required (aria-required='true') but should be optional.");
        assertAll();
    }

    @And("The field {string} should have a minimum length {string} of {int}")
    public void theFieldShouldHaveAMinimumLengthOf(String onlineOrdername,String minLength, int expectedMinLength) {
        Locator field = premodifiers.getElementByText(MenuLocators.TEXT_BOX_INPUT_FIELD,onlineOrdername,2000);

        String minLengthValue = field.getAttribute(minLength);

        // minlength is missing in DOM, HTML treats default 0
        int actualMinLength = (minLengthValue != null) ? Integer.parseInt(minLengthValue) : 0;

        if (actualMinLength != expectedMinLength) {
            throw new AssertionError("Expected minimum length " + expectedMinLength +
                    " but found " + actualMinLength + " for field '" + field + "'");
        }
            LOGGER.info("Verified: Field '" + field + "' has minimum length " + expectedMinLength);
    }

    @And("The user enters the premodifiers name {string} in the field and {string} should support {string} Characters and {string} the premodifiers")
    public void userEntersThePremodifiersNameInTheFieldAndShouldSupportAlphanumericAndSpecialCharacters(String premodifiername, String onlineOrdername,String alphanumericSpecialCharacter, String save) {
        premodifiers.waitForSelector(premodifiers.buildDynamicLocator(MenuLocators.PAGE_HEADER,  ButtonConstants.fromKey("PREMODIFIERS").getValue()),5000);
        preModifierName = ApplicationUtils.getRandomString(8);
        premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, premodifiername), preModifierName, 7000);
        premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,onlineOrdername),alphanumericSpecialCharacter, 7000);
        premodifiers.clickDynamicElement(MenuLocators.PRE_MODIFIER_SAVE_BUTTON,save,1000);
        LOGGER.info("Premodifier is saved successfully");
    }

    @And("In the {string} field,user enters {string}")
    public void userEntersCardPriceValue(String cardPrice, String cardPriceValue) {
        premodifiers.getElementByText(MenuLocators.NUMBER_INPUT_FIELD, cardPrice, 2000).fill(cardPriceValue);
    }


    @And("In the {string} field,user enters {string} and {string} the premodifiers")
    public void userEntersCashPriceAndSaveTheChanges(String cashPrice, String cashPriceValue, String save) {
        premodifiers.enterText(premodifiers.buildDynamicLocator(MenuLocators.NUMBER_INPUT_FIELD, cashPrice),cashPriceValue, 2000);
        premodifiers.clickDynamicElement(MenuLocators.PRE_MODIFIER_SAVE_BUTTON, save, 1000);
    }

    @And("The user assigns Modifier Sets To Pre Modifier")
    public void userAssignModifierSetToPreModifier() {
        premodifiers.userAssignsModifierSetToPreModifierGroup();
        boolean visible = premodifiers.isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("PREMODIFIER_UPDATE_SUCCESS_MESSAGE").getValue(), 5000);
        assertTrue(visible, "Pre modifier group is not updated successfully");
        LOGGER.info("Pre modifier group is updated successfully");
    }

    @When("The user creates a Pre-Modifier")
    public void theUserCreatesAPreModifier() {
        String preModifierGroupName = BOHDataConstants.fromKey("PREMODIFIER_GROUP_NAME").getValue();
        premodifiers.theUserSearchesAndCreatesAPreModifier();
        assertTrue(premodifiers.getVisibleElementsText(MenuLocators.PRE_MODIFIER_GROUP_LIST, 10000).contains(preModifierGroupName), "Pre Modifier Group '" + preModifierGroupName + "' is not found after search or creation.");
        LOGGER.info("Pre Modifier Group" + preModifierGroupName +  "is visible in the list after search/creation");
    }

    @When("The user delete a Pre-Modifier")
    public void theUserDeleteAPreModifier() {
        String preModifierGroupName = BOHDataConstants.fromKey("PREMODIFIER_GROUP_NAME").getValue();
        String premodifier_name = BOHDataConstants.fromKey("PREMODIFIER_NAME").getValue();
        premodifiers.theUserDeletesAPreModifier();
        premodifiers.reloadPage();
        premodifiers.waitForSelector(premodifiers.buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("PREMODIFIERS").getValue()),8000);
        boolean isPremodifierGroupStillPresent = premodifiers.isPremodifierVisibleAfterSearch();
        assertFalse(isPremodifierGroupStillPresent, "Pre-modifier Group " + preModifierGroupName + " should not be present after deletion.");
        LOGGER.info("Successfully deleted the Pre-Modifier '" + premodifier_name + "' and its group '" + preModifierGroupName + "'.");
    }

    @Then("The user deletes the newly created Pre Modifier")
    public void userDeletesTheNewlyCreatedPreModifier(){
        String specificPreModifierEle = premodifiers.buildDynamicLocator(MenuLocators.SPECIFIC_PREMODIFIER_EDIT_DELETE, preModifierName,ButtonConstants.fromKey("DELETE_BUTTON").getValue());
        premodifiers.click(specificPreModifierEle, 10000);
    }

    @When("The user click on {string} button in Popup")
    public void theUserClickOnButtonInPopup(String save) {
        premodifiers.clickDynamicElement(MenuLocators.PRE_MODIFIER_SAVE_BUTTON, save, 1000);
    }
}

