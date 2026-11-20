---
name: finishing-a-development-branch
description: Wrap up a development branch right before merging to main. Graduates plan files to evergreen docs and deletes all transient Superpowers artifacts.
---

# Finishing a development branch

Use this skill when implementation on a feature branch is complete and the branch is about to merge to `main`. When this skill runs, the plan cleanup below is mandatory.

## Steps

1. Run the test suite and confirm it passes.
2. Review the completed plan in `docs/superpowers/plans/` against the actual changes on the branch.
3. Graduate: extract lasting architectural decisions, data flows, or invariants into `docs/adr/NNNN-<topic>.md`, or update `ARCHITECTURE.md`. Skip this when the change was purely routine.
4. Delete all files in `docs/superpowers/` and `.superpowers/`.
5. Verify `git status` shows zero remaining Superpowers artifacts.
6. Commit the cleanup, then merge or open the PR.

Plan files never reach `main`. The workflow `.github/workflows/guard-no-plan-files.yml` fails any push or pull request to `main` that still contains `docs/superpowers/` or `.superpowers/`.
