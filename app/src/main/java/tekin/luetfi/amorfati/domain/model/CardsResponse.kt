package tekin.luetfi.amorfati.domain.model

import tekin.luetfi.amorfati.data.remote.dto.TarotCardDTO

data class CardsResponse(
    val cards: List<TarotCardDTO>,
    val f8Cards: List<TarotCardDTO>,
    val locationCards: List<TarotCardDTO>,
    val extraCards: List<TarotCardDTO>
) {

    val fullDeck =
        (cards + f8Cards + locationCards + extraCards.map { it.copy(online = true) })
            .map { it.toCard }

    val scannableDeck =
        (cards + f8Cards + extraCards.map { it.copy(online = true) })
            .map { it.toCard }

}