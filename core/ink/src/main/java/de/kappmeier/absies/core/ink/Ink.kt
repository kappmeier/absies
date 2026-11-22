package de.kappmeier.absies.core.ink

data class Point(val x: Float, val y: Float, val t: Long)

data class Stroke(val points: List<Point>)

enum class PenAuthor {
    CHILD,
    CORRECTOR,
}

data class InkLayer(val author: PenAuthor, val strokes: List<Stroke>)

fun clipToBounds(point: Point, width: Float, height: Float): Point? =
    if (point.x in 0f..width && point.y in 0f..height) point else null
