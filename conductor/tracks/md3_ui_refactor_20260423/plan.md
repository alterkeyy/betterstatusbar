# Implementation Plan: MD3 UI Refactor and Navigation Bar

## Phase 1: Setup and Navigation Scaffolding [checkpoint: 7353373]
- [x] Task: Migrate to Material 3 (15098d7)
    - [ ] Update `app/build.gradle.kts` to ensure Material 3 dependencies are included.
    - [ ] Update `themes.xml` (and related `colors.xml`) to inherit from `Theme.Material3.DayNight` and enable dynamic colors (Material You).
- [x] Task: Implement Main Activity Navigation Scaffolding (15098d7)
    - [ ] Create a layout for `SettingsActivity` (or rename to `MainActivity`) featuring a `BottomNavigationView` with three menu items (Home, Settings, Log).
    - [ ] Create placeholder Fragments or Views for the Home, Settings, and Log tabs.
    - [ ] Implement navigation logic to switch between tabs.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Setup and Navigation Scaffolding' (Protocol in workflow.md) (7353373)

## Phase 2: Home Tab and Module Status [checkpoint: 11ddbfa]
- [x] Task: Implement Module Status Detection (Hook Verification) (11ddbfa)
    - [ ] Write unit tests for a new `ModuleStatusChecker` class (stubbing the expected hooked method).
    - [ ] Implement `ModuleStatusChecker` to detect if the Xposed module is active.
- [x] Task: Build Home Tab UI (11ddbfa)
    - [ ] Design the Home tab layout using MD3 Cards to display module status and system info.
    - [ ] Integrate `ModuleStatusChecker` to dynamically update the UI state.
- [x] Task: Conductor - User Manual Verification 'Phase 2: Home Tab and Module Status' (Protocol in workflow.md) (11ddbfa)

## Phase 3: Settings Tab Migration [checkpoint: 1c2d0ca]
- [x] Task: Migrate Existing Preferences to MD3 (11ddbfa)
    - [ ] Refactor the existing settings UI into the Settings tab.
    - [ ] Replace standard switches with `MaterialSwitch`.
    - [ ] Replace custom sliders/seekbars with `Slider` (Material 3).
    - [ ] Group related preferences into `MaterialCardView` containers.
- [x] Task: Conductor - User Manual Verification 'Phase 3: Settings Tab Migration' (Protocol in workflow.md) (1c2d0ca)

## Phase 4: Log Tab and File Persistence [checkpoint: 30d5b76]
- [x] Task: Implement File-Based Logging Mechanism (30d5b76)
    - [ ] Write unit tests for a new `GestureLogger` class that writes timestamped strings to a local text file.
    - [ ] Implement `GestureLogger` using standard Java/Android File I/O, ensuring thread safety.
    - [ ] Integrate `GestureLogger` into the existing hook logic (e.g., `BrightnessGestureHook`) to record tap events.
- [x] Task: Build Log Tab UI (30d5b76)
    - [ ] Design the Log tab layout with a scrolling view to display the log file contents.
    - [ ] Add a `MaterialSwitch` to toggle logging on/off.
    - [ ] Implement logic to read the log file and update the UI.
- [x] Task: Conductor - User Manual Verification 'Phase 4: Log Tab and File Persistence' (Protocol in workflow.md) (30d5b76)