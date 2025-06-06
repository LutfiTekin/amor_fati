package tekin.luetfi.amorfati.ui.screens.email

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.ui.components.FlippableCard
import tekin.luetfi.amorfati.utils.Deck


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardBottomSheet(
    card: TarotCard,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        CardInfo(card)
    }
}

@Composable
fun CardInfo(card: TarotCard) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) Card image
        FlippableCard(
            card = card,
            size = 300.dp
        )
        if (card.name.isNotBlank() && Deck.locationCards.contains(card)) {
            Text(
                text = card.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // 2) Lore sections (only if present)
        card.cardLore?.let { lore ->
            // Effects
            if (lore.effects.isNotEmpty()) {
                Text(
                    text = "Effects",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 16.dp)
                ) {
                    lore.effects.forEach { effect ->
                        Text(
                            text = "• $effect",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            // Story
            if (lore.story.isNotBlank()) {
                Text(
                    text = "Story",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )
                Text(
                    text = lore.story,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}


