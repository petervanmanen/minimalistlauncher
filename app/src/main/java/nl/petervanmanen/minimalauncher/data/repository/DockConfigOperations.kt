package nl.petervanmanen.minimalauncher.data.repository

import nl.petervanmanen.minimalauncher.data.model.DockApp
import nl.petervanmanen.minimalauncher.data.model.DockConfig
import nl.petervanmanen.minimalauncher.data.model.DockPage

/** Pure [DockConfig] transforms, split out from [DockRepository] so they're testable without DataStore. */
object DockConfigOperations {

    fun addAppToPage(config: DockConfig, pageIndex: Int, app: DockApp): DockConfig = config.withPages { pages ->
        pages.mapIndexed { index, page ->
            if (index == pageIndex && page.apps.none { it.packageName == app.packageName }) {
                page.copy(apps = page.apps + app)
            } else {
                page
            }
        }
    }

    fun removeApp(config: DockConfig, packageName: String): DockConfig = config.withPages { pages ->
        pages.map { page -> page.copy(apps = page.apps.filterNot { it.packageName == packageName }) }
    }

    fun removePackages(config: DockConfig, packageNames: Set<String>): DockConfig = config.withPages { pages ->
        pages.map { page -> page.copy(apps = page.apps.filterNot { it.packageName in packageNames }) }
    }

    fun renameApp(config: DockConfig, packageName: String, newName: String): DockConfig = config.withPages { pages ->
        pages.map { page ->
            page.copy(
                apps = page.apps.map { app ->
                    if (app.packageName == packageName) app.copy(displayName = newName) else app
                }
            )
        }
    }

    fun reorderWithinPage(config: DockConfig, pageIndex: Int, fromIndex: Int, toIndex: Int): DockConfig =
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

    fun addPage(config: DockConfig): DockConfig = config.copy(pages = config.pages + DockPage())

    fun removePage(config: DockConfig, pageIndex: Int): DockConfig {
        if (config.pages.size <= 1) return config
        return config.copy(pages = config.pages.filterIndexed { index, _ -> index != pageIndex })
    }

    private inline fun DockConfig.withPages(transform: (List<DockPage>) -> List<DockPage>): DockConfig =
        copy(pages = transform(pages))
}
