package tekin.luetfi.amorfati.domain.model


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Lore(
    val cards: List<CardLore>
)

@JsonClass(generateAdapter = true)
data class CardLore(
    val code: String,
    val effects: List<String>,
    val story: String
)