package com.tonic.pageObjects.web.menu;

public interface MenuLocators {

    //Following Locators are belongs to Modifiers Page
    String PAGE_HEADER = "//li[contains(@class,'breadcrumb-item')]//*[contains(text(),'%s')]";
    String TEXT_BOX_INPUT_FIELD = "//input[contains(@placeholder, '%s')]";
    String NUMBER_INPUT_FIELD = "//input[contains(@mattooltip, '%s')]";

    String SEARCH_BAR ="//*[contains(@class,'search-bar')]";
    String NEW_BLANK_ROW = "//tr[contains(@class,'editing')]";
    // Add new
    String ADD_NEW_CANCEL_BUTTON = "//button[contains(@class,'%s')]";
    //close and tarsh icon
    String ICON_WITH_DYNAMIC_VALUE = "//*[contains(@class,'%s')]";
    //Cancel and Save
    String TXT_BUTTON = "//button[contains(@mattooltip, '%s')]";
    String ADD_BUTTONS = "//*[contains(@id,'%s')]";
    String POP_UP_MESSAGE = "//*[contains(text(), '%s')]";
    String SPECIFIC_ELE = "//td[contains(text(),'%s')]";
    String OUTSIDE_AREA ="//div[contains(@class,'cdk-overlay-backdrop')]";
    String ACTION_BUTTON = "//*[text()='%s']";

    String SAVE_BUTTON = "//button[contains(@class,'save-btn')]";
    String SPECIFIC_DELETE_BUTTON = "//*[text()='%s']//ancestor::tr//button[contains(@class,'delete')]";
    String POPUP_DELETE = "//*[contains(text(),'Delete')]//parent::button";

    String INLINE_EDITOR = "//tr[contains(@class,'editing')]";
    //Fetching this locator from MenuConfiguration Page
    String SPECIFIC_ITEM_CATEGORY = "//td[text()='%s']";


    
    //Following Locators which Belongs to Premodifiers Page
    String ADD_NEW_CTA_IN_PREMODIFIER_MODAL = "//*[@id='add-new-btn']";
    String PRE_MODIFIER_SAVE_BUTTON = "//*[@id='pre-modifier-save-button']";
    String SAVE_BUTTON_IN_PREMODIFIER_MODAL = "//*[@id='save-btn']";
    String PRE_MODIFIER_GROUP_LIST = "//td[@class='pre-modifier-group']";
    String PRICING_TYPE_LIST = "//*[@id='pre-modifier-table']/tbody/tr/td/button";
    String PRICING_TYPES = "//*[local-name()='svg' and contains(@class, '%s')]";
    String CONFIGURE_PRE_MODIFIER_GROUP_TEXT = "//*[text()='Configure Pre-Modifier Group']";
    String PRE_MODIFIER_HEADER = "//table[contains(@id,'pre-modifier-table')]";
    String PRE_POST = "//div[contains(@class,'mat-select-arrow')]";
    String PRE_POST_DROPDOWN = PRE_MODIFIER_HEADER+PRE_POST+"//parent::div[contains(@class,'mat-select-arrow')]";
    String PRE_POST_SELECTION = "//span[contains(@class, 'mat-option-text') and text()='%s']";
    String ONLINE_ORDERING_CHECKBOX = "//*[@type='checkbox']";
    String PERCENTAGE_INPUT = "//*[@type='number']";
    String SPECIFIC_PREMODIFIER_NAME = "//td/span[text()='%s']";
    String PRE_MODIFIERS_HEADER_LIST = "//th[contains(@id,'pre-modifier-table-header')]";
    String NO_RESULTS_FOUND_TEXT = "//div[@id='no-results-text' and contains(text(), 'No Results Found')]";
    String ACTION_BUTTONS = "//span[text()='%s']/ancestor::tr//td[contains(@id,'actions')]//button";
    String SPECIFIC_PRE_MODIFIER_GROUP_EDIT_DELETE = ACTION_BUTTONS+"[contains(@id,'%s-button')]";
    //Pre-modifier pop-up search locator
    String  PRE_MODIFIER_POP_UP_SEARCH = "//input[@id='search-bar'][contains(@placeholder, '%s')]";
    String PRE_MODIFIER_POP_UP_NAME = "//td[@id='pre-modifier-name-column'][text()='%s']";
    String PRE_MODIFIER_POP_UP_DELETE = "//button[@id='pre-modifier-delete-button']";
    String PRE_MODIFIER_POP_UP_SAVE = "//button[@id='save-btn']";
    String PRE_MODIFIER_MODAL_HEADER = "//div[contains(@class,'modal-header')]";
    String MODIFIER_SET_INPUT_FIELD = PRE_MODIFIER_MODAL_HEADER+"//button[contains(@mattooltip,'%s')]";
    String LIST_OF_MODIFIER_SETS = "//div[contains(@class,'modifier-sets')]//span";
    String SPECIFIC_MODIFIER_SET = LIST_OF_MODIFIER_SETS+"[text()='%s']";
    String SPECIFIC_PREMODIFIER_EDIT_DELETE = SPECIFIC_ITEM_CATEGORY+"/ancestor::tr//button[contains(@id,'%s-button')]";



