package nl.petervanmanen.minimalauncher.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherCodeMapperTest {

    @Test
    fun `describeWeatherCode covers every documented WMO code`() {
        val expected = mapOf(
            0 to "Clear sky",
            1 to "Mainly clear",
            2 to "Partly cloudy",
            3 to "Overcast",
            45 to "Fog",
            48 to "Fog",
            51 to "Drizzle",
            53 to "Drizzle",
            55 to "Drizzle",
            56 to "Freezing drizzle",
            57 to "Freezing drizzle",
            61 to "Rain",
            63 to "Rain",
            65 to "Rain",
            66 to "Freezing rain",
            67 to "Freezing rain",
            71 to "Snow",
            73 to "Snow",
            75 to "Snow",
            77 to "Snow",
            80 to "Rain showers",
            81 to "Rain showers",
            82 to "Rain showers",
            85 to "Snow showers",
            86 to "Snow showers",
            95 to "Thunderstorm",
            96 to "Thunderstorm with hail",
            99 to "Thunderstorm with hail",
        )
        expected.forEach { (code, description) ->
            assertEquals("code $code", description, describeWeatherCode(code))
        }
    }

    @Test
    fun `unrecognized code describes as Unknown`() {
        assertEquals("Unknown", describeWeatherCode(-1))
        assertEquals("Unknown", describeWeatherCode(1000))
    }

    @Test
    fun `weatherIconType groups codes into drawable shapes`() {
        val expected = mapOf(
            0 to WeatherIconType.CLEAR,
            1 to WeatherIconType.CLEAR,
            2 to WeatherIconType.PARTLY_CLOUDY,
            3 to WeatherIconType.CLOUDY,
            45 to WeatherIconType.FOG,
            48 to WeatherIconType.FOG,
            51 to WeatherIconType.RAIN,
            56 to WeatherIconType.RAIN,
            61 to WeatherIconType.RAIN,
            66 to WeatherIconType.RAIN,
            80 to WeatherIconType.RAIN,
            71 to WeatherIconType.SNOW,
            85 to WeatherIconType.SNOW,
            95 to WeatherIconType.THUNDERSTORM,
            96 to WeatherIconType.THUNDERSTORM,
            99 to WeatherIconType.THUNDERSTORM,
        )
        expected.forEach { (code, iconType) ->
            assertEquals("code $code", iconType, weatherIconType(code))
        }
    }

    @Test
    fun `unrecognized code falls back to cloudy rather than crashing`() {
        assertEquals(WeatherIconType.CLOUDY, weatherIconType(-1))
        assertEquals(WeatherIconType.CLOUDY, weatherIconType(1000))
    }
}
