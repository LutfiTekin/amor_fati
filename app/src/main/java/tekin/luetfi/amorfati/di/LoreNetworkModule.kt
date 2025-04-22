package tekin.luetfi.amorfati.di


import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import tekin.luetfi.amorfati.data.remote.LoreApi
import tekin.luetfi.amorfati.utils.LORE_API_BASE_URL

@Module
@InstallIn(SingletonComponent::class)
object LoreNetworkModule {

    @Provides
    @Singleton
    @Named("loreRetrofit")
    fun provideLoreRetrofit(
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(LORE_API_BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideLoreService(
        @Named("loreRetrofit") retrofit: Retrofit
    ): LoreApi = retrofit.create(LoreApi::class.java)
}
