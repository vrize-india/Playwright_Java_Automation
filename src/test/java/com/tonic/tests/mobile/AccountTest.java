package com.tonic.tests.mobile;

import com.tonic.annotations.TonicAnnotation;
import com.tonic.enums.CategoryType;
import com.tonic.utils.JiraPolicy;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import org.openqa.selenium.By;
import com.tonic.driver.DriverManager;

@Epic("Mobile Android Tests")
@Feature("Android Account Features")
public class AccountTest extends BaseTest {

    @TonicAnnotation(category = {CategoryType.ANDROID_MOBILE})
    @JiraPolicy(logTicketReady=true)
    @Test
    @Story("Mobile Demo")
    @Description("Basic mobile app test demonstration")
    @Severity(SeverityLevel.NORMAL)
    public void mobileAppDemo() throws Exception {
        // Create test entry in ExtentReports
        test = extent.createTest("Mobile App Demo", "Basic mobile app test demonstration");
        
        try {
            // App is already initialized in BaseTest.setUpAppium
            System.out.println("Starting mobile app test");
            
            // Take screenshot
            String base64Screenshot = takeAppiumScreenshot();
            System.out.println("Screenshot taken");
            
            // Simple app interaction example
            try {
                if (DriverManager.getDriver().findElements(By.xpath("//*[@text='ALLOW']")).size() > 0) {
                    DriverManager.getDriver().findElement(By.xpath("//*[@text='ALLOW']")).click();
                    System.out.println("Clicked ALLOW button");
                }
            } catch (Exception e) {
                System.out.println("No permission dialog found: " + e.getMessage());
            }
            
            // Test completed successfully
            test.pass("Test completed successfully");
            System.out.println("Test completed successfully");
            
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 