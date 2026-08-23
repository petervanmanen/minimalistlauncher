package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.ui.theme.PureBlack

/** Tapping a hidden app un-hides it — there's nothing else useful to do with one here. */
@Composable
fun HiddenAppsScreen(
    hiddenApps: List<InstalledApp>,
    onUnhide: (InstalledApp) -> Unit,
    onBack: () -> Unit,
) {
    Surface(color = PureBlack, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 32.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "‹ Back",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable(onClick = onBack),
                )
            }

            Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp)) {
                hiddenApps.forEach { app ->
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUnhide(app) }
                            .padding(vertical = 12.dp),
                    )
                }
            }
        }
    }
}
