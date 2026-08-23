package nl.petervanmanen.minimalauncher.data.repository

import android.content.pm.ApplicationInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppRepositoryTest {

    @Test
    fun `ordinary non-system app is removable`() {
        assertTrue(isRemovable(applicationInfoFlags = 0))
    }

    @Test
    fun `unmodified system app is not removable`() {
        assertFalse(isRemovable(ApplicationInfo.FLAG_SYSTEM))
    }

    @Test
    fun `system app updated via the Play Store is removable`() {
        val flags = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        assertTrue(isRemovable(flags))
    }

    @Test
    fun `updated flag alone without system flag is still removable`() {
        assertTrue(isRemovable(ApplicationInfo.FLAG_UPDATED_SYSTEM_APP))
    }
}
