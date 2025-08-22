package com.tonic.pageObjects.web.report;

import com.microsoft.playwright.Page;

import java.io.File;

public class ReportsPage {
    private final Page page;

    public ReportsPage(Page page) {
        this.page = page;
    }

    public void selectAttendanceDetailReport() {
        // Wait for and click the Attendance Detail option
        try {
            // First wait for any loading to complete
            page.waitForLoadState();
            
            // Wait for and click the Attendance Detail option using a general text selector
            page.waitForSelector("text=Attendance Detail", new Page.WaitForSelectorOptions().setTimeout(30000));
            page.getByText("Attendance Detail").click();
            
            // Take a screenshot for debugging if needed
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get("target", "attendance-selection-debug.png")));
        } catch (Exception e) {
            System.out.println("Failed to select Attendance Detail: " + e.getMessage());
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get("attendance-selection-error.png")));
            throw e;
        }
    }

    public void selectCustomDates() {
        page.locator("span:has-text('Date Range')").first().click();
    }

    public void enterDateRange(String start, String end) {
        page.getByText("Today").nth(2).click();
        page.getByText("Custom Dates").click();
        page.locator("span:has-text('Date Range')").first().click();
        page.fill("input[placeholder='Start date']", start);
        page.fill("input[placeholder='End date']", end);
    }

    public void selectEmployee(String employee) {
        page.getByLabel("Employees").getByText("All").click();
        page.click("text=" + employee);
    }

    public void selectGroup(String group) {
        page.getByLabel("Group").getByText("All").click();
        page.click("text=" + group);
    }

    public void selectJobTitle(String jobTitle) {
        page.getByLabel("Job Title").click();
        page.click("text=" + jobTitle);
    }

    public void runReport() {
        page.click("button:has-text('Run')");
    }

    public boolean isReportDisplayed() {
        return page.isVisible("text=Attendance Details");
    }
} 