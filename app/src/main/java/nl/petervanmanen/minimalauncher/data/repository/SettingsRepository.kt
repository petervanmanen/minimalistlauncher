package nl.petervanmanen.minimalauncher.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.petervanmanen.minimalauncher.data.datastore.launcherDataStore

private val ALLOW_ROTATION_KEY = booleanPreferencesKey("allow_rotation")
private val SHOW_WEATHER_KEY = booleanPreferencesKey("show_weather")
private val SHOW_MAP_KEY = booleanPreferencesKey("show_map")
private val SHOW_DATE_KEY = booleanPreferencesKey("show_date")

/** Whether the launcher may rotate with the device (off by default), and which dashboard sections are shown. */
class SettingsRepository(context: Context) {
    private val appContext = context.applicationContext

    val allowRotation: Flow<Boolean> = appContext.launcherDataStore.data.map { it[ALLOW_ROTATION_KEY] ?: false }
    val showWeather: Flow<Boolean> = appContext.launcherDataStore.data.map { it[SHOW_WEATHER_KEY] ?: true }
    val showMap: Flow<Boolean> = appContext.launcherDataStore.data.map { it[SHOW_MAP_KEY] ?: true }
    val showDate: Flow<Boolean> = appContext.launcherDataStore.data.map { it[SHOW_DATE_KEY] ?: true }

    suspend fun setAllowRotation(allow: Boolean) {
        appContext.launcherDataStore.edit { it[ALLOW_ROTATION_KEY] = allow }
    }

    suspend fun setShowWeather(show: Boolean) {
        appContext.launcherDataStore.edit { it[SHOW_WEATHER_KEY] = show }
    }

    suspend fun setShowMap(show: Boolean) {
        appContext.launcherDataStore.edit { it[SHOW_MAP_KEY] = show }
    }

    suspend fun setShowDate(show: Boolean) {
        appContext.launcherDataStore.edit { it[SHOW_DATE_KEY] = show }
    }
}
