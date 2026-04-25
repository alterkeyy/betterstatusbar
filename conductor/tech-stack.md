# Tech Stack

## Language
- **Java 17:** Primary programming language used for the Android application and the Xposed module logic.

## Frameworks & APIs
- **Android SDK:** The application is built against Target SDK 35, with a Minimum SDK 33.
- **LibXposed (Modern Xposed API 101):** Used to hook into Android's `SystemUI` (specifically `PhoneStatusBarView` and `NotificationShadeWindowView`) at runtime. Targets API 101 for enhanced security and performance, eliminating legacy zygote injection.

## UI & Presentation
- **Android Views / Material Components:** Standard Android views and Material Design components are used for the module's companion app and the dynamically injected brightness indicator overlay.
- **Jetpack Libraries:** Uses `androidx.fragment` and `androidx.constraintlayout` for the tabbed companion app architecture.

## Build System
- **Gradle (Kotlin DSL):** Used for dependency management and orchestrating the build process, configuring the Android plugin and SDK versions. Builds are executed via the included Gradle Wrapper (`./gradlew` or `./gradlew.bat`), removing the need for a globally installed Gradle instance.