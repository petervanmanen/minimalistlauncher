package nl.petervanmanen.minimalauncher.ui.dock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.data.model.DockApp
import nl.petervanmanen.minimalauncher.data.model.DockConfig
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.data.repository.AppRepository
import nl.petervanmanen.minimalauncher.data.repository.DockRepository
import nl.petervanmanen.minimalauncher.data.util.defaultDisplayNameFor

class DockViewModel(
    private val dockRepository: DockRepository,
    appRepository: AppRepository,
) : ViewModel() {

    val dockConfig: StateFlow<DockConfig> = dockRepository.dockConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DockConfig())

    val installedApps: StateFlow<List<InstalledApp>> = appRepository.installedApps

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _showAddAppPicker = MutableStateFlow(false)
    val showAddAppPicker: StateFlow<Boolean> = _showAddAppPicker.asStateFlow()

    fun enterEditMode() {
        _isEditing.value = true
    }

    fun exitEditMode() {
        _isEditing.value = false
        _showAddAppPicker.value = false
    }

    fun openAddAppPicker() {
        _showAddAppPicker.value = true
    }

    fun closeAddAppPicker() {
        _showAddAppPicker.value = false
    }

    fun addApp(pageIndex: Int, app: InstalledApp) = viewModelScope.launch {
        val dockApp = DockApp(
            packageName = app.packageName,
            displayName = defaultDisplayNameFor(app.packageName, app.label),
        )
        dockRepository.addAppToPage(pageIndex, dockApp)
        _showAddAppPicker.value = false
    }

    fun removeApp(packageName: String) = viewModelScope.launch {
        dockRepository.removeApp(packageName)
    }

    fun renameApp(packageName: String, newName: String) = viewModelScope.launch {
        dockRepository.renameApp(packageName, newName)
    }

    fun reorderWithinPage(pageIndex: Int, fromIndex: Int, toIndex: Int) = viewModelScope.launch {
        dockRepository.reorderWithinPage(pageIndex, fromIndex, toIndex)
    }

    fun addPage() = viewModelScope.launch {
        dockRepository.addPage()
    }

    fun removePage(pageIndex: Int) = viewModelScope.launch {
        dockRepository.removePage(pageIndex)
    }
}
