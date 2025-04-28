package tekin.luetfi.amorfati.ui.screens.drawingritual

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.ui.components.FlippableCard
import tekin.luetfi.amorfati.utils.Deck
import tekin.luetfi.amorfati.utils.sendToClipBoard

@Composable
fun DrawingRitualPane(
    selectedCards: MutableList<TarotCard>,
    onCardFlipped: (TarotCard) -> Unit = {},
    onRitualFinished: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    // 0 = pick 1 F8 card, 1 = pick 2 more, 2 = confirm final
    var phase by remember { mutableIntStateOf(0) }
    val flips = selectedCards.size

    val pool = when (phase) {
        0 -> Deck.f8Cards
        1 -> Deck.cards
        else -> Deck.cards
    }

    if (phase < 2) {
        Column(
            modifier = Modifier
                .fillMaxWidth()           // only fill width
                .wrapContentHeight(),     // shrink to content height
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (phase == 0) "🔮 Flip one F8-card" else "🔮 Flip two more cards",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (phase == 0) 4 else 8),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()   // grid only as tall as its rows
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                items(pool, key = { it.code }) { card ->
                    FlippableCard(
                        card = card,
                        size = 250.dp,
                        flippable = true,
                        startFlipped = true,
                        onFlip = { flippedCard, isFront ->
                            if (!isFront) return@FlippableCard
                            scope.launch {
                                selectedCards.add(flippedCard)
                                flippedCard.sendToClipBoard(context)
                                onCardFlipped(flippedCard)
                                delay(2000)
                                phase = when {
                                    phase == 0           -> 1
                                    flips + 1 >= 3       -> 2
                                    else                 -> phase
                                }
                            }
                        }
                    )
                }
            }
        }
    } else {
        // phase == 2: final confirmation
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔮 Tap any card to confirm your final draw",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                items(pool, key = { it.code }) { card ->
                    FlippableCard(
                        card = card,
                        size = 120.dp,
                        flippable = false,
                        startFlipped = true,
                        onTapped = { tapped ->
                            if (tapped == null) return@FlippableCard
                            scope.launch {
                                selectedCards.add(card)
                                card.sendToClipBoard(context)
                                onCardFlipped(card)
                                delay(5000)
                                onRitualFinished()
                            }
                        }
                    )
                }
            }
        }
    }
}
