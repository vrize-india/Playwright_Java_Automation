package com.tonic.stepDefinitions.web.inventory;

import com.tonic.factory.PlaywrightFactory;
import com.tonic.pageObjects.web.inventory.InventoryPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class VendorManagementSteps {

    private final InventoryPage inventoryPage;

    public VendorManagementSteps() {
        this.inventoryPage = new InventoryPage(PlaywrightFactory.getPage());
    }

    @Given("the user is logged in to the BOH system")
    public void userIsLoggedIn() {
        // Login steps should be handled elsewhere if needed
    }

    @When("the user navigates to {string} on the top-level navigation")
    public void navigateToTopLevelMenu(String menuItem) {
        inventoryPage.clickApps();
    }

    @When("the user clicks on {string}")
    public void clickOnMenuItem(String menuItem) {
        inventoryPage.clickOnInventory();
    }

    @Then("the inventory page should display with information and options")
    public void verifyInventoryPageDisplayed() {
        Assert.assertTrue(inventoryPage.isIngredientsTabSelected(), "Inventory page should be visible with Ingredients tab selected");
    }

    @And("the {string} tab should be pre-selected by default")
    public void verifyTabSelected(String tabName) {
        if (tabName.equalsIgnoreCase("Ingredients")) {
            Assert.assertTrue(inventoryPage.isIngredientsTabSelected(), "Ingredients tab should be selected");
        }
    }

    @When("the user clicks on the {string} tab")
    public void clickOnTab(String tabName) {
        if (tabName.equalsIgnoreCase("Vendors")) {
            inventoryPage.clickVendorsTab();
        }
    }

    @Then("the vendor list should be displayed")
    public void verifyVendorListDisplayed() {
        Assert.assertTrue(inventoryPage.isVendorsTabSelected(), "Vendors tab should be selected");
    }

    @When("the user clicks on the {string} icon labeled {string} at the top right corner of the page")
    public void clickAddVendor(String icon, String label) {
        inventoryPage.clickAddVendor();
    }

    @Then("the {string} modal window should appear")
    public void verifyModalWindowAppears(String modalTitle) {
        // Add modal window visibility check if implemented
    }

    @When("the user enters all required information in the Name field")
    public void enterVendorName() {
        inventoryPage.enterVendorDetails();
    }

    @When("the user enters all required information in the Address field")
    public void enterVendorAddressDetails() {
        inventoryPage.expandAddressSectionAndAddDetails();
    }

    @When("the user enters all required information in the Phone Number field")
    public void enterVendorPhoneNumberDetails() {
        inventoryPage.expandPhoneNumberSectionAndAddDetails();
    }

    @When("the user enters all required information in the Contact Number field")
    public void enterVendorContactNumberDetails() {
        inventoryPage.expandContactSectionAndAddDetails();
    }

    @And("clicks the Save button on the modal window")
    public void clickSaveButton() {
        inventoryPage.clickSave();
    }

    @And("a confirmation message Vendor added should be displayed")
    public void verifyConfirmationMessage() {
        Assert.assertTrue(inventoryPage.isSuccessMessageDisplayed(), "Vendor confirmation message not displayed");
        Assert.assertTrue(inventoryPage.isVendorVisible("Ramu1"), "Vendor 'Ramu1' is not visible in the list");
    }
}
