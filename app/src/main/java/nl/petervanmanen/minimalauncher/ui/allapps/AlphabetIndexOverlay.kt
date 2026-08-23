package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

/** Small fading letter bubble showing the first letter of the top visible app. */
@Composable
fun AlphabetIndexOverlay(
    listState: LazyListState,
    firstLetterOf: (index: Int) -> Char?,
    modifier: Modifier = Modifier,
) {
    val currentLetter by remember {
        derivedStateOf { firstLetterOf(listState.firstVisibleItemIndex) }
    }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            visible = true
        } else if (visible) {
            delay(800)
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible && currentLetter != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .padding(end = 16.dp)
                .background(PureWhite.copy(alpha = 0.12f), CircleShape)
                .padding(horizontal = 18.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = currentLetter?.toString().orEmpty(), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
