package nl.petervanmanen.minimalauncher.data.model

import android.app.PendingIntent
import kotlinx.serialization.Serializable

/** An app installed on the device that can be launched. */
data class InstalledApp(
    val packageName: String,
    val label: String,
)

/** An app placed on a dock page, with an optional user-chosen display name. */
@Serializable
data class DockApp(
    val packageName: String,
    val displayName: String,
)

@Serializable
data class DockPage(
    val apps: List<DockApp> = emptyList(),
)

@Serializable
data class DockConfig(
    val pages: List<DockPage> = listOf(DockPage()),
)

data class WeatherInfo(
    val temperatureCelsius: Double,
    val description: String,
    val locationName: String?,
)

data class NotificationEntry(
    val key: String,
    val packageName: String,
    val appLabel: String,
    val title: String,
    val text: String,
    val postTime: Long,
    val contentIntent: PendingIntent?,
)
