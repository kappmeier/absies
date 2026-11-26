package de.kappmeier.absies.feature.math

import de.kappmeier.absies.core.exercise.model.CellRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ColumnLayoutTest {

    @Test
    fun `three digit addition produces four result fields, six operand digits, and four helper cells`() {
        val cells = ColumnLayout.layout(MathExercise(Operation.ADDITION, listOf(347, 237)), 1000f, 1600f)

        assertEquals(4, cells.count { it.role == CellRole.RESULT_FIELD })
        assertEquals(6, cells.count { it.role == CellRole.OPERAND_DIGIT })
        assertEquals(4, cells.count { it.role == CellRole.HELPER })
    }

    @Test
    fun `two digit addition produces three result fields and three operand digits`() {
        val cells = ColumnLayout.layout(MathExercise(Operation.ADDITION, listOf(12, 3)), 1000f, 1600f)

        assertEquals(3, cells.count { it.role == CellRole.RESULT_FIELD })
        assertEquals(3, cells.count { it.role == CellRole.OPERAND_DIGIT })
    }

    @Test
    fun `four digit operands produce five result fields`() {
        val cells = ColumnLayout.layout(MathExercise(Operation.MULTIPLICATION, listOf(1234, 12)), 1000f, 1600f)

        assertEquals(5, cells.count { it.role == CellRole.RESULT_FIELD })
    }

    @Test
    fun `all cells lie inside the given bounds`() {
        val cells = ColumnLayout.layout(MathExercise(Operation.ADDITION, listOf(347, 237)), 1000f, 1600f)

        assertTrue(cells.all { it.left >= 0f && it.right <= 1000f && it.top >= 0f && it.bottom <= 1600f })
    }

    @Test
    fun `cell ids are unique`() {
        val cells = ColumnLayout.layout(MathExercise(Operation.ADDITION, listOf(347, 237)), 1000f, 1600f)

        assertEquals(cells.size, cells.map { it.id }.toSet().size)
    }

    @Test
    fun `operand digit cells carry their digit as label`() {
        val cells = ColumnLayout.layout(MathExercise(Operation.ADDITION, listOf(347, 237)), 1000f, 1600f)

        val labels = cells.filter { it.role == CellRole.OPERAND_DIGIT }.map { it.label }
        assertEquals(listOf("3", "4", "7", "2", "3", "7"), labels)
    }

    @Test
    fun `layout is deterministic for the same input`() {
        val exercise = MathExercise(Operation.ADDITION, listOf(347, 237))

        assertEquals(
            ColumnLayout.layout(exercise, 1000f, 1600f),
            ColumnLayout.layout(exercise, 1000f, 1600f),
        )
    }
}
