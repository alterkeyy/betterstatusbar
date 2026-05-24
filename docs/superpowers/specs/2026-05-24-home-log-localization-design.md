# Design Spec: Home and Log Fragment Localization

## 1. Overview
Localize all remaining hardcoded strings in the Home and Log fragments to ensure a consistent multilingual experience.

## 2. Requirements
- Migrate all hardcoded strings in `HomeFragment.java`, `LogFragment.java`, `fragment_home.xml`, and `fragment_log.xml` to `strings.xml`.
- Provide complete Chinese (zh-rCN) translations.
- Support dynamic language switching implemented in the previous task.
- Build a production release APK after implementation.

## 3. Architecture & Implementation

### 3.1 Persistence
- No new persistence keys are required. This task focuses on resource management.

### 3.2 UI Changes
- **Home Fragment:** 
    - Localize status (Activated/Not Activated), stats headers, and info row titles.
    - Localize build type names and Android/LSPosed detection labels.
- **Log Fragment:**
    - Localize page title, switch labels, descriptions, and the "No logs yet..." placeholder.

### 3.3 Resource Updates
- **`app/src/main/res/values/strings.xml`**:
    - Add keys for status, stats, haptic levels, info titles, and log controls.
- **`app/src/main/res/values-zh-rCN/strings.xml`**:
    - Add Chinese translations for all new keys.

## 4. Testing Strategy
- **Manual Verification:**
    - Switch language in Settings -> Verify Home and Log pages update immediately.
    - Check "Activated" status coloring and text in both languages.
    - Verify Log page empty state text.
- **Build Verification:**
    - Run `./gradlew assembleRelease` and ensure it completes successfully.
