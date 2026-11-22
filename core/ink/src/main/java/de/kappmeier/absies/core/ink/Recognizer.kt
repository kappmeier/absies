package de.kappmeier.absies.core.ink

interface Recognizer {
    fun recognize(strokes: List<Stroke>): List<String>
}
