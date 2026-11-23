package de.kappmeier.absies.feature.math

import de.kappmeier.absies.core.exercise.model.Cell
import de.kappmeier.absies.core.exercise.model.CellId
import de.kappmeier.absies.core.exercise.model.CellRole

object ColumnLayout {

    fun layout(exercise: MathExercise, width: Float, height: Float): List<Cell> {
        val cells = mutableListOf<Cell>()
        val operandCount = exercise.operands.size
        val maxDigits = exercise.operands.maxOf { it.toString().length }
        val resultFieldCount = maxDigits + 1
        val rowHeight = height / (operandCount + 2)
        val fieldWidth = width / resultFieldCount

        exercise.operands.forEachIndexed { rowIndex, operand ->
            val digits = operand.toString()
            val startCol = resultFieldCount - digits.length
            digits.forEachIndexed { digitIndex, digit ->
                val col = startCol + digitIndex
                cells.add(
                    Cell(
                        id = CellId("operand-$rowIndex-$digitIndex"),
                        role = CellRole.OPERAND_DIGIT,
                        left = col * fieldWidth,
                        top = rowIndex * rowHeight,
                        right = (col + 1) * fieldWidth,
                        bottom = (rowIndex + 1) * rowHeight,
                        label = digit.toString(),
                    ),
                )
            }
        }

        repeat(resultFieldCount) { col ->
            cells.add(
                Cell(
                    id = CellId("helper-$col"),
                    role = CellRole.HELPER,
                    left = col * fieldWidth,
                    top = operandCount * rowHeight,
                    right = (col + 1) * fieldWidth,
                    bottom = (operandCount + 1) * rowHeight,
                ),
            )
        }

        repeat(resultFieldCount) { col ->
            cells.add(
                Cell(
                    id = CellId("result-$col"),
                    role = CellRole.RESULT_FIELD,
                    left = col * fieldWidth,
                    top = (operandCount + 1) * rowHeight,
                    right = (col + 1) * fieldWidth,
                    bottom = height,
                ),
            )
        }

        return cells
    }
}
