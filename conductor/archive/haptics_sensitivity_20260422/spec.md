# Specification: Refined Haptics & Sensitivity

## Overview
Improve touch detection, adjust gesture slop, and add vibration responses for tactile feedback during the swipe. This track focuses on giving users continuous tactile feedback and the ability to customize the gesture's sensitivity and haptic intensity.

## Functional Requirements
- **Haptic Trigger (Continuous Tick):** Provide a continuous, subtle tick as the finger moves horizontally during the brightness adjustment swipe.
- **Sensitivity Configuration:** Add a setting in the companion app's UI to allow users to configure the swipe sensitivity (e.g., adjusting the distance-to-brightness ratio).
- **Haptic Intensity Configuration:** Add a setting allowing users to choose the strength of the haptic feedback.
- **Vibration Method:** Utilize `View.performHapticFeedback` with standard Android `HapticFeedbackConstants` for native-feeling vibrations that respect the user's system-wide touch vibration preferences.

## Non-Functional Requirements
- **Performance:** Continuous haptic feedback must be throttled efficiently and not cause UI stuttering or lag.
- **Integration:** The new settings must be broadcasted to the Xposed hook without requiring a reboot, consistent with existing preferences.

## Acceptance Criteria
- User can adjust swipe sensitivity via the settings app.
- User can configure the intensity of the haptic feedback.
- Swiping horizontally triggers a continuous ticking vibration based on the chosen intensity and finger movement.
- Haptic feedback relies on `HapticFeedbackConstants`.
- The gesture responsiveness changes immediately when sensitivity settings are updated.

## Out of Scope
- Using the `Vibrator` service directly for complex or custom vibration waveforms.
- Modifying the visual appearance of the brightness indicator.