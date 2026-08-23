package nl.petervanmanen.minimalauncher.data.repository

import nl.petervanmanen.minimalauncher.data.model.DockApp
import nl.petervanmanen.minimalauncher.data.model.DockConfig
import nl.petervanmanen.minimalauncher.data.model.DockPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private fun app(pkg: String, name: String = pkg) = DockApp(packageName = pkg, displayName = name)

class DockConfigOperationsTest {

    @Test
    fun `addAppToPage appends to the target page only`() {
        val config = DockConfig(pages = listOf(DockPage(), DockPage()))

        val result = DockConfigOperations.addAppToPage(config, pageIndex = 1, app = app("a"))

        assertTrue(result.pages[0].apps.isEmpty())
        assertEquals(listOf(app("a")), result.pages[1].apps)
    }

    @Test
    fun `addAppToPage is a no-op if the app is already on that page`() {
        val config = DockConfig(pages = listOf(DockPage(apps = listOf(app("a")))))

        val result = DockConfigOperations.addAppToPage(config, pageIndex = 0, app = app("a", "renamed"))

        assertEquals(listOf(app("a")), result.pages[0].apps)
    }

    @Test
    fun `removeApp removes it from whichever page it's on`() {
        val config = DockConfig(pages = listOf(DockPage(apps = listOf(app("a"), app("b"))), DockPage(apps = listOf(app("c")))))

        val result = DockConfigOperations.removeApp(config, "b")

        assertEquals(listOf(app("a")), result.pages[0].apps)
        assertEquals(listOf(app("c")), result.pages[1].apps)
    }

    @Test
    fun `removePackages removes every matching app across all pages`() {
        val config = DockConfig(pages = listOf(DockPage(apps = listOf(app("a"), app("b"))), DockPage(apps = listOf(app("b"), app("c")))))

        val result = DockConfigOperations.removePackages(config, setOf("b", "c"))

        assertEquals(listOf(app("a")), result.pages[0].apps)
        assertTrue(result.pages[1].apps.isEmpty())
    }

    @Test
    fun `renameApp changes only the matching app's display name`() {
        val config = DockConfig(pages = listOf(DockPage(apps = listOf(app("a", "old"), app("b", "keep")))))

        val result = DockConfigOperations.renameApp(config, "a", "new")

        assertEquals("new", result.pages[0].apps[0].displayName)
        assertEquals("keep", result.pages[0].apps[1].displayName)
    }

    @Test
    fun `reorderWithinPage moves an app to the target index`() {
        val config = DockConfig(pages = listOf(DockPage(apps = listOf(app("a"), app("b"), app("c")))))

        val result = DockConfigOperations.reorderWithinPage(config, pageIndex = 0, fromIndex = 0, toIndex = 2)

        assertEquals(listOf(app("b"), app("c"), app("a")), result.pages[0].apps)
    }

    @Test
    fun `reorderWithinPage ignores out-of-range indices instead of crashing`() {
        val config = DockConfig(pages = listOf(DockPage(apps = listOf(app("a"), app("b")))))

        val result = DockConfigOperations.reorderWithinPage(config, pageIndex = 0, fromIndex = 0, toIndex = 5)

        assertEquals(listOf(app("a"), app("b")), result.pages[0].apps)
    }

    @Test
    fun `addPage appends a new empty page`() {
        val config = DockConfig(pages = listOf(DockPage(apps = listOf(app("a")))))

        val result = DockConfigOperations.addPage(config)

        assertEquals(2, result.pages.size)
        assertTrue(result.pages[1].apps.isEmpty())
    }

    @Test
    fun `removePage drops the page at that index`() {
        val config = DockConfig(pages = listOf(DockPage(apps = listOf(app("a"))), DockPage()))

        val result = DockConfigOperations.removePage(config, pageIndex = 1)

        assertEquals(1, result.pages.size)
        assertEquals(listOf(app("a")), result.pages[0].apps)
    }

    @Test
    fun `removePage refuses to remove the last remaining page`() {
        val config = DockConfig(pages = listOf(DockPage(apps = listOf(app("a")))))

        val result = DockConfigOperations.removePage(config, pageIndex = 0)

        assertEquals(1, result.pages.size)
    }
}
