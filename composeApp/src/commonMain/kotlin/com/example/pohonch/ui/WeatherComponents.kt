package com.example.pohonch.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pohonch.model.CityData
import com.example.pohonch.model.ForecastItem
import com.example.pohonch.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun formatHourlyTime(dtTxt: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dtTxt)
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dtTxt
    }
}

fun formatDailyDate(dtTxt: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dtTxt)
        val outputFormat = SimpleDateFormat("dd-MMMM", Locale.getDefault())
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dtTxt
    }
}

fun formatTime(unixSeconds: Long): String {
    val date = Date(unixSeconds * 1000L)
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}

@Composable
fun GlassBox(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
            modifier =
                    modifier.clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(
                                    width = 1.dp,
                                    brush =
                                            Brush.verticalGradient(
                                                    listOf(
                                                            Color.White.copy(alpha = 0.4f),
                                                            Color.White.copy(alpha = 0.1f)
                                                    )
                                            ),
                                    shape = RoundedCornerShape(24.dp)
                            ),
            contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
fun CurrentWeatherSection(weather: WeatherResponse, city: CityData, units: String) {
    val unitSymbol = if (units == "metric") "C" else "F"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
                text = weather.name,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 24.dp)
        )
        Text(
                text =
                        weather.weather.firstOrNull()?.description?.replaceFirstChar {
                            it.uppercase()
                        }
                                ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(start = 24.dp)) {
            Box {
                Text(
                        text = "${weather.main.temp.toInt()}°",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 110.sp),
                        fontWeight = FontWeight.Light,
                        color = Color.Black.copy(alpha = 0.1f),
                        modifier = Modifier.offset(x = 4.dp, y = 4.dp)
                )
                Text(
                        text = "${weather.main.temp.toInt()}°",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 110.sp),
                        fontWeight = FontWeight.Light,
                        color = Color.White
                )
            }
            Text(
                    text = unitSymbol,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        GlassBox(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherDetailItem(
                            Icons.Outlined.WaterDrop,
                            "${weather.main.humidity}%",
                            "Humidity"
                    )
                    WeatherDetailItem(Icons.Outlined.Air, "${weather.wind?.speed} m/s", "Wind")
                    WeatherDetailItem(
                            Icons.Outlined.Thermostat,
                            "${weather.main.feels_like.toInt()}°",
                            "Feels Like"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherDetailItem(Icons.Rounded.WbSunny, formatTime(city.sunrise), "Sunrise")
                    WeatherDetailItem(Icons.Rounded.WbTwilight, formatTime(city.sunset), "Sunset")
                    WeatherDetailItem(
                            Icons.Outlined.Compress,
                            "${weather.main.pressure} hPa",
                            "Pressure"
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        Icon(
                icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
        )
        Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun HourlyForecastSection(forecastList: List<ForecastItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
                text = "Hourly Forecast",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )
        LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(forecastList.take(12)) { item ->
                GlassBox(modifier = Modifier.width(85.dp).height(130.dp)) {
                    Column(
                            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val time = formatHourlyTime(item.dt_txt)

                        Text(
                                text = time,
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                        )

                        val icon =
                                when {
                                    item.weather.firstOrNull()?.main?.contains("Rain", true) ==
                                            true -> Icons.Rounded.WaterDrop
                                    item.weather.firstOrNull()?.main?.contains("Cloud", true) ==
                                            true -> Icons.Rounded.Cloud
                                    else -> Icons.Rounded.WbSunny
                                }

                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                        )

                        Text(
                                text = "${item.main.temp.toInt()}°",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyForecastSection(forecastList: List<ForecastItem>) {
    val dailyList =
            remember(forecastList) { forecastList.filter { it.dt_txt.contains("12:00:00") } }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
                text = "Next 5 Days",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp, start = 4.dp)
        )

        dailyList.forEach { item ->
            GlassBox(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    val dateString = formatDailyDate(item.dt_txt)

                    Text(
                            text = dateString,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                                text = item.weather.firstOrNull()?.main ?: "",
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                                text = "${item.main.temp.toInt()}°",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedWeatherBackground(condition: String, isNight: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition()
    val colors =
            when {
                isNight -> listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                condition.contains("rain", true) || condition.contains("drizzle", true) ->
                        listOf(Color(0xFF243B55), Color(0xFF141E30))
                condition.contains("thunder", true) -> listOf(Color(0xFF232526), Color(0xFF414345))
                condition.contains("cloud", true) -> listOf(Color(0xFF3E5151), Color(0xFFDECBA4))
                condition.contains("snow", true) -> listOf(Color(0xFF24C6DC), Color(0xFF514A9D))
                else -> listOf(Color(0xFF2980B9), Color(0xFF6DD5FA))
            }
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors))) {
        if (isNight && !condition.contains("rain", true) && !condition.contains("snow", true)) {
            StarFieldAnimation(infiniteTransition)
        }
        when {
            condition.contains("clear", true) && !isNight -> SunHaloAnimation(infiniteTransition)
            condition.contains("rain", true) -> RainParallaxAnimation(infiniteTransition)
            condition.contains("snow", true) -> SnowParallaxAnimation(infiniteTransition)
            condition.contains("thunder", true) -> ThunderStormAnimation(infiniteTransition)
            condition.contains("cloud", true) -> CloudLayeredAnimation(infiniteTransition)
            else ->
                    if (!isNight) SunHaloAnimation(infiniteTransition)
                    else CloudLayeredAnimation(infiniteTransition)
        }
    }
}

@Composable
fun SunHaloAnimation(infiniteTransition: InfiniteTransition) {
    val pulse by
            infiniteTransition.animateFloat(
                    initialValue = 100f,
                    targetValue = 120f,
                    animationSpec =
                            infiniteRepeatable(
                                    tween(3000, easing = LinearEasing),
                                    RepeatMode.Reverse
                            )
            )
    val rotation by
            infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(tween(40000, easing = LinearEasing))
            )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.85f
        val centerY = size.height * 0.12f
        drawCircle(
                brush =
                        Brush.radialGradient(
                                colors =
                                        listOf(
                                                Color(0xFFFFEE58).copy(alpha = 0.4f),
                                                Color.Transparent
                                        ),
                                center = Offset(centerX, centerY),
                                radius = pulse * 1.5f
                        ),
                center = Offset(centerX, centerY),
                radius = pulse * 1.5f
        )
        rotate(rotation, pivot = Offset(centerX, centerY)) {
            for (i in 0 until 8) {
                drawCircle(
                        color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                        center =
                                Offset(
                                        centerX + (cos(i * PI / 4) * 80).toFloat(),
                                        centerY + (sin(i * PI / 4) * 80).toFloat()
                                ),
                        radius = 20f
                )
            }
        }
        drawCircle(
                color = Color(0xFFFFCA28),
                center = Offset(centerX, centerY),
                radius = 50.dp.toPx()
        )
    }
}

@Composable
fun RainParallaxAnimation(infiniteTransition: InfiniteTransition) {
    val dropCount = 150
    val randomPositions = remember { List(dropCount) { Random.nextFloat() to Random.nextFloat() } }
    val fallAnim by
            infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec =
                            infiniteRepeatable(
                                    tween(1000, easing = LinearEasing),
                                    RepeatMode.Restart
                            )
            )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        randomPositions.forEachIndexed { index, (startX, startY) ->
            val layerSpeed = if (index % 3 == 0) 1.5f else 1.0f
            val alpha = if (index % 3 == 0) 0.8f else 0.4f
            val thickness = if (index % 3 == 0) 3f else 1.5f
            val currentY = (startY * h + fallAnim * h * layerSpeed) % h
            val x = startX * w
            val slant = 10f
            drawLine(
                    color = Color.White.copy(alpha = alpha),
                    start = Offset(x, currentY),
                    end = Offset(x - slant, currentY + 30f),
                    strokeWidth = thickness,
                    cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun SnowParallaxAnimation(infiniteTransition: InfiniteTransition) {
    val snowflakeCount = 80
    val seeds = remember { List(snowflakeCount) { Random.nextFloat() to Random.nextFloat() } }
    val fallAnim by
            infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec =
                            infiniteRepeatable(
                                    tween(5000, easing = LinearEasing),
                                    RepeatMode.Restart
                            )
            )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        seeds.forEachIndexed { index, (rndX, rndY) ->
            val scale = if (index % 2 == 0) 1.5f else 0.8f
            val alpha = if (index % 2 == 0) 0.9f else 0.5f
            val currentY = (rndY * h + fallAnim * h * scale) % h
            val sway = sin((currentY / h) * 2 * PI + index).toFloat() * 20f
            val x = (rndX * w + sway) % w
            drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    center = Offset(x, currentY),
                    radius = (3f * scale)
            )
        }
    }
}

@Composable
fun CloudLayeredAnimation(infiniteTransition: InfiniteTransition) {
    val offset1 by
            infiniteTransition.animateFloat(
                    initialValue = -100f,
                    targetValue = 100f,
                    animationSpec =
                            infiniteRepeatable(
                                    tween(20000, easing = LinearEasing),
                                    RepeatMode.Reverse
                            )
            )
    val offset2 by
            infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 150f,
                    animationSpec =
                            infiniteRepeatable(
                                    tween(25000, easing = LinearEasing),
                                    RepeatMode.Reverse
                            )
            )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        drawCloudShape(this, Offset(w * 0.1f + offset1 * 0.5f, 200f), 0.3f, 0.7f)
        drawCloudShape(this, Offset(w * 0.7f - offset1 * 0.5f, 250f), 0.3f, 0.6f)
        drawCloudShape(this, Offset(w * 0.5f + offset2, 350f), 0.5f, 1.0f)
        drawCloudShape(this, Offset(w * 0.2f - offset2, 400f), 0.4f, 0.9f)
    }
}

private fun drawCloudShape(
        scope: androidx.compose.ui.graphics.drawscope.DrawScope,
        center: Offset,
        alpha: Float,
        scale: Float
) {
    with(scope) {
        val color = Color.White.copy(alpha = alpha)
        val r = 60.dp.toPx() * scale
        drawCircle(color, center = center, radius = r)
        drawCircle(
                color,
                center = Offset(center.x + r * 1.2f, center.y - r * 0.2f),
                radius = r * 0.8f
        )
        drawCircle(
                color,
                center = Offset(center.x - r * 1.1f, center.y + r * 0.1f),
                radius = r * 0.7f
        )
    }
}

@Composable
fun ThunderStormAnimation(infiniteTransition: InfiniteTransition) {
    RainParallaxAnimation(infiniteTransition)
    val flash by
            infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation =
                                            keyframes {
                                                durationMillis = 6000
                                                0f at 0
                                                0f at 3000
                                                0.6f at 3100
                                                0f at 3200
                                                0.8f at 4500
                                                0f at 4600
                                            },
                                    repeatMode = RepeatMode.Restart
                            )
            )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color.Black.copy(alpha = 0.3f - (flash * 0.3f)))
        if (flash > 0) {
            drawRect(Color.White.copy(alpha = flash * 0.4f))
        }
    }
}

@Composable
fun StarFieldAnimation(infiniteTransition: InfiniteTransition) {
    val starCount = 100
    val stars = remember { List(starCount) { Random.nextFloat() to Random.nextFloat() } }
    val Twinkle by
            infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec =
                            infiniteRepeatable(
                                    tween(2000, easing = LinearEasing),
                                    RepeatMode.Reverse
                            )
            )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        stars.forEachIndexed { i, (x, y) ->
            val specificTwinkle = if (i % 2 == 0) Twinkle else 1f - Twinkle
            val alpha = (0.3f + specificTwinkle * 0.7f).coerceIn(0f, 1f)
            drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    center = Offset(x * w, y * h * 0.6f),
                    radius = if (i % 5 == 0) 4f else 2f
            )
        }
    }
}
