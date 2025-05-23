package com.tonic.pages;


import com.google.common.collect.ImmutableList;
import com.tonic.constants.FrameworkConstants;
import com.tonic.driver.DriverManager;
import com.tonic.enums.LogType;
import com.tonic.enums.WaitLogic;

import com.tonic.factory.ExplicitWaitFactory;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;

import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import org.testng.Assert;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;

import static com.tonic.factory.ExplicitWaitFactory.waitExplicitlyForElement;
import static com.tonic.utils.FrameworkLogger.log;

public class BasePage {
    protected HashMap<String, String> androidXpath = new HashMap<>();
    protected HashMap<String, String> iosXpath = new HashMap<>();

    public void tap(By by, WaitLogic waitStrategy, String elementName) {
        WebElement element = waitExplicitlyForElement(waitStrategy, by);
        element.click();
        log(LogType.EXTENTANDCONSOLE, elementName + " is clicked");
    }

    public String getTexts(By by, WaitLogic waitStrategy, String elementName) {
        WebElement element = waitExplicitlyForElement(waitStrategy, by);
        return element.getText();
    }

    public String getATT(By by, WaitLogic waitStrategy, String elementName, String attr) {
        WebElement element = waitExplicitlyForElement(waitStrategy, by);
        return element.getAttribute(attr);
    }

    public void tap(WebElement webElement, String elementName) {
        webElement.click();
        log(LogType.EXTENTANDCONSOLE, elementName + " is clicked");
    }

    public void sendKeys(By by, CharSequence value, WaitLogic waitStrategy, String elementName, boolean shouldScroll) throws InterruptedException {
        if (shouldScroll) {
            scrollTo(by, elementName);
        }
        WebElement element = waitExplicitlyForElement(waitStrategy, by);
        element.sendKeys(value);
        System.out.println(value + " is entered successfully in " + elementName + " input field");
    }

    public void sendKeys(By by, CharSequence value, WaitLogic waitStrategy, String elementName) {
        if (DriverManager.getDriver().getClass().equals(IOSDriver.class)) {
            scrollTo(by, elementName);
        }

        WebElement element = waitExplicitlyForElement(waitStrategy, by);
        element.sendKeys(value);
        System.out.println(value + " is entered successfully in " + elementName + " input field");
    }

    public static boolean isVisible(By by, String elementName) {
        try {
            WebElement element = DriverManager.getDriver().findElement(by);
            if (element.isDisplayed()) {
                System.out.println(elementName + " is visible");
                return true;
            }
            System.out.println(elementName + " is present but not visible");
            return false;
        } catch (Exception e) {
            System.out.println(elementName + " is not present");
            return false;
        }
    }

    public boolean isVisible(By by, String elementName, int timeout) {
        try {
            waitExplicitlyForElement(WaitLogic.VISIBLE, by, timeout);
            return isVisible(by, elementName);
        } catch (Exception e) {
            System.out.println(elementName + " is not visible");
            return false;
        }
    }

    public static void scrollTo(By by, String elementName) {
        scrollTo(by, elementName, "down");
    }

