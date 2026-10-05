package com.example.rockpapersic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rockpapersic.data.FlightRoute
import kotlin.math.atan2

@Composable
fun InFlightMap(
    route: FlightRoute,
    progress: Float,
    altitudeFeet: Int,
    speedKmh: Int,
    remainingDistanceKm: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val startX = width * 0.15f
            val startY = height * 0.65f

            val endX = width * 0.85f
            val endY = height * 0.35f

            val controlX = width * 0.5f
            val controlY = height * 0.10f // Quadratic curve apex for flight arc

            // 1. Draw Great Circle Flight Arc (Dashed Line)
            val arcPath = Path().apply {
                moveTo(startX, startY)
                quadraticTo(controlX, controlY, endX, endY)
            }

            drawPath(
                path = arcPath,
                color = Color(0xFF38BDF8).copy(alpha = 0.4f),
                style = Stroke(
                    width = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )

            // 2. Draw Traveled Flight Arc (Solid Glowing Blue Line)
            // Calculate point along Quadratic Bezier Curve at t = progress
            val t = progress.coerceIn(0f, 1f)
            val currentX = (1 - t) * (1 - t) * startX + 2 * (1 - t) * t * controlX + t * t * endX
            val currentY = (1 - t) * (1 - t) * startY + 2 * (1 - t) * t * controlY + t * t * endY

            // Draw Traveled portion path
            val traveledPath = Path().apply {
                moveTo(startX, startY)
                // Approximate curve segment up to t
                var step = 0f
                while (step <= t) {
                    val px = (1 - step) * (1 - step) * startX + 2 * (1 - step) * step * controlX + step * step * endX
                    val py = (1 - step) * (1 - step) * startY + 2 * (1 - step) * step * controlY + step * step * endY
                    lineTo(px, py)
                    step += 0.02f
                }
                lineTo(currentX, currentY)
            }

            drawPath(
                path = traveledPath,
                color = Color(0xFF38BDF8),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            // 3. Draw Departure & Arrival Airport Markers
            drawCircle(color = Color(0xFF10B981), radius = 10f, center = Offset(startX, startY))
            drawCircle(color = Color.White, radius = 4f, center = Offset(startX, startY))

            drawCircle(color = Color(0xFFEF4444), radius = 10f, center = Offset(endX, endY))
            drawCircle(color = Color.White, radius = 4f, center = Offset(endX, endY))

            // 4. Draw Airplane Icon Marker at Current Location
            // Calculate heading tangent angle
            val dx = 2 * (1 - t) * (controlX - startX) + 2 * t * (endX - controlX)
            val dy = 2 * (1 - t) * (controlY - startY) + 2 * t * (endY - controlY)
            val angleRad = atan2(dy, dx)

            // Draw airplane glow ring
            drawCircle(
                color = Color(0xFFF59E0B).copy(alpha = 0.3f),
                radius = 24f,
                center = Offset(currentX, currentY)
            )
            drawCircle(
                color = Color(0xFFF59E0B),
                radius = 12f,
                center = Offset(currentX, currentY)
            )
        }

        // Overlay Airport Code Labels
        Text(
            text = route.departureAirport.code,
            color = Color(0xFF10B981),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        Text(
            text = route.arrivalAirport.code,
            color = Color(0xFFEF4444),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // Telemetry readout on top
        Text(
            text = "ALT $altitudeFeet FT | SPD $speedKmh KM/H | REM $remainingDistanceKm KM",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}
