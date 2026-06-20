package io.github.claudio_santos.weathr.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.WeatherIcons
import compose.icons.weathericons.*
import io.github.claudio_santos.weathr.R
import io.github.claudio_santos.weathr.util.toWindRotation
import io.github.claudio_santos.weathr.util.windUnitLabel
import io.github.claudio_santos.weathr.domain.model.DailyForecast
import io.github.claudio_santos.weathr.domain.model.HourlyData
import io.github.claudio_santos.weathr.util.wmoDescriptionRes
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DailyCard(
    forecast: DailyForecast,
    windSpeedUnit: String = "kmh",
    isExpanded: Boolean = false,
    hourlyData: List<HourlyData> = emptyList(),
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windUnitLabel = windSpeedUnit.windUnitLabel()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatDate(forecast.date),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(wmoDescriptionRes(forecast.weatherCode)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            WeatherIcons.Raindrop,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = forecast.precipitationProbabilityMax?.let { "${it.toInt()}%" } ?: "-",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            WeatherIcons.WindDeg,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp).rotate(forecast.windDirection.toWindRotation()),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "${forecast.windSpeedMax.toInt()} $windUnitLabel",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                WeatherIcon(
                    weatherCode = forecast.weatherCode,
                    isDay = true,
                    size = 40.dp
                )
                Spacer(Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "\u2191 ${forecast.tempMax.toInt()}°",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "\u2193 ${forecast.tempMin.toInt()}°",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            AnimatedVisibility(visible = isExpanded) {
                Box(modifier = Modifier.padding(bottom = 8.dp)) {
                    HourlyRow(
                        hourlyForecast = hourlyData,
                        highlightCurrentHour = false,
                        scrollToHour = 10,
                        windSpeedUnit = windSpeedUnit
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.getDefault())
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}
