package com.vrize.actions;


import com.microsoft.playwright.Page;
import com.vrize.common.web.BasePage;
import com.vrize.enums.ButtonConstants;
import com.vrize.factory.PlaywrightFactory;

/**
 * Contains user authentication-related actions such as login, logout,
 * and navigation from Login to Home page.
 * @author Gaurav Purwar
 */
public class HomeActions extends BasePage {

    protected  final Page page;
    /**
     * Constructor to initialize the Playwright Page instance.
     */
    public HomeActions(Page page) {
        super(PlaywrightFactory.getPage());
        this.page = page;
    }

}
