package com.compose.base.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * This module provides dependency injection for the standard coroutine dispatchers: Default, IO, and Main.
 * It uses `@Provides` methods to offer these dispatchers and custom qualifier annotations to differentiate them.
 */
@InstallIn(SingletonComponent::class)
@Module
object DispatcherModule {

    /**
     * Provides the Default dispatcher using Dispatchers.Default.
     * This dispatcher is suitable for CPU-bound tasks that don't require immediate UI updates.
     *
     * @return The Default coroutine dispatcher.
     */
    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provides the IO dispatcher using Dispatchers.IO.
     * This dispatcher is suitable for I/O bound tasks like network requests and file operations.
     *
     * @return The IO coroutine dispatcher.
     */
    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides the Main dispatcher using Dispatchers.Main.
     * This dispatcher is suitable for tasks that update the UI or interact with the main thread.
     *
     * @return The Main coroutine dispatcher.
     */
    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

/**
 * Qualifier annotation for identifying the Default coroutine dispatcher.
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

/**
 * Qualifier annotation for identifying the IO coroutine dispatcher.
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

/**
 * Qualifier annotation for identifying the Main coroutine dispatcher.
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher