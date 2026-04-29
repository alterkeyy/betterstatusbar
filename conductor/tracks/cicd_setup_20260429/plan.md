# Implementation Plan: GitHub CI/CD and Version Management

This plan outlines the steps to implement a complete GitHub Actions CI/CD pipeline with architecture-specific builds and automated versioning.

## Phase 1: Gradle Build Configuration [checkpoint: 0dc20bf]
This phase focuses on configuring the local Gradle build to support ABI splitting and secure signing via environment variables.

- [x] Task: Configure ABI Splits in `app/build.gradle.kts` ad5b58e
    - [x] Enable `splits.abi` for `x86_64`, `arm64-v8a`, and `armeabi-v7a`
    - [x] Set `isUniversalApk = false` to minimize release size
- [x] Task: Secure Signing Configuration 50da2b1
    - [x] Add signing logic to `app/build.gradle.kts` that reads from environment variables
    - [x] Use `System.getenv()` for keystore password, alias, etc.
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Gradle Build Configuration' (Protocol in workflow.md)

## Phase 2: Versioning and CI Workflow
This phase focuses on implementing the automated versioning logic and setting up the GitHub Actions workflow.

- [x] Task: Implement Automated Versioning 0c9a89e
    - [x] Create a Gradle task or helper to count Git commits using `git rev-list --count HEAD`
    - [x] Update `versionCode` and `versionName` dynamically in the build script
- [ ] Task: Create GitHub Actions Workflow
    - [ ] Create `.github/workflows/android.yml`
    - [ ] Define jobs for building debug/release APKs
    - [ ] Set up signing steps using secrets
    - [ ] Add GitHub Release step for tag triggers
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Versioning and CI Workflow' (Protocol in workflow.md)

## Phase 3: Documentation and Final Verification
Finalize documentation and ensure the pipeline is ready for the user.

- [ ] Task: Update README with CI/CD Instructions
    - [ ] Document required GitHub Secrets for the user to set up (Keystore, Passwords)
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Documentation and Final Verification' (Protocol in workflow.md)
