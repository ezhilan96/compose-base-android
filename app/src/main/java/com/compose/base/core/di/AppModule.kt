package com.compose.base.core.di

import android.content.Context
import com.compose.base.BuildConfig
import com.compose.base.core.Constants.JSON_AUTH_HEADER
import com.compose.base.core.Constants.JSON_BEARER_PREFIX
import com.compose.base.data.dataSource.remote.AuthService
import com.compose.base.data.dataSource.remote.UserService
import com.compose.base.domain.repository.core.PreferencesRepository
import com.compose.base.presentation.util.getAppUrl
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

/**
 * This module provides dependencies for network communication and interacting with various application services.
 * It configures an OkHttpClient instance with timeouts, an optional logging interceptor (in debug builds), and an
 * Interceptor that adds an authorization header based on user credentials. Additionally, it provides a GsonConverterFactory
 * for data serialization/deserialization and Retrofit instances for interacting with specific application services
 * (AuthService, DriverService, etc.).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides an Interceptor that adds an authorization header to outgoing requests
     * if the user is logged in (user's token stored in PreferencesRepository).
     *
     * @param preferencesRepository: The repository containing user data.
     * @return An Interceptor that adds the authorization header if a token exists.
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(preferencesRepository: PreferencesRepository): Interceptor =
        Interceptor { chain ->
            val authenticatedRequest = chain.request().newBuilder()
            runBlocking {
                preferencesRepository.userDataFlow.first()?.token?.let {
                    authenticatedRequest.header(JSON_AUTH_HEADER, "$JSON_BEARER_PREFIX $it")
                }
            }
            chain.proceed(authenticatedRequest.build())
        }

    /**
     * Provides an OkHttpClient instance configured with timeouts and the provided Interceptor.
     * In debug builds, it also adds a logging Interceptor for detailed network request/response logs.
     *
     * @param authenticator: The Interceptor for adding authorization header.
     * @return An OkHttpClient instance configured for timeouts, interception, and optional logging.
     */
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

    /**
     * Provides a GsonConverterFactory instance for converting JSON data to objects and vice versa.
     *
     * @return A GsonConverterFactory instance.
     */
    @Provides
    @Singleton
    fun providesConverterFactory(): Converter.Factory = GsonConverterFactory.create()

    /**
     * Provides a AuthService Retrofit instance configured with the base URL (fetched from context),
     * ConverterFactory, and OkHttpClient.
     *
     * @param context: The application context.
     * @param client: The configured OkHttpClient instance.
     * @param converterFactory: The ConverterFactory for data serialization/deserialization.
     * @return A Retrofit instance configured for the AuthService interface.
     */
    @Provides
    @Singleton
    fun providesAuthService(
        @ApplicationContext context: Context,
        client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): AuthService = Retrofit.Builder().baseUrl(context.getAppUrl())
        .addConverterFactory(converterFactory).client(client).build()
        .create(AuthService::class.java)

    /**
     * Provides a DriverService Retrofit instance configured with the base URL (fetched from context),
     * ConverterFactory, and OkHttpClient.
     *
     * @param context: The application context.
     * @param client: The configured OkHttpClient instance.
     * @param converterFactory: The ConverterFactory for data serialization/deserialization.
     * @return A Retrofit instance configured for the AuthService interface.
     */
    @Provides
    @Singleton
    fun providesUserService(
        @ApplicationContext context: Context,
        client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): UserService = Retrofit.Builder().baseUrl(context.getAppUrl())
        .addConverterFactory(converterFactory).client(client).build()
        .create(UserService::class.java)
}