package tekin.luetfi.amorfati.data.remote

import retrofit2.http.GET
import tekin.luetfi.amorfati.domain.model.Lore


interface LoreApi {
    /**
     * Assuming your Retrofit baseUrl is "https://lutfitek.in/"
     * this will fetch https://lutfitek.in/assets/lore.json
     */
    @GET("assets/lore.json")
    suspend fun fetchLore(): Lore
}
