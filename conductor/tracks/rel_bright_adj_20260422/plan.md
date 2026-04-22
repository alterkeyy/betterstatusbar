# Implementation Plan: Implement Relative Brightness Adjustment

## Phase 1: Research and UI Preparation
- [ ] Task: Research and Verify Touch Handling Logic
    - [ ] Analyze `BrightnessGestureHook.java` touch event propagation.
    - [ ] Verify existing `ACTION_DOWN` state tracking.
- [ ] Task: Update Settings UI and Preferences
    - [ ] Add `KEY_RELATIVE_BRIGHTNESS` and its default value to `Prefs.java`.
    - [ ] Add a new switch preference to `SettingsActivity.java` (layout and logic).
    - [ ] Update `Prefs.ACTION_PREFS_CHANGED` intent extras to include the new toggle.
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Research and UI Preparation' (Protocol in workflow.md)

## Phase 2: Core Logic Implementation (Relative Adjustment)
- [ ] Task: Write Tests for Relative Brightness Logic
    - [ ] Create unit tests for brightness calculation based on deltas.
    - [ ] Verify edge cases (min/max brightness limits).
- [ ] Task: Implement Relative Adjustment in Hook
    - [ ] Update `BrightnessGestureHook.java` to track `mInitialBrightness` on `ACTION_DOWN`.
    - [ ] Modify `computeBrightness` to support relative mode based on the preference toggle.
    - [ ] Ensure `mGestureEnabled` and `mOverlayEnabled` logic remains intact.
- [ ] Task: Verify Logic with Automated Tests
    - [ ] Run the newly created unit tests.
    - [ ] Confirm no regressions in existing absolute brightness logic.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Core Logic Implementation' (Protocol in workflow.md)

## Phase 3: Final Integration and Verification
- [ ] Task: Verify Broadcast Propagation
    - [ ] Ensure `SettingsActivity` correctly broadcasts the new setting.
    - [ ] Verify `BrightnessGestureHook` correctly receives and updates its local state.
- [ ] Task: Manual Visual Verification
    - [ ] Install and test on a physical/emulated device.
    - [ ] Toggle relative mode and verify swipe behavior.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Final Integration and Verification' (Protocol in workflow.md)
