package com.tonic.factory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

public class PlaywrightFactory {

	private Properties prop;
	private static Browser browser;
	private static BrowserContext browserContext;
	private static Page page;
	private static Playwright playwright;
	
	// Screenshots taken using this method will be used in reports
	public static String takeScreenshot() {
		String path = System.getProperty("user.dir") + "/screenshots/" + System.currentTimeMillis() + ".png";
		
		// Create the directory if it doesn't exist
		Path screenshotDir = Paths.get(System.getProperty("user.dir") + "/screenshots/");
		try {
			if (!Files.exists(screenshotDir)) {
				Files.createDirectories(screenshotDir);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (page != null) {
			byte[] buffer = page.screenshot(new Page.ScreenshotOptions()
					.setPath(Paths.get(path))
					.setFullPage(true));
			
			return Base64.getEncoder().encodeToString(buffer);
		}
		return null;
	}
	
	/**
	 * Initialize browser based on given browser name
	 */
	public Page initBrowser(Properties prop) {
		String browserName = prop.getProperty("browser").trim();
		System.out.println("Browser name is: " + browserName);
		
		// Create playwright instance
		playwright = Playwright.create();
		
		switch (browserName.toLowerCase()) {
		case "chromium":
			browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
			break;
		case "firefox":
			browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
			break;
		case "safari":
			browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
			break;
		case "chrome":
			browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
					.setChannel("chrome")
					.setHeadless(false));
			break;
		case "edge":
			browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
					.setChannel("msedge")
					.setHeadless(false));
			break;
			
		default:
			System.out.println("Please pass the correct browser name... " + browserName);
			break;
		}
		
		browserContext = browser.newContext();
		
		// Start tracing before creating / navigating a page
		browserContext.tracing().start(new Tracing.StartOptions()
				.setScreenshots(true)
				.setSnapshots(true)
				.setSources(true));
		
		page = browserContext.newPage();
		
		// Navigate to application URL
		page.navigate(prop.getProperty("url").trim());
		
		return page;
	}
	
	/**
	 * Initialize properties from config file 
	 */
	public Properties init_prop() {
		try {
			FileInputStream ip = new FileInputStream("./src/test/resources/config/config.properties");
			prop = new Properties();
			prop.load(ip);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
