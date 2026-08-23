package nl.petervanmanen.minimalauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.ln
import kotlin.math.pow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.data.remote.MapLayer
import nl.petervanmanen.minimalauncher.data.util.UnitSystem
import nl.petervanmanen.minimalauncher.data.util.currentUnitSystem
import nl.petervanmanen.minimalauncher.data.util.formatDistance
import nl.petervanmanen.minimalauncher.data.util.setBlackWallpaper
import nl.petervanmanen.minimalauncher.ui.components.AppPickerScreen
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

private enum class WallpaperStatus { IDLE, SETTING, DONE, FAILED }
private enum class AppLink { MAPS, WEATHER, DATE }

private val BUBBLE_COLOR_PALETTE = listOf(
    0xFFFF3B30.toInt(), // red
    0xFFFF9500.toInt(), // orange
    0xFFFFCC00.toInt(), // yellow
    0xFF34C759.toInt(), // green
    0xFF007AFF.toInt(), // blue
    0xFFAF52DE.toInt(), // purple
)

private const val MIN_MAP_SPAN_METERS = 100f
private const val MAX_MAP_SPAN_METERS = 1_000_000f

private fun spanToSliderPosition(spanMeters: Float): Float {
    val t = ln(spanMeters / MIN_MAP_SPAN_METERS) / ln(MAX_MAP_SPAN_METERS / MIN_MAP_SPAN_METERS)
    return t.coerceIn(0f, 1f)
}

private fun sliderPositionToSpan(position: Float): Float =
    MIN_MAP_SPAN_METERS * (MAX_MAP_SPAN_METERS / MIN_MAP_SPAN_METERS).pow(position)

@Composable
fun SettingsScreen(
    allowRotation: Boolean,
    onAllowRotationChange: (Boolean) -> Unit,
    showWeather: Boolean,
    onShowWeatherChange: (Boolean) -> Unit,
    showMap: Boolean,
    onShowMapChange: (Boolean) -> Unit,
    mapSpanMeters: Float,
    onMapSpanMetersChange: (Float) -> Unit,
    mapLayer: MapLayer,
    onMapLayerChange: (MapLayer) -> Unit,
    mapColorEnabled: Boolean,
    onMapColorEnabledChange: (Boolean) -> Unit,
    showDate: Boolean,
    onShowDateChange: (Boolean) -> Unit,
    hideStatusBar: Boolean,
    onHideStatusBarChange: (Boolean) -> Unit,
    notificationBubbleEnabled: Boolean,
    onNotificationBubbleEnabledChange: (Boolean) -> Unit,
    notificationBubbleColor: Int,
    onNotificationBubbleColorChange: (Int) -> Unit,
    installedApps: List<InstalledApp>,
    mapsAppPackage: String?,
    onMapsAppChange: (InstalledApp) -> Unit,
    weatherAppPackage: String?,
    onWeatherAppChange: (InstalledApp) -> Unit,
    dateAppPackage: String?,
    onDateAppChange: (InstalledApp) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var wallpaperStatus by remember { mutableStateOf(WallpaperStatus.IDLE) }
    var configuringLink by remember { mutableStateOf<AppLink?>(null) }

    configuringLink?.let { link ->
        AppPickerScreen(
            apps = installedApps,
            onAppSelected = { app ->
                when (link) {
                    AppLink.MAPS -> onMapsAppChange(app)
                    AppLink.WEATHER -> onWeatherAppChange(app)
                    AppLink.DATE -> onDateAppChange(app)
                }
                configuringLink = null
            },
            onBack = { configuringLink = null },
        )
        return
    }

    fun labelFor(packageName: String?): String =
        installedApps.firstOrNull { it.packageName == packageName }?.label ?: "Not set"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Text(
            text = "‹ Settings",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable(onClick = onBack),
        )

        Spacer(modifier = Modifier.height(32.dp))

        ToggleRow("Rotate with device", allowRotation, onAllowRotationChange)
        Spacer(modifier = Modifier.height(20.dp))
        ToggleRow("Show weather", showWeather, onShowWeatherChange)
        Spacer(modifier = Modifier.height(20.dp))
        ToggleRow("Show map", showMap, onShowMapChange)
        Spacer(modifier = Modifier.height(20.dp))
        MapSpanSlider(mapSpanMeters, currentUnitSystem(context), onMapSpanMetersChange)
        Spacer(modifier = Modifier.height(20.dp))
        MapLayerPicker(mapLayer, onMapLayerChange)
        Spacer(modifier = Modifier.height(20.dp))
        ToggleRow("Map color", mapColorEnabled, onMapColorEnabledChange)
        Spacer(modifier = Modifier.height(20.dp))
        ToggleRow("Show date", showDate, onShowDateChange)
        Spacer(modifier = Modifier.height(20.dp))
        ToggleRow("Hide status bar", hideStatusBar, onHideStatusBarChange)
        Spacer(modifier = Modifier.height(20.dp))
        ToggleRow("Notification bubble", notificationBubbleEnabled, onNotificationBubbleEnabledChange)
        Spacer(modifier = Modifier.height(20.dp))
        ColorPaletteRow(notificationBubbleColor, onNotificationBubbleColorChange)

        Spacer(modifier = Modifier.height(24.dp))

        LinkRow("Maps app", labelFor(mapsAppPackage)) { configuringLink = AppLink.MAPS }
        Spacer(modifier = Modifier.height(20.dp))
        LinkRow("Weather app", labelFor(weatherAppPackage)) { configuringLink = AppLink.WEATHER }
        Spacer(modifier = Modifier.height(20.dp))
        LinkRow("Date app", labelFor(dateAppPackage)) { configuringLink = AppLink.DATE }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = wallpaperStatus != WallpaperStatus.SETTING) {
                    wallpaperStatus = WallpaperStatus.SETTING
                    coroutineScope.launch {
                        wallpaperStatus = if (setBlackWallpaper(context)) WallpaperStatus.DONE else WallpaperStatus.FAILED
                        delay(2000)
                        wallpaperStatus = WallpaperStatus.IDLE
                    }
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Set black wallpaper", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = when (wallpaperStatus) {
                    WallpaperStatus.IDLE -> ""
                    WallpaperStatus.SETTING -> "…"
                    WallpaperStatus.DONE -> "Done"
                    WallpaperStatus.FAILED -> "Failed"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = DimWhite,
            )
        }
    }
}

