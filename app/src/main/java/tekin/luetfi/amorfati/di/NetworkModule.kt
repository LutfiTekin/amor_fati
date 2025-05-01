package tekin.luetfi.amorfati.di

import android.content.Context
import coil.ImageLoader
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {
    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader =
        ImageLoader.Builder(context)
            // you can tweak memory/disk cache sizes here if you like
            .build()
}



@Module
@InstallIn(SingletonComponent::class)
object RemoteConfigModule {

    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig =
        getInstance().apply {
            // set minimum fetch interval to e.g. 1 hour in prod
            setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build()
            )
        }
}