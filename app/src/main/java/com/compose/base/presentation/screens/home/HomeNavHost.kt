package com.compose.base.presentation.screens.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.base.core.Constants
import com.compose.base.presentation.screens.core.component.EnableLocationService
import com.compose.base.presentation.screens.core.component.RequestAppPermissions
import com.compose.base.presentation.screens.home.profile.ProfileDestination
import com.compose.base.presentation.screens.routes.HomeRoute
import com.compose.base.presentation.services.LocationTrackingService

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeNavHost(
    modifier: Modifier = Modifier,
    intent: Intent,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navigateUp: () -> Unit = { navController.navigateUp() }
    val navigateToDetail = { id: String ->
        HomeRoute.ListDestination.navigateToDetail(navController, id)
    }
    var shouldCheckLocationService by remember { mutableStateOf(true) }
    var isLocationServiceStateListenerRegistered by remember { mutableStateOf(false) }
    val locationServiceStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            shouldCheckLocationService = true
        }
    }

    Scaffold {
        if (shouldCheckLocationService) {
            RequestAppPermissions {
                EnableLocationService {
                    LocationTrackingService.start(context)
                    if (!isLocationServiceStateListenerRegistered) {
                        isLocationServiceStateListenerRegistered = true
                        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
                        filter.addAction(Intent.ACTION_PROVIDER_CHANGED)
                        ContextCompat.registerReceiver(
                            context,
                            locationServiceStateReceiver,
                            filter,
                            ContextCompat.RECEIVER_NOT_EXPORTED,
                        )
                    }
                    shouldCheckLocationService = false
                }
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = HomeRoute.DashboardDestination.route,
            ) {

                composable(
                    route = HomeRoute.DashboardDestination.route,
                    deepLinks = HomeRoute.DashboardDestination.deepLinks,
                ) {
//                    DashboardDestination(modifier = modifier)
                }

                composable(route = HomeRoute.ListDestination.route) {
//                    ListDestination(
//                        modifier = modifier,
//                        navigateToDetail = navigateToDetail,
//                    )
                }

                composable(
                    route = HomeRoute.DetailDestination.routeWithArgs,
                    arguments = HomeRoute.DetailDestination.arguments,
                ) { navBackStackEntry ->
//                    val detailId =
//                        remember { navBackStackEntry.arguments?.getString(HomeRoute.DetailDestination.ID_ARG) }
//                    DetailDestination(
//                        modifier = modifier,
//                        detailId = detailId,
//                        navigateUp = navigateUp,
//                    )
                }

                composable(route = HomeRoute.ProfileDestination.route) {
                    ProfileDestination(
                        modifier = modifier,
                        navigateUp = navigateUp,
                    )
                }
            }

            LaunchedEffect(intent) {
                val detailId = intent.extras?.getString(Constants.KEY_NOTIFICATION_ID)
                if (!detailId.isNullOrEmpty()) {
                    navigateToDetail(detailId)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                isLocationServiceStateListenerRegistered = false
                context.unregisterReceiver(locationServiceStateReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}