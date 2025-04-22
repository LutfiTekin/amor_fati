package tekin.luetfi.amorfati.domain.repository

import tekin.luetfi.amorfati.domain.model.Lore

interface LoreRepository {

    suspend fun getLore(): Lore
}