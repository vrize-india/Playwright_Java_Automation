package com.tonic.stepDefinitions.web.menu;

import com.microsoft.playwright.Page;
import com.tonic.enums.BOHDataConstants;
import com.tonic.enums.ButtonConstants;
import com.tonic.factory.PlaywrightFactory;
import com.tonic.pageObjects.web.menu.MenuLocators;
import com.tonic.pageObjects.web.menu.PremodifiersPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PremodifiersDualPricingDisabledSteps {

    private Page page = PlaywrightFactory.getPage();
    private final PremodifiersPage premodifiers = new PremodifiersPage(page);

    @When("The user click on {string} CTA in premodifiers modal")
    public void theUserClickOnCTAInPremodifiersModal(String addNewButton) {
        premodifiers.clickDynamicElement(MenuLocators.ADD_NEW_CTA_IN_PREMODIFIER_MODAL,addNewButton,1000);
    }

    @And("The user searches for the existing Pre Modifier Group")
    public void theUserSearchesForTheExistingPreModifierGroup() {
        premodifiers.isPremodifierVisibleAfterSearch();
    }

    @When("The user clicks on the edit icon for the existing Pre Modifier Group")
    public void theUserClicksOnTheEditIconForTheExistingPreModifierGroup() {
        String preModifierGroupName = BOHDataConstants.fromKey("PREMODIFIER_GROUP_NAME").getValue();
        premodifiers.waitForSelector(premodifiers.buildDynamicLocator(MenuLocators.PAGE_HEADER, ButtonConstants.fromKey("PREMODIFIERS").getValue()),8000);
        String specificPreModifierEle = premodifiers.buildDynamicLocator(MenuLocators.SPECIFIC_PRE_MODIFIER_GROUP_EDIT_DELETE, preModifierGroupName,ButtonConstants.fromKey("EDIT_BUTTON").getValue());
        premodifiers.click(specificPreModifierEle, 5000);
    }
}
