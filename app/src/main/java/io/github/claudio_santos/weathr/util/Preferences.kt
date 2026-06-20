package io.github.claudio_santos.weathr.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weathr")
private val Context.cacheDataStore: DataStore<Preferences> by preferencesDataStore(name = "weathr_cache")

@Singleton
class Preferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object PrefKeys {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit")
        val LANGUAGE = stringPreferencesKey("language")
        val USE_GPS = booleanPreferencesKey("use_gps")
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val CITY_NAME = stringPreferencesKey("city_name")
        val CITY_TIMEZONE = stringPreferencesKey("city_timezone")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    private object CacheKeys {
        val FORECAST_CACHE = stringPreferencesKey("forecast_cache")
        val HISTORICAL_CACHE = stringPreferencesKey("historical_cache")
        val LAST_FETCH_TIMESTAMP = longPreferencesKey("last_fetch_timestamp")
    }

    private fun defaultLanguage(): String {
        val lang = Locale.getDefault().language
        return if (lang.startsWith("pt")) "pt" else "en"
    }

    val temperatureUnit: Flow<String> = context.dataStore.data.map { it[PrefKeys.TEMPERATURE_UNIT] ?: "celsius" }.distinctUntilChanged()
    val windSpeedUnit: Flow<String> = context.dataStore.data.map { it[PrefKeys.WIND_SPEED_UNIT] ?: "kmh" }.distinctUntilChanged()
    val language: Flow<String> = context.dataStore.data.map { it[PrefKeys.LANGUAGE] ?: defaultLanguage() }.distinctUntilChanged()
    val useGps: Flow<Boolean> = context.dataStore.data.map { it[PrefKeys.USE_GPS] ?: true }.distinctUntilChanged()
    val latitude: Flow<Double?> = context.dataStore.data.map { it[PrefKeys.LATITUDE] }.distinctUntilChanged()
    val longitude: Flow<Double?> = context.dataStore.data.map { it[PrefKeys.LONGITUDE] }.distinctUntilChanged()
    val cityName: Flow<String?> = context.dataStore.data.map { it[PrefKeys.CITY_NAME] }.distinctUntilChanged()
    val cityTimezone: Flow<String?> = context.dataStore.data.map { it[PrefKeys.CITY_TIMEZONE] }.distinctUntilChanged()
    val forecastCache: Flow<String?> = context.cacheDataStore.data.map { it[CacheKeys.FORECAST_CACHE] }.distinctUntilChanged()
    val historicalCache: Flow<String?> = context.cacheDataStore.data.map { it[CacheKeys.HISTORICAL_CACHE] }.distinctUntilChanged()
    val lastFetchTimestamp: Flow<Long?> = context.cacheDataStore.data.map { it[CacheKeys.LAST_FETCH_TIMESTAMP] }.distinctUntilChanged()
    val themeMode: Flow<String> = context.dataStore.data.map { it[PrefKeys.THEME_MODE] ?: "system" }.distinctUntilChanged()

    suspend fun setTemperatureUnit(unit: String) { context.dataStore.edit { it[PrefKeys.TEMPERATURE_UNIT] = unit } }
    suspend fun setWindSpeedUnit(unit: String) { context.dataStore.edit { it[PrefKeys.WIND_SPEED_UNIT] = unit } }
    suspend fun setLanguage(lang: String) { context.dataStore.edit { it[PrefKeys.LANGUAGE] = lang } }
    suspend fun setUseGps(use: Boolean) { context.dataStore.edit { it[PrefKeys.USE_GPS] = use } }
    suspend fun setLocation(lat: Double, lon: Double, name: String, timezone: String) {
        context.dataStore.edit {
            it[PrefKeys.LATITUDE] = lat
            it[PrefKeys.LONGITUDE] = lon
            it[PrefKeys.CITY_NAME] = name
            it[PrefKeys.CITY_TIMEZONE] = timezone
        }
    }
    suspend fun setForecastCache(json: String) { context.cacheDataStore.edit { it[CacheKeys.FORECAST_CACHE] = json } }
    suspend fun setHistoricalCache(json: String) { context.cacheDataStore.edit { it[CacheKeys.HISTORICAL_CACHE] = json } }
    suspend fun setLastFetchTimestamp(ts: Long) { context.cacheDataStore.edit { it[CacheKeys.LAST_FETCH_TIMESTAMP] = ts } }
    suspend fun setThemeMode(mode: String) { context.dataStore.edit { it[PrefKeys.THEME_MODE] = mode } }

    suspend fun isLocationSet(): Boolean {
        val lat = context.dataStore.data.first()[PrefKeys.LATITUDE]
        return lat != null
    }
}
