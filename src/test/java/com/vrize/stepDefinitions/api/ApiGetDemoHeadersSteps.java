package com.vrize.stepDefinitions.api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.vrize.hooks.Hooks;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.Assert;

public class ApiGetDemoHeadersSteps {
    private APIRequestContext requestContext = Hooks.apiRequestContext.get();
    private APIResponse apiResponse;

    @When("the user sends a GET request to retrieve users")
    public void the_user_sends_a_get_request_to_retrieve_users() {
        //Temp sample url is kept, should be fetched from config in real time
        apiResponse = requestContext.get("https://gorest.co.in/public/v2/users");
    }

    @Then("the response status code should be 200")
    public void the_response_status_code_should_be_200() {
        Assert.assertEquals(200, apiResponse.status());
    }

    @Then("the response header {string} should be {string}")
    public void the_response_header_should_be(String header, String expectedValue) {
        String actualValue = apiResponse.headers().get(header);
        Assert.assertEquals(expectedValue, actualValue);
    }
} 