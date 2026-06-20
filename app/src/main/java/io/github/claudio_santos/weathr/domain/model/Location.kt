package io.github.claudio_santos.weathr.domain.model

data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val timezone: String
)
