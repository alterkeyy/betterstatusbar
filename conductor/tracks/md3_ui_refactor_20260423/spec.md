# Specification: MD3 UI Refactor and Navigation Bar

## Overview
Refactor the companion app's user interface to utilize a Material Design 3 (MD3) expressive style. The new UI will feature a bottom navigation bar with three primary tabs: Home, Settings, and Log. This refactor aims to improve the user experience, organize the increasing number of settings, and provide greater visibility into the module's activity and status.

## Functional Requirements
1. **Bottom Navigation Bar:**
   - Implement a primary navigation component (BottomNavigationView) with three destinations: Home, Settings, and Log.
2. **Home Tab:**
   - Display the current activation status of the LSPosed module.
   - Status detection will be performed using "Hook Verification" (attempting to call a method that the module hooks).
   - Display general system information relevant to the module (e.g., current screen brightness, Android version).
3. **Settings Tab:**
   - Migrate all existing configuration UI from `SettingsActivity` into this tab.
   - Group related settings into MD3 Material Cards (elevated or tonal).
   - Utilize MD3 Material Switches for all boolean toggles.
   - Utilize MD3 Material Sliders for continuous configuration values (e.g., gesture sensitivity, haptic intensity).
4. **Log Tab:**
   - Display a chronological log of tap behaviors and gesture recognitions.
   - Each log entry must include a timestamp and the specific action triggered.
   - Implement a toggle to enable/disable logging.
   - Logs must be persisted to a simple text file in the app's internal storage.
5. **MD3 Expressive Style:**
   - The entire application UI must strictly adhere to Material 3 design guidelines.
   - Integrate Dynamic Color (Material You) so the app's theme matches the system's accent colors.

## Non-Functional Requirements
- **Performance:** Writing to the text file log must be efficient and not block the main UI thread.
- **Maintainability:** The new tabbed architecture should separate concerns (e.g. using fragments).

## Acceptance Criteria
- [ ] The app launches with a bottom navigation bar containing Home, Settings, and Log tabs.
- [ ] The Home tab accurately reports if the LSPosed module is active.
- [ ] The Settings tab contains all previously existing preferences, styled with MD3 cards, switches, and sliders.
- [ ] The app's color scheme adapts to the system's Material You theme.
- [ ] Performing gestures (e.g., tapping the status bar) writes a timestamped entry to the log text file.
- [ ] The Log tab displays the contents of the log text file and provides a toggle to pause/resume logging.

## Out of Scope
- Adding new gesture functionalities or changing the core hooking logic of the LSPosed module itself.