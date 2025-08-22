package com.vrize.common.web;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.vrize.pageObjects.web.configuration.ConfigurationLocators;
import com.vrize.pageObjects.web.menu.MenuLocators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import java.util.List;

/**
 * BasePage provides reusable Playwright actions and dynamic locator helpers for all web page objects.
 */
public class BasePage implements MenuLocators, ConfigurationLocators {
    protected final Page page;
    public static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);


    /**
     * Constructor to initialize the Playwright Page instance.
     * @param page Playwright Page object
     */
    public BasePage(Page page) {
        this.page = page;
    }

    /**
     * Clicks an element by its tag and visible dynamicText.
     * @param tag HTML tag of the element (e.g., "button", "span")
     * @param dynamicText Visible dynamicText of the element
     */
    protected void clickByText(String tag, String dynamicText, int timeoutMillis) {
        waitForSelector(tag, dynamicText, timeoutMillis);
        getElementByText(tag, dynamicText).click();
    }

    /**
     * Clicks an element identified by a dynamic locator string.
     *
     * @param selectorTemplate The locator template with a placeholder (e.g., "//button[text()='%s']").
     * @param dynamicValue     The value to inject into the template.
     * @param timeoutInMillis  Timeout to wait for the element before clicking.
     */
    public void clickDynamicElement(String selectorTemplate, String dynamicValue, int timeoutInMillis) {
        try {
            String resolvedSelector = String.format(selectorTemplate, dynamicValue);
            page.waitForSelector(resolvedSelector, new Page.WaitForSelectorOptions().setTimeout(timeoutInMillis));
            page.locator(resolvedSelector).click();
            LOGGER.info("Clicked element with locator: " + resolvedSelector);
        } catch (Exception e) {
            throw new RuntimeException("Failed to click dynamic element for value: " + dynamicValue, e);
        }
    }

    public void clickElement(String selector, int timeoutInMillis) {
        try {
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(timeoutInMillis));
            page.locator(selector).click();
            LOGGER.info("Clicked element with locator: " + selector);
        } catch (Exception e) {
            throw new RuntimeException("Failed to click dynamic element for value: " + selector, e);
        }
    }

    /**
     * Fills the specified input field with the provided value after waiting for visibility.
     *
     * @param selector        CSS or XPath selector of the input field.
     * @param value           The value to enter in the field.
     * @param timeoutInMillis Timeout in milliseconds to wait for the field to appear.
     */
    public void fillField(String selector, String value, int timeoutInMillis) {
        try {
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(timeoutInMillis));
            page.fill(selector, value);
            LOGGER.info("Filled value '" + value + "' into field: " + selector);
        } catch (Exception e) {
            throw new RuntimeException("Unable to fill value into input field: " + selector, e);
        }
    }


    /**
     * Enters text into an element located by tag and visible text.
     * @param tag HTML tag of the input element
     * @param dynamicText Visible text to match
     * @param textToEnter Text to enter
     */
    protected void enterTextByText(String tag, String dynamicText, String textToEnter, int timeoutMillis) {
        waitForSelector(tag, dynamicText, timeoutMillis);
        getElementByText(tag, dynamicText).fill(textToEnter);
    }

    /**
     * Checks if an element is visible by tag and visible dynamicText.
     * @param tag HTML tag of the element
     * @param dynamicText Visible dynamicText of the element
     * @return true if visible, false otherwise
     */
    protected boolean isVisibleByText(String tag, String dynamicText, int timeoutMillis) {
        waitForSelector(tag, dynamicText, timeoutMillis);
        return getElementByText(tag, dynamicText).isVisible();
    }


    /**
     * Waits for the element to become visible within the specified timeout,
     * asserts its visibility, and returns the result.
     *
     * @param selector    The CSS or XPath selector of the element.
     * @param elementName A friendly name used in assertion and logging.
     * @return true if the element is visible; fails otherwise.
     */
    public boolean isVisible(String selector, String elementName) {
        int timeoutInMillis = 20000;
        try {
            page.locator(selector).waitFor(new Locator.WaitForOptions().setTimeout(timeoutInMillis));
            boolean visible = page.locator(selector).isVisible();
            LOGGER.info(elementName +" is visible");
            Assert.assertTrue(visible, elementName + " is not visible after waiting " + timeoutInMillis + "ms");
            return visible;
        } catch (Exception e) {
            LOGGER.error("Failed to find or wait for " + elementName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Waits for the element to become visible within the specified timeout,
     * asserts its visibility, and returns the result.
     *
     * @param locator    The CSS or XPath selector of the element.
     * @param dynamicText Visible dynamicText of the element
     * @return true if the element is visible; fails otherwise.
     */
//    public boolean isVisible(String locator, String dynamicText, int timeoutInMillis) {
//        String selector = buildDynamicLocator(locator, dynamicText);
//        try {
//            waitForSelector(selector, timeoutInMillis);
//            return page.locator(selector).isVisible();
//        } catch (Exception e) {
//            // Optional: log the error
//            LOGGER.info("Element not visible: " + selector + " | Error: " + e.getMessage());
//            return false;
//        }
    public boolean isVisible(String locator, String dynamicText, int timeoutInMillis) {
        String selector = buildDynamicLocator(locator, dynamicText);
        try {
            waitForSelector(selector, timeoutInMillis);
            return page.locator(selector).isVisible();
        } catch (Exception e) {
            // Optional: log the error
         LOGGER.info("Element not visible: " + selector + " | Error: " + e.getMessage());
            return false;
        }
    }
    /**
     * Waits for the element to become checked within the specified timeout,
     * @param selector    The CSS or XPath selector of the element.
     * @return true if the element is checked; fails otherwise.
     */
    public boolean isChecked(String selector, int timeoutInMillis) {
        try {
            waitForSelector(selector, timeoutInMillis);
            return page.locator(selector).isChecked();
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify if element is checked with selector: " + selector, e);
        }
    }


    /**
     * Returns a Locator for an element containing the given dynamicText.
     * @param tag The HTML tag to search for (e.g., "button", "span", "div")
     * @param dynamicText The visible dynamicText to match
     * @return Locator for the element
     */
    protected Locator getElementByText(String tag, String dynamicText) {
        String xpath = String.format("//%s[contains(dynamicText(),'%s')]", tag, dynamicText);
        return page.locator(xpath);
    }

    /**
     * Returns a Locator for an element containing the given text.
     * @param text The visible text to match
     * @return Locator for the element
     */
    public Locator getElementByText(String text) {
        String xpath = String.format("//*[contains(text(),'%s')]", text);
        return page.locator(xpath);
    }

    /**
     * Returns a Locator for an element containing the given dynamicText.
     * @param selector The CSS or XPath selector used to locate elements
     * @param dynamicText The visible dynamicText to match
     * @return Locator for the element

     */
    public Locator getElementByText(String selector, String dynamicText, int timeoutMillis) {
        String xpath = String.format(selector,dynamicText);
        waitForSelector(xpath,timeoutMillis);
        return page.locator(xpath);
    }

    /**
     * Clears the content of an input field.
     * @param selector The CSS or XPath selector used to locate elements
     * @param timeoutMillis Timeout in milliseconds
     */
    public void clearField(String selector, int timeoutMillis) {
        waitForSelector(selector,timeoutMillis);
         page.locator(selector).fill("");
    }

    /**
     * Clears the content of an input field
     * @param tag The HTML tag to search for (e.g., "button", "span", "div")
     * @param dynamicText The visible dynamicText to match
     */
    protected void clearField(String tag, String dynamicText,int timeoutMillis) {
        String xpath = String.format("//%s[contains(text(),'%s')]", tag, dynamicText);
        waitForSelector(xpath,timeoutMillis);
        page.locator(xpath).fill("");
    }

    /**
     * Retrieves the text content of element matching the given selector.
     * @param  selector The CSS or XPath selector used to locate elements
     * @param timeoutMillis Timeout in milliseconds
     * @return A text content from  matching element.
     */
    public String getTextOfElement(String selector, int timeoutMillis) {
        waitForSelector(selector,timeoutMillis);
        return page.locator(selector).textContent();
    }

    /**
     * Waits for an element matching the selector to appear in the DOM with a custom timeout.
     * @param selector CSS or XPath selector of the element
     * @param timeoutMillis Timeout in milliseconds
     */
    public void waitForSelector(String selector, int timeoutMillis) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout((double) timeoutMillis));
    }

    /**
     * Waits for an element matching the tag and visible text to appear in the DOM.
     * Uses default timeout if timeoutMillis is null or <= 0.
     * @param tag HTML tag of the element (e.g., "button", "span")
     * @param dynamicText Visible dynamicText of the element
     * @param timeoutMillis Optional timeout in milliseconds
     */
    protected void waitForSelector(String tag, String dynamicText, Integer timeoutMillis) {
        Locator locator = getElementByText(tag, dynamicText);
        if (timeoutMillis != null && timeoutMillis > 0) {
            locator.waitFor(new Locator.WaitForOptions().setTimeout((double) timeoutMillis));
        } else {
            locator.waitFor(); // Use default 30 sec timeout
        }
    }

    /**
     * Waits for the given selector to be visible and returns all text contents of the matching elements.
     * @param selector The CSS or XPath selector for the list of elements.
     * @param timeoutInMillis Timeout in milliseconds to wait for visibility.
     * @return List of text contents.
     */
    public List<String> getVisibleElementsText(String selector, int timeoutInMillis) {
        List<String> listOfStrings;
        try {
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(timeoutInMillis));
            Locator elements = page.locator(selector);
            listOfStrings = elements.allTextContents();
            return listOfStrings;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get elements for selector: " + selector, e);
        }
    }

    /**
     * Replaces placeholders in a selector template with dynamic values.
     *
     * @param template   The locator template, e.g., "//div[text()='%s']//button".
     * @param values      The value to insert into the placeholder.
     * @return           The formatted locator string.
     */
    public String buildDynamicLocator(String template, String... values) {
        return String.format(template, (Object[]) values);
    }

    /**
     * Enters text into an input field based on a given HTML attribute and value.
     *
     * @param attributeName   The HTML attribute name (e.g., "placeholder", "name", "id")
     * @param attributeValue  The value of the attribute to match
     * @param textToEnter     The text to enter into the field
     * @param timeoutMillis   Time in milliseconds to wait for the element to be visible
     */
    protected void enterTextByAttribute(String attributeName, String attributeValue, String textToEnter, int timeoutMillis) {
        String xpath = String.format("//*[@%s='%s']", attributeName, attributeValue);
        try {
            page.waitForSelector(xpath, new Page.WaitForSelectorOptions()
                    .setTimeout(timeoutMillis)
                    .setState(WaitForSelectorState.VISIBLE));

            page.locator(xpath).fill(textToEnter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter text into element with " + attributeName + "='" + attributeValue + "'", e);
        }
    }

    /**
     * Enters text into an element
     * @param locatorTemplate XPath or CSS template
     * @param textToEnter Value to be searched
     * @param timeoutMillis Timeout in milliseconds
     */
    public void enterText(String locatorTemplate, String textToEnter, int timeoutMillis) {
        try {
            Locator locator = page.locator(locatorTemplate).first();
            // Wait for element visibility
            locator.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMillis));
            // Clear field and enter text
            locator.clear();
            locator.fill(textToEnter);
            LOGGER.info("Successfully entered text: '" + textToEnter + "' into: " + locatorTemplate);
        } catch (Exception e) {
            LOGGER.error("Failed to enter text into: " + locatorTemplate + ". Error: " + e.getMessage());
            // Optionally rethrow or log to test report
            throw new RuntimeException("Error while entering text: " + textToEnter, e);
        }
    }

    /**
     * Waits for an element to be visible
     * @param selector CSS or XPath selector of the element
     * @param timeoutMillis Timeout in milliseconds
     * @param dynamicText The visible dynamicText to match
     */
    public void waitUntilElementIsVisible(String selector, String dynamicText, int timeoutMillis) {
        String xpath = String.format(selector,dynamicText);
        page.waitForSelector(xpath, new Page.WaitForSelectorOptions().setTimeout((double) timeoutMillis)).isVisible();
    }

    /**
     * Returns the number of elements found for a given selector.
     *
     * @param selector The CSS or XPath locator.
     * @return The number of matching elements.
     */
    public int getElementCount(String selector) {
        try {
            Locator locator = page.locator(selector);
            return locator.count();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get element count for selector: " + selector, e);
        }
    }

    /**
     * Performs a mouse click at the given (x, y) coordinates on the page.
     * @param x X-coordinate where the mouse click should happen
     * @param y Y-coordinate where the mouse click should happen
     */
    public void click(int x, int y) {
        page.mouse().click(x, y);
    }

    // Basic reload method
    public void reloadPage() {
        page.reload();
    }

   /**
    * Retrieves all Locator elements matching the given selector.
    * @param selector The CSS or XPath selector used to locate elements.
    * @return List of Locator elements.
    */
    public List<Locator> getAllElements(String selector, int timeoutMillis) {
        waitForSelector(selector, timeoutMillis);
        return page.locator(selector).all();
    }

    /**
     * Clicks an element by a dynamic locator (e.g., with text).
     * @param locator XPath or CSS template with %s for dynamic part
     * @param timeoutMillis Timeout in milliseconds
     */
    public void click(String locator, int timeoutMillis) {
        try {
            waitForSelector(locator, timeoutMillis);
            page.locator(locator).click(new Locator.ClickOptions().setTimeout(timeoutMillis));
        } catch (Exception e) {
            LOGGER.error("ERROR: Failed to click on element with locator '" + locator + "' within " + timeoutMillis + " ms.");
            throw new RuntimeException("Click failed for locator: " + locator, e);
        }
    }

    /**
     * Returns a Locator for the given selector.
     * @param selector The CSS or XPath selector string.
     * @return A Locator that represents the element.
     */
    public Locator getElement(String selector, int timeoutMillis) {
        try {
            waitForSelector(selector, timeoutMillis);
            return page.locator(selector);
        } catch (Exception e) {
            LOGGER.error("ERROR: Failed to locate element with selector '" + selector + "' within " + timeoutMillis + " ms.");
            throw new RuntimeException("Element not found: " + selector, e);
        }
    }

    /**
     * Press Enter
     * @param locator XPath or CSS template with %s for dynamic part
     * @param timeoutMillis Timeout in milliseconds
     * @param key from keyboard
     */
    public void pressRequiredKeyInKeyBoard(String locator,String key, int timeoutMillis) {
        waitForSelector(locator, timeoutMillis);
        page.locator(locator).press(key);
    }

    /**
     * Performs a force click on the element using the provided locator string (XPath or CSS).
     * @param selector The selector of the element to click (XPath or CSS).
     */
    public void forceClick(String selector, int timeoutMillis) {
        waitForSelector(selector, timeoutMillis);
        page.locator(selector).click(new Locator.ClickOptions().setForce(true));
    }

    /**
     * Clicks an element using a Playwright Locator.
     *
     * @param locator       The Playwright Locator of the element to click.
     * @param timeoutMillis Timeout in milliseconds to wait for the element before clicking.
     */
    public void click(Locator locator, int timeoutMillis) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setTimeout((double) timeoutMillis));
            locator.click(new Locator.ClickOptions().setTimeout(timeoutMillis));
            LOGGER.info("Clicked element: {}", locator);
        } catch (Exception e) {
            LOGGER.error("ERROR: Failed to click on element [{}] within {} ms", locator, timeoutMillis, e);
            throw new RuntimeException("Click failed for locator: " + locator, e);
        }
    }
}
