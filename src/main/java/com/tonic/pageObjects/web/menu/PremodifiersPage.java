package com.tonic.pageObjects.web.menu;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tonic.common.web.BasePage;
import com.tonic.enums.BOHConstants;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertTrue;

public class PremodifiersPage extends BasePage{

    private String preModifierGroupName;

    // =============== Constructor ===============
    public PremodifiersPage(Page page) {
        super(page);
    }

    // =============== Methods ===============

    /**
     * Searches for a specific premodifier group and returns whether it's visible in results.
     * @return {true} if premodifier group is visible after search, {false} otherwise
     */
    public boolean isPremodifierVisibleAfterSearch() {
        preModifierGroupName = BOHDataConstants.fromKey("PREMODIFIER_GROUP_NAME").getValue();
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("PREMODIFIERS").getValue()), 10000);
        enterText(buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, ButtonConstants.fromKey("SEARCH_HERE").getValue()), preModifierGroupName, 10000);
        return isVisible(MenuLocators.PROMPT, preModifierGroupName, 10000);
    }

    /**
     * Assigns a modifier set to a premodifier group.
     * Searches for the premodifier group, clicks edit, selects the specified
     * modifier set from the dropdown, and saves the changes.
     * @return void
     */

    public void userAssignsModifierSetToPreModifierGroup() {
        String modifiersSet = BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue();
        reloadPage();
        isPremodifierVisibleAfterSearch();
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("PREMODIFIERS").getValue()),20000);
        String specificPreModifierEle = buildDynamicLocator(MenuLocators.SPECIFIC_PRE_MODIFIER_GROUP_EDIT_DELETE, preModifierGroupName,ButtonConstants.fromKey("EDIT_BUTTON").getValue());
        click(specificPreModifierEle, 10000);
        clickDynamicElement(MenuLocators.MODIFIER_SET_INPUT_FIELD, ButtonConstants.fromKey("MODIFIERS_SETS").getValue(),10000);
        clickDynamicElement(MenuLocators.SPECIFIC_MODIFIER_SET, modifiersSet, 10000);
        try {
            click(MenuLocators.OUTSIDE_AREA, 2000);
        } catch (Exception e) {
            click(0, 0);
        }
        click(MenuLocators.PRE_MODIFIER_POP_UP_SAVE, 10000);
        boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("PREMODIFIER_UPDATE_SUCCESS_MESSAGE").getValue(), 5000);
        assertTrue(visible, "Pre modifier group is not updated successfully");
    }

    /**
     * Searches for a premodifier group and creates it if it doesn't already exist.
     * Checks if the premodifier group exists first. If not present:
     * - Clicks Add New button for premodifier group
     * - Enters group name, premodifier name, online order name, and kitchen name
     * - Selects "Post" from the dropdown
     * - Saves both the premodifier and the group
     * - Verifies successful creation
     *
     * @return void
     */

    public void theUserSearchesAndCreatesAPreModifier() {
        String preModifierName = BOHDataConstants.fromKey("PREMODIFIER_NAME").getValue();
        String OnlineOrder = BOHDataConstants.fromKey("ONLINE_ORDER").getValue();
        String KitchenName = BOHDataConstants.fromKey("KITCHEN_NAME").getValue();

        isPremodifierVisibleAfterSearch();
        if(!(isVisible(MenuLocators.PROMPT, preModifierGroupName, 5000))){

            //Click on the add new pre modifier group
            clickDynamicElement(MenuLocators.ACTION_BUTTON, ButtonConstants.fromKey("ADD_NEW_BUTTON").getValue(), 5000);
            assertTrue(isVisible(MenuLocators.CONFIGURE_PRE_MODIFIER_GROUP_TEXT, BOHConstants.fromKey("CONFIGURE_PRE_MODIFIER_GROUP").getValue()));
            String PreModGroupNameFieldLocator = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, BOHConstants.fromKey("ENTER_PRE_MODIFIER_GROUP_NAME").getValue());
            enterText(PreModGroupNameFieldLocator, preModifierGroupName, 1000);
            //Click on the add new pre modifier
            clickDynamicElement(MenuLocators.ADD_NEW_CTA_IN_PREMODIFIER_MODAL, ButtonConstants.fromKey("ADD_NEW_BUTTON").getValue(), 1000);
            String PreModNameFieldLocator = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, BOHConstants.fromKey("ENTER_PRE_MODIFIER_NAME").getValue());
            enterText(PreModNameFieldLocator, preModifierName, 1000);
            String OnlineOrderFieldLocator = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, BOHConstants.fromKey("ENTER_ONLINE_ORDERING_NAME").getValue());
            enterText(OnlineOrderFieldLocator, OnlineOrder, 1000);
            String KitchenNameFieldLocator = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, BOHConstants.fromKey("ENTER_KITCHEN_NAME").getValue());
            enterText(KitchenNameFieldLocator, KitchenName, 1000);
            click(MenuLocators.PRE_POST_DROPDOWN, 5000);
            String prePostEle = buildDynamicLocator(MenuLocators.PRE_POST_SELECTION, ButtonConstants.fromKey("POST").getValue());
            clickDynamicElement(prePostEle, ButtonConstants.fromKey("POST").getValue(), 1000);
            clickDynamicElement(MenuLocators.PRE_MODIFIER_SAVE_BUTTON, ButtonConstants.fromKey("SAVE").getValue(), 1000);
            //Click on save premodifier group
            clickDynamicElement(MenuLocators.SAVE_BUTTON_IN_PREMODIFIER_MODAL, ButtonConstants.fromKey("SAVE").getValue(), 5000);
            boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("SUCCESSFUL_PREMODIFIER_CREATION_MESSAGE").getValue(), 5000);
            assertTrue(visible, "Pre-Modifier Group is not available.");
            LOGGER.info("Successfully added the pre-modifier group.");
            isPremodifierVisibleAfterSearch();
        }
    }

    /**
     * Deletes a premodifier and its associated group.
     * @return void
     */

    public void theUserDeletesAPreModifier() {
        String PreModifierName = BOHDataConstants.fromKey("PREMODIFIER_NAME").getValue();
        isPremodifierVisibleAfterSearch();
        if ((isVisible(MenuLocators.SPECIFIC_PREMODIFIER_NAME, preModifierGroupName, 5000))) {
            String specificPreModifierEle = buildDynamicLocator(MenuLocators.SPECIFIC_PRE_MODIFIER_GROUP_EDIT_DELETE, preModifierGroupName,ButtonConstants.fromKey("EDIT_BUTTON").getValue());
            click(specificPreModifierEle, 5000);
            //Search Pre-Modifier
            enterText(buildDynamicLocator(MenuLocators.PRE_MODIFIER_POP_UP_SEARCH, ButtonConstants.fromKey("SEARCH_HERE").getValue()), PreModifierName, 5000);
            isVisible(MenuLocators.PRE_MODIFIER_POP_UP_NAME, PreModifierName, 5000);
            if ((isVisible(MenuLocators.PRE_MODIFIER_POP_UP_NAME, PreModifierName, 5000))) {
                clickDynamicElement(MenuLocators.PRE_MODIFIER_POP_UP_DELETE, PreModifierName, 5000);
                clickDynamicElement(MenuLocators.PRE_MODIFIER_POP_UP_SAVE, ButtonConstants.fromKey("SAVE").getValue(), 10000);
            }
            isPremodifierVisibleAfterSearch();
            //Delete the Pre-Modifier Group
            String specificPreModifierDeleteEle = buildDynamicLocator(MenuLocators.SPECIFIC_PRE_MODIFIER_GROUP_EDIT_DELETE, preModifierGroupName,ButtonConstants.fromKey("DELETE_BUTTON").getValue());
            click(specificPreModifierDeleteEle, 5000);
            clickDynamicElement(MenuLocators.POPUP_DELETE, preModifierGroupName, 10000);
            boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("SUCCESSFUL_DELETE_MESSAGE").getValue(), 5000);
            assertTrue(visible, "Pre-Modifier Group is available.");
            LOGGER.info("Successfully deleted the pre modifier group from the list");
        }
    }
}
