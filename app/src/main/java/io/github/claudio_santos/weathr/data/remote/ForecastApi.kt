package io.github.claudio_santos.weathr.data.remote

import io.github.claudio_santos.weathr.data.remote.dto.ForecastResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForecastApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String = "celsius",
        windSpeedUnit: String = "kmh"
    ): ForecastResponse {
        return client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("current", "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m,wind_direction_10m,is_day")
            parameter("daily", "weather_code,apparent_temperature_max,apparent_temperature_min,precipitation_probability_max,wind_speed_10m_max,sunrise,sunset")
            parameter("hourly", "temperature_2m,precipitation_probability,weather_code,wind_speed_10m,wind_direction_10m,is_day")
            parameter("timezone", "auto")
            parameter("forecast_days", "7")
            parameter("temperature_unit", temperatureUnit)
            parameter("wind_speed_unit", windSpeedUnit)
        }.body()
    }
}
