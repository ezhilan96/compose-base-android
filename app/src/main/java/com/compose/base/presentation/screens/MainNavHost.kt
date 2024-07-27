package com.compose.base.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.base.presentation.routes.MainRoute
import com.compose.base.presentation.screens.auth.AuthNavHost
import com.compose.base.presentation.screens.home.HomeNavHost
import com.compose.base.presentation.screens.shared.component.CheckAppPermissions
import com.compose.base.presentation.screens.shared.component.EnableLocationService
import com.compose.base.presentation.screens.shared.screen.SplashScreen
import com.compose.base.presentation.services.LocationTrackingService
import com.compose.base.presentation.util.navigateClearingStack

/**
 * The main navigation host composable for the application.
 *
 * This composable manages the overall navigation flow within the app based on the user's
 * login state. It utilizes Jetpack Navigation components for routing and screen transitions.
 *
 * @param modifier An optional modifier to be applied to the composable root.
 * @param appState The current app state.
 */
@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    appState: AppState,
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoute.SplashDestination,
    ) {

        composable<MainRoute.SplashDestination> {
            SplashScreen(modifier = modifier)
        }

        composable<MainRoute.AuthRoute> {
            AuthNavHost(modifier = modifier)
        }

        composable<MainRoute.HomeRoute> {
            HomeNavHost(modifier = modifier)
        }
    }

    when (appState) {
        AppState.Authorized -> {
            // Check app permissions and enable location service before navigating to home screen;
            // Since the app needs location service enabled to keep the tracking service running while the user is logged in
            CheckAppPermissions {
                EnableLocationService {
                    LaunchedEffect(Unit) {
                        navController.navigateClearingStack(MainRoute.HomeRoute)
                        LocationTrackingService.start(context)
                    }
                }
            }
        }

        AppState.UnAuthorized -> {
            LaunchedEffect(Unit) {
                navController.navigateClearingStack(MainRoute.AuthRoute)
                LocationTrackingService.stop(context)
            }
        }

        else -> {}
    }
}