# Architecture

absies is an Android app that lets small school children learn and exercise with a pen. Children write their answers by hand on the screen. The first subject is math.

## Overview

The app shows an exercise, captures the child's handwriting as pen strokes, recognizes the written input, and checks the answer. Subjects are pluggable so more subjects can follow math.

## Planned layout

- `app/` holds the Android application module.
- The exercise flow: exercise screen, pen input view, stroke capture, handwriting recognition, answer checking.
- Subject modules provide exercises and check answers; math is the first subject module.

## Data flow

1. The exercise screen presents a task.
2. The child writes with the pen on the input view.
3. The view captures the strokes.
4. Recognition turns the strokes into a candidate answer.
5. The checker compares the candidate answer with the expected answer and gives feedback.

## Decisions

Lasting architectural decisions, data flows, and invariants are recorded as ADRs in [docs/adr/](docs/adr/). Start new records from [docs/adr/0000-template.md](docs/adr/0000-template.md).

## Quality

Checker and recognition logic get unit tests; the exercise flow gets UI tests. Run the test suite before finishing a branch, as described in [AGENTS.md](AGENTS.md).
