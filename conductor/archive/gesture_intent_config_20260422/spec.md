# Specification: Streamline Gesture Intent Configuration

## Overview
Improve the user experience for configuring advanced status bar gestures. The companion app's settings dialog will pre-fill the required "intent:" prefix, validate the user's input before saving, and provide clearer instructions to reduce configuration errors.

## Functional Requirements
- **Pre-fill Prefix:** When the user taps an action row to open the configuration dialog, the `EditText` must be pre-filled with the existing value. If the current value is empty or "None", it should be pre-filled with the prefix "intent:".
- **Input Validation:** When the user clicks "Save", the app must validate the input:
    - The string must start with "intent:".
    - The remaining string (after the prefix) must either contain a "/" (indicating a `package/.Activity` component) OR be a non-empty string (indicating an intent action).
    - If the validation fails, a `Toast` message should inform the user of the error, and the value should NOT be saved.
- **UI Instructions:** Update the `AlertDialog` message and the `EditText` hint to explicitly guide the user on the expected format (e.g., `intent:pkg/.Activity`).

## Non-Functional Requirements
- **Feedback:** Error messages for invalid inputs must be clear and actionable.

## Acceptance Criteria
- Clicking an unconfigured gesture row opens a dialog with the input field pre-filled with "intent:".
- Attempting to save an invalid string (e.g., just "intent:" or a string without the prefix) triggers a Toast error and does not overwrite the setting.
- Saving a valid string successfully updates the preference and broadcasts the change.
- The dialog instructions and hints reflect the pre-filled format.

## Out of Scope
- Advanced validation of the actual existence of the package/activity on the device.