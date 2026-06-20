package io.github.claudio_santos.weathr.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.claudio_santos.weathr.R
import io.github.claudio_santos.weathr.ui.components.CurrentWeatherCard
import io.github.claudio_santos.weathr.ui.components.ErrorState
import io.github.claudio_santos.weathr.ui.components.HourlyRow
import io.github.claudio_santos.weathr.ui.components.SearchCityDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onGpsPermissionGranted()
        } else {
            viewModel.onGpsPermissionDenied()
        }
    }

    if (state.showSearchDialog) {
        SearchCityDialog(
            onSearch = { viewModel.onSearchCityQuery(it) },
            searchResults = state.searchResults,
            onSelectCity = { viewModel.onSelectCity(it) },
            onDismiss = { viewModel.hideSearchDialog() }
        )
    }

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            state.isGpsResolving -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text(stringResource(R.string.getting_location))
                    }
                }
            }
            state.error != null && state.weather == null -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.refresh() }
                )
            }
            !state.isLocationSet -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.welcome_message),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.showSearchDialog() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.search_city))
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                    ) {
                                        viewModel.onGpsPermissionGranted()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.use_gps))
                            }
                        }
                    }
                }
            }
            state.isLoading && state.weather == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    if (state.weather != null) {
                        CurrentWeatherCard(
                            weather = state.weather!!,
                            locationName = state.locationName,
                            windSpeedUnit = state.windSpeedUnit
                        )
                        Spacer(Modifier.height(16.dp))
                        HourlyRow(hourlyForecast = state.weather!!.hourlyForecast, windSpeedUnit = state.windSpeedUnit)
                    }
                }
            }
        }
    }
}
