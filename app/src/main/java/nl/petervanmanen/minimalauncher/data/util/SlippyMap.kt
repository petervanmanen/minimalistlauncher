package nl.petervanmanen.minimalauncher.data.util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.tan

/** Standard slippy-map (OSM) tile math: https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames */
object SlippyMap {

    /** Meters per pixel at zoom 0, at the equator: (Earth's circumference in meters) / 256px tile. */
    private const val EQUATORIAL_METERS_PER_PIXEL_AT_ZOOM_0 = 40_075_016.686 / 256.0

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

    /**
     * The integer OSM zoom level whose composite image (of [compositeSizePx] pixels
     * square) shows closest to [spanMeters] across, at [latitudeDegrees]. Web Mercator
     * distorts scale by latitude (`cos(lat)`), so the same zoom shows a smaller real-world
     * span farther from the equator — inherent to the projection, not adjustable here.
     */
    fun zoomForSpan(
        spanMeters: Double,
        latitudeDegrees: Double,
        compositeSizePx: Int,
        minZoom: Int = 0,
        maxZoom: Int = 19,
    ): Int {
        val metersPerPixelWanted = spanMeters / compositeSizePx
        val latRad = Math.toRadians(latitudeDegrees)
        val zoomExact = ln(EQUATORIAL_METERS_PER_PIXEL_AT_ZOOM_0 * cos(latRad) / metersPerPixelWanted) / ln(2.0)
        return zoomExact.roundToInt().coerceIn(minZoom, maxZoom)
    }
}
