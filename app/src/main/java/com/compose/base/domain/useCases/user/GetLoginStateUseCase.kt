package com.compose.base.domain.useCases.user

import com.compose.base.domain.repository.AuthRepository
import com.compose.base.presentation.screens.AppLoginState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetLoginStateUseCase @Inject constructor(
    private val checkForUpdate: CheckForUpdateUseCase,
    private val authRepo: AuthRepository,
) {

    operator fun invoke(): Flow<AppLoginState> = authRepo.isLoggedIn.flatMapLatest { isLoggedIn ->
        if (isLoggedIn) {
            checkForUpdate()
        } else {
            flowOf(AppLoginState.UnAuthorized)
        }
    }
}