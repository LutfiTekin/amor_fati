package tekin.luetfi.amorfati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import dagger.hilt.android.AndroidEntryPoint
import tekin.luetfi.amorfati.ui.screens.TabletMainScreen
import tekin.luetfi.amorfati.ui.screens.email.EmailComposeScreen
import tekin.luetfi.amorfati.ui.screens.settings.SettingsScreen
import tekin.luetfi.amorfati.ui.theme.AmorFatiTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AmorFatiTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val configuration = LocalConfiguration.current
                val isTablet = configuration.screenWidthDp >= 600
                var showSettings by remember { mutableStateOf(false) }
                val title = if (showSettings) "Settings" else {
                    if (isTablet) "Amor Fati" else "Compose Email"
                }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(title) },
                            actions = {
                                IconButton(onClick = { showSettings = !showSettings }) {
                                    Icon(Icons.Default.Settings, null)
                                }
                            }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { inner ->
                    if (showSettings) {
                        SettingsScreen(
                            modifier = Modifier.padding(inner).fillMaxSize(),
                            onDone = { showSettings = false }
                        )
                    } else {
                        if (isTablet){
                            TabletMainScreen(modifier = Modifier.padding(inner).fillMaxSize())
                        }else {
                            EmailComposeScreen(
                                modifier = Modifier.padding(inner).fillMaxSize(),
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                }
            }
        }
    }
}
