package io.github.claudio_santos.weathr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.claudio_santos.weathr.ui.forecast.ForecastScreen
import io.github.claudio_santos.weathr.ui.home.HomeScreen
import io.github.claudio_santos.weathr.ui.pastweek.PastWeekScreen
import io.github.claudio_santos.weathr.ui.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Forecast.route) { ForecastScreen() }
        composable(Screen.PastWeek.route) { PastWeekScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}
