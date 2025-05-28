package com.tonic.steps;

import com.tonic.factory.PlaywrightFactory;
import com.tonic.pages.web.LoginPage;
import com.tonic.pages.web.AdminDashboardPage;
import net.thucydides.core.annotations.Step;
import org.junit.Assert;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class LoginSteps {
    LoginPage loginPage;
    AdminDashboardPage adminDashboardPage;

    @Step("User is on the login page")
    public void user_is_on_login_page() {
        PlaywrightFactory.getPage().navigate("https://admin.test.ordyx.com/login");
        loginPage = new LoginPage(PlaywrightFactory.getPage());
    }

    @Step("User enters valid credentials")
    public void user_enters_valid_credentials() {
        loginPage.doLogin("Prasanna@vrize.com", "Password@123");
        adminDashboardPage = new AdminDashboardPage(PlaywrightFactory.getPage());
    }

    @Step("Dashboard should be displayed")
    public void dashboard_should_be_displayed() {
        Assert.assertTrue(adminDashboardPage.isDashboardLoaded());
    }

    @Given("the user is on the login page")
    public void the_user_is_on_the_login_page_cucumber() {
        user_is_on_login_page();
    }

    @When("the user enters valid credentials")
    public void the_user_enters_valid_credentials_cucumber() {
        user_enters_valid_credentials();
    }

    @Then("the dashboard should be displayed")
    public void the_dashboard_should_be_displayed_cucumber() {
        dashboard_should_be_displayed();
    }
} 