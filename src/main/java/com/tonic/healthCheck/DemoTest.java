package com.tonic.healthCheck;
import com.microsoft.playwright.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DemoTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false));
            BrowserContext context = browser.newContext();

            // Open new page
            Page page = context.newPage();

            // Go to https://demo.playwright.dev/todomvc/
            page.navigate("https://demo.playwright.dev/todomvc/");

            // Go to https://admin.test.ordyx.com/login
            page.navigate("https://admin.test.ordyx.com/login");

            // Click input[name="email"]
            page.locator("input[name=\"email\"]").click();

            // Fill input[name="email"]
            page.locator("input[name=\"email\"]").fill("Prasanna@vrize.com");

            // Click input[name="password"]
            page.locator("input[name=\"password\"]").click();

            // Fill input[name="password"]
            page.locator("input[name=\"password\"]").fill("Password@123");

            // Click text=visibility
            page.locator("text=visibility").click();

            // Click button:has-text("Log In")
            page.locator("button:has-text(\"Log In\")").click();

            // Go to https://admin.test.ordyx.com/app/v3/dashboard
            page.navigate("https://admin.test.ordyx.com/app/v3/dashboard");

            // Click text=Prasanna N
            page.locator("text=Prasanna N").click();

            // Click text=Configuration
            page.locator("text=Configuration").click();

            // Click text=Types
            page.locator("text=Types").click();
            assertThat(page).hasURL("https://admin.test.ordyx.com/system/payment/type");

            // Click text=keyboard_arrow_down
            page.locator("text=keyboard_arrow_down").click();

            // Click text=Logout
            page.locator("text=Logout").click();

            // Click text=Yes
            page.locator("text=Yes").click();
            assertThat(page).hasURL("https://admin.test.ordyx.com/login");
        }
    }
}