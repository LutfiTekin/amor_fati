package tekin.luetfi.amorfati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.res.painterResource
import dagger.hilt.android.AndroidEntryPoint
import tekin.luetfi.amorfati.ui.screens.cardrecognition.CardRecognitionScreen
import tekin.luetfi.amorfati.ui.screens.drawingritual.DrawingRitualScreen
import tekin.luetfi.amorfati.ui.screens.email.EmailComposeScreen
import tekin.luetfi.amorfati.ui.screens.settings.SettingsScreen
import tekin.luetfi.amorfati.ui.screens.tabletscreen.TabletMainScreen
import tekin.luetfi.amorfati.ui.theme.AmorFatiTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var screen by mutableStateOf(Screen.Main)

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
                val title = when (screen) {
                    Screen.Main -> if (isTablet) "Amor Fati" else "Compose Email"
                    Screen.DrawingRitual -> "Drawing Ritual"
                    Screen.Settings -> "Settings"
                    Screen.CardRecognition -> "Card Recognition"
                }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(title) },
                            navigationIcon = {
                                if (screen != Screen.Main) {
                                    IconButton(onClick = { screen = Screen.Main }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Home"
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(onClick = { screen = Screen.Settings }) {
                                    Icon(Icons.Default.Settings, null)
                                }
                                if (isTablet) {
                                    IconButton(onClick = { screen = Screen.DrawingRitual }) {
                                        Icon(Icons.Default.PlayArrow, null)
                                    }
                                    IconButton(onClick = { screen = Screen.CardRecognition }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.outline_camera_24),
                                            null
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        if (!isTablet) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = screen == Screen.Main,
                                    onClick = { screen = Screen.Main },
                                    icon = {
                                        Icon(
                                            Icons.Default.Home,
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = screen == Screen.DrawingRitual,
                                    onClick = { screen = Screen.DrawingRitual },
                                    icon = {
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            contentDescription = "Ritual"
                                        )
                                    },
                                    label = { Text("Ritual") }
                                )
                                NavigationBarItem(
                                    selected = screen == Screen.CardRecognition,
                                    onClick = { screen = Screen.CardRecognition },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.outline_camera_24),
                                            contentDescription = "Camera"
                                        )
                                    },
                                    label = { Text("Camera") }
                                )
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { inner ->
                    when (screen) {
                        Screen.Main -> {
                            if (isTablet) {
                                TabletMainScreen(
                                    modifier = Modifier
                                        .padding(inner)
                                        .fillMaxSize()
                                )
                            } else {
                                EmailComposeScreen(
                                    modifier = Modifier
                                        .padding(inner)
                                        .fillMaxSize(),
                                    snackbarHostState = snackbarHostState
                                )
                            }
                        }

                        Screen.DrawingRitual -> {
                            DrawingRitualScreen(
                                modifier = Modifier
                                    .padding(inner)
                                    .fillMaxSize(), snackbarHostState = snackbarHostState, isTablet
                            )
                        }

                        Screen.Settings -> {
                            SettingsScreen(
                                modifier = Modifier
                                    .padding(inner)
                                    .fillMaxSize(),
                                onDone = {
                                    showSettings = false
                                    screen = Screen.Main
                                }
                            )
                        }

                        Screen.CardRecognition -> {
                            CardRecognitionScreen(
                                modifier = Modifier
                                    .padding(inner)
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Screen {
    Main, DrawingRitual, Settings, CardRecognition
}
