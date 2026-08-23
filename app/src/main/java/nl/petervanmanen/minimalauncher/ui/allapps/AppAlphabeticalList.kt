package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.ui.components.AppLabelRow

/** Plain alphabetical app list with a draggable A-Z index on the trailing edge, no icons. */
@Composable
fun AppAlphabeticalList(
    apps: List<InstalledApp>,
    onAppClick: (InstalledApp) -> Unit,
    modifier: Modifier = Modifier,
    onAppLongClick: ((InstalledApp) -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var draggedLetter by remember { mutableStateOf<Char?>(null) }

    val firstIndexOfLetter = remember(apps) { firstIndexOfLetter(apps) }
    val availableLetters = remember(firstIndexOfLetter) { firstIndexOfLetter.keys }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 24.dp),
        ) {
            items(apps, key = { it.packageName }) { app ->
                AppLabelRow(
                    label = app.label,
                    onClick = { onAppClick(app) },
                    onLongClick = onAppLongClick?.let { { it(app) } },
                )
            }
            if (footer != null) {
                item { footer() }
            }
        }

        AlphabetIndexBar(
            availableLetters = availableLetters,
            onLetterSelected = { letter ->
                firstIndexOfLetter[letter]?.let { index ->
                    coroutineScope.launch { listState.scrollToItem(index) }
                }
            },
            onActiveLetterChanged = { draggedLetter = it },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
        )

        AlphabetIndexOverlay(
            letter = draggedLetter,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

/** The index of the first app whose label starts with each letter present, keyed by that uppercase letter. */
internal fun firstIndexOfLetter(apps: List<InstalledApp>): Map<Char, Int> {
    val map = mutableMapOf<Char, Int>()
    apps.forEachIndexed { index, app ->
        val letter = app.label.firstOrNull()?.uppercaseChar() ?: return@forEachIndexed
        map.getOrPut(letter) { index }
    }
    return map
}
