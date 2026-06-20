package io.github.claudio_santos.weathr.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.rotate
import compose.icons.WeatherIcons
import compose.icons.weathericons.*
import io.github.claudio_santos.weathr.util.toWindRotation
import io.github.claudio_santos.weathr.util.windUnitLabel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.claudio_santos.weathr.R
import io.github.claudio_santos.weathr.domain.model.Weather
import io.github.claudio_santos.weathr.util.wmoDescriptionRes

@Composable
fun CurrentWeatherCard(
    weather: Weather,
    locationName: String?,
    windSpeedUnit: String = "kmh",
    modifier: Modifier = Modifier
) {
    val windUnitLabel = windSpeedUnit.windUnitLabel()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (locationName != null) {
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherIcon(
                    weatherCode = weather.weatherCode,
                    isDay = weather.isDay,
                    size = 72.dp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${weather.temperature.toInt()}°",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = stringResource(R.string.feels_like, weather.feelsLike.toInt()),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "\u2191 ${weather.tempMax.toInt()}°",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "\u2193 ${weather.tempMin.toInt()}°",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(wmoDescriptionRes(weather.weatherCode)),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailCard(
                    icon = WeatherIcons.Humidity,
                    value = "${weather.humidity.toInt()}%",
                    label = stringResource(R.string.humidity),
                    modifier = Modifier.weight(1f)
                )
                DetailCard(
                    icon = WeatherIcons.WindDeg,
                    value = "${weather.windSpeed.toInt()} $windUnitLabel",
                    label = stringResource(R.string.wind),
                    modifier = Modifier.weight(1f),
                    rotation = weather.windDirection.toWindRotation()
                )
            }
            Spacer(Modifier.height(8.dp))
            if (weather.sunrise.isNotEmpty() || weather.sunset.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (weather.sunrise.isNotEmpty()) {
                        DetailCard(
                            icon = WeatherIcons.Sunrise,
                            value = weather.sunrise.substringAfter("T").take(5),
                            label = stringResource(R.string.sunrise),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (weather.sunset.isNotEmpty()) {
                        DetailCard(
                            icon = WeatherIcons.Sunset,
                            value = weather.sunset.substringAfter("T").take(5),
                            label = stringResource(R.string.sunset),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    rotation: Float = 0f
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp).rotate(rotation),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
