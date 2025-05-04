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
class CardRecognitionViewModel @Inject constructor() : ViewModel() {

    private val knownCards = Deck.scannableDeck

    private val maxDistance = 3


    private val _foundCardFlow = MutableSharedFlow<TarotCard>(replay = 0)

    val foundCardFlow = _foundCardFlow.asSharedFlow()

    // Precompute lowercase “and”-normalized names, longest first:
    private val normalizedNames = knownCards
        .map { it.name.lowercase().replace("&", "and") }
        .sortedByDescending { it.length }


    // 2. A helper to find the single best matching card in the OCR result:
    private fun findBestFuzzyMatch(text: com.google.mlkit.vision.text.Text): TarotCard? {
        var bestCard: TarotCard? = null
        var bestDistance = Int.MAX_VALUE

        for (block in text.textBlocks) {
            for (line in block.lines) {
                val lineText = line.text
                    .lowercase()
                    .replace("&", "and")

                // skip ridiculously short lines
                if (lineText.length < normalizedNames.minOf { it.length } - 1) continue

                normalizedNames.forEachIndexed { index, name ->
                    // optionally bail early if name is longer than lineText + slack
                    if (lineText.length + maxDistance < name.length) return@forEachIndexed

                    val dist = levenshtein(name, lineText)
                    if (dist < bestDistance) {
                        bestDistance = dist
                        bestCard = knownCards.first {
                            it.name.lowercase().replace("&", "and") == name
                        }
                    }
                }
            }
        }

        return if (bestDistance <= maxDistance) bestCard else null
    }

    fun searchForValidCard(scannedText: com.google.mlkit.vision.text.Text?) =
        viewModelScope.launch(Dispatchers.Default) {
            val text = scannedText ?: return@launch
            val card = findBestFuzzyMatch(text) ?: return@launch
            _foundCardFlow.emit(card)
        }


    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] +
                            if (a[i - 1] == b[j - 1]) 0 else 1  // substitution
                )
            }
        }
        return dp[a.length][b.length]
    }

}