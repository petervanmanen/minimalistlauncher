package nl.petervanmanen.minimalauncher.data.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SlippyMapTest {

    private val delta = 0.001

    @Test
    fun `equator prime meridian at zoom 1 lands on the center tile boundary`() {
        val position = SlippyMap.locate(longitude = 0.0, latitude = 0.0, zoom = 1, gridRadius = 1, tileSizePx = 256)

        assertEquals(1, position.centerTileX)
        assertEquals(1, position.centerTileY)
        assertEquals(256.0, position.markerOffsetXPx, delta)
        assertEquals(256.0, position.markerOffsetYPx, delta)
    }

    @Test
    fun `west edge of the map at zoom 1 is tile column 0`() {
        val position = SlippyMap.locate(longitude = -180.0, latitude = 0.0, zoom = 1, gridRadius = 1, tileSizePx = 256)

        assertEquals(0, position.centerTileX)
    }

    @Test
    fun `east edge of the map at zoom 1 is tile column 2`() {
        val position = SlippyMap.locate(longitude = 180.0, latitude = 0.0, zoom = 1, gridRadius = 1, tileSizePx = 256)

        assertEquals(2, position.centerTileX)
    }

    @Test
    fun `marker sits centered in the grid when exactly on a tile fractional midpoint`() {
        // Halfway across tile column 1 of 2 at zoom 1 (i.e. longitude 90) has a 0.5 fractional x offset.
        val position = SlippyMap.locate(longitude = 90.0, latitude = 0.0, zoom = 1, gridRadius = 1, tileSizePx = 256)

        assertEquals(1, position.centerTileX)
        assertEquals(384.0, position.markerOffsetXPx, delta) // (0.5 + gridRadius) * tileSize = 1.5 * 256
    }

    @Test
    fun `higher zoom yields a proportionally larger tile grid`() {
        val zoom1 = SlippyMap.locate(longitude = 0.0, latitude = 0.0, zoom = 1, gridRadius = 1, tileSizePx = 256)
        val zoom2 = SlippyMap.locate(longitude = 0.0, latitude = 0.0, zoom = 2, gridRadius = 1, tileSizePx = 256)

        // Doubling zoom doubles the tile grid resolution, so the center tile index roughly doubles too.
        assertEquals(zoom1.centerTileX * 2, zoom2.centerTileX)
        assertEquals(zoom1.centerTileY * 2, zoom2.centerTileY)
    }
}
