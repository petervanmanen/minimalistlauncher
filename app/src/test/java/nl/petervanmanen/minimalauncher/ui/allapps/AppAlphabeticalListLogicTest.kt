package nl.petervanmanen.minimalauncher.ui.allapps

import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

private fun app(label: String) = InstalledApp(packageName = label.lowercase(), label = label)

class AppAlphabeticalListLogicTest {

    @Test
    fun `firstIndexOfLetter records the first occurrence of each letter`() {
        val apps = listOf(app("Alarm"), app("Calendar"), app("Camera"), app("Chrome"))

        val index = firstIndexOfLetter(apps)

        assertEquals(0, index['A'])
        assertEquals(1, index['C']) // first C-app ("Calendar"), not the later ones
    }

    @Test
    fun `firstIndexOfLetter is uppercase-normalized regardless of source casing`() {
        val apps = listOf(app("gmail"), app("Google"))

        val index = firstIndexOfLetter(apps)

        assertEquals(0, index['G'])
        assertNull(index['g'])
    }

    @Test
    fun `firstIndexOfLetter omits letters with no matching app`() {
        val apps = listOf(app("Alarm"), app("Zoom"))

        val index = firstIndexOfLetter(apps)

        assertEquals(setOf('A', 'Z'), index.keys)
    }

    @Test
    fun `firstIndexOfLetter on an empty list is empty`() {
        assertEquals(emptyMap<Char, Int>(), firstIndexOfLetter(emptyList()))
    }
}
