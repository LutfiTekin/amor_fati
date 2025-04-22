package tekin.luetfi.amorfati.domain.use_case

import tekin.luetfi.amorfati.domain.model.Lore
import tekin.luetfi.amorfati.domain.repository.LoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLoreUseCase @Inject constructor(
    private val repository: LoreRepository
) {
    suspend operator fun invoke(): Lore = repository.getLore()
}