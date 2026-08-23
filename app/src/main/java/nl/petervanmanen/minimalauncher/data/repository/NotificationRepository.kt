package nl.petervanmanen.minimalauncher.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.flow.StateFlow
import nl.petervanmanen.minimalauncher.data.model.NotificationEntry
import nl.petervanmanen.minimalauncher.notification.NotificationBridge

class NotificationRepository(private val context: Context) {

    val notifications: StateFlow<List<NotificationEntry>> = NotificationBridge.notifications

    fun isListenerConnected(): Boolean =
        NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)

    /** Opens the notification's own destination, falling back to just launching its app. */
    fun launch(entry: NotificationEntry) {
        // entry.contentIntent?.send() would report success even when contentIntent is
        // null (the ?. short-circuits without throwing), silently skipping the fallback.
        val sentOwnIntent = entry.contentIntent?.let { runCatching { it.send() }.isSuccess } ?: false
        if (!sentOwnIntent) {
            context.packageManager.getLaunchIntentForPackage(entry.packageName)?.let { launchIntent ->
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            }
        }
    }
}
