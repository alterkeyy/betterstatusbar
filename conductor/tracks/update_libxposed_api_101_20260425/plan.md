# Implementation Plan: Update LibXposed API 101

## Phase 1: Environment and Dependencies Update
- [ ] Task: Update Target API to 101
    - [ ] Update `build.gradle.kts` to target LibXposed API 101.
    - [ ] Update `xposed_init` if necessary to reflect API 101.
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Environment and Dependencies Update' (Protocol in workflow.md)

## Phase 2: Refactoring Hooks for API 101
- [ ] Task: Remove Zygote Injection
    - [ ] Identify and remove any logic attempting to inject into the zygote process.
    - [ ] Ensure the module is only loaded within the scope of SystemUI.
- [ ] Task: Refactor SystemUI Hooks
    - [ ] Update `PhoneStatusBarView` hooks to comply with API 101 requirements.
    - [ ] Update `NotificationShadeWindowView` hooks to comply with API 101 requirements.
    - [ ] Update `BrightnessGestureHook.java` if needed for `HookBuilder` changes.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Refactoring Hooks for API 101' (Protocol in workflow.md)

## Phase 3: Testing and Verification
- [ ] Task: Verify Settings IPC
    - [ ] Write/update tests to confirm that preferences set in `SettingsActivity` propagate to the SystemUI process.
    - [ ] Verify `LogReceiver` correctly receives logs.
- [ ] Task: Verify Brightness Adjustment
    - [ ] Write/update tests to ensure brightness calculations and adjustments work as expected under API 101.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Testing and Verification' (Protocol in workflow.md)