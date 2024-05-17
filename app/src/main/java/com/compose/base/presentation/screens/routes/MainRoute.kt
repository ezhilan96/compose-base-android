package com.compose.base.presentation.screens.routes

import com.compose.base.core.Constants

sealed class MainRoute(val route: String) {

    data object SplashDestination : MainRoute(route = Constants.ROUTE_SPLASH)

    data object AuthRoute : MainRoute(route = Constants.ROUTE_AUTH)

    data object HomeRoute : MainRoute(route = Constants.ROUTE_HOME)
}