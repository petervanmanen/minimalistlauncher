package nl.petervanmanen.minimalauncher.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.WeatherInfo
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite

/**
 * Tapping opens the configured weather app (or the picker, if none is set
 * yet); long-press always opens the picker so the choice can be changed.
 */
@Composable
fun WeatherSection(
    hasLocationPermission: Boolean,
    weather: WeatherInfo?,
    isLoading: Boolean,
    onRequestLocationPermission: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!hasLocationPermission) {
        Text(
            text = "Enable location",
            style = MaterialTheme.typography.bodyLarge,
            color = DimWhite,
            modifier = modifier.clickable(onClick = onRequestLocationPermission),
        )
        return
    }

    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
            onLongClick = onLongClick,
        ),
    ) {
        when {
            isLoading && weather == null -> Text(
                text = "…",
                style = MaterialTheme.typography.bodyLarge,
                color = DimWhite,
            )
            weather != null -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WeatherIcon(iconType = weather.iconType, isDay = weather.isDay)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${weather.temperatureCelsius.toInt()}° · ${weather.description}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                weather.locationName?.let {
                    Text(text = it, color = DimWhite, style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> Text(
                text = "Weather unavailable",
                style = MaterialTheme.typography.bodyLarge,
                color = DimWhite,
            )
        }
    }
}
