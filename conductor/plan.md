# Implementation Plan: Create GEMINI.md

## Objective
Create a comprehensive `GEMINI.md` file in the root directory that outlines the project overview, build commands, and development conventions for the Status Bar Brightness Gesture application.

## Key Files & Context
- Target file: `/home/alt/projects/kingsrepo/StatusBarBrightnessGesture/GEMINI.md`
- The project is an Android app with an LSPosed module for brightness control via status bar swipe gestures.

## Implementation Steps

Write the following content to `GEMINI.md`:

```markdown
# Status Bar Brightness Gesture

## Project Overview
This repository contains an Android application functioning as an LSPosed module. The module enables users to control screen brightness by swiping horizontally across the status bar. It works on the lockscreen and functions independently of the notification shade's state.

Key features and interactions:
- Connects to SystemUI via Xposed Framework hooks.
- Hooks primarily into `PhoneStatusBarView` and `NotificationShadeWindowView`.
- Interfaces with Android's `DisplayManager` and `WindowManager` via reflection to dynamically adjust device brightness.

## Building and Running
The project is built using Gradle. Execute the following scripts from the project root:

- **Build Debug APK:** `./gradlew assembleDebug`
- **Build Release APK:** `./gradlew assembleRelease`
- **Clean Build Environment:** `./gradlew clean`

## Development Conventions
- **Language & SDK:** Java 17 source/target compatibility. Minimum SDK 33, Target SDK 35.
- **Xposed Framework:** Modifies runtime behavior utilizing Xposed APIs. Changes to `app/src/main/assets/xposed_init` are required if the hook entry point class is renamed.
- **IPC Mechanism:** User preferences configured in the main app's `SettingsActivity` are propagated to the injected SystemUI process via `BroadcastReceiver` to ensure thread-safe, immediate updates without requiring process restarts.
- **Reflection Use:** Heavy use of Java reflection for invoking hidden Android APIs (e.g., `BrightnessUtils`, `DisplayManager.setTemporaryBrightness`). Ensure robust error handling (`try-catch(Throwable)`) around reflection blocks as OEM ROMs may differ structurally from AOSP.
```

## Verification & Testing
- Ensure the newly created `GEMINI.md` file exists in the root directory.
- Confirm the formatting renders correctly as valid Markdown.