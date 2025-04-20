package tekin.luetfi.amorfati.data.remote.dto

import com.squareup.moshi.Json

data class TarotReadingJson(
    val first_card_url: String,
    val second_card_url: String,
    val third_card_url: String,
    val fourth_card_url: String
)

data class TarotReadingJsonRecipient(
    private val name: String,
    private val email: String
){
    val toMail: EmailAddress
        get() = EmailAddress(email, name)
}