package com.example.pohonch

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform