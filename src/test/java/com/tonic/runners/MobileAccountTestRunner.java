package com.tonic.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features/mobile_account.feature",
    glue = "com.tonic.steps"
)
public class MobileAccountTestRunner extends AbstractTestNGCucumberTests {} 