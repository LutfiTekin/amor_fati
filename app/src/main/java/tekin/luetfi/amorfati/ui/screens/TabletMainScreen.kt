package tekin.luetfi.amorfati.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tekin.luetfi.amorfati.ui.screens.email.EmailComposeScreen
import tekin.luetfi.amorfati.ui.screens.email.FlippableCard
import tekin.luetfi.amorfati.utils.Deck

@Composable
fun TabletMainScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    // 1) which chip is selected: 0 = Full Deck, 1 = F8, 2 = Regular
    var selectedChip by remember { mutableIntStateOf(0) }
    val chipLabels = listOf("Full Deck", "F8 Cards", "Regular Cards")

    // 2) choose which list to display
    val cardsToShow = when (selectedChip) {
        0 -> Deck.fullDeck
        1 -> Deck.f8Cards.shuffled()
        else -> Deck.cards.shuffled()
    }

    val (columns, cardSize) = if (selectedChip == 1) {
        // F8: 4 columns, bigger cards
        4 to 250.dp
    } else {
        // Default: 5 columns, smaller cards
        5 to 200.dp
    }

    val flipped = selectedChip != 0

    Row(modifier = modifier.fillMaxSize()) {
        // Left pane: chips + grid
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // -- Chip row --
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chipLabels.forEachIndexed { index, label ->
                    FilterChip(
                        selected = index == selectedChip,
                        onClick = { selectedChip = index },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            // -- Grid of cards --
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement   = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = cardsToShow,
                    key = { card ->
                        selectedChip to card.code
                    }
                ) { card ->
                    FlippableCard(
                        modifier = Modifier.padding(4.dp),
                        card = card,
                        size = cardSize,
                        startFlipped = flipped
                    )
                }
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
