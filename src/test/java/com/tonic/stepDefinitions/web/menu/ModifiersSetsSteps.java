package com.tonic.stepDefinitions.web.menu;

import com.microsoft.playwright.Page;
import com.tonic.enums.BOHConstants;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.pageObjects.web.menu.ItemsPage;
import com.tonic.pageObjects.web.menu.MenuLocators;
import com.tonic.pageObjects.web.menu.ModifiersSetsPage;
import com.tonic.pageObjects.web.menu.PremodifiersPage;
import com.tonic.stepDefinitions.BaseStep;
import com.tonic.utils.ApplicationUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ModifiersSetsSteps extends BaseStep {
    private Page page = PlaywrightFactory.getPage();
    private final ModifiersSetsPage modifiersSetsPage = new ModifiersSetsPage(page);
    private String updateModifiersSetName = null;


    @When("The user search and add the modifier sets in the Modifier sets page")
    public void theUserShouldSearchAndAddTheModifierSets() {
        modifiersSetsPage.theUserSearchesAndAddTheModifierSets();
        assertTrue(modifiersSetsPage.isModifierSetsListVisible().contains(BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue()), "Modifier set '" + BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue() + "' is not present.");
        LOGGER.info("Modifier set is Visible");
    }

    @And("The user search and delete the modifier sets in the Modifier sets page")
    public void theUserShouldSearchAndDeleteTheModifierSets() {
        String modifierSetsName = BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue();
        modifiersSetsPage.theUserSearchesAndDeleteTheModifierSets();
        boolean isModifierSetStillPresent = modifiersSetsPage.theUserSearchForASpecificModifierSetsAndReturnTheResult();
        assertFalse(isModifierSetStillPresent, "Modifiers set '" + modifierSetsName + "' should not be present after deletion.");
        LOGGER.info("Modifiers Set '" + modifierSetsName + "' successfully deleted and no longer present.");
    }

    @When("The user adds an random modifier sets {string} in the Modifier sets page")
    public void theUserShouldAddAnRandomModifierSets(String modifiersSetName) {
        modifiersSetsPage.clickDynamicElement(MenuLocators.ACTION_BUTTON,ButtonConstants.fromKey("ADD_NEW_BUTTON").getValue(),5000);
        String randomString = ApplicationUtils.getRandomString(4);
        updateModifiersSetName = modifiersSetName.concat(randomString);
        modifiersSetsPage.userMustEntersAllMandatoryFieldsOnAddModifierSets(
                updateModifiersSetName,
                BOHConstants.fromKey("MODIFIER_SETS_FIELD").getValue(),
                BOHConstants.fromKey("MODIFIER_SETS_MIN_FIELD").getValue(),
                BOHDataConstants.fromKey("MODIFIER_SETS_MIN_VALUE").getValue(),
                BOHConstants.fromKey("MODIFIER_SETS_MAX_FIELD").getValue(),
                BOHDataConstants.fromKey("MODIFIER_SETS_MAX_VALUE").getValue());
        modifiersSetsPage.chooseSpecificModifier();
        softAssertTrue(modifiersSetsPage.getElementByText(MenuLocators.ADD_NEW_MODIFIERS_DROPDOWN_OPTION, BOHDataConstants.fromKey("MODIFIER_NAME").getValue(), 30000).isVisible(), "Modifier is not visible");
        modifiersSetsPage.click(MenuLocators.SAVE_BUTTON,5000);
        boolean visible = modifiersSetsPage.isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("SUCCESSFUL_MODIFIER_SET_CREATION_MESSAGE").getValue(), 5000);
        softAssertTrue(visible, "modifier set is not available.");
        LOGGER.info("Successfully added modifier set to the list");
        modifiersSetsPage.userNewModifierSetsIsAdded();
        assertAll();
        LOGGER.info("Modifier set is Visible");
    }

    @And("The user deletes the added modifier sets in the Modifier sets page")
    public void theUserShouldDeleteTheModifierSets() {
        modifiersSetsPage.enterText(modifiersSetsPage.buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, ButtonConstants.fromKey("SEARCH_HERE").getValue()), updateModifiersSetName, 10000);
        String modifierNameEle = modifiersSetsPage.buildDynamicLocator(MenuLocators.SPECIFIC_MODIFIERS_SETS_LIST,updateModifiersSetName);
        modifiersSetsPage.isVisible(modifierNameEle, updateModifiersSetName);
        modifiersSetsPage.clickDynamicElement(MenuLocators.SPECIFIC_DELETE_BUTTON,updateModifiersSetName, 10000);
        modifiersSetsPage.click(MenuLocators.POPUP_DELETE, 10000);
        boolean visible = modifiersSetsPage.isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("MODIFIER_SET_DELETE_MESSAGE").getValue(), 5000);
        assertTrue(visible, "modifier set is available.");
        LOGGER.info("Modifiers set deleted successfully");
    }
}