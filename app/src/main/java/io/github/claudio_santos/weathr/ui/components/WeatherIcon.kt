package io.github.claudio_santos.weathr.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.claudio_santos.weathr.util.getWeatherIcon

@Composable
fun WeatherIcon(
    weatherCode: Int,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Icon(
        imageVector = getWeatherIcon(weatherCode, isDay),
        contentDescription = null,
        modifier = modifier.size(size)
    )
}
