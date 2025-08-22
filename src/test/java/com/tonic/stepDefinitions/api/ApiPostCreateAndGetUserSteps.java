package com.tonic.stepDefinitions.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.tonic.hooks.Hooks;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApiPostCreateAndGetUserSteps {
    private APIRequestContext requestContext = Hooks.apiRequestContext.get();
    private APIResponse apiPostResponse;
    private APIResponse apiGetResponse;
    private String userId;
    private String emailId;

    @When("the user sends a POST request to create a user with random email")
    public void the_user_sends_a_post_request_to_create_a_user_with_random_email() {
        emailId = "testpwautomation" + System.currentTimeMillis() + "@gmail.com";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Gaurav");
        data.put("email", emailId);
        data.put("gender", "male");
        data.put("status", "active");
        apiPostResponse = requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization", "Bearer f4bd5cb99e27882658a2233c4ddd1e8a14f49788f92938580971a46439aa774f")
                        .setData(data)
        );
    }

    @Then("the user should be created successfully with status code 201")
    public void the_user_should_be_created_successfully_with_status_code_201() throws IOException {
        Assert.assertEquals(201, apiPostResponse.status());
        Assert.assertEquals("Created", apiPostResponse.statusText());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode postJsonResponse = objectMapper.readTree(apiPostResponse.body());
        userId = postJsonResponse.get("id").asText();
    }

    @Then("the user can be retrieved by id with status code 200")
    public void the_user_can_be_retrieved_by_id_with_status_code_200() {
        apiGetResponse = requestContext.get("https://gorest.co.in/public/v2/users/" + userId,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer f4bd5cb99e27882658a2233c4ddd1e8a14f49788f92938580971a46439aa774f"));
        Assert.assertEquals(200, apiGetResponse.status());
        Assert.assertEquals("OK", apiGetResponse.statusText());
    }

    @Then("the response should contain the created user's id and email")
    public void the_response_should_contain_the_created_user_id_and_email() {
        String responseText = apiGetResponse.text();
        Assert.assertTrue(responseText.contains(userId));
        Assert.assertTrue(responseText.contains(emailId));
    }
} 