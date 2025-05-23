package com.tonic.pages;

import com.microsoft.playwright.Page;

public class TerminalsPage {
    private Page page;
    private String addTerminalButton = "button:has(svg[aria-label='add']), button:has-text('add')";
    private String addTerminalDialog = "role=dialog >> text=Add Terminal";
    private String terminalNameField = "input[placeholder='Terminal Name']";
    private String networkNameField = "input[placeholder='Network Name']";
    private String doneButton = "role=button[name='Done']";
    private String requiredError = "text=Required";

    public TerminalsPage(Page page) {
        this.page = page;
    }

    public boolean isAddTerminalButtonPresent() {
        return page.locator(addTerminalButton).isVisible();
    }

    public void clickAddTerminalButton() {
        page.locator(addTerminalButton).first().click();
    }

    public boolean isAddTerminalDialogVisible() {
        return page.locator(addTerminalDialog).isVisible();
    }

    public void fillTerminalDetails(String terminalName, String networkName) {
        page.locator(terminalNameField).fill(terminalName);
        page.locator(networkNameField).fill(networkName);
    }

    public void clickDoneButton() {
        page.locator(doneButton).click();
    }

    public boolean isRequiredErrorVisible() {
        return page.locator(requiredError).isVisible();
    }
} 