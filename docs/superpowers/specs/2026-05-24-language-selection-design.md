# Design Spec: Language Selection (System Default, Chinese, English)

## 1. Overview
Add a language selection option to the settings menu allowing users to override the system language for this app.

## 2. Requirements
- Add a "General" section at the top of the Settings screen.
- Provide options: "System Default", "中文" (Chinese), and "English".
- Persist the selection locally using `SharedPreferences`.
- Apply the language change immediately without requiring a manual app restart.
- Support Android 13+ (API 33) per-app language settings.

## 3. Architecture & Implementation

### 3.1 Persistence
- **File:** `Prefs.java`
- **Key:** `KEY_LANGUAGE` (String)
- **Values:** `""` (System Default), `"zh"`, `"en"`.

### 3.2 UI Changes
- **Layout:** `fragment_settings.xml`
- **Component:** A new `MaterialCardView` for "General" settings at the top.
- **Row:** Use `row_action.xml` for the language selector.
- **Logic:** `SettingsFragment.java` will handle the click and show a `MaterialAlertDialogBuilder` with radio buttons.

### 3.3 Locale Management
- **API:** `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))`
- **Reasoning:** This is the standard AndroidX way to handle per-app languages. It integrates with the system "App Language" settings on Android 13+ and handles persistence/application automatically.

### 3.4 Resources
- **`values/strings.xml`**:
    - `general_header`: "General"
    - `language_title`: "Language"
    - `language_system_default`: "System Default"
    - `language_chinese`: "Chinese"
    - `language_english`: "English"
- **`values-zh-rCN/strings.xml`**:
    - Translations for all strings.

## 4. Testing Strategy
- **Manual Verification:**
    - Change language to Chinese -> Verify UI updates.
    - Change language to English -> Verify UI updates.
    - Change to System Default -> Verify it follows the phone's system language.
    - Verify selection persists after closing and reopening the app.
- **Automated Tests:**
    - Add a unit test in `PrefsTest.java` to ensure the key is correctly defined.
