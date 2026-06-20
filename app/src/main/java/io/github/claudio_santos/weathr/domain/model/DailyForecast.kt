package io.github.claudio_santos.weathr.domain.model

data class DailyForecast(
    val date: String,
    val weatherCode: Int,
    val tempMax: Double,
    val tempMin: Double,
    val precipitationProbabilityMax: Double?,
    val windSpeedMax: Double,
    val windDirection: Double,
    val sunrise: String?,
    val sunset: String?
)
