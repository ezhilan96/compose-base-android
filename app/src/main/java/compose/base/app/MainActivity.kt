package compose.base.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import compose.base.app.config.util.NetworkConnectivityObserver
import compose.base.app.config.util.NetworkConnectivityObserver.NetworkStatus
import compose.base.app.data.dataSource.local.preference.UserPreferencesDataStore
import compose.base.app.presentation.pages.NoInternetScreen
import compose.base.app.presentation.pages.SplashScreen
import compose.base.app.presentation.pages.auth.login.LoginRoute
import compose.base.app.presentation.ui.theme.BaseAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: UserPreferencesDataStore

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            BaseAppTheme {
                val networkStatus by networkConnectivityObserver.observe()
                    .collectAsState(initial = NetworkStatus.UnAvailable)
                val modifier = Modifier
                when (networkStatus) {
                    NetworkStatus.Available -> {
                        val _isLoggedIn by dataStore.isLoggedIn.collectAsState(initial = null)
                        _isLoggedIn?.let { isLoggedIn ->
                            val navController = rememberNavController()
                            NavHost(
                                navController = navController,
                                startDestination = if (isLoggedIn) MainRoutes.LoginScreen.route
                                else MainRoutes.LoginScreen.route
                            ) {
                                composable(route = MainRoutes.LoginScreen.route) {
                                    LoginRoute(
                                        modifier = modifier,
                                        navController = navController,
                                    )
                                }
                            }
                        } ?: SplashScreen(modifier = modifier)
                    }

                    NetworkStatus.UnAvailable -> NoInternetScreen(modifier = modifier)

                    else -> SplashScreen(modifier = modifier)
                }
            }
        }
    }
}

sealed class MainRoutes(val route: String) {

    object LoginScreen : MainRoutes(
        route = "login"
    )

//    object OTPScreen : MainRoutes(
//        route = "otp"
//    ) {
//        const val phoneArg: String = "phoneNumber"
//        const val nameArg: String = "nameCode"
//        val routeWithArgs: String = "${route}/{$phoneArg}/{$nameArg}"
//        val arguments = listOf(navArgument(phoneArg) { type = NavType.StringType },
//            navArgument(nameArg) { type = NavType.StringType })
//    }

}