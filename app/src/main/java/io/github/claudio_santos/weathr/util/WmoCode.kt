package io.github.claudio_santos.weathr.util

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.WeatherIcons
import compose.icons.weathericons.*

enum class WmoWeather(val code: IntRange, val icon: ImageVector, val nightIcon: ImageVector) {
    CLEAR(0..0, WeatherIcons.DaySunny, WeatherIcons.NightClear),
    PARTLY_CLOUDY(1..2, WeatherIcons.DaySunnyOvercast, WeatherIcons.NightAltPartlyCloudy),
    OVERCAST(3..3, WeatherIcons.Cloudy, WeatherIcons.Cloudy),
    FOG(45..48, WeatherIcons.DayFog, WeatherIcons.NightFog),
    DRIZZLE(51..55, WeatherIcons.DaySprinkle, WeatherIcons.NightAltSprinkle),
    FREEZING_DRIZZLE(56..57, WeatherIcons.DayRainMix, WeatherIcons.NightAltRainMix),
    RAIN(61..65, WeatherIcons.DayRain, WeatherIcons.NightAltRain),
    FREEZING_RAIN(66..67, WeatherIcons.DaySleet, WeatherIcons.NightAltSleet),
    SNOW(71..77, WeatherIcons.DaySnow, WeatherIcons.NightAltSnow),
    RAIN_SHOWERS(80..82, WeatherIcons.DayShowers, WeatherIcons.NightAltShowers),
    SNOW_SHOWERS(85..86, WeatherIcons.DaySnow, WeatherIcons.NightAltSnow),
    THUNDERSTORM(95..99, WeatherIcons.DayThunderstorm, WeatherIcons.NightAltThunderstorm);

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
