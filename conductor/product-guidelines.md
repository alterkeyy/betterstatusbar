# Product Guidelines

## 1. Tone and Prose Style
- **Formal & Technical:** All user-facing text, settings descriptions, and documentation must be precise and formal. Avoid conversational fluff. Use accurate Android terminology (e.g., "Quick Settings", "Notification Shade", "SystemUI").
- **Clarity over Brevity:** While being concise is important, prioritizing clear, technical explanations for power users is paramount. Ensure that advanced options explicitly state their impact on system behavior.

## 2. Visual Identity & Branding
- **Material You Strict:** The module's companion app and any overlay UI must strictly adhere to Google's Material You design guidelines.
- **System Integration:** UI components should automatically adapt to the system's dynamic color scheme (Monet) and dark/light modes. The goal is for the app and the gesture indicator to look indistinguishable from stock Android components.

## 3. User Experience (UX) Principles
- **Unobtrusive Operation:** The core gesture must operate silently without interrupting the user's primary tasks. The brightness indicator should appear naturally and disappear promptly.
- **Feedback & Error Handling:** Use **Inline Hints** within the settings UI to communicate configuration errors or invalid states. Avoid disruptive popups or transient snackbars unless absolutely necessary. Error messages should be technically accurate and provide a clear path to resolution (e.g., "ADB permission WRITE_SECURE_SETTINGS not granted").