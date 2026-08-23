package nl.petervanmanen.minimalauncher.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import nl.petervanmanen.minimalauncher.data.model.WeatherInfo
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite

@Composable
fun WeatherSection(
    hasLocationPermission: Boolean,
    weather: WeatherInfo?,
    isLoading: Boolean,
    onRequestLocationPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when {
            !hasLocationPermission -> Text(
                text = "Enable location",
                style = MaterialTheme.typography.bodyLarge,
                color = DimWhite,
                modifier = Modifier.clickable(onClick = onRequestLocationPermission),
            )
            isLoading && weather == null -> Text(
                text = "…",
                style = MaterialTheme.typography.bodyLarge,
                color = DimWhite,
            )
            weather != null -> {
                Text(
                    text = "${weather.temperatureCelsius.toInt()}° · ${weather.description}",
                    fontSize = 32.sp,
                )
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
