package nl.petervanmanen.minimalauncher.ui.dock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.ui.components.AppPickerScreen

@Composable
fun AddAppToDockScreen(
    installedApps: List<InstalledApp>,
    alreadyInDock: Set<String>,
    onAppSelected: (InstalledApp) -> Unit,
    onBack: () -> Unit,
) {
    val selectableApps = remember(installedApps, alreadyInDock) {
        installedApps.filterNot { it.packageName in alreadyInDock }
    }

    AppPickerScreen(apps = selectableApps, onAppSelected = onAppSelected, onBack = onBack)
}
