# Repository map

| Path | Purpose |
| --- | --- |
| `AGENTS.md` | Rules for agents and contributors: branches, git identity, skills, plan lifecycle |
| `README.md` | Project overview and entry points |
| `ARCHITECTURE.md` | Architecture overview and data flow |
| `docs/coding-standards.md` | Coding standard for the app |
| `docs/repository-map.md` | This map |
| `docs/adr/` | Architecture decision records, starting from `0000-template.md` |
| `docs/superpowers/plans/` | Active plans on feature branches only; never merged to `main` |
| `.superpowers/` | Session ledgers on feature branches only; never merged to `main` |
| `.github/PULL_REQUEST_TEMPLATE.md` | Pull request template |
| `.github/workflows/guard-no-plan-files.yml` | Fails changes to `main` that contain transient plan files |
| `.github/workflows/sign-branch-commits.yml` | Manual action: signs unsigned branch commits with the `SSH_SIGNING_KEY` secret before merge |
| `.agents/skills/promote-plan-to-architecture/` | Pre-merge wrap-up skill: promote the plan to architecture docs, then delete transient files |
| `LICENSE` | Apache License 2.0 |
