package de.kappmeier.absies.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import de.kappmeier.absies.core.exercise.model.CellRole
import de.kappmeier.absies.core.ink.InMemoryInkStore
import de.kappmeier.absies.feature.math.MathExerciseGenerator
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class ExerciseScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun viewModel() =
        ExerciseViewModel(InMemoryInkStore(), MathExerciseGenerator(random = Random(42)))

    @Test
    fun resultCellsAreDisplayed() {
        val viewModel = viewModel()
        composeRule.setContent { ExerciseScreen(viewModel = viewModel) }

        val cell = viewModel.uiState.value.cells.first { it.role == CellRole.RESULT_FIELD }
        composeRule.onNodeWithTag("cell-${cell.id.value}").assertIsDisplayed()
    }

    @Test
    fun tappingACellOpensTheDialogAndDrawingThenFinishingStoresTheStroke() {
        val viewModel = viewModel()
        composeRule.setContent { ExerciseScreen(viewModel = viewModel) }
        val cell = viewModel.uiState.value.cells.first { it.role == CellRole.RESULT_FIELD }

        composeRule.onNodeWithTag("cell-${cell.id.value}").performClick()
        composeRule.onNodeWithTag("ink-canvas").assertIsDisplayed()
        composeRule.onNodeWithTag("ink-canvas").performTouchInput {
            down(center)
            moveTo(center + Offset(40f, 40f))
            up()
        }
        composeRule.onNodeWithTag("done-button").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("done-button").assertDoesNotExist()
        val layers = viewModel.uiState.value.layersByCell[cell.id]
        assertEquals(1, layers?.first()?.strokes?.size)
    }
}
