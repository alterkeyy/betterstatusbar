# Product Guide: Status Bar Brightness Gesture

## Product Vision
An LSPosed module designed for power users that allows seamless, gesture-based control over screen brightness by swiping horizontally on the Android status bar. The module aims to feel like a native Android feature, blending perfectly with Material You design language.

## Target Audience
- **Power Users:** Individuals who root their Android devices (using Magisk and LSPosed) to customize their system UI and tweak their device's functionality beyond stock capabilities.

## Design Philosophy
- **Native-Feeling:** The brightness indicator overlay must match the official Android look and feel. It leverages Material You (following the wallpaper's accent color) so that the gesture feels like a built-in feature rather than a third-party add-on.

## Key Features (Current)
- Horizontal swipe on the status bar to increase/decrease brightness.
- Functional with the notification shade open or closed, and on the lockscreen.
- A dynamic brightness indicator that matches the system's accent color.
- Configurable settings via the module's companion app (toggle gesture, toggle indicator).

## Planned Enhancements
- **Relative Brightness Adjustment (Toggle):** Brightness should be adjustable by swiping from any position (relative adjustment), rather than jumping to the absolute value corresponding to the touch's specific horizontal position on the screen.
- **Refined Haptics & Sensitivity:** Improve touch detection, adjust gesture slop, and add vibration responses for tactile feedback during the swipe.
- **Status Bar Shortcuts:** Implement tap actions on status bar icons to jump directly to their respective settings pages (e.g., tapping the battery icon opens the battery settings).

## Compatibility Strategy
- **AOSP Focus:** Maintain rock-solid stability and compatibility with AOSP-based Android 12+ ROMs, including Pixel stock (GrapheneOS, CalyxOS) and popular LineageOS derivatives (crDroid, EvolutionX, DerpFest, etc.).