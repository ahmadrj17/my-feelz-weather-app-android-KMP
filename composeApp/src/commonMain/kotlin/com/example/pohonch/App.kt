package com.example.pohonch

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import com.example.pohonch.ui.SplashScreen
import com.example.pohonch.ui.MainWeatherScreen

@Composable
fun App(apiKey: String = "", geolocator: dev.jordond.compass.geolocation.Geolocator) {
    MaterialTheme {
        var showSplash by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            delay(2000)
            showSplash = false
        }

        if (showSplash) {
            SplashScreen()
        } else {
            MainWeatherScreen(apiKey, geolocator)
        }
    }
}