package com.example.rockpapersic.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rockpapersic.data.FlightState
import com.example.rockpapersic.ui.components.AirplaneWindowView
import com.example.rockpapersic.ui.components.InFlightMap
import com.example.rockpapersic.viewmodel.FlightUiState

@Composable
fun InFlightScreen(
    uiState: FlightUiState,
    onStartFlight: () -> Unit,
    onTogglePause: () -> Unit,
    onCallServiceBell: () -> Unit,
    onToggleVoice: () -> Unit,
    onToggleEngineNoise: () -> Unit,
    onShadeChange: (Float) -> Unit,
    onReturnToTerminal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val route = uiState.selectedRoute

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Flight Top Status Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${route.airlineZh} ${route.flightNumber}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            SeatbeltIndicator(isOn = uiState.seatbeltSignOn)
                        }
                        Text(
                            text = "${route.departureAirport.code} ➔ ${route.arrivalAirport.code} (${route.arrivalAirport.cityZh})",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    // Return Button
                    OutlinedButton(
                        onClick = onReturnToTerminal,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                    ) {
                        Text("離機", fontSize = 11.sp)
                    }
                }
            }

            // Window Perspective View (Upper Half)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
            ) {
                AirplaneWindowView(
                    progress = uiState.progress,
                    flightState = uiState.flightState,
                    shadeOpenFraction = uiState.windowShadeOpen,
                    onShadeChange = onShadeChange,
                    modifier = Modifier.fillMaxSize()
                )

                // Boarding Confirm Overlay if in Boarding State
                if (uiState.flightState == FlightState.BOARDING) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🎙️ 客艙廣播：", color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "「歡迎登機！本班機前往 ${route.arrivalAirport.cityZh}，預計專注時間 ${uiState.totalTargetSeconds / 60} 分鐘。請繫好安全帶，準備起飛！」",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onStartFlight,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(50.dp)
                            ) {
                                Text("🚀 起飛專注！", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Flight Arrived Celebration Overlay
                if (uiState.flightState == FlightState.ARRIVED) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.80f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎉🛬", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "順利降落 ${route.arrivalAirport.nameZh}！",
                                color = Color(0xFF34D399),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "獲得 ${route.arrivalAirport.countryFlag} ${route.arrivalAirport.cityZh} 入境章戳！",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onReturnToTerminal,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(48.dp)
                            ) {
                                Text("返回航廈・進入下一趟旅程", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Seatback IFE Entertainment Dashboard (Lower Half)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
                    .background(Color(0xFF020617))
                    .padding(14.dp)
            ) {
                // Countdown Timer Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = getFlightStateLabel(uiState.flightState),
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = formatTimerText(uiState.remainingSeconds),
                            color = if (uiState.isPaused) Color(0xFFF59E0B) else Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // In-Flight Service Controls Row
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Voice Announcement Toggle
                        AudioControlButton(
                            label = if (uiState.isVoiceEnabled) "🔊 廣播" else "🔇 靜音",
                            isActive = uiState.isVoiceEnabled,
                            onClick = onToggleVoice
                        )

                        // Engine White Noise Toggle
                        AudioControlButton(
                            label = if (uiState.isEngineNoiseEnabled) "🎧 引擎聲" else "🎧 白噪音",
                            isActive = uiState.isEngineNoiseEnabled,
                            onClick = onToggleEngineNoise
                        )

                        // Service Bell
                        AudioControlButton(
                            label = "🛎️ 客艙服務",
                            isActive = true,
                            onClick = onCallServiceBell
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { uiState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF38BDF8),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Flight Path Map
                InFlightMap(
                    route = route,
                    progress = uiState.progress,
                    altitudeFeet = uiState.altitudeFeet,
                    speedKmh = uiState.speedKmh,
                    remainingDistanceKm = uiState.remainingDistanceKm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pause / Resume Control Bar
                if (uiState.flightState != FlightState.BOARDING && uiState.flightState != FlightState.ARRIVED) {
                    Button(
                        onClick = onTogglePause,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isPaused) Color(0xFF10B981) else Color(0xFFD97706)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (uiState.isPaused) "▶️ 繼續專注航航" else "⏸️ 暫停專注",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeatbeltIndicator(isOn: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isOn) Color(0xFFEF4444) else Color(0xFF1E293B))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = if (isOn) "🔴 繫緊安全帶" else "🟢 安全帶燈熄滅",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AudioControlButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) Color(0xFF1E293B) else Color(0xFF020617))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isActive) Color(0xFF38BDF8) else Color(0xFF64748B),
            fontWeight = FontWeight.Bold
        )
    }
}

private fun getFlightStateLabel(state: FlightState): String {
    return when (state) {
        FlightState.TERMINAL -> "離站中"
        FlightState.BOARDING -> "登機廣播中"
        FlightState.TAKEOFF -> "🛫 爬升階段 (TAKEOFF)"
        FlightState.CRUISING -> "✈️ 巡航階段 (CRUISING)"
        FlightState.DESCENT -> "🛬 降落準備 (DESCENT)"
        FlightState.ARRIVED -> "🎯 已順利抵達"
    }
}

private fun formatTimerText(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format("%02d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}
