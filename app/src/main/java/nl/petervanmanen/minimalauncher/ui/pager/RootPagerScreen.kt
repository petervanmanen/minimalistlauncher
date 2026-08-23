package nl.petervanmanen.minimalauncher.ui.pager

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.petervanmanen.minimalauncher.di.AppContainer
import nl.petervanmanen.minimalauncher.ui.allapps.AllAppsScreen
import nl.petervanmanen.minimalauncher.ui.allapps.AllAppsViewModel
import nl.petervanmanen.minimalauncher.ui.dashboard.DashboardScreen
import nl.petervanmanen.minimalauncher.ui.dashboard.DashboardViewModel
import nl.petervanmanen.minimalauncher.ui.dock.DockScreen
import nl.petervanmanen.minimalauncher.ui.dock.DockViewModel
import nl.petervanmanen.minimalauncher.ui.launcherViewModelFactory

/**
 * The three-screen horizontal pager: the dashboard (weather / notifications)
 * on the left, the dock in the center, and the alphabetical app list on the
 * right. In a [HorizontalPager], dragging a finger rightward reveals the
 * page to the left (lower index) — so with the dashboard at index 0, that
 * finger motion (the conventional "swipe right") reveals it, and dragging
 * leftward (a "swipe left") reveals all-apps at index 2.
 */
@Composable
fun RootPagerScreen(container: AppContainer) {
    val factory = remember { launcherViewModelFactory(container) }
    val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
    val dockViewModel: DockViewModel = viewModel(factory = factory)
    val allAppsViewModel: AllAppsViewModel = viewModel(factory = factory)

    val pagerState = rememberPagerState(initialPage = 1) { 3 }

    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
        when (page) {
            0 -> DashboardScreen(dashboardViewModel)
            1 -> DockScreen(dockViewModel)
            else -> AllAppsScreen(allAppsViewModel)
        }
    }
}
