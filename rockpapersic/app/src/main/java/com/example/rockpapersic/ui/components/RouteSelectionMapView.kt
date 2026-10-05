package com.example.rockpapersic.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rockpapersic.data.Airport
import com.example.rockpapersic.data.FlightRoute
import kotlin.math.sin

@Composable
fun RouteSelectionMapView(
    selectedRoute: FlightRoute,
    allRoutes: List<FlightRoute>,
    onSelectRoute: (FlightRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "map_anim")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    val planeTravelAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "plane_motion"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF090D16))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw Minimalist Lat/Lon Grid Lines
            val gridColor = Color(0xFF1E293B).copy(alpha = 0.5f)
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

            for (x in 0..10) {
                val posX = width * (x / 10f)
                drawLine(
                    color = gridColor,
                    start = Offset(posX, 0f),
                    end = Offset(posX, height),
                    strokeWidth = 1f,
                    pathEffect = dashEffect
                )
            }
            for (y in 0..6) {
                val posY = height * (y / 6f)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, posY),
                    end = Offset(width, posY),
                    strokeWidth = 1f,
                    pathEffect = dashEffect
                )
            }

            // 2. Map Geo Coordinates to Screen Space
            // Focus center on Asia-Pacific / World view mapping
            val dep = selectedRoute.departureAirport
            val arr = selectedRoute.arrivalAirport

            fun mapGeoToScreen(airport: Airport): Offset {
                // Map Lat/Lon to canvas x, y based on route bounds
                val minLat = minOf(dep.latitude, arr.latitude) - 8.0
                val maxLat = maxOf(dep.latitude, arr.latitude) + 8.0
                val minLon = minOf(dep.longitude, arr.longitude) - 15.0
                val maxLon = maxOf(dep.longitude, arr.longitude) + 15.0

                val normX = ((airport.longitude - minLon) / (maxLon - minLon)).toFloat().coerceIn(0.1f, 0.9f)
                val normY = (1.0f - ((airport.latitude - minLat) / (maxLat - minLat))).toFloat().coerceIn(0.15f, 0.85f)

                return Offset(width * normX, height * normY)
            }

            val depPos = mapGeoToScreen(dep)
            val arrPos = mapGeoToScreen(arr)

            // Calculate Arc Curve Control Point
            val midX = (depPos.x + arrPos.x) / 2f
            val midY = (depPos.y + arrPos.y) / 2f
            val arcOffset = -80f
            val controlX = midX
            val controlY = midY + arcOffset

            // 3. Draw Unselected Route Arc Background Lines
            for (route in allRoutes) {
                if (route.id != selectedRoute.id) {
                    val p1 = mapGeoToScreen(route.departureAirport)
                    val p2 = mapGeoToScreen(route.arrivalAirport)
                    val cX = (p1.x + p2.x) / 2f
                    val cY = ((p1.y + p2.y) / 2f) - 40f

                    val unselectedPath = Path().apply {
                        moveTo(p1.x, p1.y)
                        quadraticTo(cX, cY, p2.x, p2.y)
                    }
                    drawPath(
                        path = unselectedPath,
                        color = Color(0xFF334155).copy(alpha = 0.35f),
                        style = Stroke(width = 2f)
                    )
                }
            }

            // 4. Draw Selected Glowing Route Arc
            val selectedPath = Path().apply {
                moveTo(depPos.x, depPos.y)
                quadraticTo(controlX, controlY, arrPos.x, arrPos.y)
            }

            // Outer Arc Glow
            drawPath(
                path = selectedPath,
                color = Color(0xFF0284C7).copy(alpha = 0.4f),
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )

            // Inner Arc Solid Line
            drawPath(
                path = selectedPath,
                color = Color(0xFF38BDF8),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )

            // 5. Draw Animated Moving Airplane along Selected Route Arc
            val t = planeTravelAnim
            val planeX = (1 - t) * (1 - t) * depPos.x + 2 * (1 - t) * t * controlX + t * t * arrPos.x
            val planeY = (1 - t) * (1 - t) * depPos.y + 2 * (1 - t) * t * controlY + t * t * arrPos.y

            drawCircle(
                color = Color(0xFFF59E0B).copy(alpha = 0.35f),
                radius = 20f,
                center = Offset(planeX, planeY)
            )
            drawCircle(
                color = Color(0xFFF59E0B),
                radius = 8f,
                center = Offset(planeX, planeY)
            )

            // 6. Draw Airport Pulse Nodes
            val depPulseRadius = 12f + (20f * pulseAnim)
            val depPulseAlpha = (1.0f - pulseAnim).coerceIn(0f, 1f)
            drawCircle(
                color = Color(0xFF10B981).copy(alpha = depPulseAlpha),
                radius = depPulseRadius,
                center = depPos
            )
            drawCircle(color = Color(0xFF10B981), radius = 10f, center = depPos)
            drawCircle(color = Color.White, radius = 4f, center = depPos)

            val arrPulseRadius = 12f + (20f * pulseAnim)
            val arrPulseAlpha = (1.0f - pulseAnim).coerceIn(0f, 1f)
            drawCircle(
                color = Color(0xFFF43F5E).copy(alpha = arrPulseAlpha),
                radius = arrPulseRadius,
                center = arrPos
            )
            drawCircle(color = Color(0xFFF43F5E), radius = 10f, center = arrPos)
            drawCircle(color = Color.White, radius = 4f, center = arrPos)
        }

        // Overlay Map Header Info & Selected Route Card Badge
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Map Top Badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🗺️ 地圖航線圖 (LIVE FLIGHT MAP)", fontSize = 11.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }

            // Route Quick Switch Card at bottom of map
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.90f))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${selectedRoute.departureAirport.countryFlag} ${selectedRoute.departureAirport.code}", color = Color(0xFF10B981), fontWeight = FontWeight.Black, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                        Text(text = " ✈️ ", color = Color.White, fontSize = 14.sp)
                        Text(text = "${selectedRoute.arrivalAirport.countryFlag} ${selectedRoute.arrivalAirport.code}", color = Color(0xFFF43F5E), fontWeight = FontWeight.Black, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "${selectedRoute.airlineZh} ${selectedRoute.flightNumber}", color = Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${selectedRoute.durationMinutes} 分鐘 • ${selectedRoute.distanceKm} km", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
