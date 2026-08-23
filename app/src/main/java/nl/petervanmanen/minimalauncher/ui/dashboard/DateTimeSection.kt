package nl.petervanmanen.minimalauncher.ui.dashboard

import android.text.format.DateFormat
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Date
import kotlinx.coroutines.delay

/**
 * The current date and time, in the device's own format/locale settings.
 * Tapping opens the configured app (or the picker, if none is set yet);
 * long-press always opens the picker so the choice can be changed.
 */
@Composable
fun DateTimeSection(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var now by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            now = System.currentTimeMillis()
        }
    }

    val dateFormat = remember(context) { DateFormat.getDateFormat(context) }
    val timeFormat = remember(context) { DateFormat.getTimeFormat(context) }
    val text = remember(now) { "${dateFormat.format(Date(now))}  ${timeFormat.format(Date(now))}" }

    val interactionSource = remember { MutableInteractionSource() }
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
            onLongClick = onLongClick,
        ),
    )
}
