package com.example.pohonch

import androidx.compose.ui.window.ComposeUIViewController
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.mobile.MobileLocator
import dev.jordond.compass.permissions.LocationPermissionController

fun MainViewController() = ComposeUIViewController {
    val permissionController = LocationPermissionController()
    val geolocator = Geolocator(MobileLocator(permissionController))
    App(geolocator = geolocator)
}
