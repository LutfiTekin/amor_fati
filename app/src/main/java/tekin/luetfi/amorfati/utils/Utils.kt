package tekin.luetfi.amorfati.utils

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
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
