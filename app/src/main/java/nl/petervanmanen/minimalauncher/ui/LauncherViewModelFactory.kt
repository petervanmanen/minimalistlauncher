package nl.petervanmanen.minimalauncher.ui

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import nl.petervanmanen.minimalauncher.di.AppContainer
import nl.petervanmanen.minimalauncher.ui.allapps.AllAppsViewModel
import nl.petervanmanen.minimalauncher.ui.dashboard.DashboardViewModel
import nl.petervanmanen.minimalauncher.ui.dock.DockViewModel

fun launcherViewModelFactory(container: AppContainer) = viewModelFactory {
    initializer { DockViewModel(container.dockRepository, container.appRepository) }
    initializer { AllAppsViewModel(container.appRepository) }
    initializer { DashboardViewModel(container.weatherRepository, container.notificationRepository) }
}
