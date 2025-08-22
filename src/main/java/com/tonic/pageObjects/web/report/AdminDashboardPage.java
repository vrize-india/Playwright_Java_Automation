package com.tonic.pageObjects.web.report;

import com.microsoft.playwright.Page;
import com.tonic.common.web.BasePage;

public class AdminDashboardPage extends BasePage {

    private final String configurationMenu = "text=Configuration";

    public AdminDashboardPage(Page page) {
        super(page);
    }

    public boolean isDashboardLoaded() {
        return isVisible(configurationMenu, "Configuration Menu", 5000);
    }

    public void goToConfiguration() {
        click(configurationMenu, 5000);
    }
}