package nl.petervanmanen.minimalauncher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.ui.allapps.AppAlphabeticalList
import nl.petervanmanen.minimalauncher.ui.theme.PureBlack

/** A full-screen "‹ Back" + alphabetical app list, for any pick-one-app flow. */
@Composable
fun AppPickerScreen(
    apps: List<InstalledApp>,
    onAppSelected: (InstalledApp) -> Unit,
    onBack: () -> Unit,
) {
    Surface(color = PureBlack, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 32.dp)) {
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

            AppAlphabeticalList(
                apps = apps,
                onAppClick = onAppSelected,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
