package com.tonic.utils;

import com.tonic.pages.BasePage;
import com.tonic.enums.ConfigProperties;

import java.util.Random;



public class ApplicationUtils extends BasePage {

	public ApplicationUtils(){
	}

	public static String getRandomString(int stringLength) {
		String randomString = "ABCDabcd1234567890";
		Random rand = new Random();
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < stringLength; i++) {
			int randIndex = rand.nextInt(randomString.length());
			res.append(randomString.charAt(randIndex));
		}
		return res.toString();
	}

	public static String getRandomPassword() {
		String prefix = PropertyBuilder.getPropValue(ConfigProperties.PASSWORDPREFIX);
		String password = prefix + getRandomString(4);
		return password;
	}
}
