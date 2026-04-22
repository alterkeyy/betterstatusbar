# Implementation Plan: Battery Icon Tap to Settings

## Phase 1: Extensible Architecture and Settings UI
- [ ] Task: Research Status Bar Icon Views
    - [ ] Identify the class name for the Battery icon view in SystemUI.
    - [ ] Identify how touch events are dispatched to status bar icons.
- [ ] Task: Update Settings UI and Preferences
    - [ ] Add `KEY_BATTERY_TAP_ENABLED` to `Prefs.java`.
    - [ ] Add a new toggle to `SettingsActivity.java` for "Battery Icon Tap".
    - [ ] Broadcast the new setting state to SystemUI in `sendPrefs()`.
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Extensible Architecture and Settings UI' (Protocol in workflow.md)

## Phase 2: Tap Handler Implementation
- [ ] Task: Design Generic Icon Tap Handler Interface
    - [ ] Define an extensible interface/class for registering status bar icon tap handlers.
    - [ ] Create a specific implementation for the Battery icon that launches the power usage intent.
- [ ] Task: Hook Status Bar Icon Touches
    - [ ] Hook into the appropriate view/controller in SystemUI to intercept clicks on icons.
    - [ ] Route intercepted clicks through the generic tap handler.
    - [ ] Ensure the tap handler only triggers if `mBatteryTapEnabled` is true.
- [ ] Task: Verify Tap Handler with Automated Tests
    - [ ] Write unit tests for the generic tap handler logic (if isolated from Android framework).
    - [ ] Verify that the handler correctly identifies the battery icon and fires the intent.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Tap Handler Implementation' (Protocol in workflow.md)

## Phase 3: Integration and Verification
- [ ] Task: Verify Broadcast Propagation
    - [ ] Ensure `SettingsActivity` correctly broadcasts the new setting.
    - [ ] Ensure `BrightnessGestureHook` correctly receives and updates its local state.
- [ ] Task: Manual Visual Verification
    - [ ] Install and test on a physical/emulated device.
    - [ ] Toggle battery tap mode and verify tapping the battery icon opens settings.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Integration and Verification' (Protocol in workflow.md)