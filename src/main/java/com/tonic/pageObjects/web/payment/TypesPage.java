package com.tonic.pageObjects.web.payment;

import com.microsoft.playwright.Page;
import com.tonic.enums.ButtonConstants;
import com.tonic.common.web.BasePage;

public class TypesPage extends BasePage{

    // =============== Constructor ===============
    public TypesPage(Page page) {
        super(page);
    }


// =============== Methods ===============
    /**
     * Sets the Dual Pricing option to Enabled and saves the configuration.
     * @param option The Dual Pricing value to select.
     */

    public void configureDualPricing(String option) {

        clickDynamicElement(PaymentLocators.TXT_BUTTON, ButtonConstants.fromKey("DUAL_PRICING").getValue(), 5000);
        click(PaymentLocators.DUAL_PRICE_DROPDOWN, 5000);

        String optionLocator = String.format(PaymentLocators.ENABLE_DISABLE_DUAL_PRICE, option);
        clickDynamicElement(optionLocator, option, 2000);

        click(PaymentLocators.DUAL_PRICING_SAVE_BUTTON, 2000);
        System.out.println("Dual Pricing set to: " + option);
    }
}
