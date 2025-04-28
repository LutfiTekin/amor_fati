package tekin.luetfi.amorfati.ui.screens.drawingritual

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.ui.components.FlippableCard
import tekin.luetfi.amorfati.ui.screens.email.CardInfo
import tekin.luetfi.amorfati.ui.screens.email.EmailComposeScreen
import tekin.luetfi.amorfati.utils.Deck
import tekin.luetfi.amorfati.utils.animateRevealPreselected


@Composable
fun DrawingRitualScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    isTablet: Boolean
) {

    // State for whether the ritual is complete
    var ritualComplete by remember { mutableStateOf(false) }

    var cardToPreview by remember { mutableStateOf<TarotCard?>(null) }

    // TODO: replace with your actual ritual state (selected cards, step, etc)
    val selectedCards = remember { mutableStateListOf<TarotCard>() }

    if (isTablet) {
        Row(modifier = modifier.fillMaxSize()) {
            // LEFT PANE: Drawing ritual or selected‐cards preview
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                if (!ritualComplete) {
                    DrawingRitualPane(
                        selectedCards = selectedCards,
                        onCardFlipped = {
                            cardToPreview = it
                        },
                        onRitualFinished = { ritualComplete = true }
                    )
                } else {
                    SelectedCardsPreview(
                        cards = selectedCards
                    ) {
                        cardToPreview = it
                    }
                }
            }

            // RIGHT PANE: Email composer (always shown, but you may hide until ritualComplete)
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                if (ritualComplete) {
                    EmailComposeScreen(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHostState = snackbarHostState
                    )
                } else {

                    cardToPreview?.let { selectedCard ->
                        val scroll = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scroll)
                        ) {
                            CardInfo(selectedCard)
                        }

                    } ?: run {
                        // you could show a placeholder or instructions here
                        Text(
                            "Complete the ritual to compose your reading",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                }
            }
        }
    } else {
        // PHONE: just show ritual, then swap to email
        Box(modifier = modifier.fillMaxSize()) {
            if (!ritualComplete) {
                DrawingRitualPane(
                    selectedCards = selectedCards,
                    onRitualFinished = { ritualComplete = true }
                )
            } else {
                EmailComposeScreen(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}


@Composable
fun SelectedCardsPreview(cards: List<TarotCard>, cardToPreview: (TarotCard?) -> Unit = {}) {

    var selectedCards by remember { mutableStateOf(listOf<TarotCard>()) }

    LaunchedEffect(Unit) {
        delay(400)
        animateRevealPreselected(
            preselected = cards,
            pool = Deck.cards + Deck.f8Cards
        ).collect { updated ->
            selectedCards = updated
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(selectedCards, key = { it.code }) { card ->
            FlippableCard(
                card = card,
                size = 280.dp,
                flippable = false,
                startFlipped = true,
                onTapped = {
                    cardToPreview(card)
                }
            )
        }
    }
}
