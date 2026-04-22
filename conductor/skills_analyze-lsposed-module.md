---
name: analyze-lsposed-module
description: Analyzes an Android LSPosed/Xposed module project to identify hook entry points, target processes, and IPC mechanisms.
---

## When to Use
Use this skill when onboarding to an existing Android project that functions as an LSPosed or Xposed module. This is critical for understanding how the module interacts with the system and where its core logic resides.

## Procedure
1.  **Locate Hook Entry Point**: Search for the `xposed_init` file, typically found at `app/src/main/assets/xposed_init`.
    -   Read this file; it contains the fully qualified name of the class implementing `IXposedHookLoadPackage` (the module's entry point).
2.  **Identify Target Process**: Open the entry point class and look for the `handleLoadPackage` method.
    -   Identify which package names are targeted (e.g., `com.android.systemui` for status bar/lockscreen mods).
3.  **Trace Hook Points**: Search the codebase (starting from the entry point) for `XposedHelpers.findAndHookMethod` or `XposedBridge.hookMethod`.
    -   Document which system classes and methods are being hooked.
4.  **Analyze IPC and Settings**: Determine how preferences are synchronized between the module's settings activity and the hooked process.
    -   Look for `BroadcastReceiver` implementations in the hook classes.
    -   Check if the app uses `XSharedPreferences` or a custom broadcast-based IPC to propagate settings updates.
5.  **Audit Hidden API Usage**: Scan for the use of Java Reflection (`Class.getDeclaredMethod`, `Method.invoke`, etc.) or `try-catch(Throwable)` blocks.
    -   LSPosed modules often use reflection to call hidden Android APIs (e.g., `DisplayManager.setTemporaryBrightness`).

## Pitfalls and Fixes
- **Missing Hook Entry Point**: If `xposed_init` is missing, the module will not load. Ensure it's in the assets folder and contains the correct class path.
- **Process Misidentification**: Hooks in SystemUI may behave differently than hooks in standard apps due to persistence and system permissions. Always verify the target package.

## Verification
- Confirm that the class listed in `xposed_init` exists and implements the appropriate Xposed interfaces.
- Verify that the target package name in `handleLoadPackage` matches the intended system component.
