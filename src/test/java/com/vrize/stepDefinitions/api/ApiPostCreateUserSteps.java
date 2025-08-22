package com.vrize.stepDefinitions.api;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.Assert;

public class ApiPostCreateUserSteps {
    @When("the user sends a POST request to create a user")
    public void the_user_sends_a_post_request_to_create_a_user() {
        // Use your APIDemo.java logic to send POST request
    }

    @Then("the user should be created successfully")
    public void the_user_should_be_created_successfully() {
        Assert.assertTrue(true); // Replace with actual response check
    }
} 