    public static void scrollTo(By by, String elementName, String scrollDirection) {
        while (!isVisible(by, elementName)) {
            Dimension size = DriverManager.getDriver().manage().window().getSize();
            Point midPoint = new Point((int) (size.width * 0.5), (int) (size.height * 0.5));
            //midpoint is basically storing the X and Y Co-ordinates of the screen
            int bottom = midPoint.y + (int) (midPoint.y * 0.2);
            int top = midPoint.y - (int) (midPoint.y * 0.2);
            int mid = (int) midPoint.y  - (int) (midPoint.y * 0.3);

            Point start = null;
            Point end = null;

            if(scrollDirection.equalsIgnoreCase("down")) {
                start = new Point(midPoint.x, bottom);
                end = new Point(midPoint.x, top);
            } else if (scrollDirection.equalsIgnoreCase("mid")) {
                start = new Point(midPoint.x, bottom);
                end = new Point(midPoint.x, mid);
            } else {
                start = new Point(midPoint.x, top);
                end = new Point(midPoint.x, bottom);
            }

            PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
            Sequence swipe = new Sequence(input, 0);
            swipe.addAction(input.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.x, start.y));
            swipe.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(input.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), end.x, end.y));
            swipe.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            DriverManager.getDriver().perform(ImmutableList.of(swipe));

            WaitForMiliSec(100);
        }
    }

    public static void WaitForMiliSec(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (Exception e) {
        }
    }

    public static boolean waitUntilElementIsVisible(WebDriver driver, By locator) {
        boolean flag = false;
        for (int i = 1; i <= FrameworkConstants.getExplicitWait(); i++) {
            try {
                if (driver.findElement(locator).isDisplayed() || driver.findElement(locator).isEnabled()) {
                    flag = true;
                    break;
                }
            } catch (Exception e) {
                WaitForMiliSec(1000);
                System.out.println("Trial " + i + " was not able to find element with " + locator);
            }
        }
        return flag;
    }

    public boolean waitUntilElementIsVisible(By locator) {
        boolean flag = false;
        for(int i = 1; i <= FrameworkConstants.getExplicitWait(); i++)
        {
            try {
                if(DriverManager.getDriver().findElement(locator).isDisplayed() || DriverManager.getDriver().findElement(locator).isDisplayed())
                {
                    flag = true;
                    break;
                }
            }
            catch(Exception e) {
                WaitForMiliSec(1000);
                System.out.println("Trial " + i + " was not able to find element with " + locator);
            }
        }
        return flag;
    }

    public boolean waitUntilElementIsVisible(By locator,int waitTime) {
        boolean flag = false;
        for(int i = 1; i <= waitTime; i++)
        {
            try {
                if(DriverManager.getDriver().findElement(locator).isDisplayed() || DriverManager.getDriver().findElement(locator).isDisplayed())
                {
                    flag = true;
                    break;
                }
            }
            catch(Exception e) {
                WaitForMiliSec(1000);
                System.out.println("Trial " + i + " was not able to find element with " + locator);
            }
        }
        return flag;
    }

    public void clearField(By by, WaitLogic waitStrategy, String elementName) {
        Assert.assertTrue(waitUntilElementIsVisible(DriverManager.getDriver(), by), elementName + " is not visible");
        WebElement element = waitExplicitlyForElement(waitStrategy, by);
        element.clear();
        log(LogType.EXTENTANDCONSOLE, elementName + " textField is cleared successfully");
    }

    public boolean isEnabled(By by, String elementName) {
        Assert.assertTrue(waitUntilElementIsVisible(DriverManager.getDriver(), by), elementName + " is not visible");
        boolean result = true;
        try {
            WebElement element = ExplicitWaitFactory.waitExplicitlyForElement(WaitLogic.VISIBLE, by);
            if (element.isEnabled()) {
                log(LogType.EXTENTANDCONSOLE, elementName + " is  enabled");
            } else {
                log(LogType.EXTENTANDCONSOLE, elementName + " is not enabled");
                result = false;
            }

        } catch (ElementNotInteractableException e) {
            log(LogType.EXTENTANDCONSOLE, elementName + " is not enabled");
            result = false;
        }
        return result;
    }

    public String getLocator(String name) {
        if (DriverManager.getDriver().getClass().equals(AndroidDriver.class)) {
            return androidXpath.get(name);
        }

        return iosXpath.get(name);
    }

    public static void swipeTo(){
        Dimension size = DriverManager.getDriver().manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = size.getHeight() / 2;
        int endX = (int)(size.getWidth() * 0.25);
        int endY = startY;
        PointerInput finger1 = new PointerInput (PointerInput.Kind. TOUCH,"finger1");
        Sequence sequence= new Sequence(finger1,1)
                .addAction (finger1.createPointerMove (Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                .addAction (finger1.createPointerDown(PointerInput.MouseButton. LEFT.asArg()))
                .addAction (new Pause(finger1, Duration.ofMillis(200)))
                .addAction(finger1.createPointerMove (Duration.ofMillis(100), PointerInput. Origin.viewport(), endX, endY))
                .addAction (finger1.createPointerUp (PointerInput.MouseButton.LEFT.asArg()));
        DriverManager.getDriver().perform(Collections.singletonList(sequence));
    }
}

