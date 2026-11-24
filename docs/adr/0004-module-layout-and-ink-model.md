# 0004. Module layout and pen-first data model

- Status: proposed
- Date: 2026-11-22

## Context

ARCHITECTURE.md sketched an app module plus subject modules and asked to confirm the layout through
brainstorming. The app captures pen strokes from children; handwriting recognition and automatic
checking come later.

## Decision

Multi-module Gradle build: `:app` (navigation, DI), `:core:ink` (stroke model, pen canvas,
recognizer port), `:core:exercise` (exercise flow and cell grid), `:feature:math` (exercise
generator and layout for column arithmetic). More subjects add further `:feature:*` modules.

Exercises are layered: a single exercise, a worksheet (an ordered list of exercises), and a
collection (a set of worksheets). The initial version generates worksheets in the app with the
`:feature:math` generator. Later, worksheets and collections are loaded from files. The exercise
flow works against worksheets, so switching from generated to file-based exercises does not change
the UI.

An exercise renders as a grid of cells with a role: operand digit, helper row, or result field. Ink
is stored per cell as layers, one layer per author color (blue child, red correcting person), each
layer holding a list of strokes with points `(x, y, timestamp)`. The first step ships without
handwriting recognition; a person evaluates the answer. Recognition is added later behind a
`Recognizer` port in `:core:ink`, with ML Kit Digital Ink on-device as the leading candidate.

## Consequences

Pen capture, cell layout, and math generation are unit-testable without Android UI. Subject modules
stay pluggable. No child data leaves the device, and the recognizer can be swapped later without
touching the exercise flow. The cost is Gradle multi-module overhead, which stays small at four
modules.
