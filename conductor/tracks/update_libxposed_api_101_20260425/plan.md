# Implementation Plan: Update LibXposed API 101

## Phase 1: Environment and Dependencies Update
- [x] Task: Update Target API to 101
    - [x] Update `build.gradle.kts` to target LibXposed API 101.
    - [x] Update `xposed_init` if necessary to reflect API 101.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Environment and Dependencies Update' (Protocol in workflow.md)

## Phase 2: Refactoring Hooks for API 101
- [x] Task: Remove Zygote Injection
    - [x] Identify and remove any logic attempting to inject into the zygote process.
    - [x] Ensure the module is only loaded within the scope of SystemUI.
- [x] Task: Refactor SystemUI Hooks
    - [x] Update `PhoneStatusBarView` hooks to comply with API 101 requirements.
    - [x] Update `NotificationShadeWindowView` hooks to comply with API 101 requirements.
    - [x] Update `BrightnessGestureHook.java` if needed for `HookBuilder` changes.
- [x] Task: Conductor - User Manual Verification 'Phase 2: Refactoring Hooks for API 101' (Protocol in workflow.md)

## Phase 3: Testing and Verification
- [x] Task: Verify Settings IPC
    - [x] Write/update tests to confirm that preferences set in `SettingsActivity` propagate to the SystemUI process.
    - [x] Verify `LogReceiver` correctly receives logs.
- [x] Task: Verify Brightness Adjustment
    - [x] Write/update tests to ensure brightness calculations and adjustments work as expected under API 101.
- [x] Task: Conductor - User Manual Verification 'Phase 3: Testing and Verification' (Protocol in workflow.md)