package com.compose.base.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class MainRoute {

    @Serializable
    data object SplashDestination : MainRoute()

    @Serializable
    data object AuthRoute : MainRoute()

    @Serializable
    data object HomeRoute : MainRoute()
}