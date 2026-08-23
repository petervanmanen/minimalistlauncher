package nl.petervanmanen.minimalauncher

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import nl.petervanmanen.minimalauncher.ui.pager.RootPagerScreen
import nl.petervanmanen.minimalauncher.ui.theme.MinimalLauncherTheme
import nl.petervanmanen.minimalauncher.ui.theme.PureBlack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as MinimalLauncherApplication).container

        lifecycleScope.launch {
            container.settingsRepository.allowRotation.collect { allow ->
                requestedOrientation = if (allow) {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }
        }

        setContent {
            MinimalLauncherTheme {
                Surface(color = PureBlack, modifier = Modifier.fillMaxSize()) {
                    RootPagerScreen(container = container)
                }
            }
        }
    }
}
