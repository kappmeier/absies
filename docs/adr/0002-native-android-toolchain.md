# 0002. Native Android toolchain

- Status: proposed
- Date: 2026-11-22

## Context

absies is a greenfield Android app for small school children with pen input. No language, UI toolkit, or build setup has been chosen yet, and docs/coding-standards.md leaves the language open until an ADR covers it.

## Decision

Native Android development: Kotlin as the language, Jetpack Compose for the UI, Gradle with Kotlin DSL and a version catalog, Hilt for dependency injection, minSdk 24. Development happens in Android Studio with JDK 17. Logic is tested with JUnit, the exercise flow with Compose UI tests. Flutter and web were rejected because the project has no cross-platform requirement. XML layouts and Java were rejected as superseded defaults.

## Consequences

The APK stays small, and pen input gets direct access to pointer and stylus events. Pen behavior must be verified on a real tablet, not only in the emulator. The language decision in docs/coding-standards.md is now Kotlin, so Kotlin conventions apply.
