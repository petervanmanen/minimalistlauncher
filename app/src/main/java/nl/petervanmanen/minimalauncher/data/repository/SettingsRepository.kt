package nl.petervanmanen.minimalauncher.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.petervanmanen.minimalauncher.data.datastore.launcherDataStore

private val ALLOW_ROTATION_KEY = booleanPreferencesKey("allow_rotation")

/** Whether the launcher may rotate with the device — off by default. */
class SettingsRepository(context: Context) {
    private val appContext = context.applicationContext

    val allowRotation: Flow<Boolean> = appContext.launcherDataStore.data.map { it[ALLOW_ROTATION_KEY] ?: false }

    suspend fun setAllowRotation(allow: Boolean) {
        appContext.launcherDataStore.edit { it[ALLOW_ROTATION_KEY] = allow }
    }
}
