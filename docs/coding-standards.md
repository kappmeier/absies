# Coding standards

## Language

The app is written in Kotlin and follows the official Kotlin coding conventions. Keep functions small and name them after what they do.

## Formatting and lint

Format with ktlint or the Android Studio formatter before committing. PRs should not contain formatting-only diff noise.

## Tests

Every behavior change comes with a test. Checker and recognition logic get unit tests; the exercise flow gets UI tests.

## Commits

Use conventional commit messages: `<type>: <short summary>`. The types match the branch prefixes: `feature`, `fix`, `doc`, `chore`, `hotfix`, `test`, `release`.

## Comments

Code should explain itself. Add a comment only when the reason for a choice is not visible in the code.

## Docs and other text

Human-facing text gets a humanizer pass before it is finalized; see [AGENTS.md](../AGENTS.md).
