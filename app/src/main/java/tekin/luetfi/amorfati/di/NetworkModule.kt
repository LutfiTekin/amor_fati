package tekin.luetfi.amorfati.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import tekin.luetfi.amorfati.utils.SEND_GRID_BASE_URL
import tekin.luetfi.amorfati.utils.DEFAULT_TIMEOUT
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named(DEFAULT_TIMEOUT)
    fun provideDefaultTimeOut(): Long = 10000L

    @Provides
    @Singleton
    fun provideOkHTTPClient(
        @Named(DEFAULT_TIMEOUT) defaultTimeOut: Long,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .readTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun providesMoshiConverter(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient, moshiConverter: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder().baseUrl(SEND_GRID_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(moshiConverter).build()

}