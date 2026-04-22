# Status Bar Brightness Gesture

An LSPosed module that lets you swipe horizontally on the status bar to control screen brightness — works with the notification shade open or closed, and on the lockscreen.

## Requirements

- Android 12 or higher
- [Magisk](https://github.com/topjohnwu/Magisk) (rooted device)
- [LSPosed](https://github.com/LSPosed/LSPosed) (Zygisk edition recommended)

## Installation

1. Install the APK from the [Releases](../../releases) page
2. Open LSPosed → Modules → enable **Status Bar Brightness Gesture**
3. Set the scope to **System UI**
4. Reboot
5. Open the app and configure your preferences

### One-time ADB setup (required for toggle persistence across reboots)

Connect your device via ADB and run:
```
adb shell pm grant dev.module.statusbarbrightnessgesture android.permission.WRITE_SECURE_SETTINGS
```
This only needs to be run once after a fresh install. It survives reboots and app updates.

## Usage

- **Swipe right** on the status bar to increase brightness
- **Swipe left** on the status bar to decrease brightness
- Works with the notification shade open or closed
- Works on the lockscreen
- The brightness indicator follows your wallpaper accent colour

## Settings

Open the app to configure:
- **Enable gesture** — turn the swipe gesture on or off
- **Show brightness indicator** — show or hide the brightness % overlay while swiping

## Compatibility

Works on most AOSP-based Android 12+ ROMs including:
- Pixel stock (GrapheneOS, CalyxOS)
- LineageOS and derivatives (crDroid, EvolutionX, DerpFest, etc.)

May not work on heavily customised ROMs such as Samsung OneUI or Xiaomi HyperOS, as these replace the standard status bar classes.

## Tested on

- Pixel 6 (Oriole), DerpFest 16.2, Android 16, LSPosed v1.11.0

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/mbatthew/StatusBarBrightnessGesture/blob/cc585c53bd0278cc5114ed39ca640b52e12d057c/LICENSE) file for details.