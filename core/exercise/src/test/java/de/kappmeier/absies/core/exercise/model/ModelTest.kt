package de.kappmeier.absies.core.exercise.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ModelTest {

    private object FakeExercise : Exercise

    @Test
    fun `worksheet preserves exercise order`() {
        val exercises = listOf(FakeExercise, FakeExercise, FakeExercise)
        val worksheet = Worksheet(exercises)

        assertEquals(exercises, worksheet.exercises)
    }

    @Test
    fun `collection holds its worksheets`() {
        val worksheets = listOf(Worksheet(listOf(FakeExercise)), Worksheet(emptyList()))
        val collection = Collection(worksheets)

        assertEquals(worksheets, collection.worksheets)
    }

    @Test
    fun `cell role has operand digit helper and result field`() {
        assertEquals(
            listOf(CellRole.OPERAND_DIGIT, CellRole.HELPER, CellRole.RESULT_FIELD),
            CellRole.entries.toList(),
        )
    }

    @Test
    fun `cell keeps its role and bounds`() {
        val cell = Cell(CellId("result-0"), CellRole.RESULT_FIELD, 0f, 10f, 100f, 110f)

        assertEquals(CellRole.RESULT_FIELD, cell.role)
        assertEquals(0f, cell.left)
        assertEquals(10f, cell.top)
        assertEquals(100f, cell.right)
        assertEquals(110f, cell.bottom)
        assertTrue(cell.right > cell.left)
        assertTrue(cell.bottom > cell.top)
    }
}
