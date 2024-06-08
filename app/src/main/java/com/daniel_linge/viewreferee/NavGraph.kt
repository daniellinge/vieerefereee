package com.daniel_linge.viewreferee

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(navController: NavHostController) {
    val refereeViewModel: RefereeViewModel = viewModel()
    val observationViewModel: ObservationViewModel = viewModel()

    NavHost(navController = navController, startDestination = "refereeCapture") {
        composable("refereeCapture") {
            RefereeCaptureScreen(navController, refereeViewModel)
        }
        composable("referee_observations") {
            RefereeObservationsScreen(navController, refereeViewModel)
        }
        composable("start_screen") {
            StartScreen(navController, refereeViewModel, observationViewModel)
        }
        composable(
            route = "ticker_screen/{halfDuration}",
            arguments = listOf(
                navArgument("halfDuration") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val halfDuration = backStackEntry.arguments?.getInt("halfDuration") ?: 30
            TickerScreen(
                navController = navController,
                halfDuration = halfDuration,
                observationViewModel = observationViewModel,
                refereeViewModel = refereeViewModel
            )
        }
        composable("summary") {
            SummaryScreen(navController, observationViewModel, refereeViewModel)
        }
    }
}
