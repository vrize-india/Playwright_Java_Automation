package com.tonic.enums;
import lombok.Getter;

/**
 * Enum representing commonly used button labels or icons across the application.
 * Each constant maps to a string value used in the UI or locators.
 */
@Getter
public enum BOHDataConstants {
    MODIFIER_NAME("SpicyZing"),
    MODIFIER_ONLINE_ORDERING_NAME("Classic "),
    MODIFIER_Multiplier("1"),
    MODIFIER_PREPARATION_TIME("10"),
    MODIFIER_PRICE("3"),
    MODIFIER_SETS_NAME("Dressing Choices"),
    MODIFIER_SETS_MIN_VALUE("2"),
    MODIFIER_SETS_MAX_VALUE("4"),
    PREMODIFIER_GROUP_NAME("Grey"),
    PREMODIFIER_NAME("Light"),
    ONLINE_ORDER("Pizza"),
    KITCHEN_NAME("PizzaKitchen"),
    CATEGORY_NAME("Magnus"),
    ITEM_NAME("Burger");


    private final String value;

    BOHDataConstants(String value) {
        this.value = value;
    }

    /**
     * Returns the associated string value of the enum constant.
     * Useful for logging or UI locator resolution.
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Returns the associated string value of the enum constant.
     * This method is required for compatibility with existing code.
     */
    public String getValue() {
        return value;
    }

    /**
     * Parses a string key to the corresponding enum constant.
     * Replaces spaces with underscores and performs case-insensitive matching.
     *
     * @param key the input string (e.g., "Dual Pricing Option")
     * @return the matching ButtonConstants enum
     * @throws IllegalArgumentException if no match is found
     */
    public static BOHDataConstants fromKey(String key) {
        for (BOHDataConstants constant : values()) {
            if (constant.name().equalsIgnoreCase(key.replace(" ", "_"))
                    || constant.getValue().equalsIgnoreCase(key)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No matching ButtonConstants for key: " + key);
    }
}


