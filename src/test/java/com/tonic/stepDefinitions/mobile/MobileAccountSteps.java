package com.tonic.stepDefinitions.mobile;

import com.tonic.pageObjects.mobile.Login;
import com.tonic.driver.MobileDriver;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class MobileAccountSteps {

    @Given("the mobile app is launched with host {string} store id {string} and auth code {string}")
    public void theMobileAppIsLaunchedWithCredentials(String hostUrl, String storeId, String authCode) throws InterruptedException {
        Login login = new Login(MobileDriver.getDriver());
        login.performStoreLogin(hostUrl, storeId, authCode);
    }

    //TODO PLACEHOLDER
    @When("the user performs a demo action")
    public void theUserPerformsDemoActionCucumber() {
        // Implement demo action
    }

    //TODO PLACEHOLDER
    @Then("the expected result should be displayed")
    public void theExpectedResultShouldBeDisplayedCucumber() {
        // Implement result check
    }
} 