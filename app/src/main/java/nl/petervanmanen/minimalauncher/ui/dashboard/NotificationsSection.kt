package nl.petervanmanen.minimalauncher.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.NotificationEntry
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite

@Composable
fun NotificationsSection(
    isAccessGranted: Boolean,
    notifications: List<NotificationEntry>,
    onEnableAccess: () -> Unit,
    onNotificationClick: (NotificationEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when {
            !isAccessGranted -> Text(
                text = "Enable notifications",
                style = MaterialTheme.typography.bodyLarge,
                color = DimWhite,
                modifier = Modifier.clickable(onClick = onEnableAccess),
            )
            notifications.isEmpty() -> Text(
                text = "No notifications",
                style = MaterialTheme.typography.bodyLarge,
                color = DimWhite,
            )
            else -> notifications.forEach { entry ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNotificationClick(entry) }
                        .padding(vertical = 10.dp),
                ) {
                    Text(text = "${entry.appLabel}: ${entry.title}", style = MaterialTheme.typography.bodyLarge)
                    if (entry.text.isNotBlank()) {
                        Text(text = entry.text, color = DimWhite, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
