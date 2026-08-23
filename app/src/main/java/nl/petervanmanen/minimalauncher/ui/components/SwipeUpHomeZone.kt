package nl.petervanmanen.minimalauncher.ui.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private val SWIPE_UP_THRESHOLD = 56.dp

/**
 * An invisible strip anchored to the bottom of the screen: swiping up from
 * within it invokes [onSwipeUp]. Kept to a bottom-edge hit zone (rather than
 * a full-screen drag detector) so it doesn't fight vertically-scrolling
 * content — e.g. the all-apps list — for the same gesture.
 *
 * The true bottom few dp of the screen belong to Android's own
 * *mandatory* system gesture navigation (the "swipe up to go home" strip)
 * — a separate, non-excludable inset from the regular (left/right edge)
 * [WindowInsets.systemGestures]. Verified on-device: a touch starting in
 * that mandatory strip gets cancelled by the system after only a few
 * pixels of movement, and [android.view.View.setSystemGestureExclusionRects]
 * has no effect on it. So this zone sits just *above* it via
 * [WindowInsets.mandatorySystemGestures] padding, rather than overlapping.
 */
@Composable
fun SwipeUpHomeZone(onSwipeUp: () -> Unit, modifier: Modifier = Modifier) {
    val thresholdPx = with(LocalDensity.current) { SWIPE_UP_THRESHOLD.toPx() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.mandatorySystemGestures.only(WindowInsetsSides.Bottom))
            .height(40.dp)
            .pointerInput(Unit) {
                var accumulatedDrag = 0f
                detectVerticalDragGestures(
                    onDragStart = { accumulatedDrag = 0f },
                    onDragEnd = { if (accumulatedDrag < -thresholdPx) onSwipeUp() },
                    onDragCancel = { accumulatedDrag = 0f },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        accumulatedDrag += dragAmount
                    },
                )
            },
    )
}
