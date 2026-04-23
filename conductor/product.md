# Product Guide: Status Bar Brightness Gesture

## Product Vision
An LSPosed module designed for power users that allows seamless, gesture-based control over screen brightness by swiping horizontally on the Android status bar. The module aims to feel like a native Android feature, blending perfectly with Material You design language.

## Target Audience
- **Power Users:** Individuals who root their Android devices (using Magisk and LSPosed) to customize their system UI and tweak their device's functionality beyond stock capabilities.

## Design Philosophy
- **Native-Feeling:** The brightness indicator overlay must match the official Android look and feel. It leverages Material You (following the wallpaper's accent color) so that the gesture feels like a built-in feature rather than a third-party add-on.

## Key Features (Current)
- Horizontal swipe on the status bar to increase/decrease brightness.
- **Relative Brightness Adjustment (Toggle):** Brightness can be adjusted relative to its current value from any starting position.
- **Extensible Status Bar Gestures:** Supports configurable single tap, double tap, and long tap gestures on specific icons (Battery, Clock) and empty areas.
- **Built-in System Actions:** Gestures can be assigned to common system tasks like toggling Dark Mode, toggling Power Saving mode, or instantly Locking the screen, in addition to custom Android intents.
- **Refined Haptics & Sensitivity:** Continuous tactile feedback during swipes, with user-configurable vibration intensity and gesture sensitivity.
- **Tabbed Companion App (MD3):** A modern Material Design 3 interface with Home, Settings, and Log tabs for better organization.
- **Module Status Detection:** Real-time feedback on whether the LSPosed module is active and hooking correctly.
- **Persistent Activity Log:** A chronological log of recognized gestures and actions, persisted to a local text file for debugging and verification.
- Functional with the notification shade open or closed, and on the lockscreen.
- A dynamic brightness indicator that matches the system's accent color.
- Configurable settings via the module's companion app (toggle gesture, toggle indicator).

## Planned Enhancements

## Compatibility Strategy
- **AOSP Focus:** Maintain rock-solid stability and compatibility with AOSP-based Android 12+ ROMs, including Pixel stock (GrapheneOS, CalyxOS) and popular LineageOS derivatives (crDroid, EvolutionX, DerpFest, etc.).