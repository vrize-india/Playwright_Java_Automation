package com.tonic.pageObjects.web.others;

import com.microsoft.playwright.Page;

public class DashboardPage {
    private final Page page;

    public DashboardPage(Page page) {
        this.page = page;
    }

    public void goToAttendanceReports() {
        try {
            System.out.println("Starting navigation: Apps -> Attendance");
            // First wait for the page to be fully loaded
            page.waitForLoadState();
            
            // Wait for and click Apps
            page.waitForSelector("//a[normalize-space(text())='Apps']", 
                new Page.WaitForSelectorOptions().setTimeout(20000));
            page.click("//a[normalize-space(text())='Apps']");
            System.out.println("Clicked on Apps");
            
            // Wait for the dropdown to appear
            page.waitForTimeout(2000);
            
            // Wait for and click Attendance
            page.waitForSelector("//span[normalize-space(text())='Attendance']", 
                new Page.WaitForSelectorOptions().setTimeout(20000));
            page.click("//span[normalize-space(text())='Attendance']");
            System.out.println("Clicked on Attendance");
            
            // Take a screenshot for debugging
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get("target", "attendance-navigation-debug.png")));
            
            // Wait for the page to load
            page.waitForLoadState();
        } catch (Exception e) {
            System.out.println("Navigation failed: " + e.getMessage());
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get("navigation-error.png")));
            throw e;
        }
    }
} 