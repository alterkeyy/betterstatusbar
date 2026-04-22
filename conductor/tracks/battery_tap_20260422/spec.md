# Specification: Battery Icon Tap to Settings

## Overview
Implement a new feature that allows the user to tap the battery icon in the status bar to directly open the device's Battery Settings. The feature will be built with an extensible architecture to easily support additional status bar icons in the future.

## Functional Requirements
- **Extensible Architecture:** Design a generic tap-handler system for status bar icons.
- **Battery Tap Action:** Hook into the battery icon view specifically to launch the Android Battery Settings intent (`Intent.ACTION_POWER_USAGE_SUMMARY` or similar).
- **Settings Toggle:** Add a new preference key (e.g., `KEY_BATTERY_TAP_ENABLED`) in `Prefs.java` and a corresponding switch in `SettingsActivity.java` to enable/disable the feature.
- **Target Views:** The tap action will be active on the regular **Status Bar** (i.e., `PhoneStatusBarView`).
- **Live Updates:** Ensure the setting toggle state is broadcasted to the Xposed hook without requiring a reboot, matching the existing `mRelativeEnabled` and `mGestureEnabled` behavior.

## Acceptance Criteria
- User can toggle the battery tap feature on/off in the companion app.
- Tapping the battery icon on the status bar opens the battery settings when enabled.
- The tap feature does not interfere with the existing horizontal brightness gesture.
- The hook is structured to easily add more icons (e.g., Wi-Fi, Bluetooth) later.

## Out of Scope
- Implementing tap actions for icons other than the battery at this time.
- Tap actions in the Notification Shade or on the Lockscreen status bar.