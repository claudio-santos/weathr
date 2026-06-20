package io.github.claudio_santos.weathr.util

fun String.windUnitLabel(): String = when (this) {
    "kmh" -> "km/h"
    "ms" -> "m/s"
    "mph" -> "mph"
    "kn" -> "kn"
    else -> this
}

fun Double.toWindRotation(): Float = this.toFloat() + 180f