@Composable
private fun ToggleRow(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChange(!value) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = if (value) "On" else "Off",
            style = MaterialTheme.typography.bodyLarge,
            color = if (value) PureWhite else DimWhite,
        )
    }
}

/** Log-scale slider from 100m to 1000km; only persists the value once the drag ends. */
@Composable
private fun MapSpanSlider(spanMeters: Float, unitSystem: UnitSystem, onSpanChange: (Float) -> Unit) {
    var sliderPosition by remember(spanMeters) { mutableFloatStateOf(spanToSliderPosition(spanMeters)) }
    val liveSpan = sliderPositionToSpan(sliderPosition)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Map area", style = MaterialTheme.typography.bodyLarge)
            Text(text = formatDistance(liveSpan, unitSystem), style = MaterialTheme.typography.bodyLarge, color = DimWhite)
        }
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = { onSpanChange(sliderPositionToSpan(sliderPosition)) },
            colors = SliderDefaults.colors(
                thumbColor = PureWhite,
                activeTrackColor = PureWhite,
                inactiveTrackColor = DimWhite,
            ),
        )
    }
}

@Composable
private fun MapLayerPicker(selectedLayer: MapLayer, onLayerSelected: (MapLayer) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Map layer", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        MapLayer.entries.chunked(2).forEach { rowLayers ->
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                rowLayers.forEach { layer ->
                    Text(
                        text = layer.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (layer == selectedLayer) PureWhite else DimWhite,
                        modifier = Modifier
                            .clickable { onLayerSelected(layer) }
                            .padding(vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPaletteRow(selectedColor: Int, onColorSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BUBBLE_COLOR_PALETTE.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(color), CircleShape)
                    .border(2.dp, if (isSelected) PureWhite else Color.Transparent, CircleShape)
                    .clickable { onColorSelected(color) },
            )
        }
    }
}

@Composable
private fun LinkRow(label: String, currentValue: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = currentValue, style = MaterialTheme.typography.bodyLarge, color = DimWhite)
    }
}
