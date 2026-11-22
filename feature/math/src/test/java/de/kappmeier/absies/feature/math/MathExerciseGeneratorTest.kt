package de.kappmeier.absies.feature.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

class MathExerciseGeneratorTest {

    private val generator = MathExerciseGenerator(
        digitRange = 2..3,
        maxResult = 9999,
        random = Random(42),
    )

    @Test
    fun `addition and subtraction operands have a digit count in range`() {
        repeat(200) {
            val exercise = generator.nextExercise()
            if (exercise.operation != Operation.MULTIPLICATION) {
                exercise.operands.forEach { operand ->
                    assertTrue(
                        operand.toString().length in 2..3,
                        "operand $operand of $exercise is outside the digit range",
                    )
                }
            }
        }
    }

    @Test
    fun `result matches the operation`() {
        repeat(200) {
            val exercise = generator.nextExercise()
            val expected = when (exercise.operation) {
                Operation.ADDITION -> exercise.operands.sum()
                Operation.SUBTRACTION -> exercise.operands.first() - exercise.operands.drop(1).sum()
                Operation.MULTIPLICATION -> exercise.operands.fold(1) { acc, operand -> acc * operand }
            }
            assertEquals(expected, exercise.result, "wrong result for $exercise")
        }
    }

    @Test
    fun `subtraction result is never negative`() {
        repeat(200) {
            val exercise = generator.nextExercise()
            if (exercise.operation == Operation.SUBTRACTION) {
                assertTrue(exercise.result >= 0, "negative result for $exercise")
            }
        }
    }

    @Test
    fun `multiplication result stays within maxResult`() {
        repeat(200) {
            val exercise = generator.nextExercise()
            if (exercise.operation == Operation.MULTIPLICATION) {
                assertTrue(exercise.result <= 9999, "result too large for $exercise")
            }
        }
    }

    @Test
    fun `worksheet returns count exercises`() {
        val worksheet = generator.worksheet(5)

        assertEquals(5, worksheet.exercises.size)
    }
}
