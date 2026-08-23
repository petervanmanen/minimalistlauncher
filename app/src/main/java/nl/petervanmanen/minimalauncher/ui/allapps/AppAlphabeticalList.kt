package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.ui.components.AppLabelRow

/** Plain alphabetical app list with a fading current-letter indicator, no icons. */
@Composable
fun AppAlphabeticalList(
    apps: List<InstalledApp>,
    onAppClick: (InstalledApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 24.dp),
        ) {
            items(apps, key = { it.packageName }) { app ->
                AppLabelRow(label = app.label, onClick = { onAppClick(app) })
            }
        }

        AlphabetIndexOverlay(
            listState = listState,
            firstLetterOf = { index -> apps.getOrNull(index)?.label?.firstOrNull()?.uppercaseChar() },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
        )
    }
}
