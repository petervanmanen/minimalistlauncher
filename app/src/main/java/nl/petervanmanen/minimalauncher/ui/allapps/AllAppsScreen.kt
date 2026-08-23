package nl.petervanmanen.minimalauncher.ui.allapps

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import nl.petervanmanen.minimalauncher.data.model.InstalledApp

@Composable
fun AllAppsScreen(viewModel: AllAppsViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val apps by viewModel.installedApps.collectAsState()

    AppAlphabeticalList(
        apps = apps,
        onAppClick = { app -> launchApp(app, context) },
        modifier = modifier.fillMaxSize(),
    )
}

private fun launchApp(app: InstalledApp, context: Context) {
    context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { intent ->
        context.startActivity(intent)
    }
}
