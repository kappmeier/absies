# Architecture

absies is an Android app that lets small school children learn and exercise with a pen. Children write their answers by hand on the screen. The first subject is math.

## Overview

The app shows an exercise, captures the child's handwriting as pen strokes, recognizes the written input, and checks the answer. Subjects are pluggable so more subjects can follow math.

## Module layout

Confirmed through brainstorming, recorded in [docs/adr/0004](docs/adr/0004-module-layout-and-ink-model.md):

- `:app/` holds the Android application module: navigation and dependency injection.
- `:core:ink/` holds the stroke model, the pen canvas, and a `Recognizer` port for later handwriting recognition.
- `:core:exercise/` holds the exercise flow and the cell grid.
- `:feature:math/` provides exercise generation and the column arithmetic layout; math is the first subject module.

The toolchain (Kotlin, Jetpack Compose, Gradle, Hilt) is recorded in [docs/adr/0003](docs/adr/0003-native-android-toolchain.md).

## Data flow

One exercise renders as a grid of cells (operand digits, helper row, result fields below the line). Tapping a cell opens it enlarged; pen strokes are captured as an `Ink` object and stored per cell as colored layers, one per author (blue child, red correcting person), so corrections never overwrite the child's strokes. The first step ships without recognition: a person evaluates the answer. Recognition runs on-device later, behind the `Recognizer` port in `:core:ink/`, reading only the result field cells.

## Decisions

Lasting architectural decisions, data flows, and invariants are recorded as ADRs in [docs/adr/](docs/adr/). Start new records from [docs/adr/0000-template.md](docs/adr/0000-template.md).

## Quality

Checker and recognition logic get unit tests; the exercise flow gets UI tests. Run the test suite before finishing a branch, as described in [AGENTS.md](AGENTS.md).
