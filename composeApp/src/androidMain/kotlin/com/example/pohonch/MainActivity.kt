package com.example.pohonch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.mobile.MobileLocator
import dev.jordond.compass.permissions.LocationPermissionController 

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)

        val permissionController = LocationPermissionController()
        val geolocator = Geolocator(
            MobileLocator(
                permissionController = permissionController
            )
        )

        setContent {
            App(apiKey = BuildConfig.API_KEY, geolocator = geolocator)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // App()
}