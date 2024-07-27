package com.compose.base.presentation.routes

import com.compose.base.core.Constants
import kotlinx.serialization.Serializable

@Serializable
sealed class AuthRoute(val route: String) {

    @Serializable
    data object LoginDestination : AuthRoute(route = Constants.ROUTE_LOGIN)

    @Serializable
    data class VerifyOtpDestination(val phone: String) : AuthRoute(route = Constants.ROUTE_OTP)
}