package com.tonic.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features/api_post_create_user.feature",
    glue = "com.tonic.steps"
)
public class ApiPostCreateUserTestRunner extends AbstractTestNGCucumberTests {} 