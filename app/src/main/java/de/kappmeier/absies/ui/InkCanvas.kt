package de.kappmeier.absies.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.input.pointer.awaitPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import de.kappmeier.absies.core.ink.Point
import de.kappmeier.absies.core.ink.Stroke
import de.kappmeier.absies.core.ink.clipToBounds

@Composable
fun InkCanvas(
    modifier: Modifier = Modifier,
    strokeColor: Color,
    onStrokeFinished: (Stroke) -> Unit,
) {
    var currentStroke by remember { mutableStateOf<Stroke?>(null) }

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val points = mutableListOf<Point>()
                fun addPoint(x: Float, y: Float, t: Long) {
                    val clipped = clipToBounds(
                        Point(x, y, t),
                        size.width.toFloat(),
                        size.height.toFloat(),
                    )
                    if (clipped != null && size.width > 0 && size.height > 0) {
                        points.add(
                            Point(clipped.x / size.width, clipped.y / size.height, clipped.t),
                        )
                    }
                }
                addPoint(down.position.x, down.position.y, down.uptimeMillis)
                while (true) {
                    val event = awaitPointerEvent()
                    event.changes.forEach { change ->
                        if (change.pressed) {
                            addPoint(change.position.x, change.position.y, change.uptimeMillis)
                        }
                        change.consume()
                    }
                    if (event.changes.none { it.pressed }) break
                }
                if (points.size >= 2) {
                    val stroke = Stroke(points.toList())
                    currentStroke = stroke
                    onStrokeFinished(stroke)
                }
                currentStroke = null
            }
        },
    ) {
        val stroke = currentStroke
        if (stroke != null && stroke.points.size >= 2) {
            val path = Path().apply {
                stroke.points.forEachIndexed { index, point ->
                    if (index == 0) {
                        moveTo(point.x * size.width, point.y * size.height)
                    } else {
                        lineTo(point.x * size.width, point.y * size.height)
                    }
                }
            }
            drawPath(
                path,
                strokeColor,
                style = androidx.compose.ui.graphics.Stroke(
                    width = 8f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}
