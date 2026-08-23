package nl.petervanmanen.minimalauncher.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.data.util.setBlackWallpaper
import nl.petervanmanen.minimalauncher.ui.components.AppPickerScreen
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

private enum class WallpaperStatus { IDLE, SETTING, DONE, FAILED }
private enum class AppLink { MAPS, WEATHER, DATE }

@Composable
fun SettingsScreen(
    allowRotation: Boolean,
    onAllowRotationChange: (Boolean) -> Unit,
    showWeather: Boolean,
    onShowWeatherChange: (Boolean) -> Unit,
    showMap: Boolean,
    onShowMapChange: (Boolean) -> Unit,
    showDate: Boolean,
    onShowDateChange: (Boolean) -> Unit,
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
        ToggleRow("Show date", showDate, onShowDateChange)

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
