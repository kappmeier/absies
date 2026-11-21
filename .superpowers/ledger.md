# Ledger

## Rulings

- Ruling: branch `feature/sign-branch-commits` without an issue number, because the repo still has no issues.
- Ruling: `workflow_dispatch` only, with a required `branch` input, per the request; no automatic triggers.
- Ruling: `git push --force-with-lease` of the feature branch is accepted, because the request explicitly asks for pre-merge signing of branch commits.
- Ruling: `user.signingkey` is set to the private key file written from the secret; `ssh-keygen -Y sign -f` accepts a private key path directly.
- Ruling: the branch input reaches `run` steps only through `env`, never through `${{ }}` interpolation.

## Progress

- 2026-11-21: branch created from origin/main, plan and ledger committed.
- 2026-11-21: signing logic proven in a scratch repo with a throwaway GPG key (sandbox has no ssh-keygen; the runner's ubuntu-latest ships openssh-client). Rebase reuses commits that are already signed and on an unchanged base, so their hash and signature survive; the first unsigned commit and its descendants are rewritten and re-signed, and all commits in `main..branch` verified afterwards. Second run is idempotent.
