---
name: promote-plan-to-architecture
description: Pre-merge wrap-up for a development branch. Promotes the active plan to architecture docs (docs/adr/ or ARCHITECTURE.md), or deletes it, and removes all transient Superpowers artifacts. Call it before the superpowers finishing-a-development-branch skill.
---

# Promote plan to architecture

Call this skill right before a development branch merges to `main`, and always before the superpowers `finishing-a-development-branch` skill.

## Steps

1. Review the completed plan in `docs/superpowers/plans/` against the actual changes on the branch.
2. Promote: extract lasting architectural decisions, data flows, or invariants into `docs/adr/NNNN-<topic>.md`, or update `ARCHITECTURE.md`. When the plan holds nothing worth keeping, delete it without promoting.
3. Delete all files in `docs/superpowers/` and `.superpowers/`.
4. Verify `git status` shows zero remaining Superpowers artifacts.
5. Commit the result, then hand over to `finishing-a-development-branch` for the merge or PR step.

Plan files never reach `main`. The workflow `.github/workflows/guard-no-plan-files.yml` fails any push or pull request to `main` that still contains `docs/superpowers/` or `.superpowers/`.
