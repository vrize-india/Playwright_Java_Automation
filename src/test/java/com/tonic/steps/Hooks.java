package com.tonic.steps;

import com.tonic.factory.PlaywrightFactory;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {
    private Playwright playwright;
    private Browser browser;
    private Page page;

    @Before
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
        PlaywrightFactory.setPage(page);
    }

    @After
    public void tearDown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
} 