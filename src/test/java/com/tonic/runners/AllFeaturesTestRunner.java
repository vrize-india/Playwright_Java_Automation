package com.tonic.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.tonic.steps",
    plugin = {"pretty", "summary"}
)
public class AllFeaturesTestRunner extends AbstractTestNGCucumberTests {} 