# Minimal app implementation plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship a minimal absies app where a child solves generated column arithmetic exercises (addition, subtraction, multiplication, any digit count) by writing digits with a pen into cells that enlarge on tap, and a second person can correct in a different pen color on its own layer. No handwriting recognition yet.

**Architecture:** Multi-module Gradle build per ADR 0004. `:app` holds Compose UI, Hilt, and the exercise ViewModel. `:core:exercise` holds the exercise/worksheet/collection and cell model. `:core:ink` holds the stroke model, the ink store, and the recognizer port. `:feature:math` holds the exercise generator and the column layout. Unidirectional flow: the ViewModel holds state, the UI renders it, pen events become strokes in the ink store.

**Tech Stack:** Kotlin, Jetpack Compose (BOM), Gradle Kotlin DSL with version catalog, Hilt, minSdk 24, JDK 17, JUnit 5 for unit tests, Compose UI test for the flow. Current stable AGP, Kotlin, and Gradle versions pinned in `gradle/libs.versions.toml`.

**Spec:** `docs/superpowers/specs/2026-11-22-app-foundations-design.md` (decisions in `docs/adr/0003-native-android-toolchain.md` and `docs/adr/0004-module-layout-and-ink-model.md`)

**Execution:** One implementer agent per task, each writing the failing test first (superpowers:test-driven-development), then the minimal code, then a commit. After every task commit, a fresh reviewer runs /ponytail-review on that task's diff with Mistral Large 4 (`mistral-large-4-0`). Must-fix findings (bugs, security, data loss) are fixed immediately in a follow-up commit on the same branch. Findings that a later task already covers are parked in the ledger. Minor findings are deferred to the final whole-branch review. Progress, rulings, and parked findings go to `.superpowers/ledger.md` on the feature branch. In a session without a subagent dispatch tool (this one), the tasks are executed inline by the session model, which also performs the reviewer role after each commit.

## Agents and models

| Task | Agent role | Model |
| --- | --- | --- |
| 1 scaffold, 9 README | build/docs agent | mistral-small-latest |
| 2-5 core model, ink, generator, layout | core developer (also writes the tests first, per TDD) | mistral-large-4-0 |
| 6-8 ViewModel and UI | UI developer (also writes the tests first, per TDD) | mistral-large-4-0 |
| after every task commit | reviewer, /ponytail-review on the task diff, fresh context | mistral-large-4-0 (Mistral Large 4) |
| after all tasks | final whole-branch review | mistral-large-4-0 |

## Global Constraints

- Application id and package: `de.kappmeier.absies`; module namespaces `de.kappmeier.absies`, `de.kappmeier.absies.core.exercise`, `de.kappmeier.absies.core.ink`, `de.kappmeier.absies.feature.math`.
- minSdk 24, targetSdk and compileSdk current stable, JDK 17.
- Modules: `:app`, `:core:exercise`, `:core:ink`, `:feature:math`. Dependencies: `core:ink` and `feature:math` depend on `core:exercise`; `:app` depends on all three.
- Code and comments in English. UI strings externalized: `app/src/main/res/values/strings.xml` (English fallback) and `app/src/main/res/values-de/strings.xml` (German, the first shipped language).
- No network, no backend, no persistence in this plan; all state in memory.
- No handwriting recognition in this plan; only the `Recognizer` port interface is added.
- Conventional commits (`<type>: <summary>`) with trailer `Co-Authored-By: Mistral Vibe <vibe@mistral.ai>`.
- Every behavior change comes with a test: unit tests for logic, Compose UI tests for the exercise flow (docs/coding-standards.md).
- Format Kotlin with the IDE default official style before committing; no formatting-only diff noise.
- This plan file lives on the feature branch only and never reaches `main` (guard workflow `.github/workflows/guard-no-plan-files.yml`).
- The agent sandbox has no JDK and no Android SDK, so `./gradlew` verification cannot run there. Run `./gradlew :app:assembleDebug`, `./gradlew testDebugUnitTest`, and `./gradlew :app:connectedDebugAndroidTest` on a dev machine (setup in README.md, Task 9) or in CI. Agents verify statically and via the per-task ponytail review.

