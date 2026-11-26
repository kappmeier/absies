package de.kappmeier.absies.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertCountAtLeast
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import de.kappmeier.absies.core.exercise.model.CellRole
import de.kappmeier.absies.core.ink.InMemoryInkStore
import de.kappmeier.absies.core.ink.PenAuthor
import de.kappmeier.absies.feature.math.MathExerciseGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class ExerciseScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun viewModel() =
        ExerciseViewModel(InMemoryInkStore(), MathExerciseGenerator(random = Random(42)))

    private fun resultCellTag(viewModel: ExerciseViewModel): String {
        val cell = viewModel.uiState.value.cells.first { it.role == CellRole.RESULT_FIELD }
        return "cell-${cell.id.value}"
    }

    private fun drawStroke() {
        composeRule.onNodeWithTag("ink-canvas").performTouchInput {
            down(center)
            moveTo(center + Offset(40f, 40f))
            up()
        }
    }

    @Test
    fun resultCellsAreDisplayed() {
        val viewModel = viewModel()
        composeRule.setContent { ExerciseScreen(viewModel = viewModel) }

        composeRule.onNodeWithTag(resultCellTag(viewModel)).assertIsDisplayed()
    }

    @Test
    fun tappingAnOperandCellShowsTheOriginalDigitAsBackgroundInTheDialog() {
        val viewModel = viewModel()
        composeRule.setContent { ExerciseScreen(viewModel = viewModel) }
        val operandCell =
            viewModel.uiState.value.cells.first { it.role == CellRole.OPERAND_DIGIT && it.label.isNotEmpty() }

        composeRule.onNodeWithTag("cell-${operandCell.id.value}").performClick()
        composeRule.waitForIdle()

        composeRule.onAllNodesWithText(operandCell.label).assertCountAtLeast(2)
    }

    @Test
    fun tappingACellOpensTheDialogAndDrawingThenFinishingStoresTheStroke() {
        val viewModel = viewModel()
        composeRule.setContent { ExerciseScreen(viewModel = viewModel) }
        val tag = resultCellTag(viewModel)
        val cellId = viewModel.uiState.value.cells.first { it.role == CellRole.RESULT_FIELD }.id

        composeRule.onNodeWithTag(tag).performClick()
        composeRule.onNodeWithTag("ink-canvas").assertIsDisplayed()
        drawStroke()
        composeRule.onNodeWithTag("done-button").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("done-button").assertDoesNotExist()
        val layers = viewModel.uiState.value.layersByCell[cellId]
        assertEquals(1, layers?.first()?.strokes?.size)
        assertEquals(PenAuthor.CHILD, layers?.first()?.author)
    }

    @Test
    fun switchingToTheCorrectorStoresTheStrokeInTheCorrectorLayer() {
        val viewModel = viewModel()
        composeRule.setContent { ExerciseScreen(viewModel = viewModel) }
        val tag = resultCellTag(viewModel)
        val cellId = viewModel.uiState.value.cells.first { it.role == CellRole.RESULT_FIELD }.id

        composeRule.onNodeWithTag(tag).performClick()
        composeRule.onNodeWithTag("pen-corrector").performClick()
        drawStroke()
        composeRule.onNodeWithTag("done-button").performClick()
        composeRule.waitForIdle()

        val layers = viewModel.uiState.value.layersByCell[cellId]
        assertEquals(1, layers?.size)
        assertEquals(PenAuthor.CORRECTOR, layers?.first()?.author)
    }

    @Test
    fun clearLayerRemovesOnlyTheCurrentAuthorLayer() {
        val viewModel = viewModel()
        composeRule.setContent { ExerciseScreen(viewModel = viewModel) }
        val tag = resultCellTag(viewModel)
        val cellId = viewModel.uiState.value.cells.first { it.role == CellRole.RESULT_FIELD }.id

        composeRule.onNodeWithTag(tag).performClick()
        drawStroke()
        composeRule.onNodeWithTag("pen-corrector").performClick()
        drawStroke()
        composeRule.onNodeWithTag("clear-layer").performClick()
        composeRule.waitForIdle()

        val layers = viewModel.uiState.value.layersByCell[cellId]
        assertEquals(1, layers?.size)
        assertEquals(PenAuthor.CHILD, layers?.first()?.author)

        composeRule.onNodeWithTag("done-button").performClick()
    }

    @Test
    fun nextExerciseAdvancesAndHidesOnTheLastExercise() {
        val viewModel = viewModel()
        composeRule.setContent { ExerciseScreen(viewModel = viewModel) }

        repeat(ExerciseViewModel.WORKSHEET_SIZE - 1) {
            composeRule.onNodeWithTag("next-exercise").performClick()
        }
        composeRule.waitForIdle()

        assertEquals(ExerciseViewModel.WORKSHEET_SIZE - 1, viewModel.uiState.value.exerciseIndex)
        composeRule.onNodeWithTag("next-exercise").assertDoesNotExist()
    }

    @Test
    fun strokesSurviveClosingTheDialog() {
        val viewModel = viewModel()
        composeRule.setContent { ExerciseScreen(viewModel = viewModel) }
        val tag = resultCellTag(viewModel)
        val cellId = viewModel.uiState.value.cells.first { it.role == CellRole.RESULT_FIELD }.id

        composeRule.onNodeWithTag(tag).performClick()
        drawStroke()
        composeRule.onNodeWithTag("done-button").performClick()
        composeRule.waitForIdle()

        assertEquals(1, viewModel.uiState.value.layersByCell[cellId]?.first()?.strokes?.size)
        assertNull(viewModel.uiState.value.selectedCellId)
        assertTrue(viewModel.uiState.value.cells.isNotEmpty())
    }
}
