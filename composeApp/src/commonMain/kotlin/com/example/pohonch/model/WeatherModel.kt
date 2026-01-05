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
data class MainData(val temp: Double, val humidity: Int, val feels_like: Double, val pressure: Int)

@Serializable data class WeatherDescription(val description: String, val main: String)

@Serializable data class WindData(val speed: Double)

@Serializable data class ForecastResponse(val list: List<ForecastItem>, val city: CityData)

@Serializable
data class ForecastItem(
        val dt: Long,
        val main: MainData,
        val weather: List<WeatherDescription>,
        val wind: WindData,
        val dt_txt: String
)

@Serializable
data class CityData(val name: String, val country: String, val sunrise: Long, val sunset: Long)

@Serializable
data class GeocodeResponse(
        val name: String,
        val lat: Double,
        val lon: Double,
        val country: String? = null,
        val state: String? = null
)
