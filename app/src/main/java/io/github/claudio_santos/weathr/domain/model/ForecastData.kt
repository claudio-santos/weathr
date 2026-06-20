package io.github.claudio_santos.weathr.domain.model

data class ForecastData(
    val current: Weather,
    val daily: List<DailyForecast>
)
