package com.tonic.pageObjects.web;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tonic.common.web.BasePage;
import com.tonic.enums.BOHConstants;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import com.tonic.pageObjects.web.menu.MenuLocators;
import com.tonic.utils.ApplicationUtils;
import org.testng.Assert;

import java.util.List;

import static org.testng.Assert.assertTrue;


public class MenuConfigurationPage extends BasePage {
    private ApplicationUtils apputils;
    private String seperator ="of";
    private String categoryName;

    // =============== Constructor ===============

    public MenuConfigurationPage(Page page) {
        super(page);
    }

    // =============== Methods ===============


    public void selectRandomFromCustomDropdown(String dropdownToggleSelector, String optionsSelector) {
        clickElement(dropdownToggleSelector, 1000);
        waitForSelector(optionsSelector, 1000);
        List<String> options = getVisibleElementsText(optionsSelector, 1000);
        if (options.isEmpty()) {
            System.out.println("No options found in dropdown.");
            return;
        }
        String randomOption=apputils.getRandomElementFromList(options);
        getElementByText(MenuLocators.SPECIFIC_ITEM,randomOption,5000).first().click();

    }

    public String getCategoriesCount(){
        String menuCategoriesCount= getTextOfElement(MenuLocators.MENU_CATEGORY_COUNT, 10000).split(seperator)[1].trim();
        return menuCategoriesCount;
    }

    /**
     * Searches for a specific category and returns whether it's visible in results.
     * @return {true} if category is visible after search, {false} otherwise
     */

    public boolean isCategoryVisibleAfterSearch() {
        categoryName = BOHDataConstants.fromKey("CATEGORY_NAME").getValue();
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MENU_CONFIGURATION").getValue()), 10000);
        enterText(buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, ButtonConstants.fromKey("SEARCH_HERE").getValue()), categoryName, 10000);
        return isVisible(MenuLocators.PROMPT, categoryName, 5000);
    }

    /**
     * Searches for a category and creates it if it doesn't already exist.
     * @return void
     */

    public void userSearchesAndCreatesACategory() {
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MENU_CONFIGURATION").getValue()), 10000);
        boolean categoryExist = isCategoryVisibleAfterSearch();
        if (!categoryExist) {
            // Click on 'Add Category' icon
            clickElement(MenuLocators.ADD_NEW_CATEGORY_BUTTON, 5000);
            // Enter the category name
            enterText(buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, BOHConstants.fromKey("ENTER_CATEGORY_NAME").getValue()), categoryName, 10000);
            clickDynamicElement(MenuLocators.STORE_TOGGLE, ButtonConstants.fromKey("STORE_TOGGLE").getValue(), 7000);
            clickDynamicElement(MenuLocators.ONLINE_ORDER_TOGGLE, ButtonConstants.fromKey("ONLINE_ORDER_TOGGLE").getValue(), 7000);
            // Click on Save
            clickDynamicElement(MenuLocators.SAVE_BUTTON_IN_PREMODIFIER_MODAL, ButtonConstants.fromKey("SAVE").getValue(), 3000);
            boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("SUCCESSFUL_CATEGORY_CREATION_MESSAGE").getValue(), 5000);
            assertTrue(visible, "Category is not available.");
            LOGGER.info("New Category Added Successfully");
        }

    }

    /**
     * Checks if a category exists and deletes it if found.
     * @return void
     */

    public void userChecksAndDeletesTheCategory() {
        String itemName = BOHDataConstants.fromKey("ITEM_NAME").getValue();
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MENU_CONFIGURATION").getValue()),5000);
        isCategoryVisibleAfterSearch();
        if((isCategoryVisibleAfterSearch())){
            clickDynamicElement(MenuLocators.SPECIFIC_CATEGORY_VIEW_ICON, categoryName, 10000);
            Locator item = getElementByText(MenuLocators.DELETE_ITEM_CTA, itemName, 10000).first();
            click(item,5000);
            clickDynamicElement(MenuLocators.ADD_NEW_CTA, ButtonConstants.fromKey("YES_DELETE").getValue(), 10000);
            reloadPage();
            isCategoryVisibleAfterSearch();
            clickDynamicElement(MenuLocators.KEBAB_MENU_FOR_SPECIFIC_CATEGORY, categoryName, 7000);
            clickDynamicElement(MenuLocators.DELETE_CTA_FOR_SPECIFIC_CATEGORY, categoryName, 5000);
            String textBoxInConfirmationModal = buildDynamicLocator(MenuLocators.TEXT_BOX_INPUT_FIELD, BOHConstants.fromKey("CONFIRM").getValue());
            fillField(textBoxInConfirmationModal,categoryName, 10000);
            clickDynamicElement(MenuLocators.POP_UP_MESSAGE, ButtonConstants.fromKey("YES").getValue(), 3000);
            boolean visible = isVisible(MenuLocators.POP_UP_MESSAGE, BOHConstants.fromKey("CATEGORY_DELETE_MESSAGE").getValue(), 5000);
            assertTrue(visible, "Category is available.");
        }
    }

    /**
     * Adds an item to a specific category if it's not already present.
     * @return void
     */

    public void theUserAddingAnItemToTheCategory() {
        String itemName = BOHDataConstants.fromKey("ITEM_NAME").getValue();
        waitForSelector(buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("MENU_CONFIGURATION").getValue()),10000);
        isCategoryVisibleAfterSearch();
        clickDynamicElement(MenuLocators.SPECIFIC_CATEGORY_VIEW_ICON, categoryName, 10000);
        String specificItemInCategoryEle = buildDynamicLocator(MenuLocators.SPECIFIC_ITEM_CATEGORY, itemName);
        if(!isVisible(specificItemInCategoryEle, BOHConstants.fromKey("SPECIFIC_ITEM_ELEMENT").getValue() + itemName)) {
            clickDynamicElement(MenuLocators.ADD_BUTTONS, ButtonConstants.fromKey("ADD_NEW_ITEM").getValue(), 10000);
            click(MenuLocators.SELECT_ITEM, 10000);
            Locator item = getElementByText(MenuLocators.SPECIFIC_SALESGROUP, itemName, 5000).last();
            click(item,7000);
            click(MenuLocators.SAVE_BUTTON, 5000);
            assertTrue(isVisible(buildDynamicLocator(MenuLocators.SPECIFIC_ITEM_CATEGORY, itemName), BOHConstants.fromKey("SPECIFIC_ITEM_ELEMENT").getValue() + itemName), "Item '" + itemName + "' was not added to the category '" + categoryName + "'.");
            LOGGER.info("Item" + itemName + " was successfully added to category" + categoryName + ".");
        }
       click(MenuLocators.MENU_CATEGORY_POPUP_CLOSE_BUTTON, 7000);
    }
}
