package io.github.claudio_santos.weathr.ui.forecast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.claudio_santos.weathr.R
import io.github.claudio_santos.weathr.ui.components.DailyCard
import io.github.claudio_santos.weathr.ui.components.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            state.error != null && state.forecast.isEmpty() -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.refresh() }
                )
            }
            state.forecast.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_forecast))
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.forecast, key = { it.date }) { day ->
                        val nextDay = java.time.LocalDate.parse(day.date).plusDays(1).toString()
                        val dayHours = remember(day.date, state.hourlyForecast) {
                            state.hourlyForecast.filter {
                                it.time.startsWith(day.date) || it.time.startsWith(nextDay + "T00:")
                            }
                        }
                        DailyCard(
                            forecast = day,
                            windSpeedUnit = state.windSpeedUnit,
                            isExpanded = day.date == state.expandedDay,
                            hourlyData = dayHours,
                            onClick = { viewModel.selectDay(day.date) }
                        )
                    }
                }
            }
        }
    }
}
