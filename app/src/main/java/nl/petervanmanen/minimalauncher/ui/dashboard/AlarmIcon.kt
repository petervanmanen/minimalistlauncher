package nl.petervanmanen.minimalauncher.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

private val ICON_SIZE = 20.dp

/** A simple hand-drawn bell glyph — no icon assets, matching the app's style. */
@Composable
fun AlarmIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(ICON_SIZE)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        drawCircle(color = PureWhite, radius = h * 0.05f, center = Offset(cx, h * 0.08f))

        val body = Path().apply {
            moveTo(cx - w * 0.06f, h * 0.15f)
            cubicTo(cx - w * 0.4f, h * 0.15f, cx - w * 0.38f, h * 0.55f, cx - w * 0.4f, h * 0.65f)
            lineTo(cx - w * 0.42f, h * 0.72f)
            cubicTo(cx - w * 0.2f, h * 0.68f, cx + w * 0.2f, h * 0.68f, cx + w * 0.42f, h * 0.72f)
            lineTo(cx + w * 0.4f, h * 0.65f)
            cubicTo(cx + w * 0.38f, h * 0.55f, cx + w * 0.4f, h * 0.15f, cx + w * 0.06f, h * 0.15f)
            close()
        }
        drawPath(body, color = PureWhite)

        drawCircle(color = PureWhite, radius = h * 0.05f, center = Offset(cx, h * 0.8f))
    }
}
