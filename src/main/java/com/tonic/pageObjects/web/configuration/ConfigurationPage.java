package com.tonic.pageObjects.web.configuration;

import com.microsoft.playwright.Page;

public class ConfigurationPage {
    private Page page;
    private String terminalsMenu = "text=Terminals";
    private String rolesTab = "text=Roles";
    private String permissionsButton = "table button";
    private String permissionsModal = ".modal-dialog,[role=dialog]";
    private String duplicateIcon = ".modal-dialog,[role=dialog] button";
    private String storeNameField = "text=Store Name";
    private String inheritPermissionsField = "text=Inherit Permissions From";
    private String selectCombobox = "[role=combobox],select";
    private String menuconfiguration = "text=Menu Configuration";

    public ConfigurationPage(Page page) {
        this.page = page;
    }

    public boolean isConfigurationLoaded() {
        return page.isVisible(terminalsMenu);
    }

    public void goToTerminals() {
        page.locator(terminalsMenu).click();
    }

    public void goToRoles() {
        page.locator(rolesTab).click();
    }

    public void goTomenuconfiguration() {
        page.locator(menuconfiguration).click();
    }

    public void openFirstRolePermissionsModal() {
        page.locator(permissionsButton).first().click();
    }

    public boolean isPermissionsModalVisible() {
        return page.locator(permissionsModal).first().isVisible();
    }

    public void clickDuplicateIcon() {
        page.locator(duplicateIcon).nth(1).click();
    }

    public boolean isCopyModalFieldsVisible() {
        return page.locator(storeNameField).isVisible() && page.locator(inheritPermissionsField).isVisible();
    }

    public boolean isStoreNameFieldVisible() {
        page.locator(storeNameField).waitFor();
        return page.locator(storeNameField).isVisible();
    }

    public boolean isInheritPermissionsFieldVisible() {
        page.locator(inheritPermissionsField).waitFor();
        return page.locator(inheritPermissionsField).isVisible();
    }

    public boolean isSelectComboboxVisible() {
        page.locator("[role=combobox],select").locator("text=Select").first().waitFor();
        return page.locator("[role=combobox],select").locator("text=Select").first().isVisible();
    }

    public boolean isDuplicateIconNextToCloseButton() {
        // Locate the container div
        var container = page.locator("div.justify-content-center.buttons-column-data");
        // Get all buttons inside the container
        var buttons = container.locator("button");
        // There should be at least two buttons: [0]=copy, [1]=close
        if (buttons.count() < 2) return false;
        // Check the first button contains the copy icon
        boolean copyIconPresent = buttons.nth(0).locator("svg.svg-copy-icon-dims").count() > 0;
        // Check the second button contains the close icon
        boolean closeIconPresent = buttons.nth(1).locator("svg.svg-close-icon-xmark-dims").count() > 0;
        return copyIconPresent && closeIconPresent;
    }
} 