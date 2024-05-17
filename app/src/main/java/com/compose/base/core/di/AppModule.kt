package com.compose.base.core.di

import android.content.Context
import com.compose.base.BuildConfig
import com.compose.base.R
import com.compose.base.core.Constants.JSON_AUTH_HEADER
import com.compose.base.core.Constants.JSON_BEARER_PREFIX
import com.compose.base.data.dataSource.local.dataStore.UserPreferencesDataStore
import com.compose.base.data.dataSource.remote.AuthService
import com.compose.base.data.dataSource.remote.HomeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(dataStore: UserPreferencesDataStore): Interceptor =
        Interceptor { chain ->
            val authenticatedRequest = chain.request().newBuilder()
            runBlocking {
                dataStore.userDetails.first()?.token?.let {
                    authenticatedRequest.header(JSON_AUTH_HEADER, "$JSON_BEARER_PREFIX $it")
                }
            }
            chain.proceed(authenticatedRequest.build())
        }

    @Provides
    @Singleton
    fun provideHttpClient(authenticator: Interceptor): OkHttpClient =
        OkHttpClient.Builder().connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS).addNetworkInterceptor(authenticator).apply {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(loggingInterceptor)
                }
            }.build()

    @Provides
    @Singleton
    fun providesConverterFactory(): Converter.Factory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun providesAuthService(
        @ApplicationContext context: Context,
        client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): AuthService = Retrofit.Builder().baseUrl(context.getString(R.string.base_url))
        .addConverterFactory(converterFactory).client(client).build()
        .create(AuthService::class.java)

    @Provides
    @Singleton
    fun providesDriverService(
        @ApplicationContext context: Context,
        client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): HomeService = Retrofit.Builder().baseUrl(context.getString(R.string.base_url))
        .addConverterFactory(converterFactory).client(client).build()
        .create(HomeService::class.java)
}