package nl.petervanmanen.minimalauncher.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import nl.petervanmanen.minimalauncher.data.remote.WeatherIconType
import nl.petervanmanen.minimalauncher.ui.theme.PureBlack
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

private val ICON_SIZE = 24.dp

/** A simple hand-drawn monochrome glyph for the current conditions — no icon assets, matching the app's style. */
@Composable
fun WeatherIcon(iconType: WeatherIconType, isDay: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(ICON_SIZE)) {
        when (iconType) {
            WeatherIconType.CLEAR -> if (isDay) drawSun(Offset(size.width / 2, size.height / 2), size.minDimension * 0.28f) else drawMoon()
            WeatherIconType.PARTLY_CLOUDY -> {
                if (isDay) {
                    drawSun(Offset(size.width * 0.4f, size.height * 0.35f), size.minDimension * 0.2f)
                } else {
                    drawMoon(center = Offset(size.width * 0.4f, size.height * 0.35f), radius = size.minDimension * 0.2f)
                }
                drawCloud(offsetY = size.height * 0.15f)
            }
            WeatherIconType.CLOUDY -> drawCloud()
            WeatherIconType.FOG -> drawFog()
            WeatherIconType.RAIN -> {
                drawCloud(offsetY = -size.height * 0.08f)
                drawRainDrops()
            }
            WeatherIconType.SNOW -> {
                drawCloud(offsetY = -size.height * 0.08f)
                drawSnowflakes()
            }
            WeatherIconType.THUNDERSTORM -> {
                drawCloud(offsetY = -size.height * 0.08f)
                drawBolt()
            }
        }
    }
}

private fun DrawScope.drawSun(center: Offset, radius: Float) {
    drawCircle(color = PureWhite, radius = radius, center = center)
    repeat(8) { i ->
        val angle = i * (Math.PI / 4)
        val start = Offset(
            center.x + (radius * 1.35f * cos(angle)).toFloat(),
            center.y + (radius * 1.35f * sin(angle)).toFloat(),
        )
        val end = Offset(
            center.x + (radius * 1.8f * cos(angle)).toFloat(),
            center.y + (radius * 1.8f * sin(angle)).toFloat(),
        )
        drawLine(PureWhite, start, end, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
    }
}

private fun DrawScope.drawMoon(center: Offset = Offset(size.width / 2, size.height / 2), radius: Float = size.minDimension * 0.28f) {
    drawCircle(color = PureWhite, radius = radius, center = center)
    drawCircle(color = PureBlack, radius = radius * 0.9f, center = center + Offset(radius * 0.55f, -radius * 0.15f))
}

private fun DrawScope.drawCloud(offsetY: Float = 0f) {
    val w = size.width
    val h = size.height
    val baseTop = h * 0.52f + offsetY
    drawRoundRect(
        color = PureWhite,
        topLeft = Offset(w * 0.12f, baseTop),
        size = Size(w * 0.76f, h * 0.3f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(h * 0.15f),
    )
    drawCircle(PureWhite, radius = h * 0.2f, center = Offset(w * 0.33f, baseTop + h * 0.02f))
    drawCircle(PureWhite, radius = h * 0.26f, center = Offset(w * 0.55f, baseTop - h * 0.06f))
    drawCircle(PureWhite, radius = h * 0.19f, center = Offset(w * 0.74f, baseTop + h * 0.03f))
}

private fun DrawScope.drawFog() {
    val w = size.width
    val h = size.height
    val lineWidths = listOf(0.75f, 0.55f, 0.7f, 0.45f)
    lineWidths.forEachIndexed { index, fraction ->
        val y = h * (0.3f + index * 0.15f)
        drawLine(
            color = PureWhite,
            start = Offset(w * (0.5f - fraction / 2), y),
            end = Offset(w * (0.5f + fraction / 2), y),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawRainDrops() {
    val w = size.width
    val h = size.height
    listOf(0.32f, 0.5f, 0.68f).forEach { x ->
        drawLine(
            color = PureWhite,
            start = Offset(w * x, h * 0.85f),
            end = Offset(w * x - w * 0.05f, h * 0.98f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawSnowflakes() {
    val w = size.width
    val h = size.height
    listOf(0.32f, 0.5f, 0.68f).forEach { x ->
        drawCircle(color = PureWhite, radius = 1.6.dp.toPx(), center = Offset(w * x, h * 0.92f))
    }
}

private fun DrawScope.drawBolt() {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.56f, h * 0.76f)
        lineTo(w * 0.42f, h * 0.9f)
        lineTo(w * 0.5f, h * 0.9f)
        lineTo(w * 0.4f, h * 0.99f)
        lineTo(w * 0.6f, h * 0.87f)
        lineTo(w * 0.5f, h * 0.87f)
        close()
    }
    drawPath(path, color = PureWhite)
}
