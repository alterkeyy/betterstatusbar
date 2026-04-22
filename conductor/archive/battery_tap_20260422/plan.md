# Implementation Plan: Extensible Status Bar Gestures

## Phase 1: Preference and UI Overhaul
- [x] Task: Update `Prefs.java`
    - [x] Define keys for each gesture/area combination (e.g., `KEY_BATTERY_SINGLE_TAP`, `KEY_STATUSBAR_DOUBLE_TAP`, etc.).
- [x] Task: Enhance `SettingsActivity.java`
    - [x] Create a more modular UI for assigning actions to gestures.
    - [x] Implement a way for users to enter custom activity intent strings.
    - [x] Ensure all new settings are correctly broadcasted.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Preference and UI Overhaul' (Protocol in workflow.md)

## Phase 2: Core Gesture Detection and Dispatch
- [x] Task: Implement `StatusBarGestureDetector`
    - [x] Create a custom class that handles single tap, double tap, and long tap detection.
    - [x] Ensure it can distinguish between swipes (for brightness) and taps.
- [x] Task: Refactor `IconTapManager` to `StatusBarActionManager`
    - [x] Expand the manager to handle different gesture types.
    - [x] Implement action execution logic (launching intents from strings).
- [x] Task: Update `BrightnessGestureHook.java`
    - [x] Integrate the new gesture detector into the `onTouchEvent` hook.
    - [x] Pass identified gestures and target views to `StatusBarActionManager`.
- [x] Task: Conductor - User Manual Verification 'Phase 2: Core Gesture Detection and Dispatch' (Protocol in workflow.md)

## Phase 3: Verification and Refinement
- [x] Task: Write Tests for Gesture Logic
    - [x] Create unit tests for identifying gestures from motion events.
    - [x] Verify intent parsing and execution logic.
- [x] Task: Final Manual Verification
    - [x] Test all gesture combinations on a real device.
    - [x] Verify that no regressions were introduced for the brightness swipe.
- [x] Task: Conductor - User Manual Verification 'Phase 3: Verification and Refinement' (Protocol in workflow.md)
