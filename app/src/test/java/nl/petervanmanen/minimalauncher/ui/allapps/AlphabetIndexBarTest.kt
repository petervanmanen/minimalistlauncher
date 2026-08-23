package nl.petervanmanen.minimalauncher.ui.allapps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlphabetIndexBarTest {

    @Test
    fun `letterAt maps the top of the strip to A and the bottom to Z`() {
        val heightPx = 2600f

        assertEquals('A', letterAt(y = 0f, heightPx = heightPx))
        assertEquals('Z', letterAt(y = heightPx - 1f, heightPx = heightPx))
    }

    @Test
    fun `letterAt clamps out-of-bounds positions instead of crashing`() {
        val heightPx = 2600f

        assertEquals('A', letterAt(y = -50f, heightPx = heightPx))
        assertEquals('Z', letterAt(y = heightPx + 500f, heightPx = heightPx))
    }

    @Test
    fun `letterAt is evenly divided across all 26 rows`() {
        val heightPx = 2600f // 100px per letter
        assertEquals('M', letterAt(y = 1250f, heightPx = heightPx)) // row 12 -> 'M'
    }

    @Test
    fun `nearestAvailableLetter returns the exact letter when it's available`() {
        assertEquals('C', nearestAvailableLetter('C', setOf('A', 'C', 'F')))
    }

    @Test
    fun `nearestAvailableLetter returns the closest letter by alphabet distance`() {
        assertEquals('F', nearestAvailableLetter('D', setOf('A', 'F', 'Z')))
    }

    @Test
    fun `nearestAvailableLetter breaks ties toward the earlier candidate`() {
        // 'B' is equidistant from 'A' and 'C'; minByOrNull keeps the first minimum encountered.
        assertEquals('A', nearestAvailableLetter('B', setOf('A', 'C')))
    }

    @Test
    fun `nearestAvailableLetter returns null when nothing is available`() {
        assertNull(nearestAvailableLetter('M', emptySet()))
    }
}