## Review Focus

1. Strokes drawn outside the canvas are clipped, not recorded with out-of-bounds coordinates. Pinned by `clipToBounds` unit tests in Task 3 and the draw interaction in the Task 7 UI test.
2. Result field count follows the operands generically: N-digit operands produce N+1 result fields, so 999 + 1 gets 4 fields. Pinned in `ColumnLayoutTest` (Task 5).
3. Correction never destroys the child's strokes: `clearLayer` removes only the addressed author's layer. Pinned in `InkStoreTest` (Task 3).
4. Subtraction never generates negative results; the generator keeps minuend >= subtrahend. Pinned in `MathExerciseGeneratorTest` (Task 4).
5. Reopening a cell keeps its layers and appends new strokes instead of duplicating state. Pinned in `ExerciseViewModelTest` (Task 6).

---

### Task 1: Gradle multi-module scaffold

**Files:**
- Create: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, `core/exercise/build.gradle.kts`, `core/ink/build.gradle.kts`, `feature/math/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/src/main/java/de/kappmeier/absies/AbsiesApplication.kt`, `app/src/main/java/de/kappmeier/absies/MainActivity.kt`
- Test: `app/src/androidTest/java/de/kappmeier/absies/LaunchTest.kt`

**Interfaces:**
- Produces: module names `:app`, `:core:exercise`, `:core:ink`, `:feature:math`; applicationId `de.kappmeier.absies`; version catalog with entries for AGP, Kotlin, Gradle plugin, Compose BOM, Hilt, JUnit 5, Compose UI test; `AbsiesApplication` annotated `@HiltAndroidApp`; `MainActivity` with a launcher intent filter showing a placeholder text.

- [ ] **Step 1: Write the build files**

`settings.gradle.kts` includes the four modules and configures plugin management. `gradle/libs.versions.toml` pins current stable AGP, Kotlin, Compose BOM, Hilt, and test libraries. Root `build.gradle.kts` applies plugins with `apply false`. `app/build.gradle.kts` uses `com.android.application`, Compose, and Hilt; the three library modules use `com.android.library`. `gradle.properties` sets `android.useAndroidX=true` and `kotlin.code.style=official`. Generate the wrapper with `gradle wrapper --gradle-version <current stable>`.

- [ ] **Step 2: Write the placeholder app**

`AbsiesApplication` and `MainActivity` with `setContent { Text("absies") }`, manifest with `android:name=".AbsiesApplication"` and the launcher activity. `LaunchTest` asserts the placeholder text is displayed.

- [ ] **Step 3: Verify the build**

Run: `./gradlew :app:assembleDebug`
Expected: `BUILD SUCCESSFUL`.
Run: `./gradlew testDebugUnitTest`
Expected: `BUILD SUCCESSFUL` (no unit tests yet).

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "chore: scaffold multi-module Gradle project with Compose and Hilt"
```

### Task 2: Exercise, worksheet, collection, and cell model in `:core:exercise`

**Files:**
- Create: `core/exercise/src/main/java/de/kappmeier/absies/core/exercise/model/Exercise.kt`
- Test: `core/exercise/src/test/java/de/kappmeier/absies/core/exercise/model/ModelTest.kt`

**Interfaces:**
- Produces:

```kotlin
interface Exercise
data class Worksheet(val exercises: List<Exercise>)
data class Collection(val worksheets: List<Worksheet>)
enum class CellRole { OPERAND_DIGIT, HELPER, RESULT_FIELD }
data class CellId(val value: String)
data class Cell(val id: CellId, val role: CellRole, val left: Float, val top: Float, val right: Float, val bottom: Float)
```

- [ ] **Step 1: Write the failing test**

`ModelTest` asserts: a `Worksheet` preserves exercise order; a `Collection` holds its worksheets; `CellRole` has exactly `OPERAND_DIGIT`, `HELPER`, `RESULT_FIELD`; a `Cell` keeps its bounds.

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :core:exercise:testDebugUnitTest`
Expected: FAIL with unresolved references.

