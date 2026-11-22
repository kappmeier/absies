package de.kappmeier.absies.ui

import de.kappmeier.absies.core.exercise.model.CellRole
import de.kappmeier.absies.core.ink.InMemoryInkStore
import de.kappmeier.absies.core.ink.PenAuthor
import de.kappmeier.absies.core.ink.Point
import de.kappmeier.absies.core.ink.Stroke
import de.kappmeier.absies.feature.math.MathExerciseGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

class ExerciseViewModelTest {

    private val inkStore = InMemoryInkStore()
    private val viewModel = ExerciseViewModel(inkStore, MathExerciseGenerator(random = Random(42)))
    private val stroke = Stroke(listOf(Point(0.1f, 0.1f, 0L), Point(0.5f, 0.5f, 1L)))

    private fun resultCell() = viewModel.uiState.value.cells.first { it.role == CellRole.RESULT_FIELD }

    @Test
    fun `initial state has cells and shows the first exercise`() {
        val state = viewModel.uiState.value

        assertFalse(state.cells.isEmpty())
        assertEquals(0, state.exerciseIndex)
        assertEquals(ExerciseViewModel.WORKSHEET_SIZE, state.exerciseCount)
        assertNull(state.selectedCellId)
        assertEquals(PenAuthor.CHILD, state.penAuthor)
    }

    @Test
    fun `tapping a cell selects it`() {
        val cell = resultCell()

        viewModel.onCellTapped(cell)

        assertEquals(cell.id, viewModel.uiState.value.selectedCellId)
    }

    @Test
    fun `a finished stroke lands in the selected cell under the current author`() {
        val cell = resultCell()
        viewModel.onCellTapped(cell)

        viewModel.onStrokeFinished(stroke)

        val layers = viewModel.uiState.value.layersByCell[cell.id]
        assertEquals(1, layers?.size)
        assertEquals(PenAuthor.CHILD, layers?.first()?.author)
        assertEquals(1, layers?.first()?.strokes?.size)
    }

    @Test
    fun `finished clears the selection but keeps the layers`() {
        val cell = resultCell()
        viewModel.onCellTapped(cell)
        viewModel.onStrokeFinished(stroke)

        viewModel.onFinished()

        assertNull(viewModel.uiState.value.selectedCellId)
        assertEquals(1, viewModel.uiState.value.layersByCell[cell.id]?.first()?.strokes?.size)
    }

    @Test
    fun `reopening a cell appends strokes to the same layer`() {
        val cell = resultCell()
        viewModel.onCellTapped(cell)
        viewModel.onStrokeFinished(stroke)
        viewModel.onFinished()
        viewModel.onCellTapped(cell)
        viewModel.onStrokeFinished(stroke)

        val layers = viewModel.uiState.value.layersByCell[cell.id]
        assertEquals(1, layers?.size)
        assertEquals(2, layers?.first()?.strokes?.size)
    }

    @Test
    fun `a stroke without a selected cell is ignored`() {
        viewModel.onStrokeFinished(stroke)

        assertTrue(viewModel.uiState.value.layersByCell.isEmpty())
    }

    @Test
    fun `corrector strokes land in a separate layer`() {
        val cell = resultCell()
        viewModel.onCellTapped(cell)
        viewModel.onStrokeFinished(stroke)
        viewModel.onPenAuthorSelected(PenAuthor.CORRECTOR)
        viewModel.onStrokeFinished(stroke)

        val layers = viewModel.uiState.value.layersByCell[cell.id]
        assertEquals(2, layers?.size)
        assertEquals(
            setOf(PenAuthor.CHILD, PenAuthor.CORRECTOR),
            layers?.map { it.author }?.toSet(),
        )
    }

    @Test
    fun `clearLayer removes only the current author layer`() {
        val cell = resultCell()
        viewModel.onCellTapped(cell)
        viewModel.onStrokeFinished(stroke)
        viewModel.onPenAuthorSelected(PenAuthor.CORRECTOR)
        viewModel.onStrokeFinished(stroke)

        viewModel.onClearLayer()

        val layers = viewModel.uiState.value.layersByCell[cell.id]
        assertEquals(1, layers?.size)
        assertEquals(PenAuthor.CHILD, layers?.first()?.author)
    }

    @Test
    fun `next exercise advances the index and clears the selection`() {
        viewModel.onCellTapped(resultCell())

        viewModel.onNextExercise()

        assertEquals(1, viewModel.uiState.value.exerciseIndex)
        assertNull(viewModel.uiState.value.selectedCellId)
    }

    @Test
    fun `next exercise never goes past the last exercise`() {
        repeat(ExerciseViewModel.WORKSHEET_SIZE + 5) { viewModel.onNextExercise() }

        assertEquals(ExerciseViewModel.WORKSHEET_SIZE - 1, viewModel.uiState.value.exerciseIndex)
    }
}
