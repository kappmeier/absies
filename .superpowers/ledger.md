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
