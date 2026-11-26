package de.kappmeier.absies.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.kappmeier.absies.core.exercise.model.CellRole
import androidx.hilt.navigation.compose.hiltViewModel
import de.kappmeier.absies.R
import de.kappmeier.absies.core.exercise.model.Cell
import de.kappmeier.absies.core.exercise.model.CellId
import de.kappmeier.absies.core.ink.InkLayer
import de.kappmeier.absies.core.ink.PenAuthor
import de.kappmeier.absies.core.ink.Stroke as InkStroke
import kotlin.math.min

@Composable
fun ExerciseScreen(viewModel: ExerciseViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ExerciseTopBar(
                showNext = state.exerciseIndex < state.exerciseCount - 1,
                onNextExercise = { viewModel.onNextExercise() },
            )
        },
    ) { padding ->
        BoxWithConstraints(Modifier.fillMaxSize().padding(padding)) {
            val scale = min(
                maxWidth.value / ExerciseViewModel.LOGICAL_WIDTH,
                maxHeight.value / ExerciseViewModel.LOGICAL_HEIGHT,
            )
            CellGrid(
                cells = state.cells,
                layersByCell = state.layersByCell,
                scale = scale,
                operator = state.operator,
                onCellTapped = { cell -> viewModel.onCellTapped(cell) },
            )
            val selectedId = state.selectedCellId
            if (selectedId != null) {
                val cell = state.cells.first { it.id == selectedId }
                EnlargedCellDialog(
                    label = cell.label,
                    layers = state.layersByCell[cell.id].orEmpty(),
                    penAuthor = state.penAuthor,
                    onPenAuthorSelected = { author -> viewModel.onPenAuthorSelected(author) },
                    onClearLayer = { viewModel.onClearLayer() },
                    onStrokeFinished = { stroke -> viewModel.onStrokeFinished(stroke) },
                    onFinished = { viewModel.onFinished() },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseTopBar(
    showNext: Boolean,
    onNextExercise: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            if (showNext) {
                TextButton(
                    onClick = onNextExercise,
                    modifier = Modifier.testTag("next-exercise"),
                ) {
                    Text(stringResource(R.string.next_exercise))
                }
            }
        },
    )
}

@Composable
private fun CellGrid(
    cells: List<Cell>,
    layersByCell: Map<CellId, List<InkLayer>>,
    scale: Float,
    operator: String,
    onCellTapped: (Cell) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            // The Canvas draws in pixels, so strokes and the line need a pixel based
            // scale. The cell Boxes below use dp offsets with the dp based scale.
            val scalePx = min(
                size.width / ExerciseViewModel.LOGICAL_WIDTH,
                size.height / ExerciseViewModel.LOGICAL_HEIGHT,
            )
            cells.forEach { cell ->
                layersByCell[cell.id].orEmpty().forEach { layer ->
                    layer.strokes.forEach { stroke ->
                        drawCellStroke(
                            stroke = stroke,
                            color = penColor(layer.author),
                            offsetX = cell.left * scalePx,
                            offsetY = cell.top * scalePx,
                            cellWidth = (cell.right - cell.left) * scalePx,
                            cellHeight = (cell.bottom - cell.top) * scalePx,
                        )
                    }
                }
            }
            val resultTop = cells.filter { it.role == CellRole.RESULT_FIELD }.minOf { it.top } * scalePx
            drawLine(
                color = Color.DarkGray,
                start = Offset(0f, resultTop),
                end = Offset(size.width, resultTop),
                strokeWidth = 4f,
            )
        }
        val operandTop = cells.filter { it.role == CellRole.OPERAND_DIGIT }.minOf { it.top }
        Text(
            text = operator,
            fontSize = ((cells.first { it.role == CellRole.OPERAND_DIGIT }.bottom - operandTop) * scale * 0.6f).sp,
            modifier = Modifier.offset(8.dp, (operandTop * scale).dp),
        )
        cells.forEach { cell ->
            Box(
                modifier = Modifier
                    .offset((cell.left * scale).dp, (cell.top * scale).dp)
                    .size(((cell.right - cell.left) * scale).dp, ((cell.bottom - cell.top) * scale).dp)
                    .border(1.dp, Color.LightGray)
                    .clickable { onCellTapped(cell) }
                    .testTag("cell-${cell.id.value}"),
                contentAlignment = Alignment.Center,
            ) {
                if (cell.label.isNotEmpty()) {
                    Text(
                        text = cell.label,
                        fontSize = (((cell.bottom - cell.top) * scale) * 0.6f).sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun EnlargedCellDialog(
    label: String,
    layers: List<InkLayer>,
    penAuthor: PenAuthor,
    onPenAuthorSelected: (PenAuthor) -> Unit,
    onClearLayer: () -> Unit,
    onStrokeFinished: (InkStroke) -> Unit,
    onFinished: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onFinished,
        confirmButton = {
            TextButton(onClick = onFinished, modifier = Modifier.testTag("done-button")) {
                Text(stringResource(R.string.done))
            }
        },
        text = {
            Column {
                Box(Modifier.size(240.dp)) {
                    if (label.isNotEmpty()) {
                        Text(
                            text = label,
                            fontSize = 160.sp,
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    Canvas(Modifier.fillMaxSize()) {
                        layers.forEach { layer ->
                            layer.strokes.forEach { stroke ->
                                if (stroke.points.size >= 2) {
                                    val path = Path().apply {
                                        stroke.points.forEachIndexed { index, point ->
                                            val x = point.x * size.width
                                            val y = point.y * size.height
                                            if (index == 0) moveTo(x, y) else lineTo(x, y)
                                        }
                                    }
                                    drawPath(
                                        path,
                                        penColor(layer.author),
                                        style = Stroke(
                                            width = 8f,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                    InkCanvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("ink-canvas"),
                        strokeColor = penColor(penAuthor),
                        onStrokeFinished = onStrokeFinished,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    TextButton(
                        onClick = { onPenAuthorSelected(PenAuthor.CHILD) },
                        modifier = Modifier.testTag("pen-child"),
                    ) {
                        Text(stringResource(R.string.pen_child))
                    }
                    TextButton(
                        onClick = { onPenAuthorSelected(PenAuthor.CORRECTOR) },
                        modifier = Modifier.testTag("pen-corrector"),
                    ) {
                        Text(stringResource(R.string.pen_corrector))
                    }
                    TextButton(
                        onClick = onClearLayer,
                        modifier = Modifier.testTag("clear-layer"),
                    ) {
                        Text(stringResource(R.string.clear_layer))
                    }
                }
            }
        },
    )
}

private fun DrawScope.drawCellStroke(
    stroke: InkStroke,
    color: Color,
    offsetX: Float,
    offsetY: Float,
    cellWidth: Float,
    cellHeight: Float,
) {
    if (stroke.points.size < 2) return
    val path = Path().apply {
        stroke.points.forEachIndexed { index, point ->
            val x = offsetX + point.x * cellWidth
            val y = offsetY + point.y * cellHeight
            if (index == 0) moveTo(x, y) else lineTo(x, y)
        }
    }
    drawPath(
        path,
        color,
        style = Stroke(
            width = 6f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}

private fun penColor(author: PenAuthor): Color =
    if (author == PenAuthor.CHILD) Color.Blue else Color.Red
