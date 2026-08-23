package nl.petervanmanen.minimalauncher.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.petervanmanen.minimalauncher.data.model.NotificationEntry

/**
 * Process-wide bridge between [NotificationListener] (a Service, which can't
 * easily be injected into the UI layer) and the Compose UI observing it.
 */
object NotificationBridge {
    private val _notifications = MutableStateFlow<List<NotificationEntry>>(emptyList())
    val notifications: StateFlow<List<NotificationEntry>> = _notifications.asStateFlow()

    /** Set by the connected [NotificationListener]; dismisses a notification by key, system-wide. */
    private var dismissHandler: ((String) -> Unit)? = null

    fun update(entries: List<NotificationEntry>) {
        _notifications.value = entries
    }

    fun registerDismissHandler(handler: (String) -> Unit) {
        dismissHandler = handler
    }

    fun unregisterDismissHandler() {
        dismissHandler = null
    }

    fun dismiss(key: String) {
        dismissHandler?.invoke(key)
    }
}
