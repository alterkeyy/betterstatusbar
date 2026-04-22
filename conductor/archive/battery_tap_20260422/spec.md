# Specification: Extensible Status Bar Gestures

## Overview
Implement a comprehensive gesture handling system for the status bar. This system will support single tap, double tap, and long tap gestures on both specific icons (e.g., battery) and empty areas of the status bar. Each gesture/area combination will be configurable to launch a specific activity or perform a system action.

## Functional Requirements
- **Generic Gesture Detector:** Implement a detector for single tap, double tap, and long tap.
- **Target Areas:**
    - **Battery Icon:** Specific handling for the battery meter view.
    - **Status Bar Background:** Handling for taps on empty areas (the `PhoneStatusBarView` itself).
- **Configurable Actions:**
    - Each gesture (Single/Double/Long Tap) on each area (Battery/Background) can be assigned an action.
    - **Action Types:**
        - Launch specific Activity (Package/Class name string).
        - Open Battery Settings (Default for Battery tap).
        - No Action (None).
- **Settings UI:**
    - Add UI to configure these assignments.
    - Provide a way to input custom activity strings.
- **Xposed Hook:**
    - Intercept touches on `PhoneStatusBarView` and its children.
    - Use the generic detector to identify gestures.
    - Dispatch actions based on the configuration.

## Technical Details
- **Gesture Detection:** Use `android.view.GestureDetector` or a custom state machine to handle taps and double taps within the `onTouchEvent` hook.
- **Action Execution:** Use `Context.startActivity` with appropriate flags for external intents.
- **Preferences:** Use `Settings.Secure` for persistence and broadcasts for live updates.

## Acceptance Criteria
- User can configure separate actions for single, double, and long taps on the battery icon.
- User can configure separate actions for single, double, and long taps on the empty status bar area.
- Actions trigger correctly and launch the expected activities.
- Gestures do not interfere with the existing brightness swipe gesture.

## Out of Scope
- Gestures in the notification shade or lockscreen (for now).
- Non-activity actions (e.g., toggle Wi-Fi) unless implemented as activity shortcuts.
