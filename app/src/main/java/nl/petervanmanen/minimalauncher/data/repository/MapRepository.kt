package nl.petervanmanen.minimalauncher.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import androidx.compose.ui.geometry.Offset
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.petervanmanen.minimalauncher.data.model.MapSnapshot
import nl.petervanmanen.minimalauncher.data.util.SlippyMap

private const val TILE_SIZE = 256
private const val ZOOM = 16
private const val GRID_RADIUS = 1 // 3x3 tiles, your location centered in the middle one

/**
 * Fetches a small stitched map from OpenStreetMap's raw tile server — no API
 * key needed, matching the same zero-config approach as weather. Usage stays
 * well within OSM's tile usage policy for a single personal device (a
 * handful of tiles per manual dashboard refresh, with an identifying
 * User-Agent as their policy requires).
 */
class MapRepository(
    private val client: HttpClient = HttpClient(Android),
) {
    suspend fun getLocationMap(location: Location): MapSnapshot? = withContext(Dispatchers.IO) {
        runCatching {
            val position = SlippyMap.locate(location.longitude, location.latitude, ZOOM, GRID_RADIUS, TILE_SIZE)
            val gridSize = GRID_RADIUS * 2 + 1

            val composite = Bitmap.createBitmap(
                gridSize * TILE_SIZE,
                gridSize * TILE_SIZE,
                Bitmap.Config.ARGB_8888,
            )
            val canvas = Canvas(composite)
            for (dy in -GRID_RADIUS..GRID_RADIUS) {
                for (dx in -GRID_RADIUS..GRID_RADIUS) {
                    val tile = fetchTile(ZOOM, position.centerTileX + dx, position.centerTileY + dy) ?: continue
                    canvas.drawBitmap(
                        tile,
                        ((dx + GRID_RADIUS) * TILE_SIZE).toFloat(),
                        ((dy + GRID_RADIUS) * TILE_SIZE).toFloat(),
                        null,
                    )
                }
            }

            MapSnapshot(
                bitmap = composite,
                markerOffsetPx = Offset(position.markerOffsetXPx.toFloat(), position.markerOffsetYPx.toFloat()),
            )
        }.getOrNull()
    }

    private suspend fun fetchTile(z: Int, x: Int, y: Int): Bitmap? = runCatching {
        val bytes: ByteArray = client.get("https://tile.openstreetmap.org/$z/$x/$y.png") {
            header(HttpHeaders.UserAgent, "MinimalLauncher-Android/1.0 (github.com/petervanmanen/minimalistlauncher)")
        }.body()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()
}
