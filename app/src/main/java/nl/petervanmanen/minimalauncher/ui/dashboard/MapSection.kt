package nl.petervanmanen.minimalauncher.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.MapSnapshot
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

private val MAP_SIZE = 220.dp

@Composable
fun MapSection(
    hasLocationPermission: Boolean,
    mapSnapshot: MapSnapshot?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!hasLocationPermission) return

    Box(
        modifier = modifier
            .size(MAP_SIZE)
            .border(1.dp, DimWhite),
        contentAlignment = Alignment.Center,
    ) {
        when {
            mapSnapshot != null -> {
                val bitmap = mapSnapshot.bitmap
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(MAP_SIZE),
                    contentScale = ContentScale.Crop,
                )
                Canvas(modifier = Modifier.size(MAP_SIZE)) {
                    val scaleX = size.width / bitmap.width
                    val scaleY = size.height / bitmap.height
                    drawCircle(
                        color = PureWhite,
                        radius = 6.dp.toPx(),
                        center = Offset(
                            mapSnapshot.markerOffsetPx.x * scaleX,
                            mapSnapshot.markerOffsetPx.y * scaleY,
                        ),
                    )
                }
            }
            isLoading -> Text("…", style = MaterialTheme.typography.bodyLarge, color = DimWhite)
            else -> Text("Map unavailable", style = MaterialTheme.typography.bodyLarge, color = DimWhite)
        }
    }
}
