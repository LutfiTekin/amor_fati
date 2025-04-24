package tekin.luetfi.amorfati.data.remote.dto

import com.squareup.moshi.Json
import java.util.Locale

data class TarotReadingJson(
    val first_card_url: String,
    val second_card_url: String,
    val third_card_url: String,
    val fourth_card_url: String,
    val location_card_url: String
)

data class TarotReadingJsonRecipient(
    private val name: String,
    private val email: String
){
    val toMail: EmailAddress
        get() = EmailAddress(email, name)
}

data class TarotReadingMetaphorJson(
    val metaphor_image_quote: String?
){
    override fun toString(): String {
        if (metaphor_image_quote == null)
            throw Exception("Metaphor image quote is null")
        return metaphor_image_quote.replace(" ","_").lowercase(Locale.US)
    }
}