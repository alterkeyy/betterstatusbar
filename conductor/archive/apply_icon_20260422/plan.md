# Implementation Plan: Apply App Launcher Icon (Legacy PNG)

## Phase 1: Asset Organization
- [x] Task: Copy the `ic_launcher.png` files from `AppIcons/android/` density subdirectories to the corresponding `app/src/main/res/mipmap-<density>/` directories. 6238851
- [x] Task: Ensure the target directories (`mipmap-mdpi`, `mipmap-hdpi`, etc.) exist in the project structure. 6238851
- [x] Task: Conductor - User Manual Verification 'Phase 1: Asset Organization' (Protocol in workflow.md) 6238851

## Phase 2: Manifest Update and Testing
- [x] Task: Update `app/src/main/AndroidManifest.xml` to set `android:icon="@mipmap/ic_launcher"`. 6238851
- [x] Task: Remove `android:roundIcon` if it was previously used or if the round icon assets are not provided. 6238851
- [x] Task: Remove the legacy fallback launcher icon from `app/src/main/res/drawable/ic_launcher_foreground.xml`. 6238851
- [x] Task: Clean and build the debug APK using `./gradlew clean assembleDebug` to verify no resource linking errors occur. 6238851
- [x] Task: Conductor - User Manual Verification 'Phase 2: Manifest Update and Testing' (Protocol in workflow.md) 6238851