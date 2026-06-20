package io.github.claudio_santos.weathr.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

enum class WmoWeather(val code: IntRange, val icon: ImageVector, val nightIcon: ImageVector) {
    CLEAR(0..0, Icons.Filled.WbSunny, Icons.Filled.NightsStay),
    PARTLY_CLOUDY(1..2, Icons.Filled.WbCloudy, Icons.Filled.WbCloudy),
    OVERCAST(3..3, Icons.Filled.Cloud, Icons.Filled.Cloud),
    FOG(45..48, Icons.Filled.BlurOn, Icons.Filled.BlurOn),
    DRIZZLE(51..55, Icons.Filled.Grain, Icons.Filled.Grain),
    FREEZING_DRIZZLE(56..57, Icons.Filled.AcUnit, Icons.Filled.AcUnit),
    RAIN(61..65, Icons.Filled.Umbrella, Icons.Filled.Umbrella),
    FREEZING_RAIN(66..67, Icons.Filled.AcUnit, Icons.Filled.AcUnit),
    SNOW(71..77, Icons.Filled.AcUnit, Icons.Filled.AcUnit),
    RAIN_SHOWERS(80..82, Icons.Filled.Umbrella, Icons.Filled.Umbrella),
    SNOW_SHOWERS(85..86, Icons.Filled.AcUnit, Icons.Filled.AcUnit),
    THUNDERSTORM(95..99, Icons.Filled.Thunderstorm, Icons.Filled.Thunderstorm);

    companion object {
        fun fromCode(code: Int): WmoWeather =
            entries.firstOrNull { code in it.code } ?: CLEAR
    }
}

fun getWeatherIcon(code: Int, isDay: Boolean): ImageVector {
    val weather = WmoWeather.fromCode(code)
    return if (isDay) weather.icon else weather.nightIcon
}

fun wmoDescriptionRes(code: Int): Int = when (code) {
    0 -> io.github.claudio_santos.weathr.R.string.wmo_clear
    in 1..2 -> io.github.claudio_santos.weathr.R.string.wmo_partly_cloudy
    in 3..3 -> io.github.claudio_santos.weathr.R.string.wmo_overcast
    in 45..48 -> io.github.claudio_santos.weathr.R.string.wmo_fog
    in 51..55 -> io.github.claudio_santos.weathr.R.string.wmo_drizzle
    in 56..57 -> io.github.claudio_santos.weathr.R.string.wmo_freezing_drizzle
    in 61..65 -> io.github.claudio_santos.weathr.R.string.wmo_rain
    in 66..67 -> io.github.claudio_santos.weathr.R.string.wmo_freezing_rain
    in 71..77 -> io.github.claudio_santos.weathr.R.string.wmo_snow
    in 80..82 -> io.github.claudio_santos.weathr.R.string.wmo_rain_showers
    in 85..86 -> io.github.claudio_santos.weathr.R.string.wmo_snow_showers
    in 95..99 -> io.github.claudio_santos.weathr.R.string.wmo_thunderstorm
    else -> io.github.claudio_santos.weathr.R.string.wmo_clear
}
