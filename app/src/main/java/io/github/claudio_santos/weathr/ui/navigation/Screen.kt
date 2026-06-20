package io.github.claudio_santos.weathr.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Forecast : Screen("forecast", "Forecast", Icons.Filled.CalendarMonth)
    object PastWeek : Screen("past_week", "Past Week", Icons.Filled.History)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)

    companion object {
        val bottomNavItems = listOf(Home, Forecast, PastWeek, Settings)
    }
}
