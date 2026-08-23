package nl.petervanmanen.minimalauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val BUBBLE_GUTTER_WIDTH = 20.dp
private val BUBBLE_SIZE = 10.dp

/**
 * A single plain-text app row — no icon, matching the Light Phone list style.
 * A leading gutter is always reserved so the label lines up the same way
 * whether or not a notification bubble is shown in it.
 */
@Composable
fun AppLabelRow(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    showBubble: Boolean = false,
    bubbleColor: Color = Color.Unspecified,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(BUBBLE_GUTTER_WIDTH), contentAlignment = Alignment.Center) {
            if (showBubble) {
                Box(modifier = Modifier.size(BUBBLE_SIZE).background(bubbleColor, CircleShape))
            }
        }
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}
