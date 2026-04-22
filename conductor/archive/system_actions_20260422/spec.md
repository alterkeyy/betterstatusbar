# Specification: Built-in System Actions for Gestures

## Overview
Expand the capabilities of advanced status bar gestures to include built-in system actions, removing the need for users to manually configure complex intents for common tasks. This update introduces toggles for Dark Mode, Power Saving, and Screen Lock.

## Functional Requirements
- **System Actions Support:** Extend `StatusBarActionManager` to recognize and execute a new `system:` prefix.
    - `system:toggle_dark_mode`: Toggles the device's night mode setting (requires `UiModeManager`).
    - `system:toggle_power_save`: Toggles the device's battery saver mode (requires `PowerManager`).
    - `system:lock_screen`: Instantly turns off the screen and locks the device (requires `PowerManager.goToSleep()` or equivalent).
- **Settings UI Revamp:** Replace the direct `EditText` dialog with a single-choice list dialog when tapping an action row. The list must contain:
    - None (clears the action)
    - Toggle Dark Mode
    - Toggle Power Saving
    - Lock Screen
    - Custom Intent... (opens the previous `EditText` dialog to enter an `intent:` string)
- **Display Labeling:** When displaying the currently selected action in the settings UI, map the `system:` strings to their user-friendly labels (e.g., show "Toggle Dark Mode" instead of `system:toggle_dark_mode`).

## Non-Functional Requirements
- **Permissions:** Since the execution happens within the SystemUI process (which holds system-level permissions), utilize standard framework APIs for these toggles where possible. If reflection is needed, wrap it in robust try-catch blocks.

## Acceptance Criteria
- Tapping an advanced gesture row displays a list of options.
- Selecting a built-in action saves the setting and correctly maps the display label.
- Selecting "Custom Intent..." opens a secondary dialog allowing manual intent configuration.
- Executing a configured `system:` gesture performs the corresponding system toggle accurately.

## Out of Scope
- Adding root-only actions (like reboot/shutdown) that require executing shell commands outside of the SystemUI context.