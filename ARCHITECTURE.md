# Architecture

absies is an Android app that lets small school children learn and exercise with a pen. Children write their answers by hand on the screen. The first subject is math.

## Overview

The app shows an exercise, captures the child's handwriting as pen strokes, recognizes the written input, and checks the answer. Subjects are pluggable so more subjects can follow math.

## Planned layout

This sketch is not a decision yet. Confirm the module layout through brainstorming and record the outcome in `docs/adr/`.

- `app/` holds the Android application module.
- The exercise flow: exercise screen, pen input view, stroke capture, handwriting recognition, answer checking.
- Subject modules provide exercises and check answers; math is the first subject module.

## Data flow

Not decided yet. Before implementing pen input and answer checking, run the superpowers brainstorming skill, then record the resulting flow here and in `docs/adr/`.

## Decisions

Lasting architectural decisions, data flows, and invariants are recorded as ADRs in [docs/adr/](docs/adr/). Start new records from [docs/adr/0000-template.md](docs/adr/0000-template.md).

## Quality

Checker and recognition logic get unit tests; the exercise flow gets UI tests. Run the test suite before finishing a branch, as described in [AGENTS.md](AGENTS.md).
