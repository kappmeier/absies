# 0001. Agentic workflow with transient plan files

- Status: accepted
- Date: 2026-11-20

## Context

Work on this repository is done by agents on feature branches, and the superpowers workflow produces plan files and ledgers while a branch is active. Those files help reviewers and parallel worktrees during development, but they are not useful after merge and would go stale on `main`.

## Decision

Plans live in `docs/superpowers/plans/` and ledgers in `.superpowers/`, committed on the feature branch only. Before a branch merges to `main`, the `finishing-a-development-branch` skill runs: lasting decisions graduate to `docs/adr/` or `ARCHITECTURE.md`, and all Superpowers artifacts are deleted. The workflow `.github/workflows/guard-no-plan-files.yml` fails any push or pull request to `main` that still contains these directories.

## Consequences

Reviewers can read the plan on the branch, and `main` stays free of stale transient files. The cost is a mandatory cleanup step at the end of every branch, enforced by CI.
