package com.vrize.enums;

import lombok.Getter;

/**
 * Enum representing commonly used labels across the application.
 * Each constant maps to a string value used in the UI or locators.
 */

@Getter
public enum BOHConstants {

        MODIFIER_FIELD("Enter Modifier Name"),
        MODIFIER_DISPLAY_NAME("BOH Modifier Display Name"),
        MODIFIER_DISPLAY_FIELD("Enter Display Name"),
        MODIFIER_ONLINE_ORDERING_FIELD("Enter Online Ordering Name"),
        MODIFIER_Multiplier_FIELD("Multiplier"),
        MODIFIER_PREPARATION_TIME_FIELD("Preparation Time"),
        MODIFIER_PRICE_FIELD("Price"),
        MODIFIER_SETS_FIELD("Add New Modifier Set"),
        MODIFIER_SETS_MIN_FIELD("Min"),
        MODIFIER_SETS_MAX_FIELD("Max"),
        MODIFIER_FIELD_IN_MODIFIER_SETS("Add New Modifiers"),
        ENTER_CATEGORY_NAME("Enter category name here"),
        CONFIRM("to confirm"),
        DELETED("deleted"),
        SPECIFIC_ITEM_ELEMENT("Specific Item Element"),
        CONFIGURE_PRE_MODIFIER_GROUP("Configure Pre-Modifier Group"),
        ENTER_PRE_MODIFIER_GROUP_NAME("Enter Pre-Modifier Group Name"),
        ENTER_PRE_MODIFIER_NAME("Enter Pre-Modifier Name"),
        ENTER_ONLINE_ORDERING_NAME("Enter Online Ordering Name"),
        ENTER_KITCHEN_NAME("Enter Kitchen Name"),
        SPECIFIC_MODIFIER_ELEMENT("Specific Modifier Element"),
        DELETE_ITEM("Delete Item"),
        ADD_NEW_ITEMS("Add New Item"),
        ADDITIONAL_CHARGE_CATEGORY_COLUMN_HEADERS("Modifier,Cash Price,Card Price,Actions"),


        // =============== Expected Values ===============

        SUCCESSFUL_MESSAGE ("Successfully added the pre-modifier group."),
        SUCCESSFUL_DELETE_MESSAGE("Successfully deleted the pre modifier group from the list"),
        SUCCESSFUL_PREMODIFIER_CREATION_MESSAGE("Successfully added the pre-modifier group."),
        PREMODIFIER_UPDATE_SUCCESS_MESSAGE("Successfully updated the pre-modifier group."),
        SUCCESSFUL_CATEGORY_CREATION_MESSAGE("New Magnus Category Added Successfully"),
        SUCCESSFUL_MODIFIER_CREATION_MESSAGE("Successfully added the modifier to the list"),
        SUCCESSFUL_MODIFIER_SET_CREATION_MESSAGE("Successfully added the Modifier Set to the list"),
        MODIFIER_DELETE_MESSAGE("Successfully deleted the modifier from the list"),
        MODIFIER_SET_DELETE_MESSAGE("Successfully deleted the Modifier Set from the list"),
        SUCCESSFUL_ITEM_CREATION_MESSAGE("Successfully added the Item to the list"),
        ITEM_DELETE_MESSAGE("Successfully deleted the item from the list"),
        CATEGORY_DELETE_MESSAGE("Menu Magnus deleted!");


        private final String value;

        BOHConstants(String value) {
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
         * @return the matching BOHConstants enum
         * @throws IllegalArgumentException if no match is found
         */
        public static com.vrize.enums.BOHConstants fromKey(String key) {
            for (com.vrize.enums.BOHConstants constant : values()) {
                if (constant.name().equalsIgnoreCase(key.replace(" ", "_"))
                        || constant.getValue().equalsIgnoreCase(key)) {
                    return constant;
                }
            }
            throw new IllegalArgumentException("No matching BOHConstants for key: " + key);
        }
    }

