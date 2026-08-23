package nl.petervanmanen.minimalauncher.data.repository

import android.location.Location
import nl.petervanmanen.minimalauncher.data.model.WeatherInfo
import nl.petervanmanen.minimalauncher.data.remote.OpenMeteoApi
import nl.petervanmanen.minimalauncher.data.remote.describeWeatherCode
import nl.petervanmanen.minimalauncher.location.LocationProvider

class WeatherRepository(
    private val locationProvider: LocationProvider,
    private val api: OpenMeteoApi = OpenMeteoApi(),
) {
    suspend fun getCurrentWeather(location: Location): WeatherInfo? {
        val response = runCatching {
            api.getCurrentWeather(location.latitude, location.longitude)
        }.getOrNull() ?: return null
        val current = response.currentWeather ?: return null
        return WeatherInfo(
            temperatureCelsius = current.temperature,
            description = describeWeatherCode(current.weathercode),
            locationName = locationProvider.reverseGeocode(location.latitude, location.longitude),
        )
    }
}
