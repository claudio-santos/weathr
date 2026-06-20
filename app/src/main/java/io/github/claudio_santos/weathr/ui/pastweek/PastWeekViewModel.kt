package io.github.claudio_santos.weathr.ui.pastweek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.claudio_santos.weathr.data.repository.WeatherRepository
import io.github.claudio_santos.weathr.domain.model.DailyForecast
import io.github.claudio_santos.weathr.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class PastWeekUiState(
    val isLoading: Boolean = false,
    val history: List<DailyForecast> = emptyList(),
    val error: String? = null,
    val windSpeedUnit: String = "kmh"
)

@HiltViewModel
class PastWeekViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val preferences: Preferences
) : ViewModel() {

    private val _state = MutableStateFlow(PastWeekUiState())
    val state: StateFlow<PastWeekUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.temperatureUnit.drop(1).collect { refresh() }
        }
        viewModelScope.launch {
            preferences.windSpeedUnit.drop(1).collect { refresh() }
        }
        viewModelScope.launch {
            preferences.latitude.drop(1).collect { if (it != null) refresh() }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val lat = preferences.latitude.first()
                val lon = preferences.longitude.first()
                if (lat == null || lon == null) {
                    _state.value = _state.value.copy(isLoading = false, error = "No location set")
                    return@launch
                }
                val tempUnit = preferences.temperatureUnit.first()
                val windUnit = preferences.windSpeedUnit.first()

                val today = LocalDate.now()
                val endDate = today.minusDays(1)
                val startDate = endDate.minusDays(6)
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE

                val history = repository.getHistorical(
                    lat, lon,
                    startDate.format(formatter),
                    endDate.format(formatter),
                    tempUnit, windUnit
                )
                _state.value = _state.value.copy(
                    isLoading = false,
                    history = history,
                    windSpeedUnit = windUnit,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load historical data"
                )
            }
        }
    }
}
