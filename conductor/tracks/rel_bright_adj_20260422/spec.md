# Specification: Implement Relative Brightness Adjustment

## Objective
Implement a toggleable feature that allows screen brightness to be adjusted relative to its current value during a horizontal swipe on the status bar, rather than jumping to an absolute value based on the touch position.

## Technical Details
- **Current Behavior:** `computeBrightness(float fingerX)` calculates brightness as a fraction of screen width: `fraction = fingerX / mScreenWidth`.
- **New Behavior:** 
  - Track initial brightness on `ACTION_DOWN`.
  - Calculate `deltaX = currentX - initialX`.
  - Adjust brightness: `newBrightness = initialBrightness + (deltaX / mScreenWidth) * sensitivityFactor`.
- **Settings Toggle:** Add a new preference key `KEY_RELATIVE_BRIGHTNESS` in `Prefs.java` and a corresponding switch in `SettingsActivity.java`.
- **Xposed Hook Update:** Update `BrightnessGestureHook.java` to handle the relative adjustment logic when the toggle is enabled.

## Requirements
- Maintain backward compatibility (absolute adjustment remains an option).
- Support for Material You theming in the settings UI.
- Use `BroadcastReceiver` for live preference updates in SystemUI.
