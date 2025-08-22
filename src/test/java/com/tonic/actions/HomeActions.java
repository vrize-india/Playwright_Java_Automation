package com.tonic.actions;


import com.microsoft.playwright.Page;
import com.tonic.common.web.BasePage;
import com.tonic.enums.ButtonConstants;
import com.tonic.factory.PlaywrightFactory;

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

    public void navigateToSpecificPageFromHomePageHeaders(String config, String specificPage) {

        try {
            page.waitForLoadState();
            clickDynamicElement(HEADER_MENU, ButtonConstants.fromKey("HOME").getValue(),2000);
            page.waitForLoadState();
            clickDynamicElement(HEADER_MENU,config,2000);
            page.waitForLoadState();
            clickDynamicElement(HEADER_MENU,specificPage,2000);
            page.waitForLoadState();
            System.out.println("User is on the "+specificPage+" Page");
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to " + specificPage + " Page", e);
        }
    }
    /**
     * Navigates to the specified home screen by clicking the corresponding dynamic element in the page header.
     * This method waits for the page to finish loading before attempting to click the element. If the action fails,
     * it throws a RuntimeException with a detailed message including the name of the home screen.
     * @param homeScreen the name of the home screen to navigate
     * @throws RuntimeException if the navigation fails due to an exception while clicking the element
     */

    public void navigateToHomeScreen(String homeScreen) {
        try {
            page.waitForLoadState();
            clickDynamicElement(PAGE_HEADER,homeScreen,10000);
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to " + homeScreen + " Page", e);
        }
    }
}
