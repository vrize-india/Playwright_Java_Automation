package com.tonic.steps;

import net.thucydides.core.annotations.Step;
import org.junit.Assert;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class ApiGetDemoSteps {
    @Step("API endpoint is available")
    public void api_endpoint_is_available() {
        // Setup or check API endpoint availability
    }

    @Step("User sends a GET request")
    public void user_sends_get_request() {
        // Use your APIDemo.java logic to send GET request
    }

    @Step("Response should be successful")
    public void response_should_be_successful() {
        Assert.assertTrue(true); // Replace with actual response check
    }

    @Given("the API endpoint is available")
    public void the_api_endpoint_is_available_cucumber() {
        api_endpoint_is_available();
    }

    @When("the user sends a GET request")
    public void the_user_sends_a_get_request_cucumber() {
        user_sends_get_request();
    }

    @Then("the response should be successful")
    public void the_response_should_be_successful_cucumber() {
        response_should_be_successful();
    }
} 