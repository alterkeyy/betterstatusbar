# Tech Stack

## Language
- **Java 17:** Primary programming language used for the Android application and the Xposed module logic.

## Frameworks & APIs
- **Android SDK:** The application is built against Target SDK 35, with a Minimum SDK 33.
- **LSPosed (Xposed Framework):** Used to hook into Android's `SystemUI` (specifically `PhoneStatusBarView` and `NotificationShadeWindowView`) at runtime to capture touch events and manipulate display brightness.

## UI & Presentation
- **Android Views / Material Components:** Standard Android views and Material Design components are used for the module's companion app and the dynamically injected brightness indicator overlay.

## Build System
- **Gradle (Kotlin DSL):** Used for dependency management and orchestrating the build process, configuring the Android plugin and SDK versions. Builds are executed via the included Gradle Wrapper (`./gradlew` or `./gradlew.bat`), removing the need for a globally installed Gradle instance.