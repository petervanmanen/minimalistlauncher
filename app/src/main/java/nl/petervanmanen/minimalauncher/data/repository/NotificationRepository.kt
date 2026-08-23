package nl.petervanmanen.minimalauncher.data.repository

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
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
        val sentOwnIntent = entry.contentIntent?.let { pendingIntent ->
            runCatching { pendingIntent.send(backgroundActivityStartOptions()) }.isSuccess
        } ?: false
        if (!sentOwnIntent) {
            context.packageManager.getLaunchIntentForPackage(entry.packageName)?.let { launchIntent ->
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            }
        }
    }

    /** Removes the notification system-wide, as if the user swiped it away in the shade. */
    fun dismiss(entry: NotificationEntry) {
        NotificationBridge.dismiss(entry.key)
    }

    /**
     * Android 14+ can silently drop a [android.app.PendingIntent.send] from a background
     * process without throwing — the target activity just never appears. Since we're only
     * ever sending one in direct response to the user tapping a notification in our own
     * foreground UI, explicitly allowing it here is safe and matches how the system's own
     * notification shade opens a notification's content intent.
     */
    private fun backgroundActivityStartOptions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ActivityOptions.makeBasic()
                .setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
                .toBundle()
        } else {
            null
        }
}
