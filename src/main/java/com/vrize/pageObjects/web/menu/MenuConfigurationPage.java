package com.vrize.pageObjects.web.menu;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.vrize.common.web.BasePage;
import com.vrize.enums.BOHConstants;
import com.vrize.enums.BOHDataConstants;
import com.vrize.enums.ButtonConstants;
import com.vrize.utils.ApplicationUtils;
import org.testng.Assert;

import java.util.List;

import static org.testng.Assert.assertTrue;


public class MenuConfigurationPage extends BasePage {
    private ApplicationUtils apputils;
    private String seperator ="of";
    private String categoryName;

    // =============== Constructor ===============

    public MenuConfigurationPage(Page page) {
        super(page);
    }

    // =============== Methods ===============

}
