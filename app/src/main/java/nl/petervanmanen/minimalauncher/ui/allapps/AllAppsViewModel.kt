package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.data.repository.AppRepository

class AllAppsViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val appsWithHiddenFlag = combine(
        appRepository.installedApps,
        appRepository.hiddenPackages,
    ) { apps, hidden -> apps to hidden }

    val visibleApps: StateFlow<List<InstalledApp>> = appsWithHiddenFlag
        .map { (apps, hidden) -> apps.filterNot { it.packageName in hidden } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val hiddenApps: StateFlow<List<InstalledApp>> = appsWithHiddenFlag
        .map { (apps, hidden) -> apps.filter { it.packageName in hidden } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun hideApp(packageName: String) = viewModelScope.launch {
        appRepository.setHidden(packageName, hidden = true)
    }

    fun unhideApp(packageName: String) = viewModelScope.launch {
        appRepository.setHidden(packageName, hidden = false)
    }
}
