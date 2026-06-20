package io.github.claudio_santos.weathr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val latitude: Double,
    val longitude: Double,
    @SerialName("generationtime_ms") val generationtimeMs: Double,
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int,
    val timezone: String,
    @SerialName("timezone_abbreviation") val timezoneAbbreviation: String,
    val elevation: Double,
    @SerialName("current_units") val currentUnits: CurrentUnits? = null,
    val current: CurrentData? = null,
    @SerialName("hourly_units") val hourlyUnits: HourlyUnits? = null,
    val hourly: HourlyData? = null,
    @SerialName("daily_units") val dailyUnits: DailyUnits? = null,
    val daily: DailyData? = null
)

@Serializable
data class CurrentUnits(
    val time: String,
    val interval: String,
    @SerialName("temperature_2m") val temperature2m: String,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: String,
    @SerialName("apparent_temperature") val apparentTemperature: String,
    @SerialName("weather_code") val weatherCode: String,
    @SerialName("wind_speed_10m") val windSpeed10m: String,
    @SerialName("wind_direction_10m") val windDirection10m: String,
    @SerialName("is_day") val isDay: String
)

@Serializable
data class CurrentData(
    val time: String,
    val interval: Int,
    @SerialName("temperature_2m") val temperature2m: Double,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: Double,
    @SerialName("apparent_temperature") val apparentTemperature: Double,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("wind_speed_10m") val windSpeed10m: Double,
    @SerialName("wind_direction_10m") val windDirection10m: Double,
    @SerialName("is_day") val isDay: Int
)

@Serializable
data class HourlyUnits(
    val time: String,
    @SerialName("temperature_2m") val temperature2m: String,
    @SerialName("precipitation_probability") val precipitationProbability: String? = null,
    @SerialName("weather_code") val weatherCode: String,
    @SerialName("wind_speed_10m") val windSpeed10m: String,
    @SerialName("wind_direction_10m") val windDirection10m: String? = null,
    @SerialName("is_day") val isDay: String
)

@Serializable
data class HourlyData(
    val time: List<String>,
    @SerialName("temperature_2m") val temperature2m: List<Double>,
    @SerialName("precipitation_probability") val precipitationProbability: List<Double>? = null,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("wind_speed_10m") val windSpeed10m: List<Double>,
    @SerialName("wind_direction_10m") val windDirection10m: List<Double>? = null,
    @SerialName("is_day") val isDay: List<Int>
)

@Serializable
data class DailyUnits(
    val time: String,
    @SerialName("weather_code") val weatherCode: String,
    @SerialName("apparent_temperature_max") val apparentTemperatureMax: String? = null,
    @SerialName("apparent_temperature_min") val apparentTemperatureMin: String? = null,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: String? = null,
    @SerialName("wind_speed_10m_max") val windSpeed10mMax: String,
    @SerialName("wind_direction_10m_dominant") val windDirection10mDominant: String? = null,
    val sunrise: String,
    val sunset: String
)

@Serializable
data class DailyData(
    val time: List<String>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("apparent_temperature_max") val apparentTemperatureMax: List<Double>? = null,
    @SerialName("apparent_temperature_min") val apparentTemperatureMin: List<Double>? = null,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Double>? = null,
    @SerialName("wind_speed_10m_max") val windSpeed10mMax: List<Double>,
    @SerialName("wind_direction_10m_dominant") val windDirection10mDominant: List<Double>? = null,
    val sunrise: List<String>,
    val sunset: List<String>
)
