package com.tonic.stepDefinitions.web.labor;

import com.tonic.factory.PlaywrightFactory;
import com.tonic.pageObjects.web.login.LoginPage;
import com.tonic.pageObjects.web.report.AdminDashboardPage;
import com.tonic.pageObjects.web.configuration.ConfigurationPage;
import com.microsoft.playwright.Page;
import com.tonic.stepDefinitions.BaseStep;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.*;

public class RolesCopyModalSteps extends BaseStep{
    private Page page;
    private LoginPage loginPage;
    private AdminDashboardPage adminDashboardPage;
    private ConfigurationPage configurationPage;


    // TONIC4797: Modal with duplicate icon
    @When("the user is on the modal screen with a duplicate icon")
    public void user_on_modal_screen_with_duplicate_icon() {
        page = PlaywrightFactory.getPage();
        adminDashboardPage = new AdminDashboardPage(page);
        configurationPage = new ConfigurationPage(page);
        adminDashboardPage.goToConfiguration();
        configurationPage.goToRoles();
        configurationPage.openFirstRolePermissionsModal();
        assertTrue(configurationPage.isPermissionsModalVisible());
    }

    @When("the user taps on the duplicate icon")
    public void user_taps_on_duplicate_icon() {
        page = PlaywrightFactory.getPage();
        configurationPage = new ConfigurationPage(page);
        configurationPage.clickDuplicateIcon();
    }

    @Then("the screen expands")
    public void screen_expands() {
        page = PlaywrightFactory.getPage();
        configurationPage = new ConfigurationPage(page);
        assertTrue(configurationPage.isCopyModalFieldsVisible());
    }

    @Then("the following fields are displayed:")
    public void the_following_fields_are_displayed(DataTable dataTable) {
        page = PlaywrightFactory.getPage();
        configurationPage = new ConfigurationPage(page);
        List<Map<String, String>> fields = dataTable.asMaps(String.class, String.class);
        System.out.println("Checking Store Name Field...");
        assertTrue(configurationPage.isStoreNameFieldVisible(), "Store Name Field not visible");
        System.out.println("Checking Inherit Permissions Field...");
        assertTrue(configurationPage.isInheritPermissionsFieldVisible(), "Inherit Permissions Field not visible");
        System.out.println("Checking Select Combobox...");
        softAssertTrue(configurationPage.isSelectComboboxVisible(), "Select Combobox not visible");
        assertAll();
    }

    // TONIC6653: Duplicate icon next to close button
    @When("the user is on the modal screen")
    public void user_is_on_modal_screen() {
        page = PlaywrightFactory.getPage();
        adminDashboardPage = new AdminDashboardPage(page);
        configurationPage = new ConfigurationPage(page);
        adminDashboardPage.goToConfiguration();
        configurationPage.goToRoles();
        configurationPage.openFirstRolePermissionsModal();
        assertTrue(configurationPage.isPermissionsModalVisible());
    }

    @Then("the duplicate icon is displayed next to the close button")
    public void duplicate_icon_displayed_next_to_close() {
        page = PlaywrightFactory.getPage();
        configurationPage = new ConfigurationPage(page);
        assertTrue(configurationPage.isDuplicateIconNextToCloseButton());
    }
} 