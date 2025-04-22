package tekin.luetfi.amorfati.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton
import tekin.luetfi.amorfati.data.repository.LoreRepositoryImpl
import tekin.luetfi.amorfati.domain.repository.LoreRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class LoreModule {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class LoreRepositoryModule {
        @Binds
        @Singleton
        abstract fun bindLoreRepository(
            impl: LoreRepositoryImpl
        ): LoreRepository
    }
}
