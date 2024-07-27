package com.compose.base.core.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.protobuf.InvalidProtocolBufferException
import com.compose.base.UserPreferences
import com.compose.base.core.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

/**
 * This module provides a DataStore instance for storing user preferences using Protocol Buffers.
 * It defines a custom serializer for UserPreferences objects and retrieves the DataStore instance
 * specific to user preferences from the application context.
 */
@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    /**
     * Creates a lazy delegate for the user preferences DataStore.
     * This leverages the dataStore delegate from Jetpack DataStore to create a type-safe DataStore instance
     * for UserPreferences objects.
     *
     * @param Context: The application context.
     * @return A lazy delegate for the user preferences DataStore.
     */
    private val Context.userPreferencesDataStore: DataStore<UserPreferences> by dataStore(
        fileName = Constants.KEY_USER_PREFERENCES,
        serializer = object : Serializer<UserPreferences> {
            override val defaultValue: UserPreferences
                get() = UserPreferences.getDefaultInstance()

            override suspend fun readFrom(input: InputStream): UserPreferences {
                try {
                    return UserPreferences.parseFrom(input)
                } catch (exception: InvalidProtocolBufferException) {
                    Firebase.crashlytics.recordException(exception)
                    throw CorruptionException("Cannot read proto.", exception)
                }
            }

            override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
                t.writeTo(output)
            }
        },
    )

    /**
     * Provides a singleton instance of the user preferences DataStore.
     * It retrieves the lazy-initialized DataStore instance from the application context.
     *
     * @param app: The application context.
     * @return A singleton DataStore instance for user preferences.
     */
    @Singleton
    @Provides
    fun provideUserPreferencesDataStore(@ApplicationContext app: Context): DataStore<UserPreferences> =
        app.userPreferencesDataStore
}