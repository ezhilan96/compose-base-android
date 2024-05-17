package com.compose.base.presentation.screens.routes

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.compose.base.core.Constants

sealed class HomeRoute(val route: String) {

    data object DashboardDestination : HomeRoute(route = Constants.ROUTE_DASHBOARD) {
        val deepLinks = listOf(navDeepLink {
            uriPattern = "https://com.compose.base/"
            action = Intent.ACTION_VIEW
        })
    }

    data object ListDestination : HomeRoute(route = Constants.ROUTE_LIST) {

        fun navigateToDetail(navController: NavController, id: String) {
            navController.navigate("${DetailDestination.route}/$id")
        }
    }

    data object DetailDestination : HomeRoute(route = Constants.ROUTE_DETAIL) {
        const val ID_ARG: String = Constants.ROUTE_ARG_ID

        val routeWithArgs: String = "$route/{$ID_ARG}"
        val arguments = listOf(navArgument(ID_ARG) { type = NavType.StringType })
    }

    data object ProfileDestination : HomeRoute(route = Constants.ROUTE_PROFILE)
}