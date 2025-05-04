package tekin.luetfi.amorfati.data.repository

import tekin.luetfi.amorfati.data.remote.LoreApi
import tekin.luetfi.amorfati.domain.model.Lore
import tekin.luetfi.amorfati.domain.repository.LoreRepository
import javax.inject.Inject

class LoreRepositoryImpl @Inject constructor(
    private val api: LoreApi
) : LoreRepository {
    override suspend fun getLore(): Lore = api.fetchLore()

    override suspend fun getCards() = api.fetchNewCards()

}