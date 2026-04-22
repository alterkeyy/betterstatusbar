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

    /** Broadcast for live updates — supplements Settings.Secure persistence */
    public static final String ACTION_PREFS_CHANGED  =
            "dev.module.statusbarbrightnessgesture.PREFS_CHANGED";

    public static final int DEFAULT_GESTURE_ENABLED = 1;
    public static final int DEFAULT_OVERLAY_ENABLED  = 1;

    private Prefs() {}
}