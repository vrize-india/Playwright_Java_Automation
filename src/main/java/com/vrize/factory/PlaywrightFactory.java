package com.vrize.factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.vrize.enums.ConfigProperties;
import com.vrize.utils.ConfigManager;
import com.vrize.utils.PropertyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class to initialize and manage Playwright browser instances, contexts, and pages using ThreadLocal for parallel execution.
 *
 * @author Gaurav Purwar
 */
public class PlaywrightFactory {

	private static final Logger logger = LoggerFactory.getLogger(PlaywrightFactory.class);

	private static final ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
	private static final ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
	private static final ThreadLocal<BrowserContext> tlBrowserContext = new ThreadLocal<>();
	private static final ThreadLocal<Page> tlPage = new ThreadLocal<>();

	public static Playwright getPlaywright() {
		return tlPlaywright.get();
	}

	public static Browser getBrowser() {
		return tlBrowser.get();
	}

	public static BrowserContext getBrowserContext() {
		return tlBrowserContext.get();
	}

	public static Page getPage() {
		return tlPage.get();
	}

	public static void setPlaywright(Playwright playwright) {
		tlPlaywright.set(playwright);
	}

	public static void setBrowser(Browser browser) {
		tlBrowser.set(browser);
	}

	public static void setPage(Page page) {
		tlPage.set(page);
	}

	public static Page initBrowser(Properties prop) {
		String browserName = ConfigManager.getBrowser().trim().toLowerCase();
		
		// Add thread detection logs
		logger.info("=== BROWSER LAUNCH TRACKING ===");
		logger.info("Thread ID: {}", Thread.currentThread().getId());
		logger.info("Thread Name: {}", Thread.currentThread().getName());
		logger.info("Launching browser: {}", browserName);

		tlPlaywright.set(Playwright.create());

		boolean isHeadless = Boolean.parseBoolean(PropertyBuilder.getPropValue(ConfigProperties.HEADLESS_MODE));
		BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(isHeadless);

		switch (browserName) {
			case "chromium":
				tlBrowser.set(getPlaywright().chromium().launch(options));
				break;
			case "chrome":
				options.setChannel("chrome");
				tlBrowser.set(getPlaywright().chromium().launch(options));
				break;
			case "edge":
				options.setChannel("msedge");
				tlBrowser.set(getPlaywright().chromium().launch(options));
				break;
			case "firefox":
				tlBrowser.set(getPlaywright().firefox().launch(options));
				break;
			case "safari":
			case "webkit":
				tlBrowser.set(getPlaywright().webkit().launch(options));
				break;
			default:
				throw new IllegalArgumentException("Unsupported browser: " + browserName);
		}

		logger.info("=== BROWSER INSTANCE CREATED ===");
		logger.info("Thread ID: {}", Thread.currentThread().getId());
		logger.info("Browser instance launched successfully");

		tlBrowserContext.set(getBrowser().newContext());
		getBrowserContext().tracing().start(
				new Tracing.StartOptions()
						.setScreenshots(true)
						.setSnapshots(true)
						.setSources(true)
		);

		tlPage.set(getBrowserContext().newPage());

		String urlPropertyName = PropertyBuilder.getPropValue(ConfigProperties.URL_PROPERTY_NAME);
		if (urlPropertyName == null || urlPropertyName.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing 'urlpropertyname' in config.properties.");
		}

		String url = PropertyBuilder.getEnvPropValue(urlPropertyName);
		if (url == null || url.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing URL for property '" + urlPropertyName + "' in environment.properties.");
		}

		logger.info("=== NAVIGATING TO URL ===");
		logger.info("Thread ID: {}", Thread.currentThread().getId());
		logger.info("Navigating to URL: {}", url);
		getPage().navigate(url);

		logger.info("=== BROWSER INITIALIZATION COMPLETED ===");
		logger.info("Thread ID: {}", Thread.currentThread().getId());
		logger.info("Browser ready for test execution");

		return getPage();
	}

	public static Properties initProp() {
		Properties prop = new Properties();
		Path configPath = Paths.get("src", "main", "resources", "config.properties");
		try (FileInputStream ip = new FileInputStream(configPath.toFile())) {
			prop.load(ip);
			logger.info("Loaded config properties from: {}", configPath);
		} catch (IOException e) {
			logger.error("Error loading config.properties: {}", e.getMessage(), e);
		}
		return prop;
	}

	/**
	 * Cleanup method to close browser instances with thread tracking
	 */
	public static void cleanup() {
		logger.info("=== BROWSER CLEANUP TRACKING ===");
		logger.info("Thread ID: {}", Thread.currentThread().getId());
		logger.info("Thread Name: {}", Thread.currentThread().getName());
		
		try {
			if (getPage() != null) {
				logger.info("Closing page for Thread ID: {}", Thread.currentThread().getId());
				getPage().close();
				tlPage.remove();
			}
			
			if (getBrowserContext() != null) {
				logger.info("Closing browser context for Thread ID: {}", Thread.currentThread().getId());
				getBrowserContext().close();
				tlBrowserContext.remove();
			}
			
			if (getBrowser() != null) {
				logger.info("Closing browser for Thread ID: {}", Thread.currentThread().getId());
				getBrowser().close();
				tlBrowser.remove();
			}
			
			if (getPlaywright() != null) {
				logger.info("Closing playwright for Thread ID: {}", Thread.currentThread().getId());
				getPlaywright().close();
				tlPlaywright.remove();
			}
			
			logger.info("=== CLEANUP COMPLETED ===");
			logger.info("Thread ID: {}", Thread.currentThread().getId());
			
		} catch (Exception e) {
			logger.error("=== CLEANUP ERROR ===");
			logger.error("Thread ID: {}", Thread.currentThread().getId());
			logger.error("Error during cleanup: {}", e.getMessage(), e);
		}
	}
}

