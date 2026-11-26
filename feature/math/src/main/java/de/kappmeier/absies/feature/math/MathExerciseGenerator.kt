package de.kappmeier.absies.feature.math

import de.kappmeier.absies.core.exercise.model.Worksheet
import kotlin.math.pow
import kotlin.random.Random

class MathExerciseGenerator(
    private val digitRange: IntRange = 2..3,
    private val maxResult: Int = 9999,
    private val operations: List<Operation> = listOf(Operation.ADDITION, Operation.SUBTRACTION),
    private val random: Random = Random.Default,
) {

    fun nextExercise(): MathExercise {
        val operation = operations.random(random)
        val digits = random.nextInt(digitRange.first, digitRange.last + 1)
        val min = 10.0.pow(digits - 1).toInt()
        val max = 10.0.pow(digits).toInt() - 1
        val first = random.nextInt(min, max + 1)
        return when (operation) {
            Operation.ADDITION -> MathExercise(operation, listOf(first, random.nextInt(min, max + 1)))
            Operation.SUBTRACTION -> MathExercise(operation, listOf(first, random.nextInt(min, first + 1)))
            Operation.MULTIPLICATION -> {
                val second = random.nextInt(1, maxOf(1, maxResult / first) + 1)
                MathExercise(operation, listOf(first, second))
            }
        }
    }

    fun worksheet(count: Int): Worksheet = Worksheet(List(count) { nextExercise() })
}
