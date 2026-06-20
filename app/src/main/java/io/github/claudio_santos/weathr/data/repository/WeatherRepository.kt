package io.github.claudio_santos.weathr.data.repository

import io.github.claudio_santos.weathr.domain.model.DailyForecast
import io.github.claudio_santos.weathr.domain.model.ForecastData
import io.github.claudio_santos.weathr.domain.model.Location

interface WeatherRepository {
    suspend fun getForecast(latitude: Double, longitude: Double, temperatureUnit: String, windSpeedUnit: String): ForecastData
    suspend fun getCachedForecast(): ForecastData?
    suspend fun getHistorical(latitude: Double, longitude: Double, startDate: String, endDate: String, temperatureUnit: String, windSpeedUnit: String): List<DailyForecast>
    suspend fun searchCity(query: String): List<Location>
}
