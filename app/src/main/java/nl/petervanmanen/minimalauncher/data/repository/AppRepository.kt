package nl.petervanmanen.minimalauncher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import nl.petervanmanen.minimalauncher.data.datastore.launcherDataStore
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.receiver.PackageChangeReceiver

private val HIDDEN_PACKAGES_KEY = stringSetPreferencesKey("hidden_packages")

/** Queries and keeps in sync the list of apps the device can launch. */
class AppRepository(context: Context) {

    private val appContext = context.applicationContext
    private val packageManager = appContext.packageManager

    private val _installedApps = MutableStateFlow(queryLaunchableApps())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    val hiddenPackages: Flow<Set<String>> =
        appContext.launcherDataStore.data.map { it[HIDDEN_PACKAGES_KEY] ?: emptySet() }

    init {
        val receiver = PackageChangeReceiver { refresh() }
        ContextCompat.registerReceiver(
            appContext,
            receiver,
            PackageChangeReceiver.intentFilter(),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    fun refresh() {
        _installedApps.value = queryLaunchableApps()
    }

    suspend fun setHidden(packageName: String, hidden: Boolean) {
        appContext.launcherDataStore.edit { prefs ->
            val current = prefs[HIDDEN_PACKAGES_KEY] ?: emptySet()
            prefs[HIDDEN_PACKAGES_KEY] = if (hidden) current + packageName else current - packageName
        }
    }

    private fun queryLaunchableApps(): List<InstalledApp> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val ownPackage = appContext.packageName
        return queryIntentActivitiesCompat(intent)
            .asSequence()
            .map { it.activityInfo }
            .filter { it.packageName != ownPackage }
            .map { activityInfo ->
                InstalledApp(
                    packageName = activityInfo.packageName,
                    label = activityInfo.loadLabel(packageManager).toString(),
                    canRemove = isRemovable(activityInfo.applicationInfo),
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    /**
     * False for non-updated system apps (e.g. Settings, the Phone dialer):
     * Android itself refuses to uninstall those, and hiding them from the
     * launcher risks stranding the user without access to core functionality.
     */
    private fun isRemovable(appInfo: ApplicationInfo): Boolean {
        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val isUpdatedSystemApp = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        return !isSystemApp || isUpdatedSystemApp
    }

    @Suppress("DEPRECATION")
    private fun queryIntentActivitiesCompat(intent: Intent): List<ResolveInfo> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            packageManager.queryIntentActivities(intent, 0)
        }
}
