package de.kappmeier.absies.core.ink

import de.kappmeier.absies.core.exercise.model.CellId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InkStoreTest {

    private val store = InMemoryInkStore()
    private val cell = CellId("result-0")
    private val otherCell = CellId("result-1")
    private val stroke = Stroke(listOf(Point(1f, 2f, 0L), Point(3f, 4f, 1L)))

    @Test
    fun `layers of an unknown cell are empty`() {
        assertTrue(store.layers(cell).isEmpty())
    }

    @Test
    fun `addStroke creates one layer per author and appends to it`() {
        store.addStroke(cell, PenAuthor.CHILD, stroke)
        store.addStroke(cell, PenAuthor.CHILD, stroke)
        store.addStroke(cell, PenAuthor.CORRECTOR, stroke)

        val layers = store.layers(cell)
        assertEquals(2, layers.size)
        assertEquals(2, layers.first { it.author == PenAuthor.CHILD }.strokes.size)
        assertEquals(1, layers.first { it.author == PenAuthor.CORRECTOR }.strokes.size)
    }

    @Test
    fun `layers of different cells are independent`() {
        store.addStroke(cell, PenAuthor.CHILD, stroke)

        assertTrue(store.layers(otherCell).isEmpty())
        assertEquals(1, store.layers(cell).size)
    }

    @Test
    fun `clearLayer removes only the addressed author layer`() {
        store.addStroke(cell, PenAuthor.CHILD, stroke)
        store.addStroke(cell, PenAuthor.CORRECTOR, stroke)

        store.clearLayer(cell, PenAuthor.CHILD)

        val layers = store.layers(cell)
        assertEquals(1, layers.size)
        assertEquals(PenAuthor.CORRECTOR, layers.first().author)
    }

    @Test
    fun `clearLayer on an unknown cell does nothing`() {
        store.clearLayer(cell, PenAuthor.CHILD)

        assertTrue(store.layers(cell).isEmpty())
    }

    @Test
    fun `clipToBounds keeps inside points and drops outside points`() {
        assertEquals(Point(5f, 5f, 0L), clipToBounds(Point(5f, 5f, 0L), 10f, 10f))
        assertEquals(Point(0f, 10f, 0L), clipToBounds(Point(0f, 10f, 0L), 10f, 10f))
        assertNull(clipToBounds(Point(-1f, 5f, 0L), 10f, 10f))
        assertNull(clipToBounds(Point(5f, -1f, 0L), 10f, 10f))
        assertNull(clipToBounds(Point(11f, 5f, 0L), 10f, 10f))
        assertNull(clipToBounds(Point(5f, 11f, 0L), 10f, 10f))
    }
}
