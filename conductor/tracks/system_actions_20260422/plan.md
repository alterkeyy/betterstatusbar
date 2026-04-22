# Implementation Plan: Built-in System Actions for Gestures

## Phase 1: Revamp Settings UI [checkpoint: da3c6b3]
- [x] Task: Create Built-in Action Constants [0986eb9]
    - [ ] Update `Prefs.java` with predefined string constants for the new `system:` actions (e.g., `ACTION_SYSTEM_DARK_MODE`, `ACTION_SYSTEM_POWER_SAVE`, `ACTION_SYSTEM_LOCK_SCREEN`).
- [x] Task: Refactor `SettingsActivity.java` Dialog [800fd08]
    - [ ] Modify `buildActionRow` to display a single-choice list `AlertDialog` instead of the direct `EditText`.
    - [ ] Add the list options: None, Toggle Dark Mode, Toggle Power Saving, Lock Screen, Custom Intent...
    - [ ] Handle the selection: save predefined actions directly, or open the `EditText` dialog if "Custom Intent..." is chosen.
    - [ ] Create a helper method to map raw saved strings (like `system:toggle_dark_mode`) to user-friendly labels in the UI.
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Revamp Settings UI' (Protocol in workflow.md)

## Phase 2: Implement System Action Logic
- [ ] Task: Update `StatusBarActionManager` parsing
    - [ ] Add support for parsing the `system:` prefix.
    - [ ] Update the `ParsedIntent` or create a new return type to distinguish between standard intents and system actions.
- [ ] Task: Implement `performSystemAction`
    - [ ] Add logic in `StatusBarActionManager` (or a dedicated class) to handle `system:toggle_dark_mode` using `UiModeManager`.
    - [ ] Add logic to handle `system:toggle_power_save` using `PowerManager`.
    - [ ] Add logic to handle `system:lock_screen` using `PowerManager.goToSleep()` or `DevicePolicyManager`.
    - [ ] Ensure robust try-catch blocks around system API calls, logging any failures via `XposedBridge`.
- [ ] Task: Update Unit Tests
    - [ ] Modify `StatusBarActionManagerTest` to verify the parsing and execution logic of the new `system:` actions.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Implement System Action Logic' (Protocol in workflow.md)

## Phase 3: Verification
- [ ] Task: Final Manual Verification
    - [ ] Test the new UI flow in the settings app.
    - [ ] Trigger each built-in action via a gesture on a physical/emulated device.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Verification' (Protocol in workflow.md)