package nl.petervanmanen.minimalauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

/**
 * Small vertical column of dots on the screen edge, matching the Light Phone
 * page-position indicator: a filled dot marks the current page.
 */
@Composable
fun PageIndicatorDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    if (pageCount <= 1) return
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(pageCount) { index ->
            val isCurrent = index == currentPage
            Dot(size = if (isCurrent) 8.dp else 6.dp, filled = isCurrent)
        }
    }
}

@Composable
private fun Dot(size: Dp, filled: Boolean) {
    Box(
        modifier = Modifier
            .size(size)
            .then(
                if (filled) {
                    Modifier.background(PureWhite, CircleShape)
                } else {
                    Modifier.border(1.dp, DimWhite, CircleShape)
                }
            )
    )
}
