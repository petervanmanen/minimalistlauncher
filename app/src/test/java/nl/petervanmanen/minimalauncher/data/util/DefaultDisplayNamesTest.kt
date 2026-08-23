package nl.petervanmanen.minimalauncher.data.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultDisplayNamesTest {

    @Test
    fun `spotify defaults to Music`() {
        assertEquals("Music", defaultDisplayNameFor("com.spotify.music", "Spotify"))
    }

    @Test
    fun `google maps defaults to Navigation`() {
        assertEquals("Navigation", defaultDisplayNameFor("com.google.android.apps.maps", "Maps"))
    }

    @Test
    fun `unknown package falls back to its own label`() {
        assertEquals("Chrome", defaultDisplayNameFor("com.android.chrome", "Chrome"))
    }
}
