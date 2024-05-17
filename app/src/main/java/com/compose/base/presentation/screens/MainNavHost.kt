package com.compose.base.presentation.screens

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.base.presentation.screens.core.screen.SplashScreen
import com.compose.base.presentation.screens.home.HomeNavHost
import com.compose.base.presentation.screens.routes.HomeRoute
import com.compose.base.presentation.screens.routes.MainRoute
import com.compose.base.presentation.screens.user.AuthNavHost
import com.compose.base.presentation.services.LocationTrackingService
import com.compose.base.presentation.util.navigateClearingStack
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    intentState: StateFlow<Intent>,
    appLoginState: AppLoginState,
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoute.SplashDestination.route,
    ) {

        composable(route = MainRoute.SplashDestination.route) {
            SplashScreen(modifier = modifier)
        }

        composable(route = MainRoute.AuthRoute.route) {
            AuthNavHost(modifier = modifier)
        }

        composable(route = MainRoute.HomeRoute.route) {
            val intent by intentState.collectAsStateWithLifecycle()
            HomeNavHost(
                modifier = modifier,
                intent = intent,
            )
        }
    }

    LaunchedEffect(appLoginState) {
        when (appLoginState) {

            AppLoginState.Authorized -> {
                if (navController.currentDestination?.route != HomeRoute.ListDestination.route) {
                    navController.navigateClearingStack(MainRoute.HomeRoute.route)
                }
            }

            AppLoginState.UnAuthorized -> {
                navController.navigateClearingStack(MainRoute.AuthRoute.route)
                LocationTrackingService.stop(context)
            }

            else -> {}
        }
    }
}