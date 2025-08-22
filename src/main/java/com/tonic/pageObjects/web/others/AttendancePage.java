package com.tonic.pageObjects.web.others;

import com.microsoft.playwright.Page;

public class AttendancePage {
    private final Page page;

    public AttendancePage(Page page) {
        this.page = page;
    }

    public void openReportsTab() {
        try {
            // Wait for the page to finish loading
            page.waitForLoadState();
            System.out.println("Attendance page loaded, looking for Reports tab");
            
            // Wait for and click the second Reports tab
            page.waitForSelector("text=Reports", new Page.WaitForSelectorOptions().setTimeout(30000));
            page.locator("text=Reports").nth(1).click(); // Click the second 'Reports' element (index 1)
            System.out.println("Clicked on second Reports tab");
            
            // Wait for the page to stabilize
            page.waitForLoadState();
            page.waitForTimeout(2000);
            
            // Take a screenshot of the Reports tab
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get("target", "reports-tab-debug.png")));
        } catch (Exception e) {
            System.out.println("Failed to click Reports tab: " + e.getMessage());
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get("reports-tab-error.png")));
            throw e;
        }
    }

    public boolean isFilterDisplayed() {
        boolean usingTextFilter = false;
        try {
            // Correct selector usage for Playwright
            page.waitForSelector("text=Select Report Type", new Page.WaitForSelectorOptions().setTimeout(10000));
            usingTextFilter = page.getByText("Select Report Type").isVisible();
            System.out.println("Text filter found: " + usingTextFilter);
        } catch (Exception e) {
            System.out.println("Text selector not found: " + e.getMessage());
            // Print the page content for debugging
            System.out.println("Page content: " + page.content());
        }

        // Take a screenshot regardless of result
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(java.nio.file.Paths.get("target", "filter-check-debug.png")));
        return usingTextFilter;
    }
} 