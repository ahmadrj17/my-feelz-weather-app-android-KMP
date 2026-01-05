package com.example.pohonch

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.pohonch.data.WeatherRepository
import com.example.pohonch.ui.HomeScreen
import com.example.pohonch.ui.SplashScreen
import com.example.pohonch.viewmodel.WeatherViewModel
import kotlinx.coroutines.delay

@Composable
fun App(apiKey: String = "", geolocator: dev.jordond.compass.geolocation.Geolocator) {
    MaterialTheme {
        val repository = remember { WeatherRepository(apiKey, geolocator) }
        val viewModel = remember { WeatherViewModel(repository) }

        var showSplash by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            delay(3000)
            showSplash = false
        }

        if (showSplash) {
            SplashScreen()
        } else {
            HomeScreen(viewModel)
        }
    }
}
