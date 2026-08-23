package nl.petervanmanen.minimalauncher.ui.dashboard

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.data.model.NotificationEntry
import nl.petervanmanen.minimalauncher.data.model.WeatherInfo
import nl.petervanmanen.minimalauncher.data.repository.NotificationRepository
import nl.petervanmanen.minimalauncher.data.repository.WeatherRepository

class DashboardViewModel(
    private val weatherRepository: WeatherRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _weather = MutableStateFlow<WeatherInfo?>(null)
    val weather: StateFlow<WeatherInfo?> = _weather.asStateFlow()

    private val _isLoadingWeather = MutableStateFlow(false)
    val isLoadingWeather: StateFlow<Boolean> = _isLoadingWeather.asStateFlow()

    val notifications: StateFlow<List<NotificationEntry>> = notificationRepository.notifications

    fun isNotificationAccessGranted(): Boolean = notificationRepository.isListenerConnected()

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun refreshWeather() {
        viewModelScope.launch {
            _isLoadingWeather.value = true
            _weather.value = weatherRepository.getCurrentWeather()
            _isLoadingWeather.value = false
        }
    }

    fun launchNotification(entry: NotificationEntry) = notificationRepository.launch(entry)
}
