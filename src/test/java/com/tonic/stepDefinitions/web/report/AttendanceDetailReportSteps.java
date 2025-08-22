package com.tonic.stepDefinitions.web.report;

import com.tonic.factory.PlaywrightFactory;
import com.tonic.listeners.JiraListener;
import com.tonic.pageObjects.web.login.LoginPage;
import com.tonic.pageObjects.web.others.AttendancePage;
import com.tonic.pageObjects.web.others.DashboardPage;
import com.tonic.pageObjects.web.report.ReportsPage;
import com.tonic.utils.JiraPolicy;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.testng.annotations.Listeners;
import io.qameta.allure.Step;

@Listeners(JiraListener.class)
@JiraPolicy(logTicketReady =false)
public class AttendanceDetailReportSteps {
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private AttendancePage attendancePage;
    private ReportsPage reportsPage;

    @Step("User navigates to Apps → Attendance → Reports")
    @When("the user navigates to Apps → Attendance → Reports")
    public void user_navigates_to_attendance_reports() {
        dashboardPage = new DashboardPage(PlaywrightFactory.getPage());
        dashboardPage.goToAttendanceReports();
    }

    @Step("User clicks on the {option} option")
    @When("the user clicks on the {string} option")
    public void user_clicks_on_option(String option) {
        attendancePage = new AttendancePage(PlaywrightFactory.getPage());
        attendancePage.openReportsTab();
    }

    @Step("Page should update to display a filter for report selection")
    @Then("the page should update to display a filter for report selection")
    public void page_displays_filter() {
        Assert.assertTrue(attendancePage.isFilterDisplayed());
    }

    @Step("User selects {reportType} as the {selectType}")
    @When("the user selects {string} as the {string}")
    public void user_selects_report_type(String reportType, String selectType) {
        reportsPage = new ReportsPage(PlaywrightFactory.getPage());
        reportsPage.selectAttendanceDetailReport();
    }

    @Step("User chooses {dateRange} under {section} section")
    @When("chooses {string} under {string} section")
    public void user_chooses_date_range(String dateRange, String section) {
        reportsPage.selectCustomDates();
        PlaywrightFactory.getPage().locator("span:has-text('Date Range')").first().click();
    }

    @Step("User enters a specific period of date")
    @When("enters a specific period of date")
    public void user_enters_date_period() {
        reportsPage.enterDateRange("1/01/2025", "5/29/2025");
    }

    @Step("User clicks on {section} section")
    @When("clicks on {string} section")
    public void user_clicks_filter_by(String section) {
        // This step may be implicit if the filter is already open
    }

    @Step("Verify sections {employees}, {group}, and {jobTitle} with dropdown options appear")
    @Then("three sections titled {string}, {string}, and {string} with dropdown options should appear")
    public void verify_sections(String employees, String group, String jobTitle) {
        // Add assertions for dropdowns if needed
    }

    @Step("User selects options from each section needed for the report")
    @When("the user selects options from each section needed for the report")
    public void user_selects_report_options() {
        reportsPage.selectEmployee("Barbara Manager");
        reportsPage.selectGroup("FOH");
    }

    @Step("User clicks on the {button} button")
    @When("clicks on the {string} button")
    public void user_clicks_run(String button) {
        reportsPage.runReport();
    }

    @Step("Verify the Attendance Detail Report is displayed with data corresponding to the selected filters")
    @Then("the page should display the Attendance Detail Report with data corresponding to the selected filters")
    public void verify_report_displayed() {
        Assert.assertTrue(reportsPage.isReportDisplayed());
    }

    @Step("Verify the Wage, Total Wages, and Rates displayed in the report are accurate")
    @Then("the Wage, Total Wages, and Rates displayed in the report should be accurate")
    public void verify_wages_and_rates() {
        // Implement wage/rate validation
    }

    @Step("Verify that the values shown in BOH Preview report match exactly with those in the Legacy report for the same filter and date range")
    @Then("verify that the values shown in BOH Preview report match exactly with those in the Legacy report for the same filter and date range")
    public void verify_boh_vs_legacy() {
        // Implement BOH vs Legacy report comparison
    }
}
