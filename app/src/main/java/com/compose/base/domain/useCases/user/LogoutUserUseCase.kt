package com.compose.base.domain.useCases.user

import com.compose.base.data.dataSource.local.dataStore.UserPreferencesDataStore
import com.compose.base.data.util.DataState
import com.compose.base.domain.repository.AuthRepository
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class LogoutUserUseCase @Inject constructor(
    private val dataStore: UserPreferencesDataStore,
    private val repo: AuthRepository,
) {

    operator fun invoke() = repo.logout().onEach {
        if (it is DataState.Success) {
            dataStore.logout()
        }
    }
}