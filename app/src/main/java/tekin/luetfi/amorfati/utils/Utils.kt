package tekin.luetfi.amorfati.utils

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONException
import org.json.JSONObject
import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.data.remote.dto.TarotReadingJson
import tekin.luetfi.amorfati.data.remote.dto.TarotReadingJsonRecipient
import tekin.luetfi.amorfati.domain.model.TarotCard

val String.selectedCards: List<TarotCard>
    get() {
        return try {
            // Build Moshi instance with Kotlin support
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            // Create adapter for our JSON data class
            val adapter = moshi.adapter(TarotReadingJson::class.java)

            // Parse the JSON string
            val reading = adapter.fromJson(this)
                ?: throw IllegalArgumentException("Invalid JSON for Tarot reading")

            // Extract the card URLs in the order they appear
            val selectedUrls = listOf(
                reading.first_card_url,
                reading.second_card_url,
                reading.third_card_url,
                reading.fourth_card_url
            )

            // Map URLs back to TarotCard objects, preserving order
            selectedUrls.mapNotNull { code ->
                Deck.cards.firstOrNull { it.code == code }
            }
        }catch (e: Exception){
            Deck.cards.shuffled()
        }
    }

val String.recipient: EmailAddress
    get() {
        return try {
            // Build Moshi instance with Kotlin support
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            // Create adapter for our JSON data class
            val adapter = moshi.adapter(TarotReadingJsonRecipient::class.java)

            // Parse the JSON string
            val reading = adapter.fromJson(this)
                ?: throw IllegalArgumentException("Invalid JSON for Tarot reading")

            reading.toMail
        }catch (e: Exception){
            EmailAddress("", null)
        }
    }

val String.validatedJSON: String
    get() {
        return try {
            JSONObject(this)  // validate JSON
            this
        } catch (e: JSONException) {
            "Invalid JSON try again..."
        }
    }


/** Returns the English ordinal suffix for a day (1→st, 2→nd, 3→rd, 4→th, … 11–13→th, etc.) */
private fun Int.ordinalSuffix(): String = when {
    this in 11..13 -> "th"
    this % 10 == 1 -> "st"
    this % 10 == 2 -> "nd"
    this % 10 == 3 -> "rd"
    else           -> "th"
}

/**
 * Formats an epochMillis into a string like "21st of April 2025 11:11"
 */
fun formattedTarotDateTime(epochMillis: Long = System.currentTimeMillis()): String {

    val zone: ZoneId = ZoneId.systemDefault()
    val zdt: ZonedDateTime = Instant.ofEpochMilli(epochMillis).atZone(zone)


    val day    = zdt.dayOfMonth
    val month  = zdt.format(DateTimeFormatter.ofPattern("MMMM"))    // full month name
    val year   = zdt.year
    val hour   = zdt.format(DateTimeFormatter.ofPattern("HH"))      // 24‑hour, zero‑padded
    val minute = zdt.format(DateTimeFormatter.ofPattern("mm"))      // zero‑padded minute

    return "${day}${day.ordinalSuffix()} of $month $year $hour:$minute"
}
