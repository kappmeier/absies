# Ledger: minimal app implementation

Plan: `docs/superpowers/plans/2026-11-22-minimal-app.md`
Branch: `feature/minimal-app` (base `doc/app-foundations`, PR for spec+ADRs)

## Environment rulings

- Ruling: no subagent dispatch tool exists in this session, so tasks run inline in this session (model mistral-large-4-0) instead of fresh subagents; the session also performs the ponytail-review role after each commit. Cost if wrong: less context isolation per task; mitigated by per-task commits and reviews.
- Ruling: the sandbox has no JDK/Android SDK and no network for SDK downloads, so `./gradlew` verification runs on the dev machine (README, Task 9), not here. Cost if wrong: a compile error reaches the dev machine; mitigated by careful static review after each task.

## Tasks

- [ ] Task 1: scaffold
- [ ] Task 2: core:exercise model
- [ ] Task 3: core:ink model + store + recognizer port
- [ ] Task 4: math generator
- [ ] Task 5: column layout
- [ ] Task 6: exercise ViewModel
- [ ] Task 7: exercise screen + ink canvas
- [ ] Task 8: correction controls
- [ ] Task 9: README build/dev setup

## Review findings

(per-task ponytail-review results and their disposition go here)
