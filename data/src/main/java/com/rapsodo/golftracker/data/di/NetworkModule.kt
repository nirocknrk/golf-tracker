package com.rapsodo.golftracker.data.di

import com.rapsodo.golftracker.data.remote.api.GolfApiService
import com.rapsodo.golftracker.data.remote.interceptor.MockInterceptor
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
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module that wires the entire networking stack as singletons.
 *
 * Dependency chain:
 *   [Moshi] → [OkHttpClient] → [Retrofit] → [GolfApiService]
 *
 * Where network calls happen:
 *   [GolfApiService] is consumed exclusively by the two RemoteMediators
 *   ([PlayerRemoteMediator] and [ShotRemoteMediator]). Neither ViewModels
 *   nor repositories call the API directly — the mediators are the only
 *   network entry points in the app.
 *
 * Mock vs. real API:
 *   When `BuildConfig.USE_MOCK_API = true` (set in `:data/build.gradle.kts`),
 *   [MockInterceptor] intercepts every OkHttp request and returns hardcoded
 *   JSON payloads for 20 players and 15 shots per player without ever hitting
 *   the network. Logging still passes through so you can see the "responses"
 *   in Logcat tagged "OkHttp".
 *
 * Logging:
 *   [HttpLoggingInterceptor] forwards every request/response line to Timber,
 *   which in debug builds propagates to Logcat. Release builds produce no output
 *   because no Timber tree is planted (see [GolfApp]).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Moshi JSON parser with [KotlinJsonAdapterFactory] for data-class support.
     * Used by Retrofit's [MoshiConverterFactory] to deserialise [PlayerDto] and
     * [ShotDto] responses.
     */
    @Provides @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    /**
     * OkHttp client shared by the Retrofit instance.
     *
     * Interceptor order matters:
     *   1. [MockInterceptor] — short-circuits the call entirely when mock is on.
     *   2. [HttpLoggingInterceptor] — logs the (possibly mocked) request/response.
     */
    @Provides @Singleton
    fun provideOkHttpClient(mockInterceptor: MockInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor { Timber.tag("OkHttp").d(it) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Retrofit singleton bound to the production base URL.
     * The URL is only reached when [MockInterceptor] is disabled.
     */
    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.rapsodo-golf.com/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    /**
     * Retrofit-generated implementation of [GolfApiService].
     * Injected into [PlayerRemoteMediator] and [ShotRemoteMediator] via
     * their [PlayerRepositoryImpl] and [ShotRepositoryImpl] constructors.
     */
    @Provides @Singleton
    fun provideGolfApiService(retrofit: Retrofit): GolfApiService =
        retrofit.create(GolfApiService::class.java)
}
