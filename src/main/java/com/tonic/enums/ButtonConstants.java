package com.tonic.enums;
import lombok.Getter;

/**
 * Enum representing commonly used button labels or icons across the application.
 * Each constant maps to a string value used in the UI or locators.
 */
@Getter
public enum ButtonConstants {

    SAVE("Save"),
    CANCEL("Cancel"),
    EDIT("edit"),
    ADD_NEW("add"),
    CONFIGURATION("Configuration"),
    TYPES("Types"),
    DUAL_PRICING_OPTION("dualPricingOption"),
    ENABLED("Enabled"),
    DUAL_PRICING("Dual Pricing"),
    CLOSE("close-icon"),
    TRASH("trash-icon"),
    DISABLED("Disabled"),
    ADD_NEW_BUTTON("Add New"),
    HOME("Home"),
    SEARCH_HERE("Search here"),
    MENU_CONFIGURATION("Menu Configuration"),
    ONLINE_ORDER_TOGGLE("Online Order Toggle"),
    STORE_TOGGLE("Store Toggle"),
    YES("Yes"),
    ADD_NEW_ITEM("add-new-item"),
    PREMODIFIERS("Pre-Modifiers"),
    POST("Post"),
    MODIFIERS("Modifiers"),
    MODIFIERS_SETS("Modifier Sets"),
    YES_DELETE("Yes, Delete"),
    ITEMS("Items"),
    EDIT_BUTTON("edit"),
    DELETE_BUTTON("delete"),
    //premodifiers Pricing Type
    NONE("circle-slash-black");


    private final String value;

    ButtonConstants(String value) {
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
    public static ButtonConstants fromKey(String key) {
        for (ButtonConstants constant : values()) {
            if (constant.name().equalsIgnoreCase(key.replace(" ", "_"))
                    || constant.getValue().equalsIgnoreCase(key)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No matching ButtonConstants for key: " + key);
    }
}

