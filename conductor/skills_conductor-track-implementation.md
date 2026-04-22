---
name: conductor-track-implementation
description: A spec-driven development workflow for implementing features and fixes using the Conductor methodology.
---

## When to Use
Use this skill when implementing tasks in a project managed by the Conductor framework. This applies whenever a `conductor/` directory exists and you are tasked with "implementing a track" or "starting development".

## Procedure
1.  **Verify Context**: Resolve and read the core context files via `conductor/index.md`:
    -   `product.md`: Understand the vision and audience.
    -   `tech-stack.md`: Confirm language, frameworks, and build tools.
    -   `workflow.md`: Identify the task lifecycle and testing requirements (e.g., TDD, coverage targets).
2.  **Select Track**: Parse `conductor/tracks.md`.
    -   Look for the next incomplete track (`[ ]`).
    -   Note the link to the track's subfolder (e.g., `conductor/tracks/<track_id>/`).
3.  **Initiate Track**: Update the track's status to "In Progress" (`[~]`) in `conductor/tracks.md`.
4.  **Load Track Details**: Read the track-specific artifacts:
    -   `spec.md`: Detailed functional and technical requirements.
    -   `plan.md`: The phased execution plan.
5.  **Follow Plan Phased Tasks**: For each task in `plan.md`:
    -   **Mark In Progress**: Change task status from `[ ]` to `[~]`.
    -   **Apply Workflow**: Follow the project's `workflow.md` lifecycle (e.g., Write failing tests -> Implement -> Refactor -> Verify coverage).
    -   **Manual/Automated Verification**: Perform the verification steps listed in the task description.
    -   **Mark Complete**: Change task status to `[x]`.
6.  **Finalize Track**: Once all tasks in `plan.md` are complete, update the track status to `[x]` in `conductor/tracks.md`.

## Pitfalls and Fixes
- **Missing Context**: If `conductor/` is present but files are missing, run `/conductor:setup`.
- **Status Sync**: Always update `tracks.md` and the track's `plan.md` *before* starting work to maintain the source of truth.

## Verification
- Confirm `tracks.md` reflects the correct "In Progress" or "Complete" status.
- Ensure all tasks in the track's `plan.md` are marked as complete.
