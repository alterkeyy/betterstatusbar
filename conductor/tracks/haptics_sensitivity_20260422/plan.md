# Implementation Plan: Refined Haptics & Sensitivity

## Phase 1: Configuration UI and Preferences
- [x] Task: Update `Prefs.java` (ccb122c)
    - [x] Add preference keys for `KEY_HAPTIC_INTENSITY` and `KEY_SWIPE_SENSITIVITY`.
    - [x] Define sensible default values for both preferences.
- [x] Task: Update `SettingsActivity.java` (db9e3d2)
    - [x] Add UI components (e.g., sliders or dropdowns) for "Haptic Intensity" and "Swipe Sensitivity".
    - [x] Broadcast these new settings to SystemUI in `sendPrefs()`.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Configuration UI and Preferences' (Protocol in workflow.md)

## Phase 2: Haptic Feedback and Sensitivity Logic
- [x] Task: Implement Haptic and Sensitivity Handlers (b5f32f3)
    - [x] Create logic to calculate when the next continuous "tick" should fire based on finger distance moved.
    - [x] Map the chosen haptic intensity to specific `HapticFeedbackConstants` values (e.g., `CLOCK_TICK`, `KEYBOARD_PRESS`).
- [x] Task: Update `BrightnessGestureHook.java` (6bad224)
    - [x] Update `registerPrefsReceiver` to listen for the new sensitivity and intensity preferences.
    - [x] Modify `onMove` to trigger `View.performHapticFeedback()` using the distance threshold and configured intensity.
    - [x] Modify the brightness computation (e.g., in `BrightnessCalculator` or `onMove`) to apply the user's chosen sensitivity multiplier.
- [x] Task: Verify Core Logic (def0ebc)
    - [x] Update unit tests for `BrightnessCalculator` to account for the new sensitivity multiplier.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Haptic Feedback and Sensitivity Logic' (Protocol in workflow.md)

## Phase 3: Final Review and Adjustments
- [ ] Task: Final Manual Verification
    - [ ] Test the continuous tick haptic feedback during a swipe on a real device.
    - [ ] Verify that changing the sensitivity setting immediately affects the gesture distance required to change brightness.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Final Review and Adjustments' (Protocol in workflow.md)