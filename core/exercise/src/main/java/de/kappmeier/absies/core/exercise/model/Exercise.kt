package de.kappmeier.absies.core.exercise.model

interface Exercise

data class Worksheet(val exercises: List<Exercise>)

data class Collection(val worksheets: List<Worksheet>)

enum class CellRole {
    OPERAND_DIGIT,
    HELPER,
    RESULT_FIELD,
}

@JvmInline
value class CellId(val value: String)

data class Cell(
    val id: CellId,
    val role: CellRole,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val label: String = "",
)
