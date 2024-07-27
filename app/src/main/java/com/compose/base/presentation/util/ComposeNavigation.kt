package com.compose.base.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Navigates to a new destination while clearing the navigation back stack.
 *
 * This extension function simplifies navigation within your app by automatically clearing
 * the back stack when navigating to a new route. It avoids unintended behavior where users
 * can navigate back to previous screens after reaching the new destination.
 *
 * **Note:** This function only navigates if the current destination is different from the target route.
 *
 * @receiver The NavController instance responsible for navigation.
 * @param route The target route (destination) to navigate to.
 *
 *
 * @see NavController.navigate
 * @see NavOptionsBuilder.popUpTo
 */
fun NavController.navigateClearingStack(route: Any) {
    if (this.currentDestination?.route != route) {
        navigate(route) {
            popUpTo(graph.id) {
                inclusive = true
            }
        }
    }
}

/**
 * Retrieves a shared ViewModel instance from a parent navigation graph.
 *
 * This extension function provides a convenient way to access a ViewModel shared across
 * multiple screens within the same navigation graph in Jetpack Compose.
 *
 * @receiver The NavBackStackEntry of the current screen.
 * @param navController (Used to retrieve the parent navigation graph's back stack entry) The NavController instance responsible for navigation.
 * @return The shared ViewModel instance of type T (reified).
 *
 * @throws IllegalStateException if a parent navigation graph is not found.
 */

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute: String = remember { destination.parent?.route } ?: return hiltViewModel()
    val parentEntry: NavBackStackEntry =
        remember(this) { navController.getBackStackEntry(navGraphRoute) }
    return hiltViewModel(parentEntry)
}