package com.vrize.utils;

import com.vrize.enums.ConfigProperties;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * Application related common functions to be written here
 * Author: Gaurav Purwar
 */
public class ApplicationUtils {

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
		        String prefix = PropertyBuilder.getPropValue(ConfigProperties.PASSWORD_PREFIX);
		String password = prefix + getRandomString(4);
		return password;
	}

	/**
	 Returns a random element from the given list.
	 @param elements the list to choose from
	 @return a random element from the list
	 @throws IllegalArgumentException if the list is null or empty
	 */

	public static String getRandomElementFromList(List<String> elements) {

		if (elements == null || elements.isEmpty()) {
			throw new IllegalArgumentException("List cannot be null or empty.");
		}
		Random random = new Random();
		int randomIndex = random.nextInt(elements.size());
		return elements.get(randomIndex);
	}

	public String removeDollarSymbol(String input) {
		return input.replace("$", "").trim();
	}

	public float convertStringToFloat(String input) {
		try {
			return Float.parseFloat(input);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Failed to convert string to float: " + input, e);
		}
	}

	public String formatFloatToString(float value) {
		return String.format("%.2f", value);
	}

	public double convertStringToDouble(String input) {
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Failed to convert string to double: " + input, e);
		}
	}

	/**
	 * Returns a random element from the list, excluding the specified value.
	 * Does not modify the original list.
	 * @param valueToExclude the string value to exclude from selection
	 * @param originalList   the original list of strings
	 * @return a random element from the filtered list
	 * @throws IllegalArgumentException if the original list is null or all elements are excluded
	 */

	public static String getRandomElementExcludingSpecificValue(String valueToExclude, List<String> originalList) {
		if (originalList == null || originalList.isEmpty()) {
			throw new IllegalArgumentException("Original list cannot be null or empty.");
		}
		List<String> filteredList = new ArrayList<>();
		for (String element : originalList) {
			if (!element.equals(valueToExclude)) {
				filteredList.add(element);
			}
		}
		if (filteredList.isEmpty()) {
			throw new IllegalArgumentException("No elements left after excluding value: " + valueToExclude);
		}
		return getRandomElementFromList(filteredList);
	}

	/**
	 * Converts a comma-separated string into a list of trimmed strings.
	 * This method splits the input string by commas and trims each resulting element
	 * to remove leading and trailing whitespace.
	 * @param commaSeparatedString the comma-separated input string (e.g., "A, B, C")
	 * @return a list of trimmed strings extracted from the input
	 * @throws IllegalArgumentException if the input string is {@code null} or empty
	 */
	public static List<String> convertCommaSeparatedStringToList(String commaSeparatedString) {
		if (commaSeparatedString == null || commaSeparatedString.trim().isEmpty()) {
			throw new IllegalArgumentException("Input string must not be null or empty.");
		}
		String[] parts = commaSeparatedString.split(",");
		List<String> result = new ArrayList<>();
		for (String part : parts) {
			result.add(part.trim());
		}
		return result;
	}

	/**
	 * Converts a double value to a string with exactly two decimal places.
	 * @param value the double value to be converted
	 * @return a string representation of the double with two decimal places (e.g., "12.34")
	 */

	public String convertDoubleToString(double value) {
		return String.format("%.2f", value);
	}
}
