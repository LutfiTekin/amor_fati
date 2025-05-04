package tekin.luetfi.amorfati.ui.screens.cardrecognition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.utils.Deck
import javax.inject.Inject


@HiltViewModel
class CardRecognitionViewModel @Inject constructor(): ViewModel() {

    private val knownCards = Deck.f8Cards + Deck.cards

    private val knownCardNames = knownCards
        .map { it.name.lowercase() }

    private val minimumCardNameLength = knownCardNames.minOf { it.length } - 1


    private val _foundCardFlow = MutableSharedFlow<TarotCard>(replay = 0)

    val foundCardFlow = _foundCardFlow.asSharedFlow()

    fun searchForValidCard(scannedText: String) = viewModelScope.launch(Dispatchers.Default) {
        //Skip short text
        if (scannedText.length < minimumCardNameLength)
            return@launch
        val text = scannedText.lowercase().replace("&","AND")
        val card = knownCardNames.firstOrNull { it in text }
        if (card == null)
            return@launch
        val tarotCard = knownCards.first { it.name.lowercase() == card }
        _foundCardFlow.emit(tarotCard)
    }

}