package io.github.claudio_santos.weathr.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.claudio_santos.weathr.domain.model.HourlyData
import java.time.LocalDateTime

private fun windArrow(degrees: Double): String = when {
    degrees < 22.5 || degrees >= 337.5 -> "\u2191"
    degrees < 67.5 -> "\u2197"
    degrees < 112.5 -> "\u2192"
    degrees < 157.5 -> "\u2198"
    degrees < 202.5 -> "\u2193"
    degrees < 247.5 -> "\u2199"
    degrees < 292.5 -> "\u2190"
    else -> "\u2196"
}

@Composable
fun HourlyRow(
    hourlyForecast: List<HourlyData>,
    modifier: Modifier = Modifier,
    highlightCurrentHour: Boolean = true,
    scrollToHour: Int? = null,
    windSpeedUnit: String = "kmh"
) {
    val windUnitLabel = remember(windSpeedUnit) {
        when (windSpeedUnit) {
            "kmh" -> "km/h"
            "ms" -> "m/s"
            "mph" -> "mph"
            "kn" -> "kn"
            else -> windSpeedUnit
        }
    }

    val filtered = remember(hourlyForecast) {
        hourlyForecast.filterIndexed { index, _ -> index % 2 == 0 }
    }

    val currentHourIndex = if (highlightCurrentHour) {
        remember(filtered) {
            val now = LocalDateTime.now()
            var closestIdx = -1
            var minDiff = Int.MAX_VALUE
            for ((i, hour) in filtered.withIndex()) {
                val hourOfDay = hour.time.substringAfter("T").substringBefore(":").toIntOrNull() ?: continue
                val diff = kotlin.math.abs(hourOfDay - now.hour)
                if (diff < minDiff) {
                    minDiff = diff
                    closestIdx = i
                }
            }
            closestIdx
        }
    } else -1

    val scrollIndex = if (!highlightCurrentHour && scrollToHour != null) {
        remember(filtered, scrollToHour) {
            var closestIdx = -1
            var minDiff = Int.MAX_VALUE
            for ((i, hour) in filtered.withIndex()) {
                val hourOfDay = hour.time.substringAfter("T").substringBefore(":").toIntOrNull() ?: continue
                val diff = kotlin.math.abs(hourOfDay - scrollToHour)
                if (diff < minDiff) {
                    minDiff = diff
                    closestIdx = i
                }
            }
            closestIdx
        }
    } else -1

    val listState = rememberLazyListState()

    val initialScrollTo = if (highlightCurrentHour) currentHourIndex else scrollIndex
    LaunchedEffect(initialScrollTo) {
        if (initialScrollTo >= 0) listState.animateScrollToItem(initialScrollTo)
    }

    LazyRow(
        state = listState,
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(filtered) { index, hour ->
            HourCard(hour = hour, isCurrent = highlightCurrentHour && index == currentHourIndex, windUnitLabel = windUnitLabel)
        }
    }
}

@Composable
private fun HourCard(hour: HourlyData, isCurrent: Boolean = false, windUnitLabel: String = "km/h") {
    val time = hour.time.substringAfter("T").take(5)
    val arrow = windArrow(hour.windDirection)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .width(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            WeatherIcon(
                weatherCode = hour.weatherCode,
                isDay = hour.isDay,
                size = 24.dp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${hour.temperature.toInt()}°",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = hour.precipitationProbability?.let { "${it.toInt()}%" } ?: "-",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = "$arrow ${hour.windSpeed.toInt()} $windUnitLabel",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
