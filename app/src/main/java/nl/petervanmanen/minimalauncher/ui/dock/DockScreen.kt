package nl.petervanmanen.minimalauncher.ui.dock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.DockPage
import nl.petervanmanen.minimalauncher.ui.components.PageIndicatorDots

@Composable
fun DockScreen(viewModel: DockViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dockConfig by viewModel.dockConfig.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val packagesWithNotificationBubble by viewModel.packagesWithNotificationBubble.collectAsState()
    val notificationBubbleColor by viewModel.notificationBubbleColor.collectAsState()
    val pageCount = dockConfig.pages.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Box(modifier = modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            userScrollEnabled = pageCount > 1,
            modifier = Modifier.fillMaxSize(),
        ) { pageIndex ->
            val page = dockConfig.pages.getOrElse(pageIndex) { DockPage() }
            DockPageContent(
                page = page,
                onAppClick = { app ->
                    context.packageManager.getLaunchIntentForPackage(app.packageName)?.let {
                        context.startActivity(it)
                    }
                },
                onBackgroundLongClick = { viewModel.enterEditMode() },
                packagesWithNotificationBubble = packagesWithNotificationBubble,
                notificationBubbleColor = Color(notificationBubbleColor),
            )
        }

        PageIndicatorDots(
            pageCount = pageCount,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
        )
    }

    if (isEditing) {
        DockEditOverlay(viewModel = viewModel, currentPageIndex = pagerState.currentPage)
    }
}
