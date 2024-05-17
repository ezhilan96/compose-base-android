package com.compose.base.presentation.screens.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.base.presentation.screens.routes.AuthRoute
import com.compose.base.presentation.screens.user.login.LoginDestination
import com.compose.base.presentation.screens.user.otp.OtpDestination
import com.compose.base.presentation.screens.user.otp.viewModel.OtpViewModel

@Composable
fun AuthNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navigateUp: () -> Unit = { navController.navigateUp() }
    NavHost(
        navController = navController,
        startDestination = AuthRoute.LoginDestination.route,
    ) {

        AuthRoute.LoginDestination.let { destination ->
            composable(route = destination.route) {
                LoginDestination(
                    modifier = modifier,
                    navigateToOtp = {
                        destination.navigateToOtp(navController, it)
                    },
                )
            }
        }

        AuthRoute.OtpDestination.let { destination ->
            composable(
                route = destination.routeWithArgs,
                arguments = destination.arguments,
            ) { navBackStackEntry ->
                val phoneNumber =
                    remember { navBackStackEntry.arguments?.getString(destination.phoneArg)!! }
                val otpViewModel = hiltViewModel<OtpViewModel>()
                OtpDestination(
                    modifier = modifier,
                    navigateUp = navigateUp,
                    viewModel = otpViewModel,
                )
                LaunchedEffect(Unit) {
                    otpViewModel.setPhoneNumber(phoneNumber)
                }
            }
        }
    }
}