package io.github.claudio_santos.weathr.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.claudio_santos.weathr.data.repository.WeatherRepository
import io.github.claudio_santos.weathr.domain.model.Location
import io.github.claudio_santos.weathr.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val cityName: String? = null,
    val useGps: Boolean = true,
    val language: String = "en",
    val temperatureUnit: String = "celsius",
    val windSpeedUnit: String = "kmh",
    val themeMode: String = "system",
    val searchQuery: String = "",
    val searchResults: List<Location> = emptyList(),
    val isSearching: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: Preferences,
    private val repository: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                cityName = preferences.cityName.first(),
                useGps = preferences.useGps.first(),
                language = preferences.language.first(),
                temperatureUnit = preferences.temperatureUnit.first(),
                windSpeedUnit = preferences.windSpeedUnit.first(),
                themeMode = preferences.themeMode.first()
            )
        }
    }

    fun setTemperatureUnit(unit: String) {
        viewModelScope.launch {
            preferences.setTemperatureUnit(unit)
            _state.value = _state.value.copy(temperatureUnit = unit)
        }
    }

    fun setWindSpeedUnit(unit: String) {
        viewModelScope.launch {
            preferences.setWindSpeedUnit(unit)
            _state.value = _state.value.copy(windSpeedUnit = unit)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            preferences.setLanguage(lang)
            _state.value = _state.value.copy(language = lang)
        }
    }

    fun setUseGps(use: Boolean) {
        viewModelScope.launch {
            preferences.setUseGps(use)
            _state.value = _state.value.copy(useGps = use)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferences.setThemeMode(mode)
            _state.value = _state.value.copy(themeMode = mode)
        }
    }

    fun searchCity(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        if (query.length < 2) {
            _state.value = _state.value.copy(searchResults = emptyList())
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _state.value = _state.value.copy(isSearching = true)
            try {
                val results = repository.searchCity(query)
                _state.value = _state.value.copy(searchResults = results, isSearching = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSearching = false)
            }
        }
    }

    fun selectCity(location: Location) {
        viewModelScope.launch {
            preferences.setLocation(
                location.latitude,
                location.longitude,
                location.name,
                location.timezone
            )
            preferences.setUseGps(false)
            _state.value = _state.value.copy(
                cityName = location.name,
                useGps = false,
                searchResults = emptyList(),
                searchQuery = ""
            )
        }
    }
}
