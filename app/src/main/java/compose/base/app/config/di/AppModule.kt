package compose.base.app.config.di

import android.content.Context
import compose.base.app.R
import compose.base.app.config.Constants.AUTH_HEADER
import compose.base.app.config.Constants.TOKEN_PREFIX
import compose.base.app.config.util.NetworkConnectivityObserver
import compose.base.app.data.dataSource.local.preference.UserPreferencesDataStore
import compose.base.app.data.dataSource.remote.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ak1.drawbox.BuildConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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
            val token: String? = runBlocking {
                dataStore.userDetails.first()
            }
            if (!token.isNullOrEmpty()) {
                authenticatedRequest.header(AUTH_HEADER, "$TOKEN_PREFIX $token")
            }
            chain.proceed(authenticatedRequest.build())
        }

    @Provides
    @Singleton
    fun provideHttpClient(authenticator: Interceptor) =
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
    fun providesAuthService(
        @ApplicationContext context: Context,
        client: OkHttpClient
    ): AuthService =
        Retrofit.Builder().baseUrl(context.getString(R.string.base_url))
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(AuthService::class.java)

//    @Provides
//    @Singleton
//    fun providesAuthService(
//        @ApplicationContext context: Context,
//        client: OkHttpClient
//    ): AuthService =
//        Retrofit.Builder().baseUrl(context.getString(R.string.base_url))
//            .addConverterFactory(MoshiConverterFactory.create())
//            .client(client)
//            .build()
//            .create(AuthService::class.java)


    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): NetworkConnectivityObserver =
        NetworkConnectivityObserver(context)
}