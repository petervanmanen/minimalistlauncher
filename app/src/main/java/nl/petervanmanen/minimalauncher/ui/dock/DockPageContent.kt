package nl.petervanmanen.minimalauncher.ui.dock

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.DockApp
import nl.petervanmanen.minimalauncher.data.model.DockPage
import nl.petervanmanen.minimalauncher.ui.components.AppLabelRow
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite

@Composable
fun DockPageContent(
    page: DockPage,
    onAppClick: (DockApp) -> Unit,
    onBackgroundLongClick: () -> Unit,
    packagesWithNotificationBubble: Set<String> = emptySet(),
    notificationBubbleColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {},
                onLongClick = onBackgroundLongClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            if (page.apps.isEmpty()) {
                Text(
                    text = "Long-press to add an app",
                    color = DimWhite,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 20.dp),
                )
            }
            page.apps.forEach { app ->
                AppLabelRow(
                    label = app.displayName,
                    onClick = { onAppClick(app) },
                    showBubble = app.packageName in packagesWithNotificationBubble,
                    bubbleColor = notificationBubbleColor,
                )
            }
        }
    }
}
