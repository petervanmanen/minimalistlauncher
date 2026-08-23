package nl.petervanmanen.minimalauncher.ui.dashboard

import android.app.AlarmManager
import android.text.format.DateFormat
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Date
import kotlinx.coroutines.delay
import nl.petervanmanen.minimalauncher.data.util.nextAlarmToShow
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite

/**
 * The current date and time, in the device's own format/locale settings,
 * plus the next alarm on its own line if one is due within 25 hours.
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
    val alarmManager = remember(context) { context.getSystemService(AlarmManager::class.java) }

    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    var nextAlarmTrigger by remember { mutableStateOf(alarmManager?.nextAlarmClock?.triggerTime) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            now = System.currentTimeMillis()
            nextAlarmTrigger = alarmManager?.nextAlarmClock?.triggerTime
        }
    }

    val dateFormat = remember(context) { DateFormat.getDateFormat(context) }
    val timeFormat = remember(context) { DateFormat.getTimeFormat(context) }
    val alarmToShow = remember(nextAlarmTrigger, now) { nextAlarmToShow(nextAlarmTrigger, now) }

    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
            onLongClick = onLongClick,
        ),
    ) {
        Text(
            text = "${dateFormat.format(Date(now))}  ${timeFormat.format(Date(now))}",
            style = MaterialTheme.typography.bodyLarge,
        )

        if (alarmToShow != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AlarmIcon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = timeFormat.format(Date(alarmToShow)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = DimWhite,
                )
            }
        }
    }
}
