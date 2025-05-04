package tekin.luetfi.amorfati.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import tekin.luetfi.amorfati.data.remote.dto.EmailAddress
import tekin.luetfi.amorfati.data.remote.dto.TarotReadingJson
import tekin.luetfi.amorfati.data.remote.dto.TarotReadingJsonRecipient
import tekin.luetfi.amorfati.data.remote.dto.TarotReadingMetaphorJson
import tekin.luetfi.amorfati.domain.model.TarotCard
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

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
                reading.fourth_card_url,
                reading.location_card_url
            )

            // Map URLs back to TarotCard objects, preserving order
            selectedUrls.mapNotNull { code ->
                Deck.fullDeck.firstOrNull { it.code == code }
            }
        } catch (e: Exception) {
            Deck.fullDeck.shuffled()
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
        } catch (e: Exception) {
            EmailAddress("", null)
        }
    }

val String.metaphorImageName: String
    get() {
        return try {
            // Build Moshi instance with Kotlin support
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            // Create adapter for our JSON data class
            val adapter = moshi.adapter(TarotReadingMetaphorJson::class.java)

            // Parse the JSON string
            val oneliner = adapter.fromJson(this)
                ?: throw IllegalArgumentException("Invalid JSON for Tarot reading")

            oneliner.toString()
        } catch (e: Exception) {
            UUID.randomUUID().toString()
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
    else -> "th"
}

/**
 * Formats an epochMillis into a string like "21st of April 2025 11:11"
 */
fun formattedTarotDateTime(epochMillis: Long = System.currentTimeMillis()): String {

    val zone: ZoneId = ZoneId.systemDefault()
    val zdt: ZonedDateTime = Instant.ofEpochMilli(epochMillis).atZone(zone)


    val day = zdt.dayOfMonth
    val month = zdt.format(DateTimeFormatter.ofPattern("MMMM"))    // full month name
    val year = zdt.year
    val hour = zdt.format(DateTimeFormatter.ofPattern("HH"))      // 24‑hour, zero‑padded
    val minute = zdt.format(DateTimeFormatter.ofPattern("mm"))      // zero‑padded minute

    return "${day}${day.ordinalSuffix()} of $month $year, $hour:$minute"
}

suspend fun TarotCard.sendToClipBoard(context: Context) {
    val loader = ImageLoader(context)
    val req = ImageRequest.Builder(context)
        .data(image)
        .build()
    val result = loader.execute(req)
    if (result !is SuccessResult) return

    val drawable = result.drawable as BitmapDrawable
    val bitmap = drawable.bitmap

    val cacheFile = withContext(Dispatchers.IO) {
        File.createTempFile("card_${code}_", ".png", context.cacheDir)
    }
    withContext(Dispatchers.IO) {
        FileOutputStream(cacheFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    val authority = "${context.packageName}.provider"
    val uri = FileProvider.getUriForFile(context, authority, cacheFile)

    val clip = ClipData.newUri(
        context.contentResolver,
        "Card Image",
        uri
    )
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    clipboard.setPrimaryClip(clip)
}


fun <T> animateRevealPreselected(
    preselected: List<T>,
    pool: List<T>,
    spinRounds: Int = 5,
    spinDelayMs: Long = 100L
): Flow<List<T>> = flow {
    // Dedupe your targets once, then bail early if there’s nothing to do.
    val targets = preselected.distinct()
    if (targets.isEmpty() || pool.isEmpty()) {
        emit(targets)
        return@flow
    }

    // Build up the revealed list step by step
    val revealed = mutableListOf<T>()
    for (next in targets) {
        // “Roulette” spins: pick from pool minus whatever’s already in ‘revealed’
        repeat(spinRounds) {
            val available = pool - revealed.toSet()
            val preview   = available.randomOrNull() ?: next
            emit(revealed + preview)
            delay(spinDelayMs)
        }
        // Land on the real card
        revealed += next
        emit(revealed.toList())
        delay(spinDelayMs * 2)
    }
}


