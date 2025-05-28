package com.tonic.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features/add_terminal.feature",
    glue = "com.tonic.steps"
)
public class AddTerminalTestRunner extends AbstractTestNGCucumberTests {} 