package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

/** Large fading letter bubble, shown centered while the alphabet index is being dragged. */
@Composable
fun AlphabetIndexOverlay(
    letter: Char?,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = letter != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .background(PureWhite.copy(alpha = 0.12f), CircleShape)
                .padding(horizontal = 28.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = letter?.toString().orEmpty(), style = MaterialTheme.typography.bodyLarge, fontSize = 40.sp)
        }
    }
}
