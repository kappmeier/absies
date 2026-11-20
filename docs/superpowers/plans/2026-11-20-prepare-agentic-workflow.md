# Prepare agentic workflow implementation plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Set up the absies repository for agentic software engineering: contributor guidance, project docs, CI guardrails, and the plan-file lifecycle.

**Architecture:** Docs and config only, no app code. Root guidance files, `docs/` for standards and ADRs, `.github/` for the PR template, the guard workflow, and the `finishing-a-development-branch` skill. Active plans live in `docs/superpowers/plans/` on feature branches only and never reach `main`.

**Tech Stack:** Markdown, YAML (GitHub Actions).

**Spec:** Session request "Prepare the repository for software engineering within an agentic flow" (messages 16adb73e and 2364b38c).

## Global constraints

- Branch naming: `<prefix>/issue-<number>/<short-text>` when an issue exists, otherwise `<prefix>/<short-text>`. Allowed prefixes: `feature`, `fix`, `doc`, `chore`, `hotfix`, `test`, `release`. The build comes from `main`; no direct pushes to `main`.
- Before the first commit of a session, set the local git identity: `git config user.name "Jan-Philipp Kappmeier"` and `git config user.email "4357226+kappmeier@users.noreply.github.com"`.
- Plan lifecycle: store active plans in `docs/superpowers/plans/` and ledgers in `.superpowers/`, committed on the feature branch. Before merge, graduate lasting decisions into `docs/adr/NNNN-<topic>.md` and delete all files in `docs/superpowers/` and `.superpowers/`. `git status` must show zero Superpowers artifacts before the final PR.
- A GitHub Action active on pushes to `main` and on pull requests to `main` fails any change that still contains transient plan files.
- Skills in use: humanizer, ponytail, superpowers (URLs recorded in AGENTS.md). Never commit `.vibe/skills/`.
- Run the humanizer pass on all human-facing text (docs, commit messages, PR description) before finalizing.

## Review focus

- The guard workflow must trigger on push to `main` and on PRs to `main`, and it must detect both `docs/superpowers/` and `.superpowers/`.
- AGENTS.md must state that using the `finishing-a-development-branch` skill applies the cleanup that deletes all plans.
- The PR head tree must contain no transient plan files.

---

### Task 1: Plan and ledger on the branch

**Files:**
- Create: `docs/superpowers/plans/2026-11-20-prepare-agentic-workflow.md`
- Create: `.superpowers/ledger.md`

- [ ] Write plan and ledger
- [ ] Commit both files on `chore/prepare-agentic-workflow`
- [ ] Verify: `git status` clean, `git ls-files` lists both files

### Task 2: Root docs

**Files:**
- Create: `AGENTS.md`
- Modify: `README.md`
- Create: `ARCHITECTURE.md`

- [ ] Write AGENTS.md with branch rules, git identity, skills, feature workflow, plan-file lifecycle, and the finishing-a-development-branch note
- [ ] Expand README.md with project description and links to AGENTS.md, ARCHITECTURE.md, and docs
- [ ] Write ARCHITECTURE.md describing the app architecture and linking `docs/adr/`
- [ ] Verify: files exist, internal links resolve to existing paths
- [ ] Commit

### Task 3: Docs standards and ADR template

**Files:**
- Create: `docs/coding-standards.md`
- Create: `docs/repository-map.md`
- Create: `docs/adr/0000-template.md`

- [ ] Write coding standards (Kotlin conventions, tests, commit style)
- [ ] Write repository map covering root files, `docs/`, `.github/`
- [ ] Write ADR template (context, decision, consequences)
- [ ] Verify: files exist
- [ ] Commit

### Task 4: GitHub files

**Files:**
- Create: `.github/PULL_REQUEST_TEMPLATE.md`
- Create: `.github/workflows/guard-no-plan-files.yml`
- Create: `.github/skills/finishing-a-development-branch/SKILL.md`

- [ ] Write a simple PR template (summary, verification, checklist)
- [ ] Write the guard workflow: `on: push` to `main` and `pull_request` to `main`, checkout head sha, fail when `git ls-tree` shows `docs/superpowers/` or `.superpowers/`
- [ ] Write the skill capturing the pre-merge wrap-up: verify, graduate plans to ADRs, delete Superpowers artifacts, confirm clean `git status`
- [ ] Verify: YAML parses (`python3 -c "import yaml,sys; yaml.safe_load(open(...))"`), skill frontmatter has name and description
- [ ] Commit

### Task 5: Wrap-up and graduation

**Files:**
- Create: `docs/adr/0001-agentic-workflow.md`
- Delete: everything under `docs/superpowers/` and `.superpowers/`

- [ ] Review plan against actual changes
- [ ] Write ADR 0001 recording the plan-file lifecycle decision
- [ ] Delete `docs/superpowers/` and `.superpowers/`
- [ ] Verify: `git status --porcelain` shows no `docs/superpowers/` or `.superpowers/` paths
- [ ] Commit

### Task 6: Humanizer pass, push, draft PR

- [ ] Run humanizer over all human-facing text (docs, skill, template, commit messages, PR body) and apply rewrites
- [ ] Final commit if rewrites changed files
- [ ] Push `chore/prepare-agentic-workflow` to origin
- [ ] Open a draft PR against `main` and report the URL
- [ ] Verify: PR diff contains no `docs/superpowers/` or `.superpowers/` paths
