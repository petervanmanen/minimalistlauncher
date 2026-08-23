package nl.petervanmanen.minimalauncher.data.util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

/** Standard slippy-map (OSM) tile math: https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames */
object SlippyMap {

    data class TilePosition(
        val centerTileX: Int,
        val centerTileY: Int,
        /** Pixel offset of the exact location within a [gridRadius]-tile-wide composite centered on it. */
        val markerOffsetXPx: Double,
        val markerOffsetYPx: Double,
    )

    fun locate(
        longitude: Double,
        latitude: Double,
        zoom: Int,
        gridRadius: Int,
        tileSizePx: Int,
    ): TilePosition {
        val n = 2.0.pow(zoom)
        val xTileExact = (longitude + 180.0) / 360.0 * n
        val latRad = Math.toRadians(latitude)
        val yTileExact = (1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * n

        val centerX = floor(xTileExact).toInt()
        val centerY = floor(yTileExact).toInt()

        return TilePosition(
            centerTileX = centerX,
            centerTileY = centerY,
            markerOffsetXPx = (xTileExact - centerX + gridRadius) * tileSizePx,
            markerOffsetYPx = (yTileExact - centerY + gridRadius) * tileSizePx,
        )
    }
}
