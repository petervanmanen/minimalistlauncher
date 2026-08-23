package nl.petervanmanen.minimalauncher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.receiver.PackageChangeReceiver

/** Queries and keeps in sync the list of apps the device can launch. */
class AppRepository(context: Context) {

    private val appContext = context.applicationContext
    private val packageManager = appContext.packageManager

    private val _installedApps = MutableStateFlow(queryLaunchableApps())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

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

    private fun queryLaunchableApps(): List<InstalledApp> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val ownPackage = appContext.packageName
        return queryIntentActivitiesCompat(intent)
            .asSequence()
            .map { it.activityInfo }
            .filter { it.packageName != ownPackage }
            .map { InstalledApp(it.packageName, it.loadLabel(packageManager).toString()) }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    @Suppress("DEPRECATION")
    private fun queryIntentActivitiesCompat(intent: Intent): List<ResolveInfo> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            packageManager.queryIntentActivities(intent, 0)
        }
}
