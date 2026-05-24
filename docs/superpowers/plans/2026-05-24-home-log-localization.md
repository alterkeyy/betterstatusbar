# Home and Log Fragment Localization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Localize all hardcoded strings in the Home and Log fragments and build a release APK.

**Architecture:** Migrate hardcoded literals to `strings.xml` and update Java/XML files to reference them.

**Tech Stack:** Java, Android Resources.

---

### Task 1: Complete String Resource Definitions

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`

- [ ] **Step 1: Update English strings**

Add to `app/src/main/res/values/strings.xml`:
```xml
    <!-- Status & Stats -->
    <string name="status_activated">Activated</string>
    <string name="status_not_activated">Not Activated</string>
    <string name="stat_gestures">Gestures</string>
    <string name="stat_haptics">Haptics</string>
    <string name="state_enabled">Enabled</string>
    <string name="state_disabled">Disabled</string>
    
    <!-- Haptic Levels -->
    <string name="haptic_off">Off</string>
    <string name="haptic_subtle">Subtle</string>
    <string name="haptic_normal">Normal</string>
    <string name="haptic_strong">Strong</string>
    
    <!-- Info Rows -->
    <string name="info_build_time">Build Time</string>
    <string name="info_android_version">Android Version</string>
    <string name="info_framework">Framework</string>
    <string name="info_api_version">LibXposed API</string>
    <string name="info_device_model">Device Model</string>
    <string name="info_architecture">System Architecture</string>
    <string name="info_not_detected">Not Detected</string>
    
    <!-- Log Fragment -->
    <string name="log_title">Activity Log</string>
    <string name="log_enable">Enable Logging</string>
    <string name="log_enable_desc">Record gesture events to a file</string>
    <string name="log_recent_events">Recent Events</string>
    <string name="log_clear">Clear</string>
    <string name="log_empty">No logs yet...</string>
    
    <!-- Support -->
    <string name="support_issues_title">Encountered issues?</string>
    <string name="support_github_feedback">Feedback on GitHub</string>
```

- [ ] **Step 2: Update Chinese strings**

Add to `app/src/main/res/values-zh-rCN/strings.xml`:
```xml
    <!-- Status & Stats -->
    <string name="status_activated">已激活</string>
    <string name="status_not_activated">未激活</string>
    <string name="stat_gestures">手势</string>
    <string name="stat_haptics">振动</string>
    <string name="state_enabled">已启用</string>
    <string name="state_disabled">已禁用</string>
    
    <!-- Haptic Levels -->
    <string name="haptic_off">关闭</string>
    <string name="haptic_subtle">轻微</string>
    <string name="haptic_normal">正常</string>
    <string name="haptic_strong">强烈</string>
    
    <!-- Info Rows -->
    <string name="info_build_time">构建时间</string>
    <string name="info_android_version">安卓版本</string>
    <string name="info_framework">框架</string>
    <string name="info_api_version">LibXposed API</string>
    <string name="info_device_model">设备型号</string>
    <string name="info_architecture">系统架构</string>
    <string name="info_not_detected">未检测到</string>
    
    <!-- Log Fragment -->
    <string name="log_title">活动日志</string>
    <string name="log_enable">启用日志</string>
    <string name="log_enable_desc">记录手势事件到文件</string>
    <string name="log_recent_events">最近事件</string>
    <string name="log_clear">清除</string>
    <string name="log_empty">暂无日志...</string>
    
    <!-- Support -->
    <string name="support_issues_title">遇到问题？</string>
    <string name="support_github_feedback">GitHub 反馈</string>
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/res/values/strings.xml app/src/main/res/values-zh-rCN/strings.xml
git commit -m "feat: complete localization string resources"
```

---

### Task 2: Localize Home and Log Layouts

**Files:**
- Modify: `app/src/main/res/layout/fragment_home.xml`
- Modify: `app/src/main/res/layout/fragment_log.xml`

- [ ] **Step 1: Update fragment_home.xml**

Update titles and labels in `app/src/main/res/layout/fragment_home.xml` to use `@string/`.

- [ ] **Step 2: Update fragment_log.xml**

Update titles and labels in `app/src/main/res/layout/fragment_log.xml` to use `@string/`.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/res/layout/fragment_home.xml app/src/main/res/layout/fragment_log.xml
git commit -m "ui: use localized strings in home and log layouts"
```

---

### Task 4: Localize Fragment Logic

**Files:**
- Modify: `app/src/main/java/dev/module/betterstatusbar/HomeFragment.java`
- Modify: `app/src/main/java/dev/module/betterstatusbar/LogFragment.java`

- [ ] **Step 1: Update HomeFragment.java**

Replace hardcoded strings for status, build type, and info row titles with `getString(R.string...)`.

- [ ] **Step 2: Update LogFragment.java**

Replace "No logs yet..." with `getString(R.string.log_empty)`.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/dev/module/betterstatusbar/HomeFragment.java app/src/main/java/dev/module/betterstatusbar/LogFragment.java
git commit -m "feat: localize fragment logic"
```

---

### Task 5: Final Verification and Release Build

- [ ] **Step 1: Run tests**

Run: `./gradlew test`

- [ ] **Step 2: Build release APK**

Run: `./gradlew assembleRelease`

- [ ] **Step 3: Verify APK existence**

Check if `app/build/outputs/apk/release/app-release-unsigned.apk` (or similar) exists.
