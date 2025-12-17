package com.example.pohonch.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val main: MainData,
    val weather: List<WeatherDescription>,
    val name: String,
    val wind: WindData? = null
)

@Serializable
data class MainData(
    val temp: Double,
    val humidity: Int,
    val feels_like: Double,
    val pressure: Int
)

@Serializable
data class WeatherDescription(val description: String, val main: String)

@Serializable
data class WindData(val speed: Double)