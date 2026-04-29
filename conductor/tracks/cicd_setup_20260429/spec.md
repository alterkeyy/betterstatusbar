# Specification: GitHub CI/CD and Version Management

## Overview
This track implements a robust GitHub Actions CI/CD pipeline for the `betterstatusbar` Android project. It automates building, signing, and releasing architecture-specific APKs while maintaining a systematic versioning scheme.

## Functional Requirements
1.  **Architecture-Specific Builds:** The CI/CD pipeline must compile APKs for `x86_64`, `arm64-v8a`, and `armeabi-v7a` architectures separately to optimize file size.
2.  **Automated Versioning:**
    *   Format: `Major.Minor.Patch+CommitCount`.
    *   `VersionCode` will be derived from the total commit count on the main branch.
    *   `VersionName` will be derived from a base version (e.g., `1.0.0`) appended with the build number.
3.  **CI/CD Triggers:**
    *   **Pushes to Main:** Build APKs and upload as workflow artifacts.
    *   **Pull Requests:** Run build check to ensure code integrity.
    *   **Tags (v*):** Create a GitHub Release and attach signed APKs as assets.
4.  **Secure Signing:** Configure the build to use GitHub Secrets for:
    *   `KEYSTORE_FILE` (Base64 encoded)
    *   `KEY_ALIAS`
    *   `KEYSTORE_PASSWORD`
    *   `KEY_PASSWORD`

## Technical Details
- **CI Provider:** GitHub Actions.
- **Build Tool:** Gradle (with Kotlin DSL as per project convention).
- **Environment:** Ubuntu-latest runner.

## Acceptance Criteria
- [ ] Pushing a tag (e.g., `v1.1.0`) automatically creates a GitHub Release.
- [ ] Each Release contains exactly three APKs (x86_64, arm64-v8a, armeabi-v7a).
- [ ] Versioning increases automatically with every commit.
- [ ] Signing credentials are NOT hardcoded and are correctly retrieved from secrets.

## Out of Scope
- Automated testing within the CI pipeline (will be handled in a separate track if needed).
- Automatic publishing to F-Droid or Play Store.
