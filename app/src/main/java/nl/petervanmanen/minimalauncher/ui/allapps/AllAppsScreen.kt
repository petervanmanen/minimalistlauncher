package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.data.util.launchApp
import nl.petervanmanen.minimalauncher.data.util.openAppInfo
import nl.petervanmanen.minimalauncher.data.util.requestUninstall
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite

@Composable
fun AllAppsScreen(viewModel: AllAppsViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val apps by viewModel.visibleApps.collectAsState()
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val packagesWithNotificationBubble by viewModel.packagesWithNotificationBubble.collectAsState()
    val notificationBubbleColor by viewModel.notificationBubbleColor.collectAsState()

    var selectedApp by remember { mutableStateOf<InstalledApp?>(null) }
    var showHiddenApps by remember { mutableStateOf(false) }

    if (showHiddenApps) {
        HiddenAppsScreen(
            hiddenApps = hiddenApps,
            onUnhide = { app -> viewModel.unhideApp(app.packageName) },
            onBack = { showHiddenApps = false },
        )
        return
    }

    AppAlphabeticalList(
        apps = apps,
        onAppClick = { app -> launchApp(context, app.packageName) },
        onAppLongClick = { app -> selectedApp = app },
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        footer = if (hiddenApps.isNotEmpty()) {
            {
                Text(
                    text = "Hidden apps (${hiddenApps.size})",
                    color = DimWhite,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showHiddenApps = true }
                        .padding(start = 20.dp, top = 12.dp, bottom = 12.dp),
                )
            }
        } else {
            null
        },
        packagesWithNotificationBubble = packagesWithNotificationBubble,
        notificationBubbleColor = Color(notificationBubbleColor),
    )

    selectedApp?.let { app ->
        AppContextMenu(
            app = app,
            onDismiss = { selectedApp = null },
            onUninstall = { requestUninstall(context, app.packageName) },
            onHide = { viewModel.hideApp(app.packageName) },
            onAppInfo = { openAppInfo(context, app.packageName) },
        )
    }
}
