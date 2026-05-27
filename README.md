# betterstatusbar

**English** | [简体中文](./README.zh.md)

An LSPosed module that lets you swipe horizontally on the status bar to control screen brightness — works with the notification shade open or closed, and on the lockscreen.

## New in v2.0.0 (LibXposed)

- **Migrated to LibXposed (API 101)**: Improved performance and better compatibility with modern LSPosed versions.
- **Customizable Tap Actions**: Assign single tap, double tap, or long press actions to the battery icon, clock, or empty status bar areas.
- **System Toggles**: Toggle Dark Mode, Power Saving, or Lock Screen directly from the status bar.
- **Haptic Feedback**: Subtle vibration feedback when adjusting brightness or triggering actions.
- **Relative Brightness**: Option to adjust brightness relative to current levels instead of absolute positioning.
- **Swipe Sensitivity**: Fine-tune the horizontal swipe distance required to change brightness.
- **Gesture Logging**: Built-in log viewer to debug gesture detection and preference updates.

## Requirements

- **Android 13** or higher (SDK 33+)
- [Magisk](https://github.com/topjohnwu/Magisk) (rooted device)
- [LSPosed v1.9.3+](https://github.com/LSPosed/LSPosed) or other LibXposed-compatible framework

## Installation

1. Install the APK from the [Releases](https://github.com/parallelcc/MiCTS/releases) page
2. Open LSPosed → Modules → enable **betterstatusbar**
3. Ensure the scope includes **System UI**
4. Reboot
5. Open the app and configure your preferences

### One-time ADB setup (required for toggle persistence across reboots)

Connect your device via ADB and run:
```bash
adb shell pm grant dev.module.betterstatusbar android.permission.WRITE_SECURE_SETTINGS
```
This only needs to be run once after a fresh install. It survives reboots and app updates.

## Usage

- **Swipe Right/Left**: Increase or decrease brightness by sliding horizontally across the status bar.
- **Tap Actions**: 
  - **Battery Icon**: Single tap to see battery usage, or customize for other actions.
  - **Clock**: Single tap to see alarms, or customize for other actions.
  - **Empty Area**: Double tap to sleep/lock screen (if configured), etc.
- **Works Everywhere**: Lockscreen, notification shade open, or while using apps.
- **Indicator**: A non-intrusive percentage overlay follows your swipe, styled with your wallpaper's accent colour.

## Settings

Open the app to configure:
- **Gestures**: Enable/disable brightness swipe and tap actions.
- **Brightness Mode**: Choose between absolute (position-based) or relative (increment-based) adjustment.
- **Haptics**: Adjust the intensity of vibration feedback (None to Strong).
- **Sensitivity**: Adjust how far you need to swipe to trigger brightness changes.
- **Custom Actions**: Map Single Tap, Double Tap, and Long Press for:
  - Battery Icon
  - Time/Clock
  - Status Bar Background
- **Action Types**:
  - Launch Intent (System Alarms, Power Usage, etc.)
  - Toggle Dark Mode
  - Toggle Power Saving
  - Lock Screen
  - to be added ...

## Compatibility

Works on most AOSP-based Android 13+ ROMs including:
- Pixel stock (GrapheneOS, CalyxOS)
- LineageOS and derivatives (crDroid, EvolutionX, DerpFest, etc.)

**Note**: May not work on heavily customized ROMs as they often replace standard SystemUI status bar classes.

## Tested on

- Samsung tab S8, OneUI 8 Android 16, latest LSPosed v2.0.3

## CI/CD Setup(WIP)

This project uses GitHub Actions for automated building, signing, and releasing architecture-specific APKs.

### Required GitHub Secrets

To enable automated signed releases, configure the following secrets in your GitHub repository (**Settings > Secrets and variables > Actions**):

- `KEYSTORE_BASE64`: Your Android release keystore file, encoded in Base64 (`base64 -w 0 your_keystore.jks`).
- `KEYSTORE_PASSWORD`: The password for your keystore.
- `KEY_ALIAS`: The alias for your release key.
- `KEY_PASSWORD`: The password for your release key.

### Automated Workflows

- **Pull Requests**: Builds the project to ensure code integrity.
- **Push to Main**: Builds and signs architecture-specific APKs (x86_64, arm64-v8a, armeabi-v7a), uploading them as workflow artifacts.
- **Tag (v*)**: Automatically creates a GitHub Release and attaches the signed APKs.
  
## Contributing

Feel free to submit Pull Requests or open Issues to discuss new features and bug fixes! 

## Acknowledgments

The creation of this project was made possible by the following outstanding open-source projects.
[mbatthew/StatusBarBrightnessGesture: LSPosed module — swipe the status bar to control brightness on Android 12+](https://github.com/mbatthew/StatusBarBrightnessGesture)
## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/mbatthew/betterstatusbar/blob/cc585c53bd0278cc5114ed39ca640b52e12d057c/LICENSE) file for details.
