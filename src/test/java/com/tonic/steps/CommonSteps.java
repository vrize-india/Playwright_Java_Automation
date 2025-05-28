package com.tonic.steps;

import com.tonic.factory.PlaywrightFactory;
import com.tonic.pages.web.LoginPage;
import com.tonic.pages.web.AdminDashboardPage;
import com.tonic.pages.web.ConfigurationPage;
import com.tonic.pages.web.TerminalsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import com.microsoft.playwright.Page;

public class CommonSteps {
    LoginPage loginPage;
    AdminDashboardPage adminDashboardPage;
    ConfigurationPage configurationPage;
    TerminalsPage terminalsPage;

    @Given("the user is logged in")
    public void the_user_is_logged_in() {
        PlaywrightFactory.getPage().navigate("https://admin.test.ordyx.com/login");
        loginPage = new LoginPage(PlaywrightFactory.getPage());
        loginPage.doLogin("Prasanna@vrize.com", "Password@123");
        adminDashboardPage = new AdminDashboardPage(PlaywrightFactory.getPage());
    }

    @When("the user navigates to the terminals page")
    public void the_user_navigates_to_the_terminals_page() {
        adminDashboardPage.goToConfiguration();
        configurationPage = new ConfigurationPage(PlaywrightFactory.getPage());
        configurationPage.goToTerminals();
        terminalsPage = new TerminalsPage(PlaywrightFactory.getPage());
    }

    @Then("the terminals page should be displayed")
    public void the_terminals_page_should_be_displayed() {
        if (terminalsPage == null) {
            terminalsPage = new TerminalsPage(PlaywrightFactory.getPage());
        }
        // Wait for the terminal name field to be visible
        PlaywrightFactory.getPage().waitForSelector("input[placeholder='Terminal Name']", new Page.WaitForSelectorOptions().setTimeout(5000));
        // Take a screenshot for debugging
        String screenshot = PlaywrightFactory.takeScreenshot();
        Assert.assertTrue(terminalsPage.isTerminalsPageVisible());
    }
} 