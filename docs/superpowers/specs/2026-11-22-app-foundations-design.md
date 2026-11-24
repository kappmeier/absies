# 2026-11-22 app foundations design

## Intent

absies is an Android app where small school children solve exercises by writing with a pen on the screen. The first subject is math. This document records the foundations agreed in brainstorming: stack, architecture, development setup, the first feature set, and the first steps toward a minimal app. It is the input for the implementation plan.

## Goals

- A child can open an exercise, write an answer digit by digit with a pen, and the answer stays visible in the exercise.
- A second person can correct the answer in a different pen color without overwriting the child's strokes.
- The exercise layout is generic: 2, 3, 4, or more digits per operand all render the same way.
- The first step ships addition, subtraction, and multiplication as column exercises.

## Non-goals for the first step

- Handwriting recognition and automatic checking. A person evaluates the answer for now.
- Persistence, accounts, synchronization, backend.
- Subjects other than math, division, word problems, speech.

## Anti-goals

- Sending handwriting or any child data to a server. Later recognition must run on-device.
- Losing the child's original strokes when a correction happens.

## Constraints

- Android tablet with stylus or finger input, minSdk 24.
- Code in English. UI strings are externalized for multiple languages; the first shipped language is German, written for children around 6 to 8 years old (see docs/adr/0003).
- Fully offline in the first step.
- Unit tests for checker and recognition logic, UI tests for the exercise flow (see docs/coding-standards.md).

## Approaches considered

### Framework

Native Android with Kotlin and Jetpack Compose was chosen over Flutter and over a web app. The project is an Android app, so there is no cross-platform requirement. Native gives the smallest APK and direct access to pointer events, which the pen input needs. Flutter would add a runtime for no benefit here, and a web app would make pen latency and offline use worse.

### Handwriting recognition

Three options were considered: ML Kit Digital Ink Recognition (on-device, free, the same engine as Gboard), a custom TensorFlow Lite model, and no recognition in the first step. The first step ships without recognition. The app only records strokes, and a person checks the answer. Recognition is added later behind a `Recognizer` interface in `:core:ink`, with ML Kit Digital Ink as the leading candidate because it runs on-device, which the anti-goals require.

### Module layout

A multi-module Gradle build was chosen over a single module. Modules: `:app` (navigation, DI), `:core:ink` (stroke model, pen canvas, recognizer port), `:core:exercise` (exercise flow and cell grid), `:feature:math` (exercise generator and layout for column arithmetic). This keeps pen logic and math logic unit-testable and leaves room for more subjects.

## Exercise workflow (as decided)

One exercise is shown at a time, rendered as a grid of cells:

```text
  347
+ 237
  ~~
-----
□ □ □ □
```

- Operand digits are display cells, but a child can annotate them, for example crossing out a digit as a helper.
- The row between the operands and the line is a helper row for carries and other notes.
- Below the line there is one input field per result digit plus one extra field for a final carry. The field count follows from the operands, so 2, 3, or 4 digit operands all work the same.
- Tapping a cell opens that cell enlarged as an overlay canvas. The child writes, taps "Fertig", and the cell closes with the strokes still visible.
- Pen color selects the layer: blue for the child, red for the correcting person. Corrections go on their own layer. Deleting a layer is the overwrite case.
- "Nächste Aufgabe" generates the next random exercise.

## Data model

- `Exercise`: operation (`+`, `-`, `×`) and operands. Renders to cells.
- `Worksheet`: an ordered list of exercises.
- `Collection`: a set of worksheets.
- `Cell`: id, role (`operand digit`, `helper`, `result field`), bounds.
- `InkLayer`: author color plus a list of strokes, stored per cell.
- `Stroke`: list of points `(x, y, timestamp)`.

The initial version generates worksheets in the app; loading worksheets and collections from files comes later (see docs/adr/0004). Recognition later reads only the result field cells. Helper and operand annotations stay free ink.

## First feature set (minimal app)

1. Gradle project scaffold: version catalog, `:app` with Compose and a theme.
2. Exercise generator for addition, subtraction, and multiplication with configurable digit count and range, producing a worksheet of exercises.
3. Grid rendering of one exercise: operands, helper row, line, result fields.
4. Tap-to-enlarge cell overlay with pen input (`InkCanvas`: pointer events to strokes).
5. Ink layers per cell with pen color switch and layer deletion.
6. "Nächste Aufgabe" button; state held in memory.
7. Unit tests for the generator and the cell layout, plus a Compose UI test for tap, write, close.

## Development environment

- Android Studio (current stable), JDK 17.
- Gradle with Kotlin DSL and a version catalog, current stable AGP, Compose BOM, Hilt for DI.
- minSdk 24, targetSdk current.
- Tests: JUnit for logic, Compose UI tests for the flow.
- Pen input must be verified on a real tablet with a stylus; the emulator only covers the flow.

## Left to the builder

- Application id and package name: prefix `de.kappmeier`, so `de.kappmeier.absies`.
- Exact module names and package structure inside the modules above.
- Overlay animation details and the exact "Fertig" interaction.
- Default digit ranges per operation for the generator.
