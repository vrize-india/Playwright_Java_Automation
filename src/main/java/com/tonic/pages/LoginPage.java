package com.tonic.pages;

import com.microsoft.playwright.Page;

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
	
	public boolean doLogin(String appUserName, String appPassword) {
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
}
