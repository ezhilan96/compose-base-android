package com.compose.base.core.di

import com.compose.base.data.repository.core.NetworkConnectionRepositoryImpl
import com.compose.base.data.repository.core.PreferencesRepositoryImpl
import com.compose.base.data.repository.user.AuthRepositoryImpl
import com.compose.base.data.repository.user.UserRepositoryImpl
import com.compose.base.domain.repository.core.NetworkConnectionRepository
import com.compose.base.domain.repository.core.PreferencesRepository
import com.compose.base.domain.repository.user.AuthRepository
import com.compose.base.domain.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This module provides bindings for various data repository interfaces and their concrete implementations.
 * It uses Dagger's @Binds methods to ensure dependency injection for these repositories throughout the application.
 * The provided repositories handle data access for functionalities like sockets, authentication, user data
 * (potentially driver data), user preferences, B2C bookings, etc.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDriverRepository(repositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(repositoryImpl: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindNetworkConnectionRepository(repositoryImpl: NetworkConnectionRepositoryImpl): NetworkConnectionRepository

}