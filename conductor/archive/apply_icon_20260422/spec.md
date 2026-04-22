# Specification: Apply App Launcher Icon (Legacy PNG)

## Overview
This track focuses on applying a new launcher icon for the companion application of the Status Bar Brightness Gesture module. Due to the availability of PNG assets, we will implement a standard multi-density legacy icon setup.

## Functional Requirements
- **Launcher Icon Integration:** Replace the existing launcher icon in the app manifest and resources with multi-density PNG assets.
- **Multi-Density Support:** Ensure the icon is available for all standard Android screen densities (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi).

## Non-Functional Requirements
- **Asset Usage:** Utilize the pre-existing PNG assets found in the `AppIcons/android/` directory.

## Acceptance Criteria
- [ ] The app displays the new custom icon in the device app drawer and launcher.
- [ ] The icon is correctly loaded from the appropriate density folder based on the device's hardware.
- [ ] The manifest correctly references the icon using the `@mipmap` resource type.

## Out of Scope
- Adaptive icon support (foreground/background layers).
- Material You dynamic coloring for the icon.
- Creating new icon assets from scratch.