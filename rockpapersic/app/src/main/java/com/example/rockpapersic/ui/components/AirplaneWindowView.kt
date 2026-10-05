package com.example.rockpapersic.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rockpapersic.data.FlightState
import kotlin.math.sin

@Composable
fun AirplaneWindowView(
    progress: Float, // 0.0 to 1.0 flight progress
    flightState: FlightState,
    shadeOpenFraction: Float,
    onShadeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Continuous animation for clouds & engine vibration
    val infiniteTransition = rememberInfiniteTransition(label = "window_anim")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_motion"
    )

    val blinkAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strobe_blink"
    )

    val vibrationOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vibration"
    )

    Box(
        modifier = modifier
            .background(Color(0xFF1E222A)) // Cabin interior wall color
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    val newFraction = (shadeOpenFraction - (dragAmount / 400f)).coerceIn(0f, 1f)
                    onShadeChange(newFraction)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // 1. Calculate Window Frame oval bounds
            val windowMarginX = canvasWidth * 0.12f
            val windowMarginY = canvasHeight * 0.08f
            val windowRect = Rect(
                left = windowMarginX,
                top = windowMarginY,
                right = canvasWidth - windowMarginX,
                bottom = canvasHeight - windowMarginY
            )

            // 2. Define Sky Colors based on flight progress (Day -> Sunset -> Starry Night -> Day)
            val skyTopColor = when {
                progress < 0.25f -> Color(0xFF104E8B) // Morning Deep Blue
                progress < 0.50f -> Color(0xFF0C2040) // High Altitude Cruising Dark Blue
                progress < 0.75f -> Color(0xFF2C1654) // Sunset Purple
                else -> Color(0xFF071026) // Night Sky
            }

            val skyBottomColor = when {
                progress < 0.25f -> Color(0xFF87CEFA) // Light Horizon Blue
                progress < 0.50f -> Color(0xFF4A90E2) // Midday Horizon
                progress < 0.75f -> Color(0xFFFF7F50) // Golden Sunset Horizon
                else -> Color(0xFF1A2B4C) // Night Horizon
            }

            // Draw Sky Gradient inside window area
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(skyTopColor, skyBottomColor),
                    startY = windowRect.top,
                    endY = windowRect.bottom
                )
            )

            // 3. Draw Stars if Night or Sunset
            if (progress > 0.60f) {
                val numStars = 25
                for (i in 0 until numStars) {
                    val starX = windowRect.left + ((i * 137.5f) % (windowRect.width))
                    val starY = windowRect.top + ((i * 73.1f) % (windowRect.height * 0.5f))
                    val starAlpha = 0.4f + 0.6f * sin(i * 1.5 + blinkAnim * 3.14).toFloat()
                    drawCircle(
                        color = Color.White.copy(alpha = starAlpha.coerceIn(0.1f, 0.9f)),
                        radius = (1f + (i % 3)),
                        center = Offset(starX, starY)
                    )
                }
            }

            // 4. Draw Horizon & Ground Terrain beneath clouds
            val horizonY = windowRect.top + (windowRect.height * (0.60f + 0.05f * sin(progress * 3.14f)))
            val groundColor = if (progress > 0.75f) Color(0xFF0A121E) else Color(0xFF2E5A3C) // Dark ocean/land

            val groundPath = Path().apply {
                moveTo(windowRect.left, horizonY)
                lineTo(windowRect.right, horizonY)
                lineTo(windowRect.right, windowRect.bottom)
                lineTo(windowRect.left, windowRect.bottom)
                close()
            }
            drawPath(groundPath, color = groundColor)

            // 5. Draw Parallax Floating Clouds
            val cloudColor = if (progress > 0.70f) Color(0x77FFB380) else Color(0xCCFFFFFF)
            val numCloudClusters = 4
            for (c in 0 until numCloudClusters) {
                val cloudSpeed = (c + 1) * 0.8f
                val cloudXBase = (cloudOffset * cloudSpeed + c * 250) % (windowRect.width + 300)
                val cloudX = windowRect.right + 150 - cloudXBase
                val cloudY = horizonY - 40 + (c * 25) + (vibrationOffset * 0.5f)

                if (cloudX > windowRect.left - 100 && cloudX < windowRect.right + 100) {
                    drawCircle(cloudColor, radius = 35f, center = Offset(cloudX, cloudY))
                    drawCircle(cloudColor, radius = 25f, center = Offset(cloudX - 30, cloudY + 5))
                    drawCircle(cloudColor, radius = 30f, center = Offset(cloudX + 25, cloudY + 8))
                    drawCircle(cloudColor, radius = 20f, center = Offset(cloudX + 45, cloudY + 12))
                }
            }

            // 6. Draw Airplane Wing (Right wing view in passenger perspective)
            val wingPath = Path().apply {
                val wingStartX = windowRect.left + (windowRect.width * 0.25f)
                val wingStartY = windowRect.bottom - (windowRect.height * 0.05f) + (vibrationOffset * 0.8f)

                moveTo(wingStartX, wingStartY)
                // Wing edge leading to wingtip
                val wingTipX = windowRect.right - (windowRect.width * 0.08f)
                val wingTipY = horizonY - (windowRect.height * 0.12f) + (vibrationOffset * 1.2f)

                lineTo(wingTipX, wingTipY)
                // Winglet vertical tip
                lineTo(wingTipX + 15f, wingTipY - 45f)
                lineTo(wingTipX + 25f, wingTipY - 40f)
                lineTo(wingTipX + 18f, wingTipY + 10f)
                // Wing trailing edge back
                lineTo(windowRect.right, windowRect.bottom)
                close()
            }

            // Wing Metallic Gradient Paint
            drawPath(
                path = wingPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF64748B)),
                    start = Offset(windowRect.left, windowRect.bottom),
                    end = Offset(windowRect.right, windowRect.top)
                )
            )

            // Wing Red/Green Navigation Strobe LED Light on Wingtip
            val wingTipPos = Offset(
                x = windowRect.right - (windowRect.width * 0.08f) + 20f,
                y = horizonY - (windowRect.height * 0.12f) - 40f + (vibrationOffset * 1.2f)
            )
            val strobeAlpha = if (blinkAnim > 0.5f) 1.0f else 0.1f
            drawCircle(
                color = Color.Red.copy(alpha = strobeAlpha),
                radius = 10f,
                center = wingTipPos
            )
            drawCircle(
                color = Color.White.copy(alpha = strobeAlpha * 0.8f),
                radius = 4f,
                center = wingTipPos
            )

            // 7. Draw Window Frame Mask (Cutting out oval window from fuselage wall)
            val framePath = Path().apply {
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
            }
            val holePath = Path().apply {
                addOval(windowRect)
            }
            val maskedWallPath = Path.combine(
                operation = PathOperation.Difference,
                path1 = framePath,
                path2 = holePath
            )

            // Fuselage Wall Color & Texture
            drawPath(
                path = maskedWallPath,
                color = Color(0xFF1B1F27)
            )

            // 8. Draw Inner Bevel & Glass Reflection Ring
            drawOval(
                path = holePath,
                color = Color(0xFF333A48),
                style = Stroke(width = 24f)
            )
            drawOval(
                path = holePath,
                color = Color(0xFF525D73),
                style = Stroke(width = 10f)
            )
            drawOval(
                path = holePath,
                color = Color.White.copy(alpha = 0.25f),
                style = Stroke(width = 3f)
            )

            // Glass Specular Reflection Highlight Diagonal Arc
            val glassGlintPath = Path().apply {
                addArc(
                    rect = windowRect,
                    startAngleDegrees = 200f,
                    sweepAngleDegrees = 70f
                )
            }
            drawPath(
                path = glassGlintPath,
                color = Color.White.copy(alpha = 0.35f),
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )

            // 9. Draw Window Shade / Shutter (Pull-down curtain)
            if (shadeOpenFraction < 1.0f) {
                val shadeHeight = windowRect.height * (1.0f - shadeOpenFraction)
                val shadeRect = Rect(
                    left = windowRect.left - 10,
                    top = windowRect.top - 10,
                    right = windowRect.right + 10,
                    bottom = windowRect.top + shadeHeight
                )

                // Clip Shade to Oval window
                val shadePath = Path().apply { addRect(shadeRect) }
                val clippedShadePath = Path.combine(
                    operation = PathOperation.Intersect,
                    path1 = shadePath,
                    path2 = holePath
                )

                drawPath(
                    path = clippedShadePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2C3240), Color(0xFF1A1E27)),
                        startY = shadeRect.top,
                        endY = shadeRect.bottom
                    )
                )

                // Shade Handle Bar
                val handleY = windowRect.top + shadeHeight - 12f
                if (handleY in windowRect.top..windowRect.bottom) {
                    drawRoundRect(
                        color = Color(0xFF8895AE),
                        topLeft = Offset(windowRect.center.x - 40f, handleY),
                        size = Size(80f, 16f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                }
            }
        }

        // Overlay drag hint if shade is fully closed
        if (shadeOpenFraction < 0.15f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👇 上下滑動拉開窗簾",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
