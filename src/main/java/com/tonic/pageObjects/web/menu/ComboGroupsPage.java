package com.tonic.pageObjects.web.menu;
import com.microsoft.playwright.Page;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.tonic.common.web.BasePage;

/**
 * Page object representing the Combo Groups screen.
 * Provides methods to fetch combo group data and validate the visibility of icons.
 */

public class ComboGroupsPage extends BasePage  {


    public ComboGroupsPage(Page page) {
        super(page);
    }
    /**
     * Returns a list of all visible combo group names.
     * @return List of combo group names.
     */
    public List<String> getComboGroupsList() {
        List<String> listOfCombos;
        listOfCombos = getVisibleElementsText(COMBO_LIST,10000);
        return listOfCombos;
    }

    /**
     * Returns a map of combo names and their corresponding visibility status for a given dynamic locator template.
     * This method is useful when verifying the visibility of elements (e.g., toggle buttons, delete icons,edit icons)
     * associated with each combo in a list. It dynamically builds locators using the provided template and combo names,
     * checks visibility for each, and stores the result in a map.
     * @param specificItemELe The dynamic locator template (e.g., XPath or CSS)
     * @return A Map<String, Boolean> where the key is the combo name and the value is  true if the element
     *  is visible for that combo,  false otherwise.
     */
    public Map<String, Boolean> getElementVisibilityMapForEachCombo(String specificItemELe,String elementDescription) {
        List<String> comboNames = getVisibleElementsText(COMBO_LIST, 5000);
        Map<String, Boolean> visibilityMap = new LinkedHashMap<>();
        for (String comboName : comboNames) {
            String locator = buildDynamicLocator(specificItemELe, comboName);
            boolean isVisible = isVisible(locator, elementDescription+" for "+comboName);
            visibilityMap.put(comboName, isVisible);
        }
        return visibilityMap;
    }



}