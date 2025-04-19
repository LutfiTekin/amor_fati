package tekin.luetfi.amorfati.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import tekin.luetfi.amorfati.data.remote.SendGridApi
import tekin.luetfi.amorfati.data.repository.MailComposerRepositoryImpl
import tekin.luetfi.amorfati.domain.repository.MailComposerRepository
import tekin.luetfi.amorfati.domain.use_case.SendEmailUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSendGridApi(retrofit: Retrofit): SendGridApi = retrofit.create(SendGridApi::class.java)

    @Provides
    @Singleton
    fun provideOrderRepository(api: SendGridApi): MailComposerRepository = MailComposerRepositoryImpl(api)

    @Provides
    fun provideUseCase(repository: MailComposerRepository): SendEmailUseCase = SendEmailUseCase(repository)


}