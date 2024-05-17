package com.compose.base.presentation.screens.routes

import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.compose.base.core.Constants

sealed class AuthRoute(val route: String) {

    data object LoginDestination : AuthRoute(route = Constants.ROUTE_LOGIN) {
        fun navigateToOtp(navController: NavController, phone: String) {
            navController.navigate("${OtpDestination.route}/$phone")
        }
    }

    data object OtpDestination : AuthRoute(route = Constants.ROUTE_OTP) {
        const val phoneArg: String = Constants.ROUTE_ARG_PHONE
        val routeWithArgs: String = "${OtpDestination.route}/{$phoneArg}"
        val arguments = listOf(
            navArgument(phoneArg) { type = NavType.StringType },
        )
    }
}