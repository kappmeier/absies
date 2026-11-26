# Ledger: minimal app implementation

Plan: `docs/superpowers/plans/2026-11-22-minimal-app.md`
Branch: `feature/minimal-app` (base `doc/app-foundations`, spec/ADRs in PR #5, implementation in PR #6)

## Environment rulings

- Ruling: no subagent dispatch tool exists in this session, so tasks ran inline in this session (model mistral-large-4-0) instead of fresh subagents; the session also performed the ponytail-review role after each commit. Cost if wrong: less context isolation per task; mitigated by per-task commits and reviews.
- Ruling: the sandbox has no JDK/Android SDK and no network for SDK downloads, so `./gradlew` verification runs on the dev machine (README), not here. Cost if wrong: a compile error reaches the dev machine; mitigated by static review after each task. Not checked in sandbox: dependency resolution and all builds/tests.
- Ruling: correction controls (pen author toggle, clear layer) live inside the enlarged cell dialog, not the top bar, because the dialog blocks the rest of the UI. The top bar holds only the next exercise button. Cost if wrong: minor UX change, easily moved.

## Tasks

- [x] Task 1: scaffold (39607d1)
- [x] Task 2: core:exercise model (625bf8e)
- [x] Task 3: core:ink model + store + recognizer port (cee54a0)
- [x] Task 4: math generator (532ab1a)
- [x] Task 5: column layout (6977c69)
- [x] Task 6: exercise ViewModel (dbe38c6)
- [x] Task 7: exercise screen + ink canvas (870a15d)
- [x] Task 8: correction controls + next exercise (4993268)
- [x] Task 9: README build/dev setup (this commit)

## Review findings (ponytail-review per task commit, disposition)

- Task 1: no must-fix. Not checked: dependency versions resolve on Maven and the Gradle build passes; both run on the dev machine (`./gradlew :app:assembleDebug`, `./gradlew testDebugUnitTest`).
- Task 2: ship. Plain data model, tests pin order, roles, bounds.
- Task 3: ship. `clearLayer` removes only the addressed author (Review Focus 3 pinned); `clipToBounds` pinned (Review Focus 1).
- Task 4: ship. Subtraction never negative and multiplication capped at maxResult are pinned by tests (Review Focus 4).
- Task 5: ship. N+1 result fields and bounds pinned (Review Focus 2); layout deterministic, ids unique.
- Task 6: ship. Reopen-appends-to-same-layer pinned (Review Focus 5); next exercise clamps at the last index.
- Task 7: ship with note. Placeholder LaunchTest was replaced by ExerciseScreenTest because MainActivity now hosts the exercise screen. Compose gesture code is unverifiable in the sandbox; covered by `connectedDebugAndroidTest` on the dev machine.
- Task 8: ship. Clear-layer acts on the selected cell while the dialog is open; tested.
- Task 9: docs only.

## Deferred to final whole-branch review

- Minor: none recorded beyond the sandbox verification gap above.
- Final review still to run on the whole branch diff once the dev machine build is green.

## Final whole-branch review (PR #6, ponytail-review over merge-base main..HEAD)

Must fix (both fixed immediately in 99b4b44):

1. Cell ids repeated across exercises (`result-0` etc.), so `InMemoryInkStore` keyed by `CellId` bled strokes from one exercise into the next on `onNextExercise`. Fix: ViewModel prefixes ids with the exercise index (`ex<index>-...`); test `cell ids are unique per exercise` pins it.
2. The exercise itself was never rendered: no operand digits, no operator, no result line, so the child could not see the task. Fix: `Cell.label` carries each operand digit, state carries `operator`, the screen draws labels, operator, and the line; test pins digit labels.

Should fix (fixed in 99b4b44):

3. Manifest had no `allowBackup=false`; set it, matching the no-child-data-leaves-device anti-goal.
4. Generator edge: `maxResult / first` could be 0 with non-default config, making `nextInt(1, 1)` throw; clamped with `maxOf(1, ...)`.

Nice to have / deferred:

- Dependency versions in `gradle/libs.versions.toml` (activity 1.12.0, core-ktx 1.17.0, lifecycle 2.10.0, junit 5.13.4) are unverified in the sandbox; first dev-machine build resolves them.

Not checked:

- All Gradle builds and test runs (no JDK/Android SDK in the sandbox). Run on the dev machine: `./gradlew :app:assembleDebug`, `./gradlew testDebugUnitTest`, `./gradlew :app:connectedDebugAndroidTest`.

Scoped re-review of fix range 99b4b44: clean, no new findings. Verdict: ship after the dev-machine build is green.

## UI fix + docs alignment (after user's version bump 0965a3d)

- Ruling: the grid Canvas in `CellGrid` drew strokes and the result line with the dp based scale in pixel coordinates. Fixed by computing a pixel based scale from the canvas size inside the draw scope. This explains both reported bugs: line through the second row and ink small in the top left corner of a cell.
- Docs aligned with the actual code: plan tech stack and constraints now pin Gradle 9.6, AGP 9.5.2, Kotlin 2.4.21, Compose BOM 2026.01.01, compileSdk/targetSdk 37, legacy-kapt; ADR 0003 records the pinned versions; plan Task 7 documents the dp-vs-px rendering rule.
- Not checked: on-device rendering (no emulator in the sandbox); verify visually on the tablet.

## Original digit as background in the enlarged cell

- Feature: tapping a cell with a label (operand digit) shows the digit as a light gray background in the enlarged dialog, behind the ink layers, so the child can write over or next to it. UI test asserts the digit appears in grid and dialog.
- Docs: plan Task 7 and ARCHITECTURE.md data flow updated.
- Not checked: on-device rendering (no emulator in the sandbox); verify visually.

## First drop: only addition and subtraction

- Ruling: the generator now takes `operations` (default ADDITION, SUBTRACTION only); MULTIPLICATION stays in the enum and the layout/generator branches for a later drop. Tests pin that only + and - are generated by default, and that multiplication stays within maxResult when enabled explicitly.
- Docs: spec goals/feature set and plan goal/interface updated.
