package tekin.luetfi.amorfati.data.remote

import retrofit2.http.GET
import tekin.luetfi.amorfati.domain.model.Lore


interface LoreApi {

    @GET("assets/lore.json")
    suspend fun fetchLore(): Lore
}
