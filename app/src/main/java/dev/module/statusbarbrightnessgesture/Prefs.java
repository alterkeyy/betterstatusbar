package dev.module.statusbarbrightnessgesture;

public final class Prefs {
    /**
     * Keys used in Settings.Secure for persistent cross-process state.
     * Readable by any process including SystemUI from boot.
     * Writable by the app after WRITE_SECURE_SETTINGS is granted via ADB once.
     * Persists across reboots and app updates (as long as package name unchanged).
     *
     * One-time ADB setup (run after install):
     *   adb shell pm grant dev.module.statusbarbrightnessgesture android.permission.WRITE_SECURE_SETTINGS
     */
    public static final String KEY_GESTURE_ENABLED  = "sbbrightness_gesture_enabled";
    public static final String KEY_OVERLAY_ENABLED   = "sbbrightness_overlay_enabled";
    public static final String KEY_RELATIVE_BRIGHTNESS = "sbbrightness_relative_brightness";
    public static final String KEY_BATTERY_TAP_ENABLED = "sbbrightness_battery_tap_enabled";

    // ── Gesture Actions ──────────────────────────────────────────────────────
    
    // Battery Icon
    public static final String KEY_BATTERY_SINGLE_TAP_ACTION = "sbbrightness_battery_single_tap_action";
    public static final String KEY_BATTERY_DOUBLE_TAP_ACTION = "sbbrightness_battery_double_tap_action";
    public static final String KEY_BATTERY_LONG_TAP_ACTION   = "sbbrightness_battery_long_tap_action";

    // Time/Clock Icon
    public static final String KEY_TIME_SINGLE_TAP_ACTION = "sbbrightness_time_single_tap_action";
    public static final String KEY_TIME_DOUBLE_TAP_ACTION = "sbbrightness_time_double_tap_action";
    public static final String KEY_TIME_LONG_TAP_ACTION   = "sbbrightness_time_long_tap_action";

    // Status Bar Background (Empty Area)
    public static final String KEY_STATUSBAR_SINGLE_TAP_ACTION = "sbbrightness_statusbar_single_tap_action";
    public static final String KEY_STATUSBAR_DOUBLE_TAP_ACTION = "sbbrightness_statusbar_double_tap_action";
    public static final String KEY_STATUSBAR_LONG_TAP_ACTION   = "sbbrightness_statusbar_long_tap_action";

    /** Broadcast for live updates — supplements Settings.Secure persistence */
    public static final String ACTION_PREFS_CHANGED  =
            "dev.module.statusbarbrightnessgesture.PREFS_CHANGED";

    public static final int DEFAULT_GESTURE_ENABLED = 1;
    public static final int DEFAULT_OVERLAY_ENABLED  = 1;
    public static final int DEFAULT_RELATIVE_BRIGHTNESS = 0;
    public static final int DEFAULT_BATTERY_TAP_ENABLED = 0;

    // Default actions (empty string means no action)
    public static final String DEFAULT_ACTION_BATTERY_TAP = "intent:android.intent.action.POWER_USAGE_SUMMARY";
    public static final String DEFAULT_ACTION_TIME_TAP = "intent:android.intent.action.SHOW_ALARMS";
    public static final String DEFAULT_ACTION_NONE = "";

    private Prefs() {}
}
