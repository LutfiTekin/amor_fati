package tekin.luetfi.amorfati.domain.repository

import tekin.luetfi.amorfati.domain.model.CardsResponse
import tekin.luetfi.amorfati.domain.model.Lore

interface LoreRepository {

    suspend fun getLore(): Lore

    suspend fun getCards(): CardsResponse
}