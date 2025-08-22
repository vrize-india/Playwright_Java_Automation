package com.tonic.pageObjects.web.labor;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tonic.utils.ApplicationUtils;

import java.util.List;

public class RolesPage {
    private final Page page;
    private final ApplicationUtils apputils;

    // =============== Locators ===============
    // Navigation locators
    private final String rolesTitle = "//div[text()='Roles']";
    private final String configurationMenu = "text=Configuration";
    private final String rolesMenu = "text=Roles";

    // User list and search locators
    private final String userRolesList = "//td[contains(@class,'name')]//span";
    private final String searchInput = "input[placeholder='Search']";
    private final String userLocatorFormat = "//span[contains(text(), '%s')]";

    // Edit form locators
    private final String editButtonXPath = "//span[text()='%s']/ancestor::tr//button[@mattooltip='Edit']";
    private final String hourlyWagesOfSpecificUser = "//span[text()='%s']/ancestor::tr//td[contains(@class,'hourly')]";
    private final String hourlyWagesInput = "input[type='number'][matinput][appdecimalinput][placeholder='0']";
    private final String saveButton = "//button[@mattooltip='Save']";
    private final String cancelButton = "button:has-text('Cancel')";

    // =============== Constructor ===============
    public RolesPage(Page page) {
        this.page = page;
        this.apputils = new ApplicationUtils();
    }

    // =============== Navigation Methods ===============
    public void navigateToRoles() {
        try {
            page.waitForLoadState();
            page.waitForSelector(configurationMenu, new Page.WaitForSelectorOptions().setTimeout(20000));
            page.click(configurationMenu);
            
            page.waitForSelector(rolesMenu, new Page.WaitForSelectorOptions().setTimeout(20000));
            page.click(rolesMenu);
            
            page.waitForLoadState();
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to Roles page", e);
        }
    }

    public boolean isRolesPageLoaded() {
        try {
            page.waitForSelector(rolesTitle, new Page.WaitForSelectorOptions().setTimeout(10000));
            page.waitForSelector(userRolesList, new Page.WaitForSelectorOptions().setTimeout(10000));
            return page.isVisible(rolesTitle) && page.locator(userRolesList).count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // =============== User List Methods ===============
    public boolean isUserRolesListVisible() {
        try {
            page.waitForSelector(userRolesList, new Page.WaitForSelectorOptions().setTimeout(20000));
            return page.locator(userRolesList).count() > 0;
        } catch (Exception e) {
            throw new RuntimeException("User roles list not visible within timeout", e);
        }
    }

    public List<String> getListOfUser() {
        try {
            page.waitForSelector(userRolesList, new Page.WaitForSelectorOptions().setTimeout(10000));
            Locator userNames = page.locator(userRolesList);
            return userNames.allTextContents();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get list of user roles", e);
        }
    }

    public String getFirstVisibleUser() {
        try {
            page.waitForSelector(userRolesList, new Page.WaitForSelectorOptions().setTimeout(10000));
            List<String> listOfUsers = getListOfUser();
            System.out.println("List of users " + listOfUsers);
            String randomUser = ApplicationUtils.getRandomElementFromList(listOfUsers);
            System.out.println("Random user " + randomUser);
            return randomUser;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get first visible user", e);
        }
    }

    // =============== Search Methods ===============
    public void searchUser(String userName) {
        try {
            page.waitForLoadState();
            page.waitForSelector(searchInput, new Page.WaitForSelectorOptions().setTimeout(20000));
            
            Locator searchBox = page.locator(searchInput);
            if (searchBox.isVisible()) {
                searchBox.clear();
                searchBox.type(userName);
                page.waitForTimeout(2000);
                System.out.println("Searching for user: " + userName);
                
                String userLocator = String.format(userLocatorFormat, userName);
                page.waitForSelector(userLocator, new Page.WaitForSelectorOptions().setTimeout(5000));
            } else {
                throw new RuntimeException("Search input not found or not visible");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to search for user: " + userName, e);
        }
    }

    public boolean isUserVisible(String userName) {
        try {
            String userLocator = String.format(userLocatorFormat, userName);
            page.waitForSelector(userLocator, new Page.WaitForSelectorOptions().setTimeout(5000));
            return page.locator(userLocator).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    // =============== Edit Form Methods ===============
    public void clickEditForUser(String userName) {
        try {
            page.waitForSelector(userRolesList, new Page.WaitForSelectorOptions().setTimeout(10000));
            String userLocator = String.format(userLocatorFormat, userName);
            page.waitForSelector(userLocator, new Page.WaitForSelectorOptions().setTimeout(10000));
            
            String editButtonLocator = String.format(editButtonXPath, userName);
            page.waitForSelector(editButtonLocator, new Page.WaitForSelectorOptions().setTimeout(10000));
            page.click(editButtonLocator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to click edit for user '" + userName + "'", e);
        }
    }

    public void setHourlyWages(String wages) {
        try {
            page.waitForSelector(hourlyWagesInput, new Page.WaitForSelectorOptions().setTimeout(10000));
            Locator wagesInput = page.locator(hourlyWagesInput);
            
            if (wagesInput.isVisible()) {
                wagesInput.clear();
                wagesInput.type(wages);
                page.waitForTimeout(500);
            } else {
                throw new RuntimeException("Hourly wages input not found or not visible");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set hourly wages to " + wages, e);
        }
    }

    public String getHourlyWagesOfSpecificUser(String userName) {
        try {
            String locator = String.format(hourlyWagesOfSpecificUser, userName);
            page.waitForSelector(locator, new Page.WaitForSelectorOptions().setTimeout(20000));
            return page.locator(locator).textContent();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get hourly wages for user: " + userName, e);
        }
    }

    public void saveChanges() {
        try {
            page.waitForLoadState();
            page.waitForSelector(saveButton, new Page.WaitForSelectorOptions()
                .setTimeout(20000)
                .setState(WaitForSelectorState.VISIBLE));
            
            Locator saveBtn = page.locator(saveButton);
            if (saveBtn.isVisible() && saveBtn.isEnabled()) {
                saveBtn.click();
                page.waitForLoadState();
                page.waitForSelector(saveButton, new Page.WaitForSelectorOptions()
                    .setTimeout(10000)
                    .setState(WaitForSelectorState.HIDDEN));
                page.waitForTimeout(2000);
            } else {
                throw new RuntimeException("Save button is not visible or enabled");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save changes: " + e.getMessage(), e);
        }
    }

    public void cancelChanges() {
        try {
            page.waitForSelector(cancelButton, new Page.WaitForSelectorOptions().setTimeout(5000));
            Locator cancelBtn = page.locator(cancelButton);
            
            if (cancelBtn.isVisible()) {
                cancelBtn.click();
                page.waitForLoadState();
                page.waitForTimeout(1000);
            } else {
                throw new RuntimeException("Cancel button not found or not visible");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to cancel changes", e);
        }
    }

    public boolean isEditFormVisible() {
        try {
            page.waitForSelector(hourlyWagesInput, new Page.WaitForSelectorOptions().setTimeout(10000));
            return page.locator(hourlyWagesInput).isVisible();
        } catch (Exception e) {
            return false;
        }
    }
} 