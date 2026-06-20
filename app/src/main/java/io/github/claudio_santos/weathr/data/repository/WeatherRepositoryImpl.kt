package io.github.claudio_santos.weathr.data.repository

import io.github.claudio_santos.weathr.data.remote.ForecastApi
import io.github.claudio_santos.weathr.data.remote.GeocodingApi
import io.github.claudio_santos.weathr.data.remote.HistoricalForecastApi
import io.github.claudio_santos.weathr.data.remote.dto.ForecastResponse
import io.github.claudio_santos.weathr.data.remote.dto.GeocodingResult
import io.github.claudio_santos.weathr.domain.model.DailyForecast
import io.github.claudio_santos.weathr.domain.model.ForecastData
import io.github.claudio_santos.weathr.domain.model.HourlyData
import io.github.claudio_santos.weathr.domain.model.Location
import io.github.claudio_santos.weathr.domain.model.Weather
import io.github.claudio_santos.weathr.util.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val forecastApi: ForecastApi,
    private val historicalForecastApi: HistoricalForecastApi,
    private val geocodingApi: GeocodingApi,
    private val preferences: Preferences,
    private val json: Json
) : WeatherRepository {

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String,
        windSpeedUnit: String
    ): ForecastData {
        val response = forecastApi.getForecast(latitude, longitude, temperatureUnit, windSpeedUnit)
        val jsonStr = json.encodeToString(ForecastResponse.serializer(), response)
        preferences.setForecastCache(jsonStr)
        preferences.setLastFetchTimestamp(System.currentTimeMillis())
        return response.toDomain()
    }

    override suspend fun getCachedForecast(): ForecastData? {
        val jsonStr = preferences.forecastCache.first() ?: return null
        return try {
            val response = json.decodeFromString<ForecastResponse>(jsonStr)
            response.toDomain()
        } catch (_: Exception) { null }
    }

    override suspend fun getHistorical(
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String,
        temperatureUnit: String,
        windSpeedUnit: String
    ): List<DailyForecast> {
        val response = historicalForecastApi.getHistorical(latitude, longitude, startDate, endDate, temperatureUnit, windSpeedUnit)
        val jsonStr = json.encodeToString(ForecastResponse.serializer(), response)
        preferences.setHistoricalCache(jsonStr)
        return response.dailyForecasts()
    }

    override suspend fun searchCity(query: String): List<Location> {
        val response = geocodingApi.searchCity(query)
        return response.results?.map { it.toDomain() } ?: emptyList()
    }

    private fun ForecastResponse.toDomain(): ForecastData {
        val current = current ?: return ForecastData(
            current = Weather(0.0, 0.0, 0.0, 0, 0.0, 0.0, true, "", "", 0.0, 0.0, emptyList()),
            daily = emptyList()
        )

        val hourlyList = hourly?.let { h ->
            h.time.mapIndexed { index, time ->
                HourlyData(
                    time = time,
                    temperature = h.temperature2m.getOrElse(index) { 0.0 },
                    precipitationProbability = h.precipitationProbability?.getOrElse(index) { null },
                    weatherCode = h.weatherCode.getOrElse(index) { 0 },
                    windSpeed = h.windSpeed10m.getOrElse(index) { 0.0 },
                    windDirection = h.windDirection10m?.getOrElse(index) { 0.0 } ?: 0.0,
                    isDay = h.isDay.getOrElse(index) { 1 } == 1
                )
            }
        } ?: emptyList()

        val sunriseStr = daily?.sunrise?.firstOrNull() ?: ""
        val sunsetStr = daily?.sunset?.firstOrNull() ?: ""
        val todayMax = (daily?.apparentTemperatureMax?.firstOrNull() ?: 0.0)
        val todayMin = (daily?.apparentTemperatureMin?.firstOrNull() ?: 0.0)

        val currentWeather = Weather(
            temperature = current.temperature2m,
            feelsLike = current.apparentTemperature,
            humidity = current.relativeHumidity2m,
            weatherCode = current.weatherCode,
            windSpeed = current.windSpeed10m,
            windDirection = current.windDirection10m,
            isDay = current.isDay == 1,
            sunrise = sunriseStr,
            sunset = sunsetStr,
            tempMax = todayMax,
            tempMin = todayMin,
            hourlyForecast = hourlyList
        )

        val dailyForecasts = daily?.let { d ->
            d.time.drop(1).mapIndexed { index, date ->
                val i = index + 1
                DailyForecast(
                    date = date,
                    weatherCode = d.weatherCode.getOrElse(i) { 0 },
                    tempMax = d.apparentTemperatureMax?.getOrElse(i) { 0.0 } ?: 0.0,
                    tempMin = d.apparentTemperatureMin?.getOrElse(i) { 0.0 } ?: 0.0,
                    precipitationProbabilityMax = d.precipitationProbabilityMax?.getOrElse(i) { null },
                    windSpeedMax = d.windSpeed10mMax.getOrElse(i) { 0.0 },
                    windDirection = d.windDirection10mDominant?.getOrElse(i) { 0.0 } ?: 0.0,
                    sunrise = d.sunrise.getOrElse(i) { "" },
                    sunset = d.sunset.getOrElse(i) { "" }
                )
            }
        } ?: emptyList()

        return ForecastData(current = currentWeather, daily = dailyForecasts)
    }

    private fun ForecastResponse.dailyForecasts(): List<DailyForecast> {
        val d = daily ?: return emptyList()
        return d.time.mapIndexed { index, date ->
            DailyForecast(
                date = date,
                weatherCode = d.weatherCode.getOrElse(index) { 0 },
                tempMax = d.apparentTemperatureMax?.getOrElse(index) { 0.0 } ?: 0.0,
                tempMin = d.apparentTemperatureMin?.getOrElse(index) { 0.0 } ?: 0.0,
                precipitationProbabilityMax = d.precipitationProbabilityMax?.getOrElse(index) { null },
                windSpeedMax = d.windSpeed10mMax.getOrElse(index) { 0.0 },
                windDirection = d.windDirection10mDominant?.getOrElse(index) { 0.0 } ?: 0.0,
                sunrise = d.sunrise.getOrElse(index) { "" },
                sunset = d.sunset.getOrElse(index) { "" }
            )
        }
    }

    private fun GeocodingResult.toDomain(): Location = Location(
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country ?: "",
        timezone = timezone ?: ""
    )
}
