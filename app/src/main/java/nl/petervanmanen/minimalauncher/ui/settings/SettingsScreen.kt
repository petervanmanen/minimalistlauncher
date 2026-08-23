package nl.petervanmanen.minimalauncher.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureWhite

@Composable
fun SettingsScreen(
    allowRotation: Boolean,
    onAllowRotationChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Text(
            text = "‹ Settings",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable(onClick = onBack),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAllowRotationChange(!allowRotation) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Rotate with device", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (allowRotation) "On" else "Off",
                style = MaterialTheme.typography.bodyLarge,
                color = if (allowRotation) PureWhite else DimWhite,
            )
        }
    }
}
