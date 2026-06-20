# Weathr

Android weather app. Jetpack Compose + Material 3. Open-Meteo API (free, no key).
English + Portuguese (PT-PT).

## Features
- Current conditions with hourly forecast (every 2 hours, today only)
- 7-day forecast with inline hourly expansion per day
- Historical past week
- GPS auto-location or city search
- Wind speed & direction, precipitation probability, feels-like
- Material 3 dynamic color (Monet), dark/light/system theme
- Pull-to-refresh (no background sync)

## Tech Stack
Kotlin 2.2.10 · AGP 9.2.1 · KSP 2.2.10-2.0.2 · Hilt 2.59.2 · Ktor 3.1.2
Kotlinx Serialization 1.8.1 · Compose BOM 2026.02.01 · Navigation Compose 2.8.6
DataStore Preferences 1.1.3 · Play Services Location 21.3.0
minSdk 29 · targetSdk 36 · compileSdk 37
