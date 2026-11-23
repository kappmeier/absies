# 0002. Sign branch commits before merge with a manual action

- Status: accepted
- Date: 2026-11-21

## Context

Local commits are SSH-signed, but commits created in the ephemeral sandbox are unsigned because the sandbox holds no authorized private key. Rewriting merged history is not an option, and requiring signed commits in branch protection would block PRs that contain sandbox commits.

## Decision

The manually triggered workflow `.github/workflows/sign-branch-commits.yml` (`workflow_dispatch` with a required `branch` input, refuses `main`) signs commits before merge. It rebases `main..<branch>` and amends every commit without a `gpgsig` header with a fresh SSH signature from the `SSH_SIGNING_KEY` repository secret, verifies that every commit in the range carries a signature, and pushes with `--force-with-lease`. Commits upstream of the first unsigned commit keep their hash and signature; later commits are re-signed because their parent hashes change. The secret is set by the repo owner with `gh secret set SSH_SIGNING_KEY`; only the public key is registered on the GitHub account as a signing key.

## Consequences

Unsigned sandbox commits become verified before they reach `main` without blocking the PR flow. The cost is a manual run before merging such a branch and a force-with-lease push, so reviewers must expect rewritten hashes on that branch.