- [ ] **Step 3: Implement the model in `Exercise.kt`**

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :core:exercise:testDebugUnitTest`
Expected: PASS, `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add core/exercise
git commit -m "feat: add exercise, worksheet, collection, and cell model"
```

### Task 3: Ink model, ink store, and recognizer port in `:core:ink`

**Files:**
- Create: `core/ink/src/main/java/de/kappmeier/absies/core/ink/Ink.kt`, `core/ink/src/main/java/de/kappmeier/absies/core/ink/InkStore.kt`, `core/ink/src/main/java/de/kappmeier/absies/core/ink/Recognizer.kt`
- Modify: `core/ink/build.gradle.kts` (add `api(project(":core:exercise"))`)
- Test: `core/ink/src/test/java/de/kappmeier/absies/core/ink/InkStoreTest.kt`

**Interfaces:**
- Consumes: `CellId` from Task 2.
- Produces:

```kotlin
data class Point(val x: Float, val y: Float, val t: Long)
data class Stroke(val points: List<Point>)
enum class PenAuthor { CHILD, CORRECTOR }
data class InkLayer(val author: PenAuthor, val strokes: List<Stroke>)

interface InkStore {
    fun layers(cellId: CellId): List<InkLayer>
    fun addStroke(cellId: CellId, author: PenAuthor, stroke: Stroke)
    fun clearLayer(cellId: CellId, author: PenAuthor)
}
class InMemoryInkStore : InkStore

interface Recognizer {
    fun recognize(strokes: List<Stroke>): List<String>
}

fun clipToBounds(point: Point, width: Float, height: Float): Point?
```

`clipToBounds` returns the point when it lies inside `0..width` x `0..height`, otherwise null. `InMemoryInkStore.addStroke` appends to the existing layer of that author, creating the layer on first use. `Recognizer` is a port only; no implementation in this plan.

- [ ] **Step 1: Write the failing test**

`InkStoreTest` asserts: `layers` on an unknown cell is empty; `addStroke` creates one layer per author and appends further strokes to that layer; layers of different cells are independent; `clearLayer(cell, CHILD)` removes only the child layer and keeps the corrector layer; `clipToBounds` returns null for (-1, 5) and (5, -1) and the point for (5, 5) with size 10x10.

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :core:ink:testDebugUnitTest`
Expected: FAIL with unresolved references.

- [ ] **Step 3: Implement `Ink.kt`, `InkStore.kt`, `Recognizer.kt`**

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :core:ink:testDebugUnitTest`
Expected: PASS, `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add core/ink
git commit -m "feat: add ink model, in-memory ink store, and recognizer port"
```

### Task 4: Math exercise and generator in `:feature:math`

**Files:**
- Create: `feature/math/src/main/java/de/kappmeier/absies/feature/math/MathExercise.kt`, `feature/math/src/main/java/de/kappmeier/absies/feature/math/MathExerciseGenerator.kt`
- Modify: `feature/math/build.gradle.kts` (add `api(project(":core:exercise"))`)
- Test: `feature/math/src/test/java/de/kappmeier/absies/feature/math/MathExerciseGeneratorTest.kt`

**Interfaces:**
- Consumes: `Exercise`, `Worksheet` from Task 2.
- Produces:

```kotlin
enum class Operation { ADDITION, SUBTRACTION, MULTIPLICATION }

data class MathExercise(val operation: Operation, val operands: List<Int>) : Exercise {
    val result: Int
}

