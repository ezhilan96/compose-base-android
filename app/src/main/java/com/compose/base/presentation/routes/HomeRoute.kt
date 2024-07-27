package com.compose.base.presentation.routes

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.compose.base.R
import kotlinx.serialization.Serializable

@Serializable
sealed class HomeRoute {

    @Serializable
    data object DashBoardDestination : HomeRoute()

    @Serializable
    data object ProfileDestination : HomeRoute()

    @Serializable
    data object CameraCaptureDestination : HomeRoute()
}

@Serializable
sealed class DashboardRoute(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int,
) {

    @Serializable
    data object HomeDestination :
        DashboardRoute(iconRes = R.drawable.ic_home, labelRes = R.string.label_home)

    @Serializable
    data object ListDestination :
        DashboardRoute(iconRes = R.drawable.ic_list, labelRes = R.string.label_list)

    @Serializable
    data object HistoryDestination :
        DashboardRoute(iconRes = R.drawable.ic_history, labelRes = R.string.label_history)

    @Serializable
    data class OneDestination(val id: Int) :
        DashboardRoute(iconRes = R.drawable.ic_launcher_foreground, labelRes = R.string.label_id)
}

val DashboardNavItems = listOf(
    DashboardRoute.HomeDestination,
    DashboardRoute.ListDestination,
    DashboardRoute.HistoryDestination,
)