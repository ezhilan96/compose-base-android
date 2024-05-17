package com.compose.base.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.compose.base.core.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
        name = Constants.KEY_USER_PREFERENCES
    )

    @Singleton
    @Provides
    fun provideUserPreferencesDataStore(
        @ApplicationContext app: Context
    ): DataStore<Preferences> = app.userPreferencesDataStore
}