class MathExerciseGenerator(
    private val digitRange: IntRange = 2..3,
    private val maxResult: Int = 9999,
    private val random: Random = Random.Default,
) {
    fun nextExercise(): MathExercise
    fun worksheet(count: Int): Worksheet
}
```

`MathExercise.result` computes sum for ADDITION, difference for SUBTRACTION, product for MULTIPLICATION. The generator picks a random operation and two operands with digit count in `digitRange`; for SUBTRACTION it ensures the first operand is >= the second; for MULTIPLICATION it ensures the product stays <= `maxResult`.

- [ ] **Step 1: Write the failing test**

`MathExerciseGeneratorTest` asserts with a seeded `Random`: every generated operand has a digit count in `digitRange`; `result` equals sum/difference/product of the operands; SUBTRACTION results are >= 0; MULTIPLICATION results are <= `maxResult`; `worksheet(5)` returns a `Worksheet` with 5 exercises in order.

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :feature:math:testDebugUnitTest`
Expected: FAIL with unresolved references.

- [ ] **Step 3: Implement `MathExercise` and `MathExerciseGenerator`**

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :feature:math:testDebugUnitTest`
Expected: PASS, `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add feature/math
git commit -m "feat: add math exercise generator for column arithmetic"
```

### Task 5: Generic column layout in `:feature:math`

**Files:**
- Create: `feature/math/src/main/java/de/kappmeier/absies/feature/math/ColumnLayout.kt`
- Test: `feature/math/src/test/java/de/kappmeier/absies/feature/math/ColumnLayoutTest.kt`

**Interfaces:**
- Consumes: `MathExercise` from Task 4; `Cell`, `CellId`, `CellRole` from Task 2.
- Produces:

```kotlin
object ColumnLayout {
    fun layout(exercise: MathExercise, width: Float, height: Float): List<Cell>
}
```

Layout: one row per operand at the top (the operator sign sits in the first operand row), one helper row below the operands, a horizontal line, and one row of `RESULT_FIELD` cells below the line. The result row has max(operand digit counts) + 1 cells, so the final carry has a field. The helper row has one `HELPER` cell per result field. Every `Cell` lies within `0..width` x `0..height`. Positions are deterministic for the same input.

- [ ] **Step 1: Write the failing test**

`ColumnLayoutTest` asserts for `MathExercise(ADDITION, listOf(347, 237))` at 1000x1600: exactly 4 `RESULT_FIELD` cells, exactly 6 `OPERAND_DIGIT` cells, at least 4 `HELPER` cells, all cells inside the bounds, and identical positions across two calls. For `MathExercise(ADDITION, listOf(12, 3))`: exactly 3 `RESULT_FIELD` cells and 3 `OPERAND_DIGIT` cells.

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :feature:math:testDebugUnitTest`
Expected: FAIL with unresolved reference to `ColumnLayout`.

