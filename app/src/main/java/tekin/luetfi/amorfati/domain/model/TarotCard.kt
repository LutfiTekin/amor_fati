package tekin.luetfi.amorfati.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tekin.luetfi.amorfati.utils.Defaults
import tekin.luetfi.amorfati.utils.IMAGE_HOST_DIR

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
            return IMAGE_HOST_DIR + code.lowercase() + ".png"
        }

    val cardLore: CardLore?
        get() {
            return Defaults.mainLore.find { it.code == code }
        }

    val backsideImageUrl: String
        get() {
            return IMAGE_HOST_DIR + "back.png"
        }

}

