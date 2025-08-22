package com.tonic.pageObjects.web.menu;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tonic.common.web.BasePage;
import com.tonic.enums.BOHConstants;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import com.tonic.utils.ApplicationUtils;
import io.cucumber.java.en.And;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertTrue;

public class ItemsPage extends BasePage {
    private final Page page;

    public ItemsPage(Page page) {
        super(page);
        this.page = page;
    }
    private String salesGroup;
    private String itemSet;

    // =============== Methods ===============

    /**
     * Randomly selects a visible modifier set from the list.
     * @param modifierSetListLocator Locator to fetch all visible modifier set options.
     */
    public void chooseModifierSet(String modifierSetListLocator) {
        List<String> modifierSetList = getVisibleElementsText(modifierSetListLocator, 2000);
        getElementByText(SPECIFIC_MODIFIERSET,ApplicationUtils.getRandomElementFromList(modifierSetList),5000).first().click();
        try {
            click(OUTSIDE_AREA, 2000);
        } catch (Exception e) {
           click(0, 0);
        }
    }

    /**
     * Toggles the item state (enabled <-> disabled)
     * @param expectedStateAfterClick true if toggle should be enabled after click, false if disabled
     * @param locator  toggle locator
     * @return
     */
    public boolean validateItemToggleState(boolean expectedStateAfterClick, String locator, int timeoutMillis) {
        page.waitForTimeout(2000);
        boolean toggleStatus = isChecked(locator,timeoutMillis);
        return toggleStatus ==expectedStateAfterClick;
    }

    /**
     * Returns a map of item names and their corresponding visibility status for a given dynamic locator template.
     * This method is useful when verifying the visibility of elements (e.g., toggle buttons, delete icons,edit icons)
     * associated with each item in a list. It dynamically builds locators using the provided template and item names,
     * checks visibility for each, and stores the result in a map.
     * @param specificItemEle The dynamic locator template (e.g., XPath or CSS)
     * @return A Map<String, Boolean> where the key is the item name and the value is  true if the element
     *  is visible for that item,  false otherwise.
     */
    public Map<String, Boolean> getElementVisibilityMapForEachItem(String specificItemEle,String elementDescription) {
        List<String> itemNames = getVisibleElementsText(ITEM_LIST, 5000);
        Map<String, Boolean> visibilityMap = new LinkedHashMap<>();
        for (String itemName : itemNames) {
            String locator = buildDynamicLocator(specificItemEle, itemName);
            boolean isVisible = isVisible(locator, elementDescription+" for "+itemName);
            visibilityMap.put(itemName, isVisible);
        }
        return visibilityMap;
    }

    /**
     * Checks whether a specific element is visible after performing a search.
     * The method performs the following steps:
     * 1. Reloads the page to refresh the UI state.
     * 2. Enters the provided search text into the specified input field.
     * 3. Returns whether the specific element is currently visible on the page.
     * * @param inputFieldLocator     The dynamic locator (XPath or CSS) for the input field used to perform the search.
     *  * @param specificElementLocator  The locator for the element whose visibility is being verified.
     *  * @param text                    The text to input in the search field
     *  * @param timeout                Maximum wait time (in milliseconds) to wait for the element to detach.
     *  * @return                        true if the element is still visible after the search,  false otherwise.
     **/

    public boolean isElementVisibleAfterSearch(String inputFieldLocator, String specificElementLocator,String text, int timeout) {
        reloadPage();
        enterText(inputFieldLocator, text, timeout);
        return page.locator(specificElementLocator).isVisible();
    }

    /**
     * Performs a search for a specific item and returns whether it's visible in results.
     * @return {true} if item is visible after search, {false} otherwise
     */

