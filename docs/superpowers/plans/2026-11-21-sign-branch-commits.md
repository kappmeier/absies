# Sign branch commits implementation plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** A manually triggered GitHub Action that signs the unsigned commits of a feature branch before merge, using the `SSH_SIGNING_KEY` repository secret.

**Architecture:** One `workflow_dispatch` workflow (`.github/workflows/sign-branch-commits.yml`) with a required `branch` input. It checks out the branch with full history, configures SSH commit signing from the secret, rebases `main..<branch>` and amends only commits without a `gpgsig` header, verifies that every commit in the range is signed, then pushes with `--force-with-lease`. AGENTS.md, the repository map, and the coding standard get short updates.

**Tech Stack:** GitHub Actions (YAML), git SSH signing (`gpg.format=ssh`), bash.

**Spec:** Session request (message f115702c): use `writing-plans`, then implement a GitHub Action that signs unsigned commits before merge with a secret `SSH_SIGNING_KEY`; local commits are already signed; the action must not run automatically, only on request for a branch before merge.

## Global constraints

- Branch naming per AGENTS.md; build from `main`; no direct pushes to `main`.
- The workflow runs on `workflow_dispatch` only, with a required `branch` input, and refuses to run on `main`.
- Never print or log the secret. Write it to a file with mode 600 and delete it in an `if: always()` step. GitHub masks registered secrets in logs.
- Only commits without a `gpgsig` header are amended; already signed commits keep their content and hash.
- Push with `--force-with-lease`, never a plain force push.
- Pass the branch input to `run` steps via `env`, not via `${{ }}` interpolation in the script.
- Plan lifecycle per AGENTS.md: plan and ledger are committed on this branch, graduated to `docs/adr/` and deleted before the PR.
- Commit trailer: `Co-authored-by: mistral-vibe <220374863+mistral-vibe@users.noreply.github.com>`.
- Humanizer pass on all new human-facing text before committing.
- The secret itself is set by the repo owner with `gh secret set SSH_SIGNING_KEY` from their own machine; the sandbox token cannot manage secrets.

## Review focus

- Commits upstream of the first unsigned commit keep their hash and signature; the first unsigned commit and its descendants are rewritten and re-signed, because their parent hashes change. Verified locally with a throwaway GPG key. The run must handle an empty range without failing.
- The verification step must fail the run when any commit in `main..branch` lacks a signature.
- The key file must be removed even when an earlier step fails.
- YAML parses, and `run` blocks contain no `${{ }}` interpolation of user input.

---

### Task 1: branch, plan, and ledger

**Files:**
- Create: `docs/superpowers/plans/2026-11-21-sign-branch-commits.md`
- Create: `.superpowers/ledger.md`

- [ ] Write plan and ledger
- [ ] Commit on `feature/sign-branch-commits`
- [ ] Verify: `git status` clean, both files tracked

### Task 2: the signing workflow

**Files:**
- Create: `.github/workflows/sign-branch-commits.yml`

- [ ] Write the workflow: refuse `main`, checkout branch with `fetch-depth: 0`, fetch `origin/main`, write secret key to a 600 file, configure `gpg.format=ssh` / `user.signingkey` / `allowedSignersFile`, rebase `--exec` amending only unsigned commits, verify all commits in `main..branch` carry `gpgsig`, push `--force-with-lease`, always remove the key file
- [ ] Verify: `python3 -c "import yaml; yaml.safe_load(...)"` parses
- [ ] Commit

### Task 3: documentation

**Files:**
- Modify: `AGENTS.md` (new "Commit signing" section)
- Modify: `docs/repository-map.md` (new workflow row)
- Modify: `docs/coding-standards.md` (one line in Commits)

- [ ] Verify: internal links still resolve
- [ ] Commit

### Task 4: local proof of the signing logic

- [ ] In a scratch repo under `/tmp`: `main` plus a feature branch with two unsigned commits and one commit signed with a throwaway key
- [ ] Run the same `git rebase --exec` command the workflow uses
- [ ] Assert: every commit in `main..feature` has a `gpgsig` header and `git verify-commit` passes with an allowed signers file; the pre-signed commit was not amended
- [ ] Record the result in the ledger

### Task 5: graduation

**Files:**
- Create: `docs/adr/0002-sign-unsigned-branch-commits.md`
- Delete: everything under `docs/superpowers/` and `.superpowers/`

- [ ] Write ADR 0002 recording the decision
- [ ] Delete the transient artifacts
- [ ] Verify: `git status --porcelain` lists no `docs/superpowers/` or `.superpowers/` paths
- [ ] Commit

### Task 6: humanizer pass, push, draft PR

- [ ] Humanizer check on all new and changed human-facing text
- [ ] Commit if anything changed
- [ ] Push `feature/sign-branch-commits`
- [ ] Open a draft PR against `main` and report the URL
- [ ] Verify: PR diff contains no `docs/superpowers/` or `.superpowers/` paths
