package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite
import kotlin.math.abs

private val LETTERS = ('A'..'Z').toList()

/** Persistent A-Z strip along the trailing edge — tap or drag to jump the list to a letter. */
@Composable
fun AlphabetIndexBar(
    availableLetters: Set<Char>,
    onLetterSelected: (Char) -> Unit,
    onActiveLetterChanged: (Char?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var activeLetter by remember { mutableStateOf<Char?>(null) }

    Column(
        modifier = modifier
            .width(20.dp)
            .fillMaxHeight()
            .pointerInput(availableLetters) {
                awaitEachGesture {
                    var change = awaitFirstDown(requireUnconsumed = false)
                    while (true) {
                        change.consume()
                        val letter = letterAt(change.position.y, size.height.toFloat())
                        if (letter != activeLetter) {
                            activeLetter = letter
                            onActiveLetterChanged(letter)
                            nearestAvailableLetter(letter, availableLetters)?.let(onLetterSelected)
                        }
                        val event = awaitPointerEvent()
                        val next = event.changes.firstOrNull { it.id == change.id }
                        if (next == null || !next.pressed) break
                        change = next
                    }
                    activeLetter = null
                    onActiveLetterChanged(null)
                }
            },
    ) {
        LETTERS.forEach { letter ->
            Text(
                text = letter.toString(),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = when {
                    letter == activeLetter -> PureWhite
                    letter in availableLetters -> DimWhite
                    else -> DimWhite.copy(alpha = 0.35f)
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

internal fun letterAt(y: Float, heightPx: Float): Char {
    val rowHeight = heightPx / LETTERS.size
    val index = (y / rowHeight).toInt().coerceIn(0, LETTERS.lastIndex)
    return LETTERS[index]
}

internal fun nearestAvailableLetter(target: Char, available: Set<Char>): Char? =
    available.minByOrNull { abs(it - target) }
