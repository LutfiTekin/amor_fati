package tekin.luetfi.amorfati

import android.content.Intent
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
import dagger.hilt.android.AndroidEntryPoint
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
                // session‑only flag
                var showSettings by remember { mutableStateOf(false) }
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(if (showSettings) "Settings" else "Compose Email") },
                            actions = {
                                IconButton(onClick = { showSettings = !showSettings }) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings"
                                    )
                                }
                            }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    if (showSettings) {
                        SettingsScreen(
                            modifier = Modifier.padding(innerPadding).fillMaxSize(),
                            onDone = { showSettings = false })
                    } else {
                        EmailComposeScreen(
                            modifier = Modifier.padding(innerPadding).fillMaxSize(),
                            snackbarHostState = snackbarHostState
                        )
                    }

                }
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // this updates the Activity.intent property so Compose can observe it
        setIntent(intent)
    }
}



