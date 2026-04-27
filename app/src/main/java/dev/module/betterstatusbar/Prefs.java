package dev.module.betterstatusbar;

public final class Prefs {
    /**
     * Keys used in Settings.Secure for persistent cross-process state.
     * Readable by any process including SystemUI from boot.
     * Writable by the app after WRITE_SECURE_SETTINGS is granted via ADB once.
     * Persists across reboots and app updates (as long as package name unchanged).
     *
     * One-time ADB setup (run after install):
     *   adb shell pm grant dev.module.betterstatusbar android.permission.WRITE_SECURE_SETTINGS
     */
    public static final String KEY_GESTURE_ENABLED  = "sbbrightness_gesture_enabled";
    public static final String KEY_OVERLAY_ENABLED   = "sbbrightness_overlay_enabled";
    public static final String KEY_RELATIVE_BRIGHTNESS = "sbbrightness_relative_brightness";
    public static final String KEY_BATTERY_TAP_ENABLED = "sbbrightness_battery_tap_enabled";
    public static final String KEY_HAPTIC_INTENSITY = "sbbrightness_haptic_intensity";
    public static final String KEY_SWIPE_SENSITIVITY = "sbbrightness_swipe_sensitivity";
    public static final String KEY_LOGGING_ENABLED = "sbbrightness_logging_enabled";

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

    // ── System Actions ──────────────────────────────────────────────────────
    public static final String ACTION_SYSTEM_DARK_MODE   = "system:toggle_dark_mode";
    public static final String ACTION_SYSTEM_POWER_SAVE  = "system:toggle_power_save";
    public static final String ACTION_SYSTEM_LOCK_SCREEN = "system:lock_screen";

    /** Broadcast for live updates — supplements Settings.Secure persistence */
    public static final String ACTION_PREFS_CHANGED  =
            "dev.module.betterstatusbar.PREFS_CHANGED";

    /** Broadcast for logging gestures — from hook to companion app */
    public static final String ACTION_GESTURE_LOG =
            "dev.module.betterstatusbar.GESTURE_LOG";
    public static final String EXTRA_LOG_MESSAGE = "log_message";

    /** Broadcast for module status updates — from hook to companion app */
    public static final String ACTION_MODULE_STATUS =
            "dev.module.betterstatusbar.MODULE_STATUS";
    public static final String EXTRA_FRAMEWORK_NAME = "framework_name";
    public static final String EXTRA_FRAMEWORK_VERSION = "framework_version";
    public static final String EXTRA_API_VERSION = "api_version";

    // ── Local SharedPreferences Keys (not Settings.Secure) ───────────────────
    public static final String LOCAL_PREFS_NAME = "module_status";
    public static final String KEY_STATUS_LAST_SEEN = "status_last_seen";
    public static final String KEY_LAST_FRAMEWORK_NAME = "last_framework_name";
    public static final String KEY_LAST_FRAMEWORK_VERSION = "last_framework_version";
    public static final String KEY_LAST_API_VERSION = "last_api_version";

    public static final int DEFAULT_GESTURE_ENABLED = 1;
    public static final int DEFAULT_OVERLAY_ENABLED  = 1;
    public static final int DEFAULT_RELATIVE_BRIGHTNESS = 0;
    public static final int DEFAULT_BATTERY_TAP_ENABLED = 0;
    public static final int DEFAULT_HAPTIC_INTENSITY = 1; // 0: None, 1: Subtle, 2: Normal, 3: Strong
    public static final int DEFAULT_SWIPE_SENSITIVITY = 100; // Percentage multiplier
    public static final int DEFAULT_LOGGING_ENABLED = 0;

    // Default actions (empty string means no action)
    public static final String DEFAULT_ACTION_BATTERY_TAP = "intent:android.intent.action.POWER_USAGE_SUMMARY";
    public static final String DEFAULT_ACTION_TIME_TAP = "intent:android.intent.action.SHOW_ALARMS";
    public static final String DEFAULT_ACTION_NONE = "";

    public static String getActionLabel(String action) {
        if (action == null || action.isEmpty()) return "None";
        if (action.equals(ACTION_SYSTEM_DARK_MODE)) return "Toggle Dark Mode";
        if (action.equals(ACTION_SYSTEM_POWER_SAVE)) return "Toggle Power Saving";
        if (action.equals(ACTION_SYSTEM_LOCK_SCREEN)) return "Lock Screen";
        return action;
    }

    private Prefs() {}
}
