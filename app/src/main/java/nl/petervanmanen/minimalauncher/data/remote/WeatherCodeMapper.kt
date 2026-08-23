package nl.petervanmanen.minimalauncher.data.remote

/** Maps Open-Meteo's WMO weather codes to a short plain-text description. */
fun describeWeatherCode(code: Int): String = when (code) {
    0 -> "Clear sky"
    1 -> "Mainly clear"
    2 -> "Partly cloudy"
    3 -> "Overcast"
    45, 48 -> "Fog"
    51, 53, 55 -> "Drizzle"
    56, 57 -> "Freezing drizzle"
    61, 63, 65 -> "Rain"
    66, 67 -> "Freezing rain"
    71, 73, 75, 77 -> "Snow"
    80, 81, 82 -> "Rain showers"
    85, 86 -> "Snow showers"
    95 -> "Thunderstorm"
    96, 99 -> "Thunderstorm with hail"
    else -> "Unknown"
}

enum class WeatherIconType {
    CLEAR,
    PARTLY_CLOUDY,
    CLOUDY,
    FOG,
    RAIN,
    SNOW,
    THUNDERSTORM,
}

/** Same WMO grouping as [describeWeatherCode], collapsed to a shape we can draw. */
fun weatherIconType(code: Int): WeatherIconType = when (code) {
    0, 1 -> WeatherIconType.CLEAR
    2 -> WeatherIconType.PARTLY_CLOUDY
    3 -> WeatherIconType.CLOUDY
    45, 48 -> WeatherIconType.FOG
    51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> WeatherIconType.RAIN
    71, 73, 75, 77, 85, 86 -> WeatherIconType.SNOW
    95, 96, 99 -> WeatherIconType.THUNDERSTORM
    else -> WeatherIconType.CLOUDY
}
