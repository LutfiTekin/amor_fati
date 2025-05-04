package tekin.luetfi.amorfati.domain.use_case

import tekin.luetfi.amorfati.domain.model.CardsResponse
import tekin.luetfi.amorfati.domain.model.Lore
import tekin.luetfi.amorfati.domain.repository.LoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNewCardsUseCase @Inject constructor(
    private val repository: LoreRepository
) {
    suspend operator fun invoke(): CardsResponse = repository.getCards()
}