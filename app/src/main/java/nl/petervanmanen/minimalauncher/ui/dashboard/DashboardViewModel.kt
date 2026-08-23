package nl.petervanmanen.minimalauncher.ui.dashboard

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.data.model.MapSnapshot
import nl.petervanmanen.minimalauncher.data.model.NotificationEntry
import nl.petervanmanen.minimalauncher.data.model.WeatherInfo
import nl.petervanmanen.minimalauncher.data.repository.MapRepository
import nl.petervanmanen.minimalauncher.data.repository.NotificationRepository
import nl.petervanmanen.minimalauncher.data.repository.WeatherRepository
import nl.petervanmanen.minimalauncher.location.LocationProvider

class DashboardViewModel(
    private val locationProvider: LocationProvider,
    private val weatherRepository: WeatherRepository,
    private val mapRepository: MapRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _weather = MutableStateFlow<WeatherInfo?>(null)
    val weather: StateFlow<WeatherInfo?> = _weather.asStateFlow()

    private val _mapSnapshot = MutableStateFlow<MapSnapshot?>(null)
    val mapSnapshot: StateFlow<MapSnapshot?> = _mapSnapshot.asStateFlow()

    private val _isLoadingLocationData = MutableStateFlow(false)
    val isLoadingLocationData: StateFlow<Boolean> = _isLoadingLocationData.asStateFlow()

    val notifications: StateFlow<List<NotificationEntry>> = notificationRepository.notifications

    fun isNotificationAccessGranted(): Boolean = notificationRepository.isListenerConnected()

    /** One location fix, shared by weather and the map so we don't request GPS twice. */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun refreshLocationData() {
        viewModelScope.launch {
            _isLoadingLocationData.value = true
            val location = locationProvider.getCurrentLocation()
            if (location != null) {
                _weather.value = weatherRepository.getCurrentWeather(location)
                _mapSnapshot.value = mapRepository.getLocationMap(location)
            }
            _isLoadingLocationData.value = false
        }
    }

    fun launchNotification(entry: NotificationEntry) = notificationRepository.launch(entry)

    fun dismissNotification(entry: NotificationEntry) = notificationRepository.dismiss(entry)
}
