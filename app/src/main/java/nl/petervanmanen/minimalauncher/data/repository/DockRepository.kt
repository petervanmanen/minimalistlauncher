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
import nl.petervanmanen.minimalauncher.data.model.DockPage

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

    suspend fun addAppToPage(pageIndex: Int, app: DockApp) = update { config ->
        config.withPages { pages ->
            pages.mapIndexed { index, page ->
                if (index == pageIndex && page.apps.none { it.packageName == app.packageName }) {
                    page.copy(apps = page.apps + app)
                } else {
                    page
                }
            }
        }
    }

    suspend fun removeApp(packageName: String) = update { config ->
        config.withPages { pages ->
            pages.map { page -> page.copy(apps = page.apps.filterNot { it.packageName == packageName }) }
        }
    }

    suspend fun removePackages(packageNames: Set<String>) = update { config ->
        config.withPages { pages ->
            pages.map { page -> page.copy(apps = page.apps.filterNot { it.packageName in packageNames }) }
        }
    }

    suspend fun renameApp(packageName: String, newName: String) = update { config ->
        config.withPages { pages ->
            pages.map { page ->
                page.copy(
                    apps = page.apps.map { app ->
                        if (app.packageName == packageName) app.copy(displayName = newName) else app
                    }
                )
            }
        }
    }

    suspend fun reorderWithinPage(pageIndex: Int, fromIndex: Int, toIndex: Int) = update { config ->
        config.withPages { pages ->
            pages.mapIndexed { index, page ->
                if (index != pageIndex) return@mapIndexed page
                val apps = page.apps.toMutableList()
                if (fromIndex !in apps.indices || toIndex !in apps.indices) return@mapIndexed page
                val moved = apps.removeAt(fromIndex)
                apps.add(toIndex, moved)
                page.copy(apps = apps)
            }
        }
    }

    suspend fun addPage() = update { config -> config.copy(pages = config.pages + DockPage()) }

    suspend fun removePage(pageIndex: Int) = update { config ->
        if (config.pages.size <= 1) return@update config
        config.copy(pages = config.pages.filterIndexed { index, _ -> index != pageIndex })
    }

    private inline fun DockConfig.withPages(transform: (List<DockPage>) -> List<DockPage>): DockConfig =
        copy(pages = transform(pages))
}
