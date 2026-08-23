package nl.petervanmanen.minimalauncher.ui.dock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.data.model.DockApp
import nl.petervanmanen.minimalauncher.data.model.DockConfig
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.data.repository.AppLinkRepository
import nl.petervanmanen.minimalauncher.data.repository.AppRepository
import nl.petervanmanen.minimalauncher.data.repository.DEFAULT_NOTIFICATION_BUBBLE_COLOR
import nl.petervanmanen.minimalauncher.data.repository.DockRepository
import nl.petervanmanen.minimalauncher.data.repository.NotificationRepository
import nl.petervanmanen.minimalauncher.data.repository.SettingsRepository
import nl.petervanmanen.minimalauncher.data.util.defaultDisplayNameFor

class DockViewModel(
    private val dockRepository: DockRepository,
    appRepository: AppRepository,
    private val settingsRepository: SettingsRepository,
    private val appLinkRepository: AppLinkRepository,
    notificationRepository: NotificationRepository,
) : ViewModel() {

    val dockConfig: StateFlow<DockConfig> = dockRepository.dockConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DockConfig())

    val installedApps: StateFlow<List<InstalledApp>> = appRepository.installedApps

    val allowRotation: StateFlow<Boolean> = settingsRepository.allowRotation
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val showWeather: StateFlow<Boolean> = settingsRepository.showWeather
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val showMap: StateFlow<Boolean> = settingsRepository.showMap
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val showDate: StateFlow<Boolean> = settingsRepository.showDate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val hideStatusBar: StateFlow<Boolean> = settingsRepository.hideStatusBar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val notificationBubbleEnabled: StateFlow<Boolean> = settingsRepository.notificationBubbleEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val notificationBubbleColor: StateFlow<Int> = settingsRepository.notificationBubbleColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DEFAULT_NOTIFICATION_BUBBLE_COLOR)

    /** Packages with an active notification, only while the bubble setting is on — empty otherwise. */
    val packagesWithNotificationBubble: StateFlow<Set<String>> = combine(
        notificationRepository.notifications,
        settingsRepository.notificationBubbleEnabled,
    ) { notifications, enabled ->
        if (enabled) notifications.map { it.packageName }.toSet() else emptySet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val mapsAppPackage: StateFlow<String?> = appLinkRepository.mapsAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val weatherAppPackage: StateFlow<String?> = appLinkRepository.weatherAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val dateAppPackage: StateFlow<String?> = appLinkRepository.dateAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _showAddAppPicker = MutableStateFlow(false)
    val showAddAppPicker: StateFlow<Boolean> = _showAddAppPicker.asStateFlow()

    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

    fun enterEditMode() {
        _isEditing.value = true
    }

    fun exitEditMode() {
        _isEditing.value = false
        _showAddAppPicker.value = false
        _showSettings.value = false
    }

    fun openSettings() {
        _showSettings.value = true
    }

    fun closeSettings() {
        _showSettings.value = false
    }

    fun setAllowRotation(allow: Boolean) = viewModelScope.launch {
        settingsRepository.setAllowRotation(allow)
    }

    fun setShowWeather(show: Boolean) = viewModelScope.launch { settingsRepository.setShowWeather(show) }

    fun setShowMap(show: Boolean) = viewModelScope.launch { settingsRepository.setShowMap(show) }

    fun setShowDate(show: Boolean) = viewModelScope.launch { settingsRepository.setShowDate(show) }

    fun setHideStatusBar(hide: Boolean) = viewModelScope.launch { settingsRepository.setHideStatusBar(hide) }

    fun setNotificationBubbleEnabled(enabled: Boolean) = viewModelScope.launch {
        settingsRepository.setNotificationBubbleEnabled(enabled)
    }

    fun setNotificationBubbleColor(color: Int) = viewModelScope.launch {
        settingsRepository.setNotificationBubbleColor(color)
    }

    fun setMapsApp(packageName: String) = viewModelScope.launch { appLinkRepository.setMapsApp(packageName) }

    fun setWeatherApp(packageName: String) = viewModelScope.launch { appLinkRepository.setWeatherApp(packageName) }

    fun setDateApp(packageName: String) = viewModelScope.launch { appLinkRepository.setDateApp(packageName) }

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
