package io.github.claudio_santos.weathr.domain.model

data class Weather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Double,
    val weatherCode: Int,
    val windSpeed: Double,
    val windDirection: Double,
    val isDay: Boolean,
    val sunrise: String,
    val sunset: String,
    val tempMax: Double,
    val tempMin: Double,
    val hourlyForecast: List<HourlyData>
)

data class HourlyData(
    val time: String,
    val temperature: Double,
    val precipitationProbability: Double?,
    val weatherCode: Int,
    val windSpeed: Double,
    val windDirection: Double,
    val isDay: Boolean
)
