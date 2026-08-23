package nl.petervanmanen.minimalauncher.ui.allapps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import nl.petervanmanen.minimalauncher.data.model.InstalledApp
import nl.petervanmanen.minimalauncher.ui.theme.DimWhite
import nl.petervanmanen.minimalauncher.ui.theme.PureBlack

/**
 * Long-press action menu for an app row. Uninstall and Hide are omitted for
 * apps that can't actually be removed (see [InstalledApp.canRemove]) — App
 * info is always available since it works for any installed package.
 */
@Composable
fun AppContextMenu(
    app: InstalledApp,
    onDismiss: () -> Unit,
    onUninstall: () -> Unit,
    onHide: () -> Unit,
    onAppInfo: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = PureBlack) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = app.label,
                    color = DimWhite,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                if (app.canRemove) {
                    MenuRow("Uninstall") {
                        onUninstall()
                        onDismiss()
                    }
                    MenuRow("Hide") {
                        onHide()
                        onDismiss()
                    }
                }
                MenuRow("App info") {
                    onAppInfo()
                    onDismiss()
                }
            }
        }
    }
}

@Composable
private fun MenuRow(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
    )
}
