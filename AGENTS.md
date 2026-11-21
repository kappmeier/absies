# AGENTS.md

Rules for agents and contributors working on absies.

absies is an Android learning app for small school children. Children answer with a pen and write by hand. The first subject to implement is math.

## Branch organization

The build is done from the `main` branch. No direct pushes to `main`.

Branch names use one of these prefixes: `feature`, `fix`, `doc`, `chore`, `hotfix`, `test`, `release`. When an issue exists the format is `<prefix>/issue-<number>/<short-text>`, otherwise `<prefix>/<short-text>`.

## Git identity

Before the first commit of a session, set the local git identity:

```
git config user.name "Jan-Philipp Kappmeier"
git config user.email "jp.kappmeier@gmail.com"
```

## Skills

The following skills are often used:

- humanizer: https://github.com/blader/humanizer
- ponytail (all skills): https://github.com/DietrichGebert/ponytail/tree/main
- superpowers (all skills): https://github.com/obra/superpowers/

ponytail is always loaded. Every piece of human-facing text (docs, commit messages, PR and issue text) gets a humanizer pass before it is finalized. Skills are installed into `.vibe/skills/` per session; never commit that directory.

## Feature implementation

Features are implemented with the superpowers workflow: brainstorming, writing-plans, executing-plans, and test-driven-development. Work starts from a plan, and each step is verified before moving on.

## Plan files

Work is organized with a "commit plans on feature branches, graduate to evergreen docs, and delete before merge" lifecycle. Store active plans in `docs/superpowers/plans/` and ledgers in `.superpowers/`. Commit plans to the feature branch so worktrees and reviewers can access them.

Pre-merge graduation (required before finishing a branch): review the completed plan in `docs/superpowers/plans/` against the actual code changes. Extract any lasting architectural decisions, data flows, or invariants into `docs/adr/NNNN-<topic>.md` (or update `ARCHITECTURE.md`). Skip this when the change was purely routine. Delete all files in `docs/superpowers/` and `.superpowers/`. Verify `git status` shows zero remaining Superpowers artifacts before opening or merging the final PR.

Plan files must not reach `main`. The workflow `.github/workflows/guard-no-plan-files.yml` fails pushes to `main` and pull requests to `main` that still contain `docs/superpowers/` or `.superpowers/`.

Graduation is captured in the separately callable skill `promote-plan-to-architecture` at `.agents/skills/promote-plan-to-architecture/SKILL.md`. Whenever the superpowers `finishing-a-development-branch` skill (https://github.com/obra/superpowers/blob/main/skills/finishing-a-development-branch/SKILL.md) is called, call `promote-plan-to-architecture` prior to it: the plan is graduated to an architecture doc (`docs/adr/` or `ARCHITECTURE.md`), or deleted, before the branch is finished.

## Where to look

- [ARCHITECTURE.md](ARCHITECTURE.md) for the architecture overview
- [docs/coding-standards.md](docs/coding-standards.md) for the coding standard
- [docs/repository-map.md](docs/repository-map.md) for a map of the repository
- [docs/adr/](docs/adr/) for decision records
