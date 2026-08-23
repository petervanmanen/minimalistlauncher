package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.data.repository.AppRepository

class AllAppsViewModel(appRepository: AppRepository) : ViewModel() {
    val installedApps: StateFlow<List<InstalledApp>> = appRepository.installedApps
}
