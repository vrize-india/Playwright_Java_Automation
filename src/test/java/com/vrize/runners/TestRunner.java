package com.vrize.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import io.qameta.allure.testng.AllureTestNg;
import com.vrize.listeners.XrayListener;
// RetryListener import removed - using RetryUtility instead

@CucumberOptions(
        features = "src/test/resources/features",
        glue     = { "com.vrize.stepDefinitions", "com.vrize.hooks" },
        plugin   = {
                "pretty",
                "summary",
                "html:target/cucumber-report.html",
                "json:target/cucumber-report.json",
                "message:target/cucumber.ndjson",
                "com.aventstack.chaintest.plugins.ChainTestCucumberListener:" +
                        "target/chaintest-report",
                "com.vrize.listeners.XrayCucumberPlugin",
                "com.vrize.listeners.CucumberRetryListener"
        },
        // Native Cucumber Retry Configuration
        monochrome = true,
        dryRun = false
        //tags = "@XrayKey=TONIC-7438"
)
@Listeners({ AllureTestNg.class, XrayListener.class, com.vrize.listeners.CustomChainTestReporter.class })
public class TestRunner extends AbstractTestNGCucumberTests {

    static {
        // Disable Cucumber's built-in publishing
        System.setProperty("cucumber.publish.enabled", "false");
        
        // Enable enhanced retry functionality
        System.setProperty("retry.enabled", "true");
        System.setProperty("retry.immediate.rerun", "true");
        
        // Set default retry count if not specified
        if (System.getProperty("retry.count") == null) {
            System.setProperty("retry.count", "3");
        }
        
        // Set default rerun file path
        if (System.getProperty("cucumber.rerun.file") == null) {
            System.setProperty("cucumber.rerun.file", "target/rerun.txt");
        }
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
