package tekin.luetfi.amorfati.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tekin.luetfi.amorfati.utils.ASSETS_DIR
import tekin.luetfi.amorfati.utils.Deck
import tekin.luetfi.amorfati.utils.Defaults

/**
 * Represents a Tarot card with its name, image URL, and a unique code.
 *
 * This data class is used to store the essential information about a single
 * Tarot card, making it easy to manage and use in applications that deal with
 * Tarot readings or card collections.
 *
 * @property name The name of the Tarot card (e.g., "The Fool", "The Magician").
 * @property code A unique code or identifier for the Tarot card.
 */
@Parcelize
data class TarotCard(
    val name: String,
    val code: String
) : Parcelable {
    val imageUrl: String
        get() {
            return Defaults.imageHostDir + code.lowercase() + ".png"
        }

    val localImageFile: String
        get() {
            return ASSETS_DIR + code.lowercase() + ".png"
        }

    val cardLore: CardLore?
        get() {
            return Defaults.mainLore.find { it.code == code }
        }

    val backSideImage: String
        get() {
            if (isLocationCard)
                return ASSETS_DIR + code.lowercase() + "_back.png"
            if (isF8Card)
                return "${ASSETS_DIR}f8_back.png"
            return "${ASSETS_DIR}back.png"
        }

    private val isLocationCard: Boolean
        get() {
            return Deck.locationCards.any { it.code == code }
        }

    val isF8Card: Boolean
        get() = Deck.f8Cards.any { it.code == code }

}

