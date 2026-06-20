package io.github.claudio_santos.weathr.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.claudio_santos.weathr.data.repository.WeatherRepository
import io.github.claudio_santos.weathr.domain.model.Weather
import io.github.claudio_santos.weathr.util.Preferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val error: String? = null,
    val locationName: String? = null,
    val isLocationSet: Boolean = false,
    val windSpeedUnit: String = "kmh",
    val showSearchDialog: Boolean = false,
    val isGpsResolving: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<io.github.claudio_santos.weathr.domain.model.Location> = emptyList(),
    val isSearching: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val preferences: Preferences,
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var isGpsRunning = false

    init {
        viewModelScope.launch {
            preferences.useGps.drop(1).collect { use ->
                if (use && hasLocationPermission()) {
                    getGpsLocation()
                }
            }
        }
        viewModelScope.launch {
            preferences.temperatureUnit.drop(1).collect {
                if (preferences.latitude.first() != null) refresh()
            }
        }
        viewModelScope.launch {
            preferences.windSpeedUnit.drop(1).collect {
                if (preferences.latitude.first() != null) refresh()
            }
        }
        viewModelScope.launch {
            preferences.latitude.drop(1).collect { lat ->
                if (lat != null && !isGpsRunning) {
                    val name = preferences.cityName.first()
                    _state.value = _state.value.copy(locationName = name)
                    refresh()
                }
            }
        }
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            val useGps = preferences.useGps.first()
            val lat = preferences.latitude.first()
            val lon = preferences.longitude.first()
            val name = preferences.cityName.first()
            val windUnit = preferences.windSpeedUnit.first()

            if (lat != null && lon != null) {
                val cached = repository.getCachedForecast()
                _state.value = _state.value.copy(
                    isLocationSet = true,
                    locationName = name,
                    weather = cached?.current?.copy(hourlyForecast = filterTodayHours(cached.current.hourlyForecast)),
                    windSpeedUnit = windUnit
                )
            }
            if (useGps && hasLocationPermission()) {
                getGpsLocation()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun onGpsPermissionGranted() {
        getGpsLocation()
    }

    fun onGpsPermissionDenied() {
        _state.value = _state.value.copy(isGpsResolving = false)
    }

    private fun getGpsLocation() {
        if (isGpsRunning) return
        isGpsRunning = true
        _state.value = _state.value.copy(isGpsResolving = true, error = null)
        viewModelScope.launch {
            try {
                val location = withTimeout(10_000) {
                    kotlinx.coroutines.suspendCancellableCoroutine<Location?> { cont ->
                        val tokenSource = CancellationTokenSource()
                        cont.invokeOnCancellation { tokenSource.cancel() }
                        val task = fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            tokenSource.token
                        )
                        task.addOnSuccessListener { if (cont.isActive) cont.resume(it, onCancellation = null) }
                        task.addOnFailureListener { if (cont.isActive) cont.resume(null, onCancellation = null) }
                    }
                }
                if (location != null) {
                    val gpsName = context.getString(io.github.claudio_santos.weathr.R.string.gps_current_location)
                    preferences.setLocation(
                        location.latitude,
                        location.longitude,
                        gpsName,
                        "auto"
                    )
                    _state.value = _state.value.copy(
                        isLocationSet = true,
                        locationName = gpsName,
                        isGpsResolving = false,
                        isLoading = true
                    )
                    val lat = preferences.latitude.first()
                    val lon = preferences.longitude.first()
                    if (lat != null && lon != null) {
                        val tempUnit = preferences.temperatureUnit.first()
                        val windUnit = preferences.windSpeedUnit.first()
                        val forecast = repository.getForecast(lat, lon, tempUnit, windUnit)
                        val name = preferences.cityName.first()
                        val weather = forecast.current.copy(hourlyForecast = filterTodayHours(forecast.current.hourlyForecast))
                        _state.value = _state.value.copy(
                            isLoading = false,
                            weather = weather,
                            locationName = name,
                            windSpeedUnit = windUnit,
                            error = null
                        )
                    }
                } else {
                    _state.value = _state.value.copy(isGpsResolving = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isGpsResolving = false)
            } finally {
                isGpsRunning = false
            }
        }
    }

    fun onSearchCityQuery(query: String) {
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

    fun onSelectCity(location: io.github.claudio_santos.weathr.domain.model.Location) {
        viewModelScope.launch {
            preferences.setLocation(
                location.latitude,
                location.longitude,
                location.name,
                location.timezone
            )
            preferences.setUseGps(false)
            _state.value = _state.value.copy(
                isLocationSet = true,
                locationName = location.name,
                showSearchDialog = false,
                searchResults = emptyList(),
                searchQuery = ""
            )
        }
    }

    fun showSearchDialog() {
        _state.value = _state.value.copy(showSearchDialog = true)
    }

    fun hideSearchDialog() {
        _state.value = _state.value.copy(showSearchDialog = false)
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val lat = preferences.latitude.first() ?: run {
                    _state.value = _state.value.copy(isLoading = false, error = "No location set")
                    return@launch
                }
                val lon = preferences.longitude.first() ?: return@launch
                val tempUnit = preferences.temperatureUnit.first()
                val windUnit = preferences.windSpeedUnit.first()

                val forecast = repository.getForecast(lat, lon, tempUnit, windUnit)
                val name = preferences.cityName.first()
                val weather = forecast.current.copy(hourlyForecast = filterTodayHours(forecast.current.hourlyForecast))
                _state.value = _state.value.copy(
                    isLoading = false,
                    weather = weather,
                    locationName = name,
                    windSpeedUnit = windUnit,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load weather"
                )
            }
        }
    }

    private fun filterTodayHours(hours: List<io.github.claudio_santos.weathr.domain.model.HourlyData>): List<io.github.claudio_santos.weathr.domain.model.HourlyData> {
        val today = LocalDate.now().toString()
        val tomorrow = LocalDate.now().plusDays(1).toString()
        return hours.filter { it.time.startsWith(today) || it.time.startsWith(tomorrow + "T00:") }
    }
}
