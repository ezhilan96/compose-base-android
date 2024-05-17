package com.compose.base.domain.repository.core

import com.compose.base.data.repository.core.ConnectionState
import kotlinx.coroutines.flow.Flow

interface NetworkConnectionRepository {

    val connectionState: Flow<ConnectionState>

    fun checkConnection()
}