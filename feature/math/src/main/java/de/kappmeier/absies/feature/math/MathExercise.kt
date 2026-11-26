package de.kappmeier.absies.feature.math

import de.kappmeier.absies.core.exercise.model.Exercise

enum class Operation {
    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
}

data class MathExercise(val operation: Operation, val operands: List<Int>) : Exercise {

    val result: Int = when (operation) {
        Operation.ADDITION -> operands.sum()
        Operation.SUBTRACTION -> operands.first() - operands.drop(1).sum()
        Operation.MULTIPLICATION -> operands.fold(1) { acc, operand -> acc * operand }
    }
}
