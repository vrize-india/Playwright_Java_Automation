package com.vrize.enums;

public enum WaitEnums {

    CLICKABLE,
    PRESENCE,
    VISIBLE,
    NONE,
    // Mobile-specific wait types
    APP_LAUNCH,     // Wait for app to fully load
    UI_INTERACTION, // Wait after taps, swipes, text input
    NAVIGATION      // Wait for screen transitions
}
