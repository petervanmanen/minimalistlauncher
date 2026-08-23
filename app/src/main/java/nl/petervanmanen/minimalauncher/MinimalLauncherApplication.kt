package nl.petervanmanen.minimalauncher

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.di.AppContainer

class MinimalLauncherApplication : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        pruneDockWhenAppsAreUninstalled()
    }

    /** Keeps the dock free of apps the user has since uninstalled. */
    private fun pruneDockWhenAppsAreUninstalled() {
        applicationScope.launch {
            container.appRepository.installedApps.collect { installedApps ->
                val installedPackages = installedApps.map { it.packageName }.toSet()
                val dockPackages = container.dockRepository.dockConfig.first()
                    .pages.flatMap { it.apps }
                    .map { it.packageName }
                    .toSet()
                val stalePackages = dockPackages - installedPackages
                if (stalePackages.isNotEmpty()) {
                    container.dockRepository.removePackages(stalePackages)
                }
            }
        }
    }
}
