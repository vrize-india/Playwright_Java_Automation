package com.tonic.steps;

import com.tonic.factory.PlaywrightFactory;
import com.tonic.pages.web.LoginPage;
import com.tonic.pages.web.AdminDashboardPage;
import com.tonic.pages.web.ConfigurationPage;
import com.tonic.pages.web.TerminalsPage;
import net.thucydides.core.annotations.Step;
import org.junit.Assert;

public class TerminalNavigationSteps {
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

    @Step("Terminals page should be displayed")
    public void terminals_page_should_be_displayed() {
        Assert.assertTrue(terminalsPage.isTerminalsPageVisible());
    }
} 