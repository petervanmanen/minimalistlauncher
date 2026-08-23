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
import nl.petervanmanen.minimalauncher.location.hasLocationPermission

@SuppressLint("MissingPermission")
@Composable
fun DashboardScreen(viewModel: DashboardViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasLocationPermission by remember { mutableStateOf(hasLocationPermission(context)) }
    var isNotificationAccessGranted by remember { mutableStateOf(viewModel.isNotificationAccessGranted()) }

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 56.dp),
    ) {
        WeatherSection(
            hasLocationPermission = hasLocationPermission,
            weather = weather,
            isLoading = isLoadingLocationData,
            onRequestLocationPermission = {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            },
            modifier = Modifier.padding(horizontal = 32.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        MapSection(
            hasLocationPermission = hasLocationPermission,
            mapSnapshot = mapSnapshot,
            isLoading = isLoadingLocationData,
        )

        Spacer(modifier = Modifier.height(24.dp))

        NotificationsSection(
            isAccessGranted = isNotificationAccessGranted,
            notifications = notifications,
            onEnableAccess = {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            },
            onNotificationClick = { entry -> viewModel.launchNotification(entry) },
            modifier = Modifier.weight(1f).padding(horizontal = 32.dp),
        )
    }
}
