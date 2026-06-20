package io.github.claudio_santos.weathr.data.remote

import io.github.claudio_santos.weathr.data.remote.dto.ForecastResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoricalForecastApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun getHistorical(
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String,
        temperatureUnit: String = "celsius",
        windSpeedUnit: String = "kmh"
    ): ForecastResponse {
        return client.get("https://historical-forecast-api.open-meteo.com/v1/forecast") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("start_date", startDate)
            parameter("end_date", endDate)
            parameter("daily", "weather_code,apparent_temperature_max,apparent_temperature_min,precipitation_probability_max,wind_speed_10m_max,sunrise,sunset")
            parameter("timezone", "auto")
            parameter("temperature_unit", temperatureUnit)
            parameter("wind_speed_unit", windSpeedUnit)
        }.body()
    }
}
