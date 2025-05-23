package com.tonic.pages;

import com.microsoft.playwright.Page;

public class AdminDashboardPage {
    private Page page;
    private String configurationMenu = "text=Configuration";

    public AdminDashboardPage(Page page) {
        this.page = page;
    }

    public boolean isDashboardLoaded() {
        return page.isVisible(configurationMenu);
    }

    public void goToConfiguration() {
        page.locator(configurationMenu).click();
    }
} 