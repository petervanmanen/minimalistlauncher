package nl.petervanmanen.minimalauncher.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.MapSnapshot
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

/**
 * Tapping opens the configured maps app (or the picker, if none is set yet);
 * long-press always opens the picker so the choice can be changed.
 */
@Composable
fun MapSection(
    hasLocationPermission: Boolean,
    mapSnapshot: MapSnapshot?,
    isLoading: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!hasLocationPermission) return

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(1.dp, DimWhite)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            mapSnapshot != null -> {
                val bitmap = mapSnapshot.bitmap
                val greyscale = remember { ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) }
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    contentScale = ContentScale.Crop,
                    colorFilter = greyscale,
                )
                Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
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
