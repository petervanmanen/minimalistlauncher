package nl.petervanmanen.minimalauncher.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.NotificationEntry
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureBlack

private const val COLLAPSED_TEXT_LINES = 2

@Composable
fun NotificationsSection(
    isAccessGranted: Boolean,
    notifications: List<NotificationEntry>,
    onEnableAccess: () -> Unit,
    onNotificationClick: (NotificationEntry) -> Unit,
    onNotificationDismiss: (NotificationEntry) -> Unit,
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
                NotificationRow(
                    entry = entry,
                    onOpen = { onNotificationClick(entry) },
                    onDismiss = { onNotificationDismiss(entry) },
                )
            }
        }
    }
}

/**
 * Tapping a truncated notification expands it (photo included, if it has one);
 * tapping it again (now expanded) opens its app. Swiping either direction
 * dismisses it, same as swiping it away in the system shade.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationRow(entry: NotificationEntry, onOpen: () -> Unit, onDismiss: () -> Unit) {
    var isExpanded by remember(entry.key) { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            val dismissed = value != SwipeToDismissBoxValue.Settled
            if (dismissed) onDismiss()
            dismissed
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { Box(modifier = Modifier.fillMaxSize().padding(vertical = 10.dp)) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureBlack)
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
            if (isExpanded && entry.picture != null) {
                Image(
                    bitmap = entry.picture.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(entry.picture.width.toFloat() / entry.picture.height)
                        .padding(top = 8.dp),
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
    }
}
