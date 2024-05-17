package com.compose.base.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

fun NavController.navigateClearingStack(route: String) {
    if (this.currentDestination?.route != route) {
        navigate(route) {
            popUpTo(graph.id) {
                inclusive = true
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = remember { destination.parent?.route } ?: return hiltViewModel()
    val parentEntry = remember(this) { navController.getBackStackEntry(navGraphRoute) }
    return viewModel(parentEntry)
}