package com.giapa.kontroller.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.giapa.kontroller.feature.connection.ConnectionRoute
import com.giapa.kontroller.feature.controls.ControlsRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Connection,
        modifier = modifier,
    ) {
        composable(Routes.Connection) {
            ConnectionRoute(
                onConnected = {
                    // simple navigation for now; later we can carry args (host/token) if needed
                    navController.navigate(Routes.Controls)
                },
            )
        }
        composable(Routes.Controls) {
            ControlsRoute(
                onNavigateUp = { navController.popBackStack() },
            )
        }
    }
}
