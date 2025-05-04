package tekin.luetfi.amorfati.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tekin.luetfi.amorfati.domain.use_case.GetNewCardsUseCase
import tekin.luetfi.amorfati.utils.Deck
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getNewCardsUseCase: GetNewCardsUseCase
) : ViewModel() {

    fun downloadNewCards(onFinished: () -> Unit) = viewModelScope.launch {
        val cards = getNewCardsUseCase()
        Deck.fullDeck = cards.fullDeck
        Deck.scannableDeck = cards.scannableDeck
        onFinished()
    }

}