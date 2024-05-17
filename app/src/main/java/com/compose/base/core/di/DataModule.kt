package com.compose.base.core.di

import com.compose.base.data.dataSource.remote.SocketService
import com.compose.base.data.dataSource.remote.SocketServiceImpl
import com.compose.base.data.repository.AuthRepositoryImpl
import com.compose.base.data.repository.HomeRepositoryImpl
import com.compose.base.data.repository.core.NetworkConnectionRepositoryImpl
import com.compose.base.domain.repository.AuthRepository
import com.compose.base.domain.repository.HomeRepository
import com.compose.base.domain.repository.core.NetworkConnectionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindSocketService(socketServiceImpl: SocketServiceImpl): SocketService

    @Binds
    @Singleton
    abstract fun bindNetworkConnectionRepository(repositoryImpl: NetworkConnectionRepositoryImpl): NetworkConnectionRepository

}