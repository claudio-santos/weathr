package io.github.claudio_santos.weathr.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.claudio_santos.weathr.data.repository.WeatherRepository
import io.github.claudio_santos.weathr.domain.model.DailyForecast
import io.github.claudio_santos.weathr.domain.model.HourlyData
import io.github.claudio_santos.weathr.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForecastUiState(
    val isLoading: Boolean = false,
    val forecast: List<DailyForecast> = emptyList(),
    val hourlyForecast: List<HourlyData> = emptyList(),
    val expandedDay: String? = null,
    val error: String? = null,
    val windSpeedUnit: String = "kmh"
)

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val preferences: Preferences
) : ViewModel() {

    private val _state = MutableStateFlow(ForecastUiState())
    val state: StateFlow<ForecastUiState> = _state.asStateFlow()

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
            _state.value = _state.value.copy(isLoading = true, error = null, expandedDay = null)
            try {
                val lat = preferences.latitude.first()
                val lon = preferences.longitude.first()
                if (lat == null || lon == null) {
                    _state.value = _state.value.copy(isLoading = false, error = "No location set")
                    return@launch
                }
                val tempUnit = preferences.temperatureUnit.first()
                val windUnit = preferences.windSpeedUnit.first()

                val forecast = repository.getForecast(lat, lon, tempUnit, windUnit)
                _state.value = _state.value.copy(
                    isLoading = false,
                    forecast = forecast.daily,
                    hourlyForecast = forecast.current.hourlyForecast,
                    windSpeedUnit = windUnit,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load forecast"
                )
            }
        }
    }

    fun selectDay(date: String?) {
        val current = _state.value.expandedDay
        _state.value = _state.value.copy(expandedDay = if (current == date) null else date)
    }
}
