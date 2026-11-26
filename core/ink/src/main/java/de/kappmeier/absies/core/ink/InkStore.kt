package de.kappmeier.absies.core.ink

import de.kappmeier.absies.core.exercise.model.CellId

interface InkStore {
    fun layers(cellId: CellId): List<InkLayer>
    fun addStroke(cellId: CellId, author: PenAuthor, stroke: Stroke)
    fun clearLayer(cellId: CellId, author: PenAuthor)
}

class InMemoryInkStore : InkStore {

    private val layersByCell = mutableMapOf<CellId, MutableList<InkLayer>>()

    override fun layers(cellId: CellId): List<InkLayer> =
        layersByCell[cellId]?.toList() ?: emptyList()

    override fun addStroke(cellId: CellId, author: PenAuthor, stroke: Stroke) {
        val layers = layersByCell.getOrPut(cellId) { mutableListOf() }
        val index = layers.indexOfFirst { it.author == author }
        if (index >= 0) {
            layers[index] = layers[index].copy(strokes = layers[index].strokes + stroke)
        } else {
            layers.add(InkLayer(author, listOf(stroke)))
        }
    }

    override fun clearLayer(cellId: CellId, author: PenAuthor) {
        layersByCell[cellId]?.removeAll { it.author == author }
    }
}
