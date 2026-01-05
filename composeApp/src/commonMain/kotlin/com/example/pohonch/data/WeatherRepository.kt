package com.example.pohonch.data

import com.example.pohonch.model.ForecastResponse
import com.example.pohonch.model.GeocodeResponse
import com.example.pohonch.model.WeatherResponse
import dev.jordond.compass.geolocation.Geolocator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class WeatherRepository(private val apiKey: String, private val geolocator: Geolocator) {
    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        return try {
            val result = geolocator.current()
            val location = result.getOrNull()
            location?.coordinates?.let { it.latitude to it.longitude }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getWeather(lat: Double, lon: Double, units: String = "metric"): WeatherResponse {
        return client.get(
                        "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=$units&appid=$apiKey"
                )
                .body()
    }

    suspend fun getForecast(lat: Double, lon: Double, units: String = "metric"): ForecastResponse {
        return client.get(
                        "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&units=$units&appid=$apiKey"
                )
                .body()
    }

    suspend fun searchLocation(query: String): List<GeocodeResponse> {
        return client.get(
                        "https://api.openweathermap.org/geo/1.0/direct?q=$query&limit=5&appid=$apiKey"
                )
                .body()
    }
}
