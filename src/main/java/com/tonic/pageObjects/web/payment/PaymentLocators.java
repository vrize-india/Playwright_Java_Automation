package com.tonic.pageObjects.web.payment;

public interface PaymentLocators {

    //Common Locators
    String TXT_BUTTON = "//button[contains(@mattooltip, '%s')]";

    //Following Locators belongs to Types Page
    String DUAL_PRICE_DROPDOWN = "//mat-select[@id= 'selectDualPrice']";
    String ENABLE_DISABLE_DUAL_PRICE = "//span[contains(@class, 'mat-option-text') and text()='%s']";
    String DUAL_PRICING_SAVE_BUTTON = "//mat-icon[text()='check']";

}
