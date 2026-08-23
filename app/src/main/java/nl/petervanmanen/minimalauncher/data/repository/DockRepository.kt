package nl.petervanmanen.minimalauncher.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.petervanmanen.minimalauncher.data.datastore.launcherDataStore
import nl.petervanmanen.minimalauncher.data.model.DockApp
import nl.petervanmanen.minimalauncher.data.model.DockConfig

private val DOCK_CONFIG_KEY = stringPreferencesKey("dock_config_json")
private val json = Json { ignoreUnknownKeys = true }

class DockRepository(private val context: Context) {

    val dockConfig: Flow<DockConfig> =
        context.launcherDataStore.data.map { prefs ->
            val raw = prefs[DOCK_CONFIG_KEY]
            if (raw.isNullOrBlank()) {
                DockConfig()
            } else {
                runCatching { json.decodeFromString<DockConfig>(raw) }.getOrDefault(DockConfig())
            }
        }

    private suspend fun update(transform: (DockConfig) -> DockConfig) {
        context.launcherDataStore.edit { prefs ->
            val current = prefs[DOCK_CONFIG_KEY]?.let {
                runCatching { json.decodeFromString<DockConfig>(it) }.getOrDefault(DockConfig())
            } ?: DockConfig()
            val next = transform(current)
            prefs[DOCK_CONFIG_KEY] = json.encodeToString(next)
        }
    }

    suspend fun addAppToPage(pageIndex: Int, app: DockApp) =
        update { config -> DockConfigOperations.addAppToPage(config, pageIndex, app) }

    suspend fun removeApp(packageName: String) =
        update { config -> DockConfigOperations.removeApp(config, packageName) }

    suspend fun removePackages(packageNames: Set<String>) =
        update { config -> DockConfigOperations.removePackages(config, packageNames) }

    suspend fun renameApp(packageName: String, newName: String) =
        update { config -> DockConfigOperations.renameApp(config, packageName, newName) }

    suspend fun reorderWithinPage(pageIndex: Int, fromIndex: Int, toIndex: Int) =
        update { config -> DockConfigOperations.reorderWithinPage(config, pageIndex, fromIndex, toIndex) }

    suspend fun addPage() = update { config -> DockConfigOperations.addPage(config) }

    suspend fun removePage(pageIndex: Int) = update { config -> DockConfigOperations.removePage(config, pageIndex) }
}
