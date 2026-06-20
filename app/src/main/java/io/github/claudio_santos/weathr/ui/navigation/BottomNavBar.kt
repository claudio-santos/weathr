package io.github.claudio_santos.weathr.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.claudio_santos.weathr.R

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Screen.Home.icon, contentDescription = stringResource(id = R.string.nav_home)) },
            label = { Text(stringResource(id = R.string.nav_home)) },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Screen.Forecast.icon, contentDescription = stringResource(id = R.string.nav_forecast)) },
            label = { Text(stringResource(id = R.string.nav_forecast)) },
            selected = currentRoute == Screen.Forecast.route,
            onClick = {
                if (currentRoute != Screen.Forecast.route) {
                    navController.navigate(Screen.Forecast.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Screen.PastWeek.icon, contentDescription = stringResource(id = R.string.nav_past_week)) },
            label = { Text(stringResource(id = R.string.nav_past_week)) },
            selected = currentRoute == Screen.PastWeek.route,
            onClick = {
                if (currentRoute != Screen.PastWeek.route) {
                    navController.navigate(Screen.PastWeek.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Screen.Settings.icon, contentDescription = stringResource(id = R.string.nav_settings)) },
            label = { Text(stringResource(id = R.string.nav_settings)) },
            selected = currentRoute == Screen.Settings.route,
            onClick = {
                if (currentRoute != Screen.Settings.route) {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}