- [ ] **Step 3: Implement `ColumnLayout.layout`**

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :feature:math:testDebugUnitTest`
Expected: PASS, `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add feature/math
git commit -m "feat: add generic column layout for math exercises"
```

### Task 6: Exercise ViewModel in `:app`

**Files:**
- Create: `app/src/main/java/de/kappmeier/absies/ui/ExerciseViewModel.kt`, `app/src/main/java/de/kappmeier/absies/di/AppModule.kt`
- Modify: `app/build.gradle.kts` (add `api(project(":core:exercise"))`, `implementation(project(":core:ink"))`, `implementation(project(":feature:math"))`)
- Test: `app/src/test/java/de/kappmeier/absies/ui/ExerciseViewModelTest.kt`

**Interfaces:**
- Consumes: `Cell`, `CellId`, `Worksheet` from Task 2; `InkStore`, `Stroke`, `PenAuthor`, `InkLayer` from Task 3; `MathExerciseGenerator`, `ColumnLayout` from Tasks 4 and 5.
- Produces:

```kotlin
data class ExerciseUiState(
    val cells: List<Cell>,
    val layersByCell: Map<CellId, List<InkLayer>>,
    val selectedCellId: CellId?,
    val penAuthor: PenAuthor,
    val exerciseIndex: Int,
    val exerciseCount: Int,
)

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val inkStore: InkStore,
    generator: MathExerciseGenerator,
) : ViewModel() {
    val uiState: StateFlow<ExerciseUiState>
    fun onCellTapped(cell: Cell)
    fun onStrokeFinished(stroke: Stroke)
    fun onFinished()
    fun onNextExercise()
    fun onPenAuthorSelected(author: PenAuthor)
    fun onClearLayer()
}
```

`AppModule` binds `InkStore` to `InMemoryInkStore` as a singleton and provides `MathExerciseGenerator`. The ViewModel generates a worksheet of 10 exercises, lays out the current exercise with `ColumnLayout.layout(exercise, LOGICAL_WIDTH, LOGICAL_HEIGHT)` (logical size 1080x1600, scaled by the UI). `onCellTapped` selects the cell; `onStrokeFinished` adds the stroke to the selected cell under the current `penAuthor`; `onFinished` clears the selection but keeps layers; `onNextExercise` advances the index and reloads cells, never exceeding the last exercise; `onClearLayer` clears the current author's layer on the selected cell.

- [ ] **Step 1: Write the failing test**

`ExerciseViewModelTest` with `InMemoryInkStore` and a seeded generator asserts: initial state has non-empty cells, `exerciseIndex == 0`, `exerciseCount == 10`, `selectedCellId == null`; `onCellTapped(cell)` sets `selectedCellId`; `onStrokeFinished(stroke)` puts the stroke in that cell under `CHILD`; `onFinished()` clears the selection and keeps the layer; reopening the cell and adding a second stroke yields 2 strokes in one layer (Review Focus 5); `onPenAuthorSelected(CORRECTOR)` plus a stroke lands in a separate `CORRECTOR` layer; `onClearLayer()` removes only the current author's layer; `onNextExercise()` increments the index and clears the selection; at the last exercise `onNextExercise()` keeps the index at `exerciseCount - 1`.

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :app:testDebugUnitTest`
Expected: FAIL with unresolved reference to `ExerciseViewModel`.

- [ ] **Step 3: Implement `ExerciseViewModel` and `AppModule`; wire `MainActivity` to `ExerciseScreen` placeholder**

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :app:testDebugUnitTest`
Expected: PASS, `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add app
git commit -m "feat: add exercise view model with ink layers and exercise navigation"
```

### Task 7: Exercise screen, cell grid, tap-to-enlarge overlay, and ink canvas in `:app`

**Files:**
- Create: `app/src/main/java/de/kappmeier/absies/ui/ExerciseScreen.kt`, `app/src/main/java/de/kappmeier/absies/ui/InkCanvas.kt`, `app/src/main/res/values/strings.xml`, `app/src/main/res/values-de/strings.xml`
- Modify: `app/src/main/java/de/kappmeier/absies/MainActivity.kt`
- Test: `app/src/androidTest/java/de/kappmeier/absies/ui/ExerciseScreenTest.kt`

**Interfaces:**
- Consumes: `ExerciseViewModel` from Task 6.
- Produces:

```kotlin
@Composable
fun ExerciseScreen(viewModel: ExerciseViewModel)

@Composable
fun InkCanvas(
    modifier: Modifier = Modifier,
    strokeColor: Color,
    onStrokeFinished: (Stroke) -> Unit,
)
```

`ExerciseScreen` renders the cells of the current exercise as a grid scaled from the logical size to the actual size, drawing each cell's ink layers (child blue, corrector red). Tapping a cell calls `onCellTapped`; while a cell is selected, a dialog shows that cell enlarged with an `InkCanvas` and a "Fertig" button that calls `onFinished`. `InkCanvas` captures pointer drags into a `Stroke` using `clipToBounds` for every point, draws the finished stroke, and calls `onStrokeFinished`. Strings: `done`, `next_exercise` in `values/strings.xml` (English) and `values-de/strings.xml` (German: "Fertig", "Nächste Aufgabe"). Cells get a `testTag` of their `CellId` value.

- [ ] **Step 1: Write the failing UI test**

`ExerciseScreenTest` sets `ExerciseScreen(viewModel)` with a fake ViewModel (seeded generator, `InMemoryInkStore`). It asserts: a result field cell with its testTag is displayed; tapping it shows the dialog with the "Fertig" text; `performTouchInput` draws a stroke on the canvas; clicking "Fertig" dismisses the dialog; the view model state then holds exactly one stroke in that cell (Review Focus 1: a point dragged outside the canvas bounds is clipped and not recorded).

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest` (needs an emulator with an API >= 24 image)
Expected: FAIL with unresolved reference to `ExerciseScreen`.

