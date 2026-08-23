package nl.petervanmanen.minimalauncher.notification

import android.app.Notification
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import nl.petervanmanen.minimalauncher.data.model.NotificationEntry

/** OS/System UI chrome — status messages, not app notifications a user would tap through to. */
private val SYSTEM_PACKAGES = setOf("android", "com.android.systemui")

class NotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        publish()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        publish()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        publish()
    }

    private fun publish() {
        val entries = runCatching { activeNotifications }.getOrNull()
            ?.filterNot {
                it.isOngoing ||
                    it.packageName in SYSTEM_PACKAGES ||
                    (it.notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0
            }
            ?.mapNotNull(::toEntry)
            ?.sortedByDescending { it.postTime }
            .orEmpty()
        NotificationBridge.update(entries)
    }

    private fun toEntry(sbn: StatusBarNotification): NotificationEntry? {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return null
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        return NotificationEntry(
            key = sbn.key,
            packageName = sbn.packageName,
            appLabel = appLabelFor(sbn.packageName),
            title = title,
            text = text,
            postTime = sbn.postTime,
            contentIntent = sbn.notification.contentIntent,
        )
    }

    private fun appLabelFor(packageName: String): String = runCatching {
        val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getApplicationInfo(packageName, 0)
        }
        packageManager.getApplicationLabel(appInfo).toString()
    }.getOrDefault(packageName)
}
