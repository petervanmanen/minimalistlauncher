package nl.petervanmanen.minimalauncher.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.petervanmanen.minimalauncher.data.datastore.launcherDataStore

private val MAPS_APP_KEY = stringPreferencesKey("maps_app_package")
private val WEATHER_APP_KEY = stringPreferencesKey("weather_app_package")
private val DATE_APP_KEY = stringPreferencesKey("date_app_package")

/** Which app opens when the dashboard's map, weather, or date section is tapped. */
class AppLinkRepository(context: Context) {
    private val appContext = context.applicationContext

    val mapsAppPackage: Flow<String?> = appContext.launcherDataStore.data.map { it[MAPS_APP_KEY] }
    val weatherAppPackage: Flow<String?> = appContext.launcherDataStore.data.map { it[WEATHER_APP_KEY] }
    val dateAppPackage: Flow<String?> = appContext.launcherDataStore.data.map { it[DATE_APP_KEY] }

    suspend fun setMapsApp(packageName: String) {
        appContext.launcherDataStore.edit { it[MAPS_APP_KEY] = packageName }
    }

    suspend fun setWeatherApp(packageName: String) {
        appContext.launcherDataStore.edit { it[WEATHER_APP_KEY] = packageName }
    }

    suspend fun setDateApp(packageName: String) {
        appContext.launcherDataStore.edit { it[DATE_APP_KEY] = packageName }
    }
}
