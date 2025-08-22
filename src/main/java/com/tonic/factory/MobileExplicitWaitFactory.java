package com.tonic.factory;

import com.tonic.constants.FrameworkConstants;
import com.tonic.driver.MobileDriver;
import com.tonic.enums.WaitEnums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.Optional;

/**
 * Utility class for waiting on mobile elements using different strategies.
 * Returns elements safely using Optional.
 */
public final class MobileExplicitWaitFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileExplicitWaitFactory.class);

    private MobileExplicitWaitFactory() {}

    public static Optional<WebElement> waitForElement(WaitEnums waitStrategy, By by) {
        return waitForElement(waitStrategy, by, FrameworkConstants.getExplicitWait());
    }

    public static Optional<WebElement> waitForElement(WaitEnums waitStrategy, By by, int timeoutInSeconds) {
        try {
            LOGGER.debug("Applying wait strategy: {}, Timeout: {}s, Locator: {}", waitStrategy, timeoutInSeconds, by);
            WebDriverWait wait = new WebDriverWait(MobileDriver.getDriver(), Duration.ofSeconds(timeoutInSeconds));

            switch (waitStrategy) {
                case CLICKABLE:
                    return Optional.of(wait.until(ExpectedConditions.elementToBeClickable(by)));
                case PRESENCE:
                    return Optional.of(wait.until(ExpectedConditions.presenceOfElementLocated(by)));
                case VISIBLE:
                    return Optional.of(wait.until(ExpectedConditions.visibilityOfElementLocated(by)));
                case NONE:
                    return Optional.of(MobileDriver.getDriver().findElement(by));
                default:
                    LOGGER.warn("Unknown wait strategy: {}. Returning empty.", waitStrategy);
                    return Optional.empty();
            }
        } catch (Exception e) {
            LOGGER.error("Element not found using strategy: {}, Locator: {}, Error: {}", waitStrategy, by, e.getMessage());
            return Optional.empty();
        }
    }
}