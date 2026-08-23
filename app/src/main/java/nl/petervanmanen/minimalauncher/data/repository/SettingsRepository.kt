package nl.petervanmanen.minimalauncher.data.repository

import android.content.Context
import android.graphics.Color
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.petervanmanen.minimalauncher.data.datastore.launcherDataStore
import nl.petervanmanen.minimalauncher.data.remote.MapLayer

private val ALLOW_ROTATION_KEY = booleanPreferencesKey("allow_rotation")
private val SHOW_WEATHER_KEY = booleanPreferencesKey("show_weather")
private val SHOW_MAP_KEY = booleanPreferencesKey("show_map")
private val SHOW_DATE_KEY = booleanPreferencesKey("show_date")
private val HIDE_STATUS_BAR_KEY = booleanPreferencesKey("hide_status_bar")
private val NOTIFICATION_BUBBLE_ENABLED_KEY = booleanPreferencesKey("notification_bubble_enabled")
private val NOTIFICATION_BUBBLE_COLOR_KEY = intPreferencesKey("notification_bubble_color")
private val MAP_SPAN_METERS_KEY = floatPreferencesKey("map_span_meters")
private val MAP_LAYER_KEY = stringPreferencesKey("map_layer")
private val MAP_COLOR_ENABLED_KEY = booleanPreferencesKey("map_color_enabled")

/** Default notification bubble color (a plain red), also used as the fallback before the setting loads. */
val DEFAULT_NOTIFICATION_BUBBLE_COLOR: Int = Color.parseColor("#FF3B30")

/** Roughly what the map showed before its zoom level became configurable. */
const val DEFAULT_MAP_SPAN_METERS = 1100f

/** Whether the launcher may rotate with the device (off by default), and which dashboard sections are shown. */
class SettingsRepository(context: Context) {
    private val appContext = context.applicationContext

    val allowRotation: Flow<Boolean> = appContext.launcherDataStore.data.map { it[ALLOW_ROTATION_KEY] ?: false }
    val showWeather: Flow<Boolean> = appContext.launcherDataStore.data.map { it[SHOW_WEATHER_KEY] ?: true }
    val showMap: Flow<Boolean> = appContext.launcherDataStore.data.map { it[SHOW_MAP_KEY] ?: true }
    val showDate: Flow<Boolean> = appContext.launcherDataStore.data.map { it[SHOW_DATE_KEY] ?: true }
    val hideStatusBar: Flow<Boolean> = appContext.launcherDataStore.data.map { it[HIDE_STATUS_BAR_KEY] ?: true }
    val notificationBubbleEnabled: Flow<Boolean> =
        appContext.launcherDataStore.data.map { it[NOTIFICATION_BUBBLE_ENABLED_KEY] ?: false }
    val notificationBubbleColor: Flow<Int> =
        appContext.launcherDataStore.data.map { it[NOTIFICATION_BUBBLE_COLOR_KEY] ?: DEFAULT_NOTIFICATION_BUBBLE_COLOR }
    val mapSpanMeters: Flow<Float> =
        appContext.launcherDataStore.data.map { it[MAP_SPAN_METERS_KEY] ?: DEFAULT_MAP_SPAN_METERS }
    val mapLayer: Flow<MapLayer> =
        appContext.launcherDataStore.data.map { MapLayer.fromId(it[MAP_LAYER_KEY]) }
    val mapColorEnabled: Flow<Boolean> =
        appContext.launcherDataStore.data.map { it[MAP_COLOR_ENABLED_KEY] ?: false }

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

    suspend fun setHideStatusBar(hide: Boolean) {
        appContext.launcherDataStore.edit { it[HIDE_STATUS_BAR_KEY] = hide }
    }

    suspend fun setNotificationBubbleEnabled(enabled: Boolean) {
        appContext.launcherDataStore.edit { it[NOTIFICATION_BUBBLE_ENABLED_KEY] = enabled }
    }

    suspend fun setNotificationBubbleColor(color: Int) {
        appContext.launcherDataStore.edit { it[NOTIFICATION_BUBBLE_COLOR_KEY] = color }
    }

    suspend fun setMapSpanMeters(spanMeters: Float) {
        appContext.launcherDataStore.edit { it[MAP_SPAN_METERS_KEY] = spanMeters }
    }

    suspend fun setMapLayer(layer: MapLayer) {
        appContext.launcherDataStore.edit { it[MAP_LAYER_KEY] = layer.name }
    }

    suspend fun setMapColorEnabled(enabled: Boolean) {
        appContext.launcherDataStore.edit { it[MAP_COLOR_ENABLED_KEY] = enabled }
    }
}
