package com.example.pohonch.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.pohonch.viewmodel.WeatherUiState
import com.example.pohonch.viewmodel.WeatherViewModel

// --- 1. NEW ANIMATED SPLASH SCREEN ---
@Composable
fun SplashScreen() {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by
            animateFloatAsState(
                    targetValue = if (startAnimation) 1f else 0f,
                    animationSpec = tween(durationMillis = 1500)
            )
    val scaleAnim by
            animateFloatAsState(
                    targetValue = if (startAnimation) 1f else 0.5f,
                    animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
            )

    LaunchedEffect(Unit) { startAnimation = true }

    Box(
            modifier =
                    Modifier.fillMaxSize()
                            .background(
                                    Brush.verticalGradient(
                                            listOf(Color(0xFF4CA1AF), Color(0xFFC4E0E5))
                                    )
                            ),
            contentAlignment = Alignment.Center
    ) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(scaleAnim).alpha(alphaAnim)
        ) {
            Box(
                    modifier =
                            Modifier.size(120.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        imageVector = Icons.Rounded.Cloud,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                    text = "My Feelz",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
            )
            Text(
                    text = "Weather Forecast",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// --- 2. HOME SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    var testCondition by remember { mutableStateOf<String?>(null) }
    var testIsNight by remember { mutableStateOf<Boolean?>(null) }

    val successState = uiState as? WeatherUiState.Success
    val weatherData = successState?.current

    val headerTitle =
            if (successState != null) {
                "${successState.current.name}, ${successState.forecast.city.country}"
            } else {
                "My Feelz"
            }

    val realCondition = weatherData?.weather?.firstOrNull()?.main ?: "Clear"
    val realIconCode = weatherData?.weather?.firstOrNull()?.let { "d" } ?: "d"
    val realIsNight = realIconCode.endsWith("n")

    val finalCondition = testCondition ?: realCondition
    val finalIsNight = testIsNight ?: realIsNight

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedWeatherBackground(condition = finalCondition, isNight = finalIsNight)

        Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.systemBars
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                text = headerTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                            onClick = { showSettings = true },
                            colors =
                                    IconButtonDefaults.iconButtonColors(
                                            containerColor = Color.White.copy(alpha = 0.2f)
                                    )
                    ) {
                        Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                        )
                    }
                }

                Crossfade(targetState = uiState, label = "WeatherState") { state ->
                    when (state) {
                        is WeatherUiState.Loading -> {
                            Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(color = Color.White) }
                        }
                        is WeatherUiState.Error -> {
                            Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                            text = state.message,
                                            color = Color.White,
                                            modifier = Modifier.padding(16.dp),
                                            style = MaterialTheme.typography.bodyLarge
                                    )
                                    Button(
                                            onClick = { viewModel.loadCurrentLocationWeather() },
                                            colors =
                                                    ButtonDefaults.buttonColors(
                                                            containerColor =
                                                                    Color.White.copy(alpha = 0.3f)
                                                    )
                                    ) { Text("Retry", color = Color.White) }
                                }
                            }
                        }
                        is WeatherUiState.Success -> {
                            PullToRefreshBox(
                                    isRefreshing = false,
                                    onRefresh = { viewModel.loadCurrentLocationWeather() },
                                    modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .verticalScroll(rememberScrollState())
                                                        .padding(horizontal = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CurrentWeatherSection(
                                            weather = state.current,
                                            city = state.forecast.city,
                                            units = state.units
                                    )
                                    Spacer(modifier = Modifier.height(32.dp))
                                    HourlyForecastSection(state.forecast.list)
                                    Spacer(modifier = Modifier.height(24.dp))
                                    DailyForecastSection(state.forecast.list)
                                    Spacer(modifier = Modifier.height(48.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showSettings) {
            SettingsDialog(
                    viewModel = viewModel,
                    onDismiss = { showSettings = false },
                    onTestCondition = { cond, night ->
                        testCondition = cond
                        testIsNight = night
                    }
            )
        }
    }
}

@Composable
fun SettingsDialog(
        viewModel: WeatherViewModel,
        onDismiss: () -> Unit,
        onTestCondition: (String?, Boolean?) -> Unit // Callback for debug
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = viewModel.searchResults
    val isSearching = viewModel.isSearching

    Dialog(onDismissRequest = onDismiss) {
        Card(
                shape = RoundedCornerShape(24.dp),
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            "Search City",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            if (it.length > 2) viewModel.searchCity(it)
                        },
                        label = { Text("Enter city name") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                )

                if (isSearching) {
                    LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                    )
                }

                if (searchResults.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    searchResults.forEach { city ->
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clickable {
                                                    viewModel.loadWeatherForLocation(
                                                            city.lat,
                                                            city.lon
                                                    )
                                                    viewModel.clearSearch()
                                                    onTestCondition(null, null)
                                                    onDismiss()
                                                }
                                                .padding(vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                    "${city.name}, ${city.country ?: ""}",
                                    style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                        "Preferences",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                        onClick = { viewModel.toggleUnits() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                ) { Text("Toggle Unit (°C / °F)") }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BugReport, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                            "Test Animations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text("Click to force a weather state:", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))

                val debugOptions =
                        listOf(
                                Triple("Clear Day", "Clear", false),
                                Triple("Clear Night", "Clear", true),
                                Triple("Rain", "Rain", false),
                                Triple("Snow", "Snow", false),
                                Triple("Thunder", "Thunderstorm", false),
                                Triple("Cloudy", "Clouds", false)
                        )

                debugOptions.chunked(2).forEach { rowItems ->
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { (label, cond, night) ->
                            OutlinedButton(
                                    onClick = {
                                        onTestCondition(cond, night)
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(4.dp)
                            ) { Text(label, fontSize = 12.sp) }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Button(
                        onClick = {
                            onTestCondition(null, null)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) { Text("Reset to Real Weather") }
            }
        }
    }
}
