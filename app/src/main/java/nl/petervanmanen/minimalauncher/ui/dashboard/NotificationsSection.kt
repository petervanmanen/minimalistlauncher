package nl.petervanmanen.minimalauncher.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.NotificationEntry
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite

private const val COLLAPSED_TEXT_LINES = 2

@Composable
fun NotificationsSection(
    isAccessGranted: Boolean,
    notifications: List<NotificationEntry>,
    onEnableAccess: () -> Unit,
    onNotificationClick: (NotificationEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        !isAccessGranted -> Text(
            text = "Enable notifications",
            style = MaterialTheme.typography.bodyLarge,
            color = DimWhite,
            modifier = modifier.clickable(onClick = onEnableAccess),
        )
        notifications.isEmpty() -> Text(
            text = "No notifications",
            style = MaterialTheme.typography.bodyLarge,
            color = DimWhite,
            modifier = modifier,
        )
        else -> LazyColumn(modifier = modifier.fillMaxSize()) {
            items(notifications, key = { it.key }) { entry ->
                NotificationRow(entry = entry, onOpen = { onNotificationClick(entry) })
            }
        }
    }
}

/** Tapping a truncated notification expands it; tapping it again (now expanded) opens its app. */
@Composable
private fun NotificationRow(entry: NotificationEntry, onOpen: () -> Unit) {
    var isExpanded by remember(entry.key) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (isExpanded) onOpen() else isExpanded = true }
            .padding(vertical = 10.dp),
    ) {
        Text(
            text = "${entry.appLabel}: ${entry.title}",
            style = MaterialTheme.typography.bodyLarge,
            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (entry.text.isNotBlank()) {
            Text(
                text = entry.text,
                color = DimWhite,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (isExpanded) Int.MAX_VALUE else COLLAPSED_TEXT_LINES,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
