package tekin.luetfi.amorfati.ui.screens.tabletscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.ui.screens.CHIP_FULL_DECK
import tekin.luetfi.amorfati.ui.screens.email.FlippableCard

@Composable
fun CardGrid(
    columns: Int = 5,
    cardsToShow: List<TarotCard> = listOf(),
    selectedChip: Int = CHIP_FULL_DECK,
    pickedCards: SnapshotStateList<TarotCard> = mutableStateListOf(),
    cardSize: Dp = 200.dp,
    flippable: Boolean = false,
    flipped: Boolean = false,
    scope: CoroutineScope,
    onCardSelected: (TarotCard?) -> Unit = {}
) {


    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = cardsToShow,
            key = { card ->
                if (selectedChip == CHIP_FULL_DECK)
                    card.code
                else Triple(selectedChip, card.code, pickedCards.contains(card))
            }
        ) { card ->
            val isPicked by remember(pickedCards) {
                derivedStateOf {
                    pickedCards.contains(
                        card
                    )
                }
            }
            FlippableCard(
                modifier = Modifier.padding(4.dp),
                card = card,
                size = cardSize,
                isPicked = isPicked,
                flippable = flippable,
                startFlipped = if (pickedCards.contains(card)) false else flipped,
                onTapped = {
                    onCardSelected(it)
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CardGridPreview() {
    CardGrid(
        columns = 5,
        cardSize = 200.dp,
        scope = rememberCoroutineScope()
    )
}