package nl.petervanmanen.minimalauncher.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoResponse(
    @SerialName("current_weather") val currentWeather: CurrentWeatherDto? = null,
)

@Serializable
data class CurrentWeatherDto(
    val temperature: Double,
    val weathercode: Int,
)
