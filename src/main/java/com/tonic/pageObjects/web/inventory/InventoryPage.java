package com.tonic.pageObjects.web.inventory;

import com.microsoft.playwright.Page;
import com.tonic.common.web.BasePage;

public class InventoryPage extends BasePage {

    private final String appspage = "//li[contains(@class,'nav-item')]//a[text()='Apps']";
    private final String inventorypage = "//span[contains(text(),'Inventory')]";

    private final String ingredientsTab = "//mat-icon[text()=' menu_book ']";
    private final String vendorsTab = "//span[contains(text(),'Vendors')]";
    private final String vendorsTabIsSelected = "//div//mat-icon[text()=' menu_book ']//following-sibling::span//parent::div//following::div[@aria-selected='true']";

    private final String addVendorButton = "//button[contains(@mattooltip, 'Add Vendor')]";
    private final String vendorNameInput = "//input[@formcontrolname='name']";
    private final String accountNumberInput = "//input[@formcontrolname='accountNumber']";
    private final String contactNameInput = "//input[@formcontrolname='contactName']";
    private final String contactEmailInput = "//input[@formcontrolname='contactEmail']";
    private final String notesInput = "//textarea[@name='notes']";

    private final String expandAddressSection = "//mat-panel-title[contains(text(),'Address')]";
    private final String addressInput = "//input[@formcontrolname='address']";
    private final String stateInput = "//input[@formcontrolname='state']";
    private final String cityInput = "//input[@formcontrolname='city']";
    private final String postalCodeInput = "//input[@formcontrolname='postalCode']";

    private final String expandPhoneNumberSection = "//mat-panel-title[contains(text(),'Phone Number')]";
    private final String phoneNumberInput = "//input[@formcontrolname='phoneNumber']";

    private final String expandContactSection = "//mat-panel-title[contains(text(),'Contact')]";
    private final String websiteInput = "//input[@name='website']";

    private final String saveButton = "//button[@mattooltip='Save']";
    private final String successMessage = "//div[contains(@class, 'success-message')]";
    private final String specificVendor = "//td[contains(text(),'%s')]";

    public InventoryPage(Page page) {
        super(page);
    }

    public void clickApps() {
        click(appspage, 10000);
    }

    public boolean isVendorVisible(String vendorName) {
        String vendorLocator = String.format(specificVendor, vendorName);
        return isVisible(vendorLocator, "Vendor Row", 5000);
    }

    public void clickOnInventory() {
        click(inventorypage, 10000);
    }

    public void expandAddressSectionAndAddDetails() {
        click(expandAddressSection, 3000);
        fillField(addressInput, "Street21", 3000);
        fillField(stateInput, "NY", 3000);
        fillField(cityInput, "New", 3000);
        fillField(postalCodeInput, "10005", 3000);
    }

    public void expandPhoneNumberSectionAndAddDetails() {
        click(expandPhoneNumberSection, 3000);
        fillField(phoneNumberInput, "8749304875", 3000);
    }

    public void expandContactSectionAndAddDetails() {
        click(expandContactSection, 3000);
        fillField(contactNameInput, "ramu31", 3000);
        fillField(contactEmailInput, "ramu31@abc.com", 3000);
    }

    public void clickIngredientsTab() {
        click(ingredientsTab, 3000);
    }

    public void clickVendorsTab() {
        click(vendorsTab, 3000);
    }

    public void clickAddVendor() {
        click(addVendorButton, 5000);
    }

    public void enterVendorDetails() {
        fillField(vendorNameInput, "Ramu1", 3000);
        fillField(accountNumberInput, "4567", 3000);
    }

    public void clickSave() {
        click(saveButton, 3000);
    }

    public boolean isSuccessMessageDisplayed() {
        return isVisible(successMessage, "Success Message", 3000);
    }

    public String getSuccessMessage() {
        return getTextOfElement(successMessage, 3000);
    }

    public boolean isVendorsTabSelected() {
        return isVisible(vendorsTabIsSelected, "Vendors Tab Selected", 3000);
    }

    public boolean isIngredientsTabSelected() {
        return isVisible(ingredientsTab + "[@aria-selected='true']", "Ingredients Tab Selected", 3000);
    }
}
