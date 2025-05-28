package com.tonic.steps;

import com.tonic.factory.PlaywrightFactory;
import com.tonic.pages.web.LoginPage;
import com.tonic.pages.web.AdminDashboardPage;
import com.tonic.pages.web.ConfigurationPage;
import com.tonic.pages.web.TerminalsPage;
import net.thucydides.core.annotations.Step;
import org.junit.Assert;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import com.microsoft.playwright.Page;

public class AddTerminalSteps {
    LoginPage loginPage;
    AdminDashboardPage adminDashboardPage;
    ConfigurationPage configurationPage;
    TerminalsPage terminalsPage;

    @Step("User is logged in")
    public void user_is_logged_in() {
        PlaywrightFactory.getPage().navigate("https://your-app-url/login");
        loginPage = new LoginPage(PlaywrightFactory.getPage());
        loginPage.doLogin("Prasanna@vrize.com", "Password@123");
        adminDashboardPage = new AdminDashboardPage(PlaywrightFactory.getPage());
    }

    @Step("User navigates to the terminals page")
    public void user_navigates_to_terminals_page() {
        adminDashboardPage.goToConfiguration();
        configurationPage = new ConfigurationPage(PlaywrightFactory.getPage());
        configurationPage.goToTerminals();
        terminalsPage = new TerminalsPage(PlaywrightFactory.getPage());
    }

    @Step("User clicks the add terminal button")
    public void user_clicks_add_terminal_button() {
        if (terminalsPage == null) {
            terminalsPage = new TerminalsPage(PlaywrightFactory.getPage());
        }
        terminalsPage.clickAddTerminalButton();
    }

    @Step("Add terminal dialog should be visible")
    public void add_terminal_dialog_should_be_visible() {
        if (terminalsPage == null) {
            terminalsPage = new TerminalsPage(PlaywrightFactory.getPage());
        }
        Assert.assertTrue(terminalsPage.isAddTerminalTextVisible());
    }

    @When("the user clicks the add terminal button")
    public void the_user_clicks_the_add_terminal_button_cucumber() {
        user_clicks_add_terminal_button();
    }

    @Then("the add terminal dialog should be visible")
    public void the_add_terminal_dialog_should_be_visible_cucumber() {
        add_terminal_dialog_should_be_visible();
    }
} 