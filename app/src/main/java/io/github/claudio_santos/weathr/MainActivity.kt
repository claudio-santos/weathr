package io.github.claudio_santos.weathr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import io.github.claudio_santos.weathr.ui.navigation.BottomNavBar
import io.github.claudio_santos.weathr.ui.navigation.NavGraph
import io.github.claudio_santos.weathr.ui.theme.WeathrTheme
import io.github.claudio_santos.weathr.util.LocaleHelper
import io.github.claudio_santos.weathr.util.Preferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialLang = runBlocking { preferences.language.first() }
        LocaleHelper.applyLocale(this, initialLang)

        lifecycleScope.launch {
            preferences.language.drop(1).collect { lang ->
                LocaleHelper.applyLocale(this@MainActivity, lang)
                recreate()
            }
        }

        setContent {
            val themeMode by preferences.themeMode.collectAsState(initial = "system")
            val darkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }
            WeathrTheme(darkTheme = darkTheme) {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
