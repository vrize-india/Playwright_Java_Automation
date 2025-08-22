package com.tonic.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * Enum representing common keyboard keys used in automation.
 */


@Getter
public enum KeyboardKey {

    ENTER("Enter"),
    SPACE(" ");

    /**
     * -- GETTER --
     *  Gets the actual key string value.
     *
     * @return the key value as used in automation
     */
    private final String value;

    KeyboardKey(String value) {
        this.value = value;
    }

    /**
     * Parses a given string into the corresponding {@link KeyboardKey} enum.
     * Case-insensitive and trims the input. Throws a descriptive exception for invalid inputs.
     *
     * @param key input key string
     * @return matching {@link KeyboardKey} enum
     * @throws IllegalArgumentException if the key is null, empty, or unsupported
     */
    public static KeyboardKey fromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyboard key cannot be null or empty.");
        }

        try {
            return KeyboardKey.valueOf(key.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported keyboard key: '" + key + "'. Allowed keys are: " +
                            Arrays.toString(KeyboardKey.values()));
        }
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }
}




