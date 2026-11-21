# Ledger

## Rulings

- Ruling: branch `feature/sign-branch-commits` without an issue number, because the repo still has no issues.
- Ruling: `workflow_dispatch` only, with a required `branch` input, per the request; no automatic triggers.
- Ruling: `git push --force-with-lease` of the feature branch is accepted, because the request explicitly asks for pre-merge signing of branch commits.
- Ruling: `user.signingkey` is set to the private key file written from the secret; `ssh-keygen -Y sign -f` accepts a private key path directly.
- Ruling: the branch input reaches `run` steps only through `env`, never through `${{ }}` interpolation.

## Progress

- 2026-11-21: branch created from origin/main, plan and ledger committed.
