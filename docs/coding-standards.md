# Coding standards

## Language

The implementation language is not decided yet, and no ADR covers it. Once a language is chosen, record the decision in `docs/adr/` and follow that language's official conventions. Keep functions small and name them after what they do.

## Formatting and lint

Format with the toolchain's standard formatter before committing. PRs should not contain formatting-only diff noise.

## Tests

Every behavior change comes with a test. Checker and recognition logic get unit tests; the exercise flow gets UI tests.

## Commits

Use conventional commit messages: `<type>: <short summary>`. The types match the branch prefixes: `feature`, `fix`, `doc`, `chore`, `hotfix`, `test`, `release`. Commits are SSH-signed. Unsigned commits, for example from the sandbox, are signed before merge with the Sign branch commits action; see [Agents.md](../AGENTS.md). Commits written with agent assistance carry a co-author trailer so GitHub credits the agent: `Co-Authored-By: Mistral Vibe <vibe@mistral.ai>`.

## Clean code

Follow clean code standards, except where they introduce unreasonable overhead.

## Comments

Code should explain itself. Add a comment only when the reason for a choice is not visible in the code.

## Docs and other text

Human-facing text gets a humanizer pass before it is finalized; see [AGENTS.md](../AGENTS.md).
