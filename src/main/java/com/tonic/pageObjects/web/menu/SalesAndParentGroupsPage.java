package com.tonic.pageObjects.web.menu;

import com.microsoft.playwright.Page;
import com.tonic.common.web.BasePage;
import java.util.ArrayList;
import java.util.List;
public class SalesAndParentGroupsPage extends BasePage {
    /**
     * Constructor to initialize the Playwright Page instance.
     *
     * @param page Playwright Page object
     */

    public SalesAndParentGroupsPage(Page page) {
        super(page);
    }

    public List<String> isSalesAndParentGroupsVisible() {
        List<String> listOfSalesAndParentGroups;
        listOfSalesAndParentGroups = getVisibleElementsText(SALES_GROUP_LIST,10000);
        return listOfSalesAndParentGroups;
    }

    public List<Boolean> getIconsVisibility(List<String> names, String xpathTemplate, String iconType) {
        List<Boolean> visibilityList = new ArrayList<>();

        for (String name : names) {
            String element = String.format(xpathTemplate, name);
            boolean isElementVisible = isVisible(element, iconType + " for " + name);

            if (!isElementVisible) {
                System.out.println(iconType + " is NOT visible for: " + name);
            }

            visibilityList.add(isElementVisible);
        }
        return visibilityList;
    }
}