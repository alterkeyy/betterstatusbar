# Specification: Update LibXposed API 101

## Overview
This track focuses on migrating the Status Bar Brightness Gesture module to target LibXposed API version 101. The primary objective is a full migration, ensuring that all hooks comply with the new API rules, particularly the removal of zygote injection capabilities.

## Functional Requirements
- **Update Target API:** Update the build configuration (e.g., `build.gradle.kts`) and the Xposed manifest (`xposed_init`) to reflect API version 101.
- **Refactor Hooks for API 101 Compliance:** Ensure the module is only loaded within the process of the scope (SystemUI) and remove any existing zygote injection logic, as API 101 no longer supports zygote injection.
- **SystemUI Hooks Verification:** Verify and test the primary hooks in `PhoneStatusBarView` and `NotificationShadeWindowView` to ensure they function correctly under the new API version.
- **Settings IPC Verification:** Ensure that the Inter-Process Communication (BroadcastReceiver) between the companion app and the injected SystemUI process continues to propagate settings reliably.

## Non-Functional Requirements
- Maintain backward compatibility with existing AOSP-based Android 12+ ROMs where applicable.
- Adhere to the established code style and testing guidelines outlined in the project documentation.

## Acceptance Criteria
- The project successfully builds and links against LibXposed API 101.
- The module no longer attempts to inject into the zygote process.
- The gesture overlay correctly captures horizontal swipes on the status bar and adjusts screen brightness.
- The user preferences configured in the SettingsActivity are immediately propagated to the active SystemUI process without requiring a restart.

## Out of Scope
- Introducing new gesture types or actions not related to the API 101 migration.
- Re-architecting the companion app UI beyond what is necessary for the IPC verification.