- [ ] **Step 3: Implement `ExerciseScreen`, `InkCanvas`, strings, and the `MainActivity` wiring**

- [ ] **Step 4: Verify**

Run: `./gradlew :app:assembleDebug`
Expected: `BUILD SUCCESSFUL`.
Run: `./gradlew :app:connectedDebugAndroidTest`
Expected: PASS, `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add app
git commit -m "feat: add exercise screen with tap-to-enlarge cells and pen input"
```

### Task 8: Correction controls in `:app`

**Files:**
- Modify: `app/src/main/java/de/kappmeier/absies/ui/ExerciseScreen.kt`, `app/src/main/res/values/strings.xml`, `app/src/main/res/values-de/strings.xml`
- Test: `app/src/androidTest/java/de/kappmeier/absies/ui/ExerciseScreenTest.kt`

**Interfaces:**
- Consumes: `ExerciseViewModel` actions `onPenAuthorSelected`, `onClearLayer`, `onNextExercise` from Task 6.

Add a top bar with: a pen author toggle (two buttons for child and corrector, current author highlighted, strings `pen_child` / `pen_corrector`), a clear-layer button (string `clear_layer`, calls `onClearLayer`), and a "Nächste Aufgabe" button (string `next_exercise`, calls `onNextExercise`, hidden on the last exercise).

- [ ] **Step 1: Write the failing UI tests**

In `ExerciseScreenTest` assert: selecting the corrector button and drawing a stroke puts the stroke in the `CORRECTOR` layer while the child layer stays intact (Review Focus 3); `onClearLayer` as corrector removes only the corrector layer; tapping "Nächste Aufgabe" advances `exerciseIndex` and hides the button on the last exercise.

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest`
Expected: FAIL, buttons not found.

- [ ] **Step 3: Implement the top bar and strings**

- [ ] **Step 4: Verify**

Run: `./gradlew :app:connectedDebugAndroidTest`
Expected: PASS, `BUILD SUCCESSFUL`.
Run: `./gradlew testDebugUnitTest`
Expected: all unit tests PASS.

- [ ] **Step 5: Commit**

```bash
git add app
git commit -m "feat: add pen color switch, layer clear, and next exercise controls"
```

### Task 9: Build and dev environment docs in README.md

**Files:**
- Modify: `README.md`

**Interfaces:**
- Produces: a "Build" section and a "Development setup" section in README.md.

- [ ] **Step 1: Write the README sections**

Add to README.md:

- **Build:** prerequisites are JDK 17 and Android Studio (current stable, which bundles the Android SDK and Gradle wrapper). Build the debug APK with `./gradlew :app:assembleDebug`, run all unit tests with `./gradlew testDebugUnitTest`, run the UI tests on an emulator or device with `./gradlew :app:connectedDebugAndroidTest`.
- **Development setup:** install Android Studio and let it install the SDK; open the project root; JDK 17 is configured via the IDE/Gradle toolchain. To run on a real tablet or phone, enable developer options and USB debugging (or Wi-Fi debugging) on the device, connect it to the dev machine, and pick it in the Run configuration. To use the emulator, create a device with API level >= 24 in the Device Manager and run. Pen input must be tested on a real device with a stylus; the emulator only covers the flow.

- [ ] **Step 2: Verify**

Read README.md top to bottom; the commands match the ones in this plan. No build run needed for a docs change.

- [ ] **Step 3: Commit**

```bash
git add README.md
git commit -m "doc: add build and development setup instructions to README"
```
