package com.compose.base.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.base.core.Constants
import com.compose.base.presentation.routes.HomeRoute
import com.compose.base.presentation.screens.home.dashboard.DashBoardNavHost
import com.compose.base.presentation.screens.home.profile.ProfileDestination
import com.compose.base.presentation.screens.shared.screen.photoCapture.PhotoCaptureScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navigateUp: () -> Unit = { navController.navigateUp() }

    Scaffold(containerColor = MaterialTheme.colorScheme.inverseSurface) {
        NavHost(
            navController = navController,
            startDestination = HomeRoute.DashBoardDestination,
        ) {

            composable<HomeRoute.DashBoardDestination> {
                DashBoardNavHost()
            }

            composable<HomeRoute.CameraCaptureDestination> {
                PhotoCaptureScreen(
                    modifier = modifier,
                    onSubmit = { uri ->
                        val savedStateHandle =
                            navController.previousBackStackEntry?.savedStateHandle!!
                        savedStateHandle[Constants.KEY_PHOTO_URI] = uri
                        navigateUp()
                    },
                    navigateUp = navigateUp,
                )
            }

            composable<HomeRoute.ProfileDestination> {
                ProfileDestination(
                    modifier = modifier,
                    navigateUp = navigateUp,
                )
            }
        }
    }
}