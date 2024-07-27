@file:OptIn(ExperimentalMaterial3Api::class)

package com.compose.base.presentation.screens.home.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.routes.DashboardNavItems
import com.compose.base.presentation.routes.DashboardRoute

@Composable
fun DashBoardNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedRoute: DashboardRoute by remember { mutableStateOf(DashboardRoute.HomeDestination) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = selectedRoute.labelRes))
                },
            )
        },
        bottomBar = {
            NavigationBar {
                DashboardNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = item == selectedRoute,
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = "",
                            )
                        },
                        label = { Text(text = stringResource(id = item.labelRes)) },
                        onClick = {
                            selectedRoute = item
                            navController.navigate(item) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { safeAreaPadding ->
        NavHost(
            modifier = modifier
                .fillMaxSize()
                .padding(safeAreaPadding),
            navController = navController,
            startDestination = DashboardRoute.HomeDestination,
        ) {
            composable<DashboardRoute.HomeDestination> {
                Box(modifier = modifier.fillMaxSize()) {
                    Text(
                        modifier = modifier.align(Alignment.Center),
                        text = "Home",
                    )
                    Button(modifier = modifier.align(Alignment.BottomCenter),
                        onClick = { navController.navigate(DashboardRoute.OneDestination(id = 1)) }) {
                        Text(text = "Navigate")
                    }
                }
            }

            composable<DashboardRoute.ListDestination> {
                Box(modifier = modifier.fillMaxSize()) {
                    Text(
                        modifier = modifier.align(Alignment.Center),
                        text = "List",
                    )
                }
            }

            composable<DashboardRoute.HistoryDestination> {
                Box(modifier = modifier.fillMaxSize()) {
                    Text(
                        modifier = modifier.align(Alignment.Center),
                        text = "History",
                    )
                }
            }

            composable<DashboardRoute.OneDestination> {
                val id = it.toRoute<DashboardRoute.OneDestination>().id
                Box(modifier = modifier.fillMaxSize()) {
                    Text(
                        modifier = modifier.align(Alignment.Center),
                        text = "id:${id}",
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DashBoardNavHostPreview() {
    ComposeBaseTheme {
        DashBoardNavHost()
    }
}