    //Following Locators which Belongs to Items Page
    String ITEM_ROW = "//td[contains(@class,'name ')]//span[text()='%s']//ancestor::tr";
    String ADD_NEW_CTA = "//span[text()='%s']//parent::button";
    String SALES_GROUP_INPUT_FIELD = "//td[contains(@class,'sales-group')]//mat-select";
    String LIST_OF_SALESGROUPS = "//*[contains(@role,'listbox')]//span";
    String SPECIFIC_SALESGROUP = LIST_OF_SALESGROUPS + "[normalize-space(text())='%s']";
    String MODIFIERSET_INPUT_FIELD = "//td[contains(@class,'modifier-sets ')]//div[contains(@class,'modifier')]//button";
    String LIST_OF_MODIFIERSETS = "//div[contains(@class,'modifier-item')]//span";
    String SPECIFIC_MODIFIERSET = LIST_OF_MODIFIERSETS+"[text()='%s']";
    String ITEM_LIST = "//td[contains(@class,'name')]//span";
    String SPECIFIC_ITEM_NAME = ITEM_LIST+"[text()='%s']";
    String SPECIFIC_ITEM_EDIT_BUTTON = ITEM_ROW+"//button[contains(@class,'delete')]//preceding-sibling::button";
    String AVAILABLE_MODIFIER_SETS_HEADER = "//*[text()='Available Modifier Sets']";
    String AVAILABLE_MODIFIER_SETS = AVAILABLE_MODIFIER_SETS_HEADER+"//parent::div//div[contains(@class,'modifier')]//span";
    String DELETE_ICON = "//*[contains(@class,'trash-icon')]";
    String SPECIFIC_ITEM_TOGGLE_BUTTON = ITEM_ROW+"//label";
    String NEWLY_CREATED_ITEM_TOGGLE = "//tr[contains(@class,'editing')]//label";
    String MODIFIER_SET = "/ancestor::div[contains(@class,'modifier')]";
    String MODIFIERS_SET_REQUIRED_TOGGLE = ACTION_BUTTON+MODIFIER_SET+"//label[contains(@class,'label')]";
    String SPECIFIC_MODIFIERS_SPECIFIC_ITEM = ACTION_BUTTON+"/ancestor::*//mat-chip[normalize-space(text())='%s']";

    //Modifier Locators
    String MODIFIERS_LIST = "//td[contains(@class,'mat-tooltip-trigger modifier text-truncate ng-star-inserted')]";
    String SPECIFIC_MODIFIER_EDIT = "//td[text()='%s']//parent::tr//td[contains(@class,'actions')]//button[not(contains(@class, 'delete'))]";
    String ERROR_LOCATOR = "//td[contains(text(),'%s')]/parent::tr//div[contains(@class,'error')]";
    String SPECIFIC_MODIFIERS_LIST = "//td[contains(@class,'modifier') and (text()='%s')]";


    //Following Locators are belongs to Menu Configuration Page
    String DROPDOWNTOGGLE_SELECTOR = "//*[@name='modifierName']/..//*[local-name()= 'svg']";
    String OPTION_SELECTOR= "//div[@role= 'listbox']//span";
    String SPECIFIC_ITEM = OPTION_SELECTOR + "[text()='%s']";
    String CANCEL_ICON_ADD_NEW_CATEGORY_POPUP = "//button[contains(@class, 'icons delete-button')]/following-sibling::button";
    String KEBAB_MENU_FOR_SPECIFIC_CATEGORY = "//span[text()='%s']/../following-sibling::div//*[local-name()='svg' and contains(@class, 'svg-eye-white-dims ml-2')]";
    String DELETE_CTA_FOR_SPECIFIC_CATEGORY ="//span[text()='%s']/../following::div[contains(@class, 'kebab-menu-action-btn')]//*[local-name()='svg' and contains(@class, 'svg-trash')]";
    String MENU_CATEGORY_COUNT = "//*[contains(@class,'range-actions')]/div";
    String ADD_NEW_CATEGORY_BUTTON = "//*[normalize-space(text())='Add new category']";
    String SEARCH_HERE_TEXT_BOX= "//app-search-input[not(@id='category-items-search')]//input";
    String STORE_TOGGLE = "//label[@for='store-toggle-input']";
    String ONLINE_ORDER_TOGGLE = "//div[contains(@id,'online-order-toggle')]//label";
    String SELECT_ITEM = "//input[@placeholder= 'Select Item']";
    String PROMPT = "//span[text()='%s']";
    String SPECIFIC_CATEGORY_VIEW_ICON = KEBAB_MENU_FOR_SPECIFIC_CATEGORY+"/preceding-sibling::*[local-name()='svg' and contains(@class, 'svg-eye-white-dims')]";
    String MENU_CATEGORY_POPUP_CLOSE_BUTTON = "//*[contains(@class,'delete')]//following-sibling::button";
    String DELETE_ITEM_CTA = "//button[contains(@id,'item-delete')]";
    String ADDITIONAL_CHARGES_TABLE="//table[contains(@class,'additional-charges')]";
    String COLUMN_HEADERS = ADDITIONAL_CHARGES_TABLE+"//th";

    //Following Locators are belongs to Combo Groups Page
    String ADD_OPTIONS ="//div[contains(@id,'combo-groups')]//*[contains(text(), '%s')]";
    String COMBO_LIST = "//td[contains(@class,'combo-groups-name')]";
    String AVAILABLE_OPTIONS_LIST ="//*[contains(@id,'options')]//div[contains(@class,'option')]";
    String DELETE_MODAL_POPUP ="//*[contains(@class,'confirmation-modal-content')]";
    String COMBO_ROW_ACTIONS = "//td[text()='%s']//parent::tr//td[contains(@Class,'actions')]";
    String SPECIFIC_COMBO_EDIT_BUTTON = COMBO_ROW_ACTIONS + "//button[not(contains(@Class, 'delete'))]";
    String SPECIFIC_COMBO_DELETE_BUTTON = COMBO_ROW_ACTIONS + "//button[contains(@Class, 'delete')]";

    //Following Locators are belongs to Sales Groups And Parent Groups Page
    String SALES_GROUP_LIST = "//td[contains(@class,'sales-groups')]";
    String SEAT_COUNT_CHECKBOX = "//input[contains(@type,'checkbox')]";
    String PARENT_GROUP_DROPDOWN_OPTION = "//*[contains(@class,'mat-option-text')]//*[contains(text(),'%s')]";
    String PARENT_GROUP_DROPDOWN_OPTIONS_LIST="//*[contains(@class,'mat-option-text')]//span";
    String SPECIFIC_SALES_GROUP_EDIT = "//td[contains(@Class,'sales') and text()='%s']//ancestor::tr//button//preceding-sibling::button";
    String CANCEL_BUTTON = "//button[contains(@class,'save-btn')]//preceding-sibling::button";
    String SPECIFIC_SALES_GROUP_DELETE = "//td[contains(@class,'sales-groups') and text()='%s']//ancestor::tr//button[contains(@class,'delete')]";
    String DELETE_MODAL_CONTENT ="//*[contains(@class,'delete-confirmation-modal-content')]";

    //Following Locators belongs to Modifier sets Page
    String SPECIFIC_MODIFIERS_SETS_LIST = "//td[contains(@class,'name') and (text()='%s')]";
    String ADD_NEW_MODIFIERS_DROPDOWN= "//mat-select[contains(@placeholder, '%s')]";
    String LIMIT_TEXT_BOX_INPUT_FIELD = "//input[contains(@mattooltip,'%s')]";
    String ADD_NEW_MODIFIERS_DROPDOWN_OPTION = "//*[contains(@class,'mat-option-text') and contains(text(),'%s')]";
    String ADD_NEW_MODIFIERS_DROPDOWN_LIST="//*[contains(@class,'mat-option-text')]";
    String MODIFIER_SETS_LIST = "//td[contains(@mattooltipclass,'custom-tooltip')]";

}
