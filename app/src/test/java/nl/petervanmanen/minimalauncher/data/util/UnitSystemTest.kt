package nl.petervanmanen.minimalauncher.data.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UnitSystemTest {

    @Test
    fun `metric distances under 1000m are shown in meters`() {
        assertEquals("100 m", formatDistance(100f, UnitSystem.METRIC))
        assertEquals("999 m", formatDistance(999f, UnitSystem.METRIC))
    }

    @Test
    fun `metric distances of 1000m or more are shown in kilometers with one decimal`() {
        assertEquals("1.0 km", formatDistance(1000f, UnitSystem.METRIC))
        assertEquals("2.5 km", formatDistance(2500f, UnitSystem.METRIC))
        assertEquals("1000.0 km", formatDistance(1_000_000f, UnitSystem.METRIC))
    }

    @Test
    fun `imperial distances under 1000ft are shown in feet`() {
        // 100m = ~328ft
        assertEquals("328 ft", formatDistance(100f, UnitSystem.IMPERIAL))
    }

    @Test
    fun `imperial distances of 1000ft or more are shown in miles with one decimal`() {
        // 1609.34m is exactly one mile
        assertEquals("1.0 mi", formatDistance(1609.34f, UnitSystem.IMPERIAL))
    }
}
