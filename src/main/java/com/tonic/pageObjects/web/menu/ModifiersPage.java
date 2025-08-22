package com.tonic.pageObjects.web.menu;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tonic.common.web.BasePage;
import com.tonic.enums.BOHConstants;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.utils.FileUtils;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ModifiersPage extends BasePage {


    public ModifiersPage(Page page) {
        super(page);
    }
    private Page page = PlaywrightFactory.getPage();
    private  String modifierName;

// =============== Methods ===============

    public List<String> isModifiersListVisible() {
        List<String> listOfModifiers;
        listOfModifiers = getVisibleElementsText(MODIFIERS_LIST,20000);
        waitForSelector(MenuLocators.MODIFIERS_LIST,5000);
        return listOfModifiers;
    }

    public List<Boolean> isEditIconVisibleForAllRows() {
        List<String> modifierList = isModifiersListVisible();
        List<Boolean> isVisible = new ArrayList<>();

        for (String modifierName : modifierList) {
            String xpath = String.format(SPECIFIC_MODIFIER_EDIT, modifierName);
            boolean visible = isVisible(xpath, "Edit Icon for " + modifierName);

            isVisible.add(visible); // collect result
            if (!visible) {
                System.out.println("Edit Icon is not visible for " + modifierName);
            }
        }

        return isVisible;
    }

    public void enterTheMandatoryDetailOnAddModifiers(String modifierName, String modifierNameField, String displayName, String displayNameField, String onlineOdering, String onlineOrderingField, String multiplier, String multiplierField, String prepTIme, String prepTimeField, String price, String priceField) {

            String modifierNameEle = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,modifierNameField);
            String displayNameEle = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,displayNameField);
            String onlineOrderingEle = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD,onlineOrderingField);
            String multiplierEle = buildDynamicLocator(MenuLocators.NUMBER_INPUT_FIELD,multiplierField);
            String preparationTimeEle = buildDynamicLocator(MenuLocators.NUMBER_INPUT_FIELD,prepTimeField);
            String priceEle = buildDynamicLocator(MenuLocators.NUMBER_INPUT_FIELD,priceField);
            fillField(modifierNameEle,modifierName,3000);
            fillField(displayNameEle,displayName,3000);
            fillField(onlineOrderingEle,onlineOdering,3000);
            fillField(multiplierEle,multiplier,3000);
            fillField(preparationTimeEle,prepTIme,3000);
            fillField(priceEle,price,3000);
    }

    /**
     * Searches for a specific modifier and returns whether it's visible in results.
     * @return {true} if modifier is visible after search, {false} otherwise
     */

    public boolean isModifiersVisibleAfterSearch() {
        modifierName = BOHDataConstants.fromKey("MODIFIER_NAME").getValue();
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MODIFIERS").getValue()), 10000);
        enterText(buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, ButtonConstants.fromKey("SEARCH_HERE").getValue()), modifierName, 20000);
        String modifierNameEle = buildDynamicLocator(MenuLocators.SPECIFIC_MODIFIERS_LIST,modifierName);
        return isVisible(modifierNameEle, modifierName);
    }

    /**
     * Searches for a modifier and adds it if it doesn't already exist.
     * @return void
     */

    public void theUserSearchAndAddTheModifiers() {
        boolean isModifierPresent = isModifiersVisibleAfterSearch();
        if (isModifierPresent) {
         LOGGER.info("Modifier already exists. Skipping add process.");
            return;
        }
        clickDynamicElement(MenuLocators.ACTION_BUTTON,ButtonConstants.fromKey("ADD_NEW_BUTTON").getValue(),5000);
        enterTheMandatoryDetailOnAddModifiers(
                BOHDataConstants.fromKey("MODIFIER_NAME").getValue(),
                BOHConstants.fromKey("MODIFIER_FIELD").getValue(),
                BOHConstants.fromKey("MODIFIER_DISPLAY_NAME").getValue(),
                BOHConstants.fromKey("MODIFIER_DISPLAY_FIELD").getValue(),
                BOHDataConstants.fromKey("MODIFIER_ONLINE_ORDERING_NAME").getValue(),
                BOHConstants.fromKey("MODIFIER_ONLINE_ORDERING_FIELD").getValue(),
                BOHDataConstants.fromKey("MODIFIER_Multiplier").getValue(),
                BOHConstants.fromKey("MODIFIER_Multiplier_FIELD").getValue(),
                BOHDataConstants.fromKey("MODIFIER_PREPARATION_TIME").getValue(),
                BOHConstants.fromKey("MODIFIER_PREPARATION_TIME_FIELD").getValue(),
                BOHDataConstants.fromKey("MODIFIER_PRICE").getValue(),
                BOHConstants.fromKey("MODIFIER_PRICE_FIELD").getValue());
        clickDynamicElement(MenuLocators.TXT_BUTTON,ButtonConstants.fromKey("SAVE").getValue(),10000);
        boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("SUCCESSFUL_MODIFIER_CREATION_MESSAGE").getValue(), 5000);
        assertTrue(visible, "modifier is not available.");
        LOGGER.info("Successfully added modifier to the list");
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MODIFIERS").getValue()),10000);
        enterText(MenuLocators.SEARCH_BAR,BOHDataConstants.fromKey("MODIFIER_NAME").getValue(),10000);
    }

    /**
     * Searches for a modifier and deletes it if it exists.
     * @return void
     */

    public void theUserSearchAndDeleteTheModifiers() {
        boolean isModifierPresent = isModifiersVisibleAfterSearch();
        if (!isModifierPresent) {
           LOGGER.info("Modifier not found, Skipping delete process.");
            return;
        }
        clickDynamicElement(MenuLocators.SPECIFIC_DELETE_BUTTON, BOHDataConstants.fromKey("MODIFIER_NAME").getValue(), 15000);
        click(MenuLocators.POPUP_DELETE, 10000);
        boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("MODIFIER_DELETE_MESSAGE").getValue(), 5000);
        assertTrue(visible, "Modifier available.");
        LOGGER.info("Modifier deleted successfully");
        isModifiersVisibleAfterSearch();
    }

}
