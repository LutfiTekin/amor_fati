package tekin.luetfi.amorfati.data.remote.dto

import tekin.luetfi.amorfati.domain.model.TarotCard

data class TarotCardDTO(
    val name: String,
    val code: String,
    val online: Boolean = false
){
    val toCard: TarotCard
        get() = TarotCard(name, code, online)
}
