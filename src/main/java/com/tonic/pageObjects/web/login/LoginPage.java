package com.tonic.pageObjects.web.login;

import com.microsoft.playwright.Page;
import com.tonic.utils.PropertyBuilder;

public class LoginPage {

	private Page page;

	// Updated String Locators - OR
	private String emailId = "//*[@id='inputLogInEmail']";
	private String password = "//*[@id='inputLogInPassword']";
	private String loginBtn = "//*[@id='buttonLogIn']";
	private String forgotPwdLink = "//*[@id='linkForgotPassword']";
	private String home = "//a[text()='Home']";

	// 2. page constructor:
	public LoginPage(Page page) {
		this.page = page;
	}
	
	// 3. page actions/methods:
	public String getLoginPageTitle() {
		return page.title();
	}
	
	public boolean isForgotPwdLinkExist() {
		return page.isVisible(forgotPwdLink);
	}
	
	public boolean performLogin(String appUserName, String appPassword) {
		System.out.println("App creds: " + appUserName + ":" + appPassword);
		page.fill(emailId, appUserName);
		page.fill(password, appPassword);
		page.click(loginBtn);
		page.locator(home).waitFor();
		if(page.locator(home).isVisible()) {
			System.out.println("user is logged in successfully....");
			return true;
		}else {
			System.out.println("user is not logged in successfully....");
			return false;
		}
	}

	public void navigateToLogin() {
		page.navigate(PropertyBuilder.getEnvPropValue("url"));
	}

	public void login(String username, String password) {
		page.fill(emailId, username);
		page.fill(this.password, password);
		page.click(loginBtn);
		// Wait for dashboard to load (Configuration menu visible)
		page.locator("text=Configuration").waitFor();
	}
}
