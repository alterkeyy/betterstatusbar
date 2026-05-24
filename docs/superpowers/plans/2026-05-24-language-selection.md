# Language Selection Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a language selection setting (System Default, Chinese, English) to the app using Android 13's per-app language APIs.

**Architecture:** Use `AppCompatDelegate.setApplicationLocales()` to manage the app's locale. Persist the key in `Prefs.java` and expose the UI in `SettingsFragment.java`.

**Tech Stack:** Java, AndroidX AppCompat, Material Components.

---

### Task 1: Define Preference Key

**Files:**
- Modify: `app/src/main/java/dev/module/betterstatusbar/Prefs.java`
- Test: `app/src/test/java/dev/module/betterstatusbar/PrefsTest.java`

- [ ] **Step 1: Write the failing test**

Modify `app/src/test/java/dev/module/betterstatusbar/PrefsTest.java`:
```java
@Test
public void testLanguageKeyDefined() {
    assertNotNull(Prefs.KEY_LANGUAGE);
    assertEquals("sbbrightness_language", Prefs.KEY_LANGUAGE);
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test`
Expected: Compilation error or failure if `KEY_LANGUAGE` is missing.

- [ ] **Step 3: Add the key to Prefs.java**

Add to `app/src/main/java/dev/module/betterstatusbar/Prefs.java`:
```java
public static final String KEY_LANGUAGE = "sbbrightness_language";
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/dev/module/betterstatusbar/Prefs.java app/src/test/java/dev/module/betterstatusbar/PrefsTest.java
git commit -m "feat: define language preference key"
```

---

### Task 2: Add String Resources

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values-zh-rCN/strings.xml`

- [ ] **Step 1: Add English strings**

Modify `app/src/main/res/values/strings.xml`:
```xml
<string name="general_header">General</string>
<string name="language_title">Language</string>
<string name="language_system_default">System Default</string>
<string name="language_chinese">中文 (Chinese)</string>
<string name="language_english">English</string>
```

- [ ] **Step 2: Add Chinese strings**

Create `app/src/main/res/values-zh-rCN/strings.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">betterstatusbar</string>
    <string name="app_description">在状态栏水平滑动调节屏幕亮度</string>
    <string name="module_description">在状态栏增加水平滑动动作以控制屏幕亮度，镜像自 crDroid 的内置实现。</string>
    <string name="general_header">常规</string>
    <string name="language_title">语言</string>
    <string name="language_system_default">系统默认</string>
    <string name="language_chinese">中文</string>
    <string name="language_english">英文</string>
    
    <!-- Other existing strings if any need translation -->
    <string name="settings_title">设置</string>
</resources>
```
*Note: I'll need to check if other strings exist in `fragment_settings.xml` that need translation.*

- [ ] **Step 3: Commit**

```bash
git add app/src/main/res/values/strings.xml app/src/main/res/values-zh-rCN/strings.xml
git commit -m "feat: add language string resources"
```

---

### Task 3: Update Settings Layout

**Files:**
- Modify: `app/src/main/res/layout/fragment_settings.xml`

- [ ] **Step 1: Add General section with Language row**

Modify `app/src/main/res/layout/fragment_settings.xml` to add the "General" card at the top (before "Core Gestures"):
```xml
        <!-- General Header -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp"
            android:layout_marginBottom="8dp"
            android:text="@string/general_header"
            android:textAppearance="?attr/textAppearanceTitleSmall"
            android:textColor="?attr/colorPrimary" />

        <!-- General Card -->
        <com.google.android.material.card.MaterialCardView
            style="?attr/materialCardViewElevatedStyle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            app:cardCornerRadius="24dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <include layout="@layout/row_action" android:id="@+id/row_language" />

            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/res/layout/fragment_settings.xml
git commit -m "ui: add language setting row to layout"
```

---

### Task 4: Implement Language Logic in SettingsFragment

**Files:**
- Modify: `app/src/main/java/dev/module/betterstatusbar/SettingsFragment.java`

- [ ] **Step 1: Implement getLanguageLabel helper**

In `SettingsFragment.java`, add a helper to get the display label for a language tag:
```java
private String getLanguageLabel(String tag) {
    if (tag == null || tag.isEmpty()) return getString(R.string.language_system_default);
    if (tag.equals("zh")) return getString(R.string.language_chinese);
    if (tag.equals("en")) return getString(R.string.language_english);
    return tag;
}
```

- [ ] **Step 2: Bind the language row in onCreateView**

In `onCreateView`, add:
```java
bindLanguageRow(view.findViewById(R.id.row_language));
```

- [ ] **Step 3: Implement bindLanguageRow and showLanguageDialog**

```java
private void bindLanguageRow(View row) {
    TextView labelView = row.findViewById(R.id.action_label);
    TextView valView = row.findViewById(R.id.action_value);

    labelView.setText(R.string.language_title);
    String current = mPrefs.getString(Prefs.KEY_LANGUAGE, "");
    valView.setText(getLanguageLabel(current));

    row.setOnClickListener(v -> showLanguageDialog(valView));
}

private void showLanguageDialog(TextView valView) {
    String[] options = {
            getString(R.string.language_system_default),
            getString(R.string.language_chinese),
            getString(R.string.language_english)
    };
    String[] tags = {"", "zh", "en"};
    String current = mPrefs.getString(Prefs.KEY_LANGUAGE, "");
    
    int checkedItem = 0;
    for (int i = 0; i < tags.length; i++) {
        if (tags[i].equals(current)) {
            checkedItem = i;
            break;
        }
    }

    new MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.language_title)
            .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                dialog.dismiss();
                String newTag = tags[which];
                mPrefs.edit().putString(Prefs.KEY_LANGUAGE, newTag).apply();
                valView.setText(getLanguageLabel(newTag));
                
                // Apply locale
                androidx.core.os.LocaleListCompat appLocales = 
                    newTag.isEmpty() ? 
                    androidx.core.os.LocaleListCompat.getEmptyLocaleList() : 
                    androidx.core.os.LocaleListCompat.forLanguageTags(newTag);
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocales);
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
}
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/dev/module/betterstatusbar/SettingsFragment.java
git commit -m "feat: implement language selection logic"
```

---

### Task 5: Final Verification

- [ ] **Step 1: Build the app**

Run: `./gradlew assembleDebug`

- [ ] **Step 2: Manual testing**
- Open settings.
- Change language to "Chinese".
- Verify UI text changes to Chinese.
- Change language to "English".
- Verify UI text changes back.
- Change to "System Default".
- Verify it follows system language.
