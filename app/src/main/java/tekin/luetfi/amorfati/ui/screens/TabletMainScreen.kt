package tekin.luetfi.amorfati.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tekin.luetfi.amorfati.ui.screens.email.EmailComposeScreen
import tekin.luetfi.amorfati.ui.screens.email.FlippableCard
import tekin.luetfi.amorfati.utils.Deck

@Composable
fun TabletMainScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState
) {
    Row(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement   = Arrangement.spacedBy(16.dp)
        ) {
            items(Deck.fullDeck) { card ->
                FlippableCard(
                    card = card,
                    size = 250.dp,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }

        // Right pane: the email compose form (1/4th of width)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            EmailComposeScreen(
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState
            )
        }
    }
}
