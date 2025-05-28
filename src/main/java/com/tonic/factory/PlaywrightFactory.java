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
	private static ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
	private static ThreadLocal<BrowserContext> tlBrowserContext = new ThreadLocal<>();
	private static ThreadLocal<Page> tlPage = new ThreadLocal<>();
	private static ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();

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

		if (getPage() != null) {
			byte[] buffer = getPage().screenshot(new Page.ScreenshotOptions()
					.setPath(Paths.get(path))
					.setFullPage(true));

			return Base64.getEncoder().encodeToString(buffer);
		}
		return null;
	}

	public static Playwright getPlaywright(){
		return tlPlaywright.get();
	}

	public static Browser getBrowser(){
		return tlBrowser.get();
	}

	public static BrowserContext getBrowserContext(){
		return tlBrowserContext.get();
	}

	public static Page getPage(){
		return tlPage.get();
	}

	/**
	 * Initialize browser based on given browser name
	 */
	public Page initBrowser(Properties prop) {
		String browserName = prop.getProperty("browser").trim();
		System.out.println("Browser name is: " + browserName);

		// Create playwright instance
		tlPlaywright.set(Playwright.create());

		switch (browserName.toLowerCase()) {
			case "chromium":
				tlBrowser.set(getPlaywright().chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)));
				break;
			case "firefox":
				tlBrowser.set(getPlaywright().firefox().launch(new BrowserType.LaunchOptions().setHeadless(false)));
				break;
			case "safari":
				tlBrowser.set(getPlaywright().webkit().launch(new BrowserType.LaunchOptions().setHeadless(false)));
				break;
			case "chrome":
				tlBrowser.set(getPlaywright().chromium().launch(new BrowserType.LaunchOptions()
						.setChannel("chrome")
						.setHeadless(false)));
				break;
			case "edge":
				tlBrowser.set(getPlaywright().chromium().launch(new BrowserType.LaunchOptions()
						.setChannel("msedge")
						.setHeadless(false)));
				break;

			default:
				System.out.println("Please pass the correct browser name... " + browserName);
				break;
		}

		tlBrowserContext.set(getBrowser().newContext());

		// Start tracing before creating / navigating a page
		getBrowserContext().tracing().start(new Tracing.StartOptions()
				.setScreenshots(true)
				.setSnapshots(true)
				.setSources(true));

		tlPage.set(getBrowserContext().newPage());

		// Navigate to application URL
		getPage().navigate(prop.getProperty("url").trim());

		return getPage();
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

	public static void setPage(Page page) {
		tlPage.set(page);
	}
}

