package io.github.claudio_santos.weathr.data.remote

import io.github.claudio_santos.weathr.data.remote.dto.GeocodingResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun searchCity(name: String, count: Int = 10): GeocodingResponse {
        return client.get("https://geocoding-api.open-meteo.com/v1/search") {
            parameter("name", name)
            parameter("count", count)
            parameter("language", "en")
            parameter("format", "json")
        }.body()
    }
}
