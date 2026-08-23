package nl.petervanmanen.minimalauncher.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import nl.petervanmanen.minimalauncher.data.util.launchApp
import nl.petervanmanen.minimalauncher.location.hasLocationPermission
import nl.petervanmanen.minimalauncher.ui.components.AppPickerScreen

private enum class DashboardLink { MAPS, WEATHER, DATE }

@SuppressLint("MissingPermission")
@Composable
fun DashboardScreen(viewModel: DashboardViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasLocationPermission by remember { mutableStateOf(hasLocationPermission(context)) }
    var isNotificationAccessGranted by remember { mutableStateOf(viewModel.isNotificationAccessGranted()) }
    var configuringLink by remember { mutableStateOf<DashboardLink?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> hasLocationPermission = granted }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasLocationPermission = hasLocationPermission(context)
                isNotificationAccessGranted = viewModel.isNotificationAccessGranted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) viewModel.refreshLocationData()
    }

    val weather by viewModel.weather.collectAsState()
    val mapSnapshot by viewModel.mapSnapshot.collectAsState()
    val isLoadingLocationData by viewModel.isLoadingLocationData.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val mapsAppPackage by viewModel.mapsAppPackage.collectAsState()
    val weatherAppPackage by viewModel.weatherAppPackage.collectAsState()
    val dateAppPackage by viewModel.dateAppPackage.collectAsState()
    val showWeather by viewModel.showWeather.collectAsState()
    val showMap by viewModel.showMap.collectAsState()
    val showDate by viewModel.showDate.collectAsState()

    configuringLink?.let { link ->
        AppPickerScreen(
            apps = installedApps,
            onAppSelected = { app ->
                when (link) {
                    DashboardLink.MAPS -> viewModel.setMapsApp(app.packageName)
                    DashboardLink.WEATHER -> viewModel.setWeatherApp(app.packageName)
                    DashboardLink.DATE -> viewModel.setDateApp(app.packageName)
                }
                configuringLink = null
            },
            onBack = { configuringLink = null },
        )
        return
    }

    var isFirstSection = true

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 56.dp),
    ) {
        if (showDate) {
            isFirstSection = false
            DateTimeSection(
                onClick = {
                    val pkg = dateAppPackage
                    if (pkg != null) launchApp(context, pkg) else configuringLink = DashboardLink.DATE
                },
                onLongClick = { configuringLink = DashboardLink.DATE },
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }

        if (showWeather) {
            if (!isFirstSection) Spacer(modifier = Modifier.height(24.dp))
            isFirstSection = false
            WeatherSection(
                hasLocationPermission = hasLocationPermission,
                weather = weather,
                isLoading = isLoadingLocationData,
                onRequestLocationPermission = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                },
                onClick = {
                    val pkg = weatherAppPackage
                    if (pkg != null) launchApp(context, pkg) else configuringLink = DashboardLink.WEATHER
                },
                onLongClick = { configuringLink = DashboardLink.WEATHER },
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }

        if (showMap) {
            if (!isFirstSection) Spacer(modifier = Modifier.height(24.dp))
            isFirstSection = false
            MapSection(
                hasLocationPermission = hasLocationPermission,
                mapSnapshot = mapSnapshot,
                isLoading = isLoadingLocationData,
                onClick = {
                    val pkg = mapsAppPackage
                    if (pkg != null) launchApp(context, pkg) else configuringLink = DashboardLink.MAPS
                },
                onLongClick = { configuringLink = DashboardLink.MAPS },
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }

        if (!isFirstSection) Spacer(modifier = Modifier.height(24.dp))

        NotificationsSection(
            isAccessGranted = isNotificationAccessGranted,
            notifications = notifications,
            onEnableAccess = {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            },
            onNotificationClick = { entry -> viewModel.launchNotification(entry) },
            onNotificationDismiss = { entry -> viewModel.dismissNotification(entry) },
            modifier = Modifier.weight(1f).padding(horizontal = 32.dp),
        )
    }
}