    public boolean isItemVisibleAfterSearch() {
        itemSet = BOHDataConstants.fromKey("ITEM_NAME").getValue();
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("ITEMS").getValue()), 10000);
        enterText(buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, ButtonConstants.fromKey("SEARCH_HERE").getValue()), itemSet, 20000);
        return isVisible(MenuLocators.SPECIFIC_ITEM_NAME, itemSet, 10000);
    }

    /**
     * Adds a new item if it doesn't already exist in the system.
     * Checks if the item exists first, and if not, creates it
     * @return void
     */

    public void theUserAddsAnItem() {
        itemSet = BOHDataConstants.fromKey("ITEM_NAME").getValue();
        boolean itemExist = isItemVisibleAfterSearch();
        if(!itemExist) {
            //User clicks on Add New CTA
            clickDynamicElement(MenuLocators.ADD_NEW_CTA, ButtonConstants.fromKey("ADD_NEW_BUTTON").getValue(), 5000);
            waitForSelector(MenuLocators.ITEM_LIST, 10000);
            String itemNameFieldLocator = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, BOHConstants.fromKey("ADD_NEW_ITEMS").getValue());
            //User Entering the Item Name in the Add New Item Text Box
            enterText(itemNameFieldLocator, itemSet, 1000);
            //user Select the Sales Group from the List
            click(MenuLocators.SALES_GROUP_INPUT_FIELD, 10000);
            List<String> salesGroupList = getVisibleElementsText(MenuLocators.LIST_OF_SALESGROUPS, 2000);
            salesGroup = ApplicationUtils.getRandomElementFromList(salesGroupList);
            Locator item = getElementByText(MenuLocators.SPECIFIC_SALESGROUP, salesGroup, 5000).first();
            click(item,7000);
            //User Clicks on Save Button
            click(MenuLocators.SAVE_BUTTON, 5000);
            boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("SUCCESSFUL_ITEM_CREATION_MESSAGE").getValue(), 5000);
            assertTrue(visible, "Item is not available.");
            LOGGER.info("Successfully added the Item to the list");
            isItemVisibleAfterSearch();
        }
    }

    /**
     * Deletes an existing item from the system.
     * Verifies the item exists, clicks the delete button, confirms deletion
     * @return void
     */

    public void theUserDeletesAnItem() {
        isItemVisibleAfterSearch();
        if((getVisibleElementsText(MenuLocators.ITEM_LIST, 10000).contains(itemSet))){
            clickDynamicElement(MenuLocators.SPECIFIC_DELETE_BUTTON, itemSet, 7000);
            assertTrue(getElementByText(BOHConstants.fromKey("DELETE_ITEM").getValue()).isVisible(), "Item Added message not displayed");
            clickDynamicElement(MenuLocators.ADD_NEW_CTA, ButtonConstants.fromKey("YES_DELETE").getValue(), 5000);
            boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("ITEM_DELETE_MESSAGE").getValue(), 5000);
            assertTrue(visible, "Item available.");
            LOGGER.info("Item deleted successfully");
            isItemVisibleAfterSearch();
        }
        isItemVisibleAfterSearch();
    }

    /**
     * Adds modifier sets to a specific item if not already assigned.
     * @return void
     */

    public void theUserAddsModifierSetsToAnItem() {
        itemSet = BOHDataConstants.fromKey("ITEM_NAME").getValue();
        String ModifiersSet = BOHDataConstants.fromKey("MODIFIER_SETS_NAME").getValue();
        isItemVisibleAfterSearch();
        String specificModiferSetEle = buildDynamicLocator(MenuLocators.SPECIFIC_MODIFIERS_SPECIFIC_ITEM, itemSet, ModifiersSet);
        if (!isVisible(specificModiferSetEle, BOHConstants.fromKey("SPECIFIC_MODIFIER_ELEMENT").getValue())) {
            clickDynamicElement(MenuLocators.SPECIFIC_ITEM_EDIT_BUTTON, itemSet, 10000);
            click(MenuLocators.MODIFIERSET_INPUT_FIELD, 10000);
            clickDynamicElement(MenuLocators.SPECIFIC_MODIFIERSET, ModifiersSet, 10000);
            clickDynamicElement(MenuLocators.MODIFIERS_SET_REQUIRED_TOGGLE, ModifiersSet, 10000);
            try {
                click(MenuLocators.OUTSIDE_AREA, 2000);
            } catch (Exception e) {
                click(0, 0);
            }
            click(MenuLocators.SAVE_BUTTON, 10000);
        }
    }

}