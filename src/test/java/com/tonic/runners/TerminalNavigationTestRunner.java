package com.tonic.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features/terminal_navigation.feature",
    glue = "com.tonic.steps"
)
public class TerminalNavigationTestRunner extends AbstractTestNGCucumberTests {} 