package com.tonic.pageObjects.web.menu;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tonic.common.web.BasePage;
import com.tonic.enums.BOHConstants;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.testng.Assert.assertTrue;

public class ModifiersSetsPage extends BasePage {

    /**
     * Constructor to initialize the Playwright Page instance.
     *
     * @param page Playwright Page object
     */


    public ModifiersSetsPage(Page page) {
        super(page);
    }


    // =============== Methods ===============

    /**
     * Enters mandatory details for adding a new modifier set.
     * Fills in the modifier set name, minimum limit, and maximum limit fields
     * with the provided values.
     * @param newModifierSetName the name of the modifier set
     * @param newModifierSetField the field identifier for the modifier set name
     * @param minLimitTextField the field identifier for minimum limit
     * @param minLimit the minimum limit value
     * @param maxLimitTextField the field identifier for maximum limit
     * @param maxLimit the maximum limit value
     * @return void
     */

    public void enterTheMandatoryDetailOnAddModifierSets(String newModifierSetName, String newModifierSetField, String minLimitTextField, String minLimit, String maxLimitTextField, String maxLimit) {

        String newModifierSetEle = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, newModifierSetField);
        String minimumLimitEle = buildDynamicLocator(MenuLocators.LIMIT_TEXT_BOX_INPUT_FIELD, minLimitTextField);
        String maximumLimitEle = buildDynamicLocator(MenuLocators.LIMIT_TEXT_BOX_INPUT_FIELD, maxLimitTextField);
        fillField(newModifierSetEle, newModifierSetName, 3000);
        fillField(minimumLimitEle, minLimit, 3000);
        fillField(maximumLimitEle, maxLimit, 3000);
    }

    /**
     * Retrieves the list of visible modifier sets from the page.
     * @return List of modifier set names currently visible on the page
     */

    public List<String> isModifierSetsListVisible() {
        List<String> listOfModifierSets;
        listOfModifierSets = getVisibleElementsText(MODIFIER_SETS_LIST, 3000);
        waitForSelector(MenuLocators.MODIFIER_SETS_LIST, 3000);
        return listOfModifierSets;
    }

    /**
     * Searches for a specific modifier set and returns whether it's visible in results.
     * @return {true} if modifier set is visible after search, {false} otherwise
     */

    public boolean theUserSearchForASpecificModifierSetsAndReturnTheResult() {
        String modifierSetsName = BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue();
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MODIFIERS_SETS").getValue()), 10000);
        enterText(buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, ButtonConstants.fromKey("SEARCH_HERE").getValue()), modifierSetsName, 10000);
        String modifierNameEle = buildDynamicLocator(MenuLocators.SPECIFIC_MODIFIERS_SETS_LIST,modifierSetsName);
        return isVisible(modifierNameEle, BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue());
    }

    /**
     * Enters all mandatory fields for adding a modifier set and logs the action.
     * Delegates to enterTheMandatoryDetailOnAddModifierSets method and logs
     * successful completion of field entry.
     * @param modifiersSetName the name of the modifier set
     * @param newModifierSetField the field identifier for the modifier set name
     * @param minLimitTextField the field identifier for minimum limit
     * @param minLimit the minimum limit value
     * @param maxLimitTextField the field identifier for maximum limit
     * @param maxLimit the maximum limit value
     * @return void
     */

    public void userMustEntersAllMandatoryFieldsOnAddModifierSets (String modifiersSetName, String newModifierSetField, String minLimitTextField, String minLimit ,String maxLimitTextField, String maxLimit) {
        enterTheMandatoryDetailOnAddModifierSets(modifiersSetName,newModifierSetField,minLimitTextField,minLimit,maxLimitTextField,maxLimit);
        LOGGER.info("Entered the mandatory field in modifier sets");
    }

    /**
     * Searches for a modifier set and adds it if it doesn't already exist.
     * Checks if the modifier set exists first. If not present:
     * - Clicks the Add New button
     * - Enters all mandatory fields (name, min/max limits)
     * - Chooses specific modifiers
     * - Saves the modifier set
     * - Verifies successful addition
     * @return void
     */

    public void theUserSearchesAndAddTheModifierSets() {
        boolean isModifierSetsPresent = theUserSearchForASpecificModifierSetsAndReturnTheResult();
        if (isModifierSetsPresent) {
          LOGGER.info("Modifiers set already exists. Skipping add process.");
            return;
        }
        clickDynamicElement(MenuLocators.ACTION_BUTTON,ButtonConstants.fromKey("ADD_NEW_BUTTON").getValue(),5000);
        userMustEntersAllMandatoryFieldsOnAddModifierSets(
                BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue(),
                BOHConstants.fromKey("MODIFIER_SETS_FIELD").getValue(),
                BOHConstants.fromKey("MODIFIER_SETS_MIN_FIELD").getValue(),
                BOHDataConstants.fromKey("MODIFIER_SETS_MIN_VALUE").getValue(),
                BOHConstants.fromKey("MODIFIER_SETS_MAX_FIELD").getValue(),
                BOHDataConstants.fromKey("MODIFIER_SETS_MAX_VALUE").getValue()
        );
        chooseSpecificModifier();
        click(MenuLocators.SAVE_BUTTON,5000);
        boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("SUCCESSFUL_MODIFIER_SET_CREATION_MESSAGE").getValue(), 5000);
        assertTrue(visible, "modifier set is not available.");
        LOGGER.info("Successfully added modifier set to the list");
        userNewModifierSetsIsAdded();
    }

    /**
     * Selects a specific modifier from the dropdown in modifier sets.
     * @return void
     */

    public void chooseSpecificModifier() {
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MODIFIERS_SETS").getValue()),50000);
        clickDynamicElement(MenuLocators.ADD_NEW_MODIFIERS_DROPDOWN, BOHConstants.fromKey("MODIFIER_FIELD_IN_MODIFIER_SETS").getValue(), 30000);
        page.waitForSelector(MenuLocators.ADD_NEW_MODIFIERS_DROPDOWN_LIST);
        clickDynamicElement(MenuLocators.ADD_NEW_MODIFIERS_DROPDOWN_OPTION, BOHDataConstants.fromKey("MODIFIER_NAME").getValue(), 30000);
        LOGGER.info("Modifier is selected from the dropdown");

    }

    /**
     * Verifies that a new modifier set was successfully added to the system.
     * @return void
     */

    public void userNewModifierSetsIsAdded() {
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MODIFIERS_SETS").getValue()),20000);
        enterText(MenuLocators.SEARCH_BAR,BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue(),10000);
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER,  ButtonConstants.fromKey("MODIFIERS_SETS").getValue()),10000);
        List<String>newModifiersList= isModifierSetsListVisible();
        assertTrue(newModifiersList.contains(BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue()), "Modifier sets Name Changes was not saved");
        LOGGER.info("The new modifier set is added to the list");
    }

    /**
     * Searches for a modifier set and deletes it if it exists.
     * @return void
     */

    public void theUserSearchesAndDeleteTheModifierSets() {
        boolean isModifierPresent = theUserSearchForASpecificModifierSetsAndReturnTheResult();
        if (!(isModifierPresent)) {
           LOGGER.info("Modifier Sets not present. Cannot delete.");
            return;
        }
       clickDynamicElement(MenuLocators.SPECIFIC_DELETE_BUTTON, BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue(), 10000);
       click(MenuLocators.POPUP_DELETE, 10000);
        boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("MODIFIER_SET_DELETE_MESSAGE").getValue(), 5000);
        assertTrue(visible, "modifier set is available.");
        LOGGER.info("Modifiers set deleted successfully");
        theUserSearchForASpecificModifierSetsAndReturnTheResult();
    }

}