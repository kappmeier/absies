package de.kappmeier.absies.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kappmeier.absies.core.exercise.model.Cell
import de.kappmeier.absies.core.exercise.model.CellId
import de.kappmeier.absies.core.exercise.model.Worksheet
import de.kappmeier.absies.core.ink.InkLayer
import de.kappmeier.absies.core.ink.InkStore
import de.kappmeier.absies.core.ink.PenAuthor
import de.kappmeier.absies.core.ink.Stroke
import de.kappmeier.absies.feature.math.ColumnLayout
import de.kappmeier.absies.feature.math.MathExercise
import de.kappmeier.absies.feature.math.MathExerciseGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

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

    private val worksheet: Worksheet = generator.worksheet(WORKSHEET_SIZE)
    private val exercises: List<MathExercise> = worksheet.exercises.filterIsInstance<MathExercise>()

    private val _uiState = MutableStateFlow(
        ExerciseUiState(
            cells = layoutOf(exercises.first()),
            layersByCell = emptyMap(),
            selectedCellId = null,
            penAuthor = PenAuthor.CHILD,
            exerciseIndex = 0,
            exerciseCount = exercises.size,
        ),
    )
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    fun onCellTapped(cell: Cell) {
        _uiState.update { it.copy(selectedCellId = cell.id) }
    }

    fun onStrokeFinished(stroke: Stroke) {
        val cellId = _uiState.value.selectedCellId ?: return
        inkStore.addStroke(cellId, _uiState.value.penAuthor, stroke)
        _uiState.update { state ->
            state.copy(layersByCell = state.layersByCell + (cellId to inkStore.layers(cellId)))
        }
    }

    fun onFinished() {
        _uiState.update { it.copy(selectedCellId = null) }
    }

    fun onNextExercise() {
        val nextIndex = _uiState.value.exerciseIndex + 1
        if (nextIndex >= exercises.size) return
        val cells = layoutOf(exercises[nextIndex])
        _uiState.update { state ->
            state.copy(
                cells = cells,
                layersByCell = cells.associate { it.id to inkStore.layers(it.id) },
                selectedCellId = null,
                exerciseIndex = nextIndex,
            )
        }
    }

    fun onPenAuthorSelected(author: PenAuthor) {
        _uiState.update { it.copy(penAuthor = author) }
    }

    fun onClearLayer() {
        val cellId = _uiState.value.selectedCellId ?: return
        inkStore.clearLayer(cellId, _uiState.value.penAuthor)
        _uiState.update { state ->
            state.copy(layersByCell = state.layersByCell + (cellId to inkStore.layers(cellId)))
        }
    }

    private fun layoutOf(exercise: MathExercise): List<Cell> =
        ColumnLayout.layout(exercise, LOGICAL_WIDTH, LOGICAL_HEIGHT)

    companion object {
        const val LOGICAL_WIDTH = 1080f
        const val LOGICAL_HEIGHT = 1600f
        const val WORKSHEET_SIZE = 10
    }
}
