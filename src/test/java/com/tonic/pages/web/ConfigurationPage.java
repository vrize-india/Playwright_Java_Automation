package com.tonic.pages.web;

import com.microsoft.playwright.Page;

public class ConfigurationPage {
    private Page page;
    private String terminalsMenu = "text=Terminals";

    public ConfigurationPage(Page page) {
        this.page = page;
    }

    public boolean isConfigurationLoaded() {
        return page.isVisible(terminalsMenu);
    }

    public void goToTerminals() {
        page.locator(terminalsMenu).click();
    }
} 