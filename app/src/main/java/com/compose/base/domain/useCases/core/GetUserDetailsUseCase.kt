package com.compose.base.domain.useCases.core

import com.compose.base.domain.entity.UserData
import com.compose.base.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDetailsUseCase @Inject constructor(
    private val authRepo: AuthRepository
) {
    operator fun invoke(): Flow<UserData?> = authRepo.userDetails
}