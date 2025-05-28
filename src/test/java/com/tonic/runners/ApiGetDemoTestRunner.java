package com.tonic.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features/api_get_demo.feature",
    glue = "com.tonic.steps"
)
public class ApiGetDemoTestRunner extends AbstractTestNGCucumberTests {} 