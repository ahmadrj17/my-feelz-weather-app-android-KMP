package com.example.pohonch.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pohonch.data.WeatherRepository
import com.example.pohonch.model.ForecastResponse
import com.example.pohonch.model.GeocodeResponse
import com.example.pohonch.model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
            val current: WeatherResponse,
            val forecast: ForecastResponse,
            val units: String // "metric" or "imperial"
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var currentUnits = "metric"
    private var currentLat: Double? = null
    private var currentLon: Double? = null

    var searchResults by mutableStateOf<List<GeocodeResponse>>(emptyList())
        private set
    var isSearching by mutableStateOf(false)
        private set

    init {
        loadCurrentLocationWeather()
    }

    fun loadCurrentLocationWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val loc = repository.getCurrentLocation()
                if (loc != null) {
                    currentLat = loc.first
                    currentLon = loc.second
                    fetchWeather(currentLat!!, currentLon!!)
                } else {
                    _uiState.value =
                            WeatherUiState.Error(
                                    "Could not get GPS location. Please check permissions or search for a city."
                            )
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("Failed to load location: ${e.message}")
            }
        }
    }

    fun loadWeatherForLocation(lat: Double, lon: Double) {
        currentLat = lat
        currentLon = lon
        fetchWeather(lat, lon)
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val current = repository.getWeather(lat, lon, currentUnits)
                val forecast = repository.getForecast(lat, lon, currentUnits)
                _uiState.value = WeatherUiState.Success(current, forecast, currentUnits)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("Network Error: ${e.message}")
            }
        }
    }

    fun toggleUnits() {
        currentUnits = if (currentUnits == "metric") "imperial" else "metric"
        if (currentLat != null && currentLon != null) {
            fetchWeather(currentLat!!, currentLon!!)
        }
    }

    fun searchCity(query: String) {
        viewModelScope.launch {
            isSearching = true
            try {
                searchResults = repository.searchLocation(query)
            } catch (e: Exception) {
                searchResults = emptyList()
            } finally {
                isSearching = false
            }
        }
    }

    fun clearSearch() {
        searchResults = emptyList()
    }
}
