package io.github.claudio_santos.weathr.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.claudio_santos.weathr.R
import io.github.claudio_santos.weathr.ui.components.SearchCityDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCitySearch by remember { mutableStateOf(false) }

    if (showCitySearch) {
        SearchCityDialog(
            onSearch = { viewModel.searchCity(it) },
            searchResults = state.searchResults,
            onSelectCity = { location ->
                viewModel.selectCity(location)
                showCitySearch = false
            },
            onDismiss = { showCitySearch = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionCard(stringResource(R.string.location)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.current_city, state.cityName ?: stringResource(R.string.not_set)))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCitySearch = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.search_city), modifier = Modifier.weight(1f))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.use_gps), modifier = Modifier.weight(1f))
                Switch(
                    checked = state.useGps,
                    onCheckedChange = { viewModel.setUseGps(it) }
                )
            }
        }

        SectionCard(stringResource(R.string.language)) {
            ChipGroup(
                options = listOf("en" to R.string.lang_en, "pt" to R.string.lang_pt),
                selected = state.language,
                onSelect = { viewModel.setLanguage(it) }
            )
        }

        SectionCard(stringResource(R.string.temperature_unit)) {
            ChipGroup(
                options = listOf("celsius" to R.string.celsius, "fahrenheit" to R.string.fahrenheit),
                selected = state.temperatureUnit,
                onSelect = { viewModel.setTemperatureUnit(it) }
            )
        }

        SectionCard(stringResource(R.string.wind_speed_unit)) {
            ChipGroup(
                options = listOf(
                    "kmh" to R.string.kmh,
                    "mph" to R.string.mph,
                    "ms" to R.string.ms,
                    "kn" to R.string.knots
                ),
                selected = state.windSpeedUnit,
                onSelect = { viewModel.setWindSpeedUnit(it) }
            )
        }

        SectionCard(stringResource(R.string.theme)) {
            ChipGroup(
                options = listOf(
                    "system" to R.string.system_default,
                    "light" to R.string.light,
                    "dark" to R.string.dark
                ),
                selected = state.themeMode,
                onSelect = { viewModel.setThemeMode(it) }
            )
        }

        SectionCard(stringResource(R.string.about)) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
            )
            Text(
                text = stringResource(R.string.app_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = stringResource(R.string.version),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = stringResource(R.string.powered_by),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipGroup(
    options: List<Pair<String, Int>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (value, labelRes) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelect(value) },
                label = { Text(stringResource(id = labelRes)) }
            )
        }
    }
}
