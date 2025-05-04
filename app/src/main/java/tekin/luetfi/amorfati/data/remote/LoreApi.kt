package tekin.luetfi.amorfati.data.remote

import retrofit2.http.GET
import tekin.luetfi.amorfati.domain.model.CardsResponse
import tekin.luetfi.amorfati.domain.model.Lore


interface LoreApi {

    @GET("lore.json")
    suspend fun fetchLore(): Lore

    @GET("cards.json")
    suspend fun fetchNewCards(): CardsResponse
}
