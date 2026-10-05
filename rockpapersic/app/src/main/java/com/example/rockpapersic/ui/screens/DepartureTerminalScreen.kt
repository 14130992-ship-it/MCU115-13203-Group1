package com.example.rockpapersic.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rockpapersic.data.CabinClass
import com.example.rockpapersic.data.FlightCategory
import com.example.rockpapersic.data.FlightRepository
import com.example.rockpapersic.data.FlightRoute
import com.example.rockpapersic.data.SpeedMultiplier
import com.example.rockpapersic.ui.components.BoardingPassCard
import com.example.rockpapersic.viewmodel.FlightUiState

@Composable
fun DepartureTerminalScreen(
    uiState: FlightUiState,
    onSelectRoute: (FlightRoute) -> Unit,
    onSelectSpeed: (SpeedMultiplier) -> Unit,
    onSelectCabin: (CabinClass) -> Unit,
    onStartBoarding: () -> Unit,
    onOpenPassport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<FlightCategory?>(null) }
    var showCustomRouteDialog by remember { mutableStateOf(false) }
    var showBoardingPassModal by remember { mutableStateOf(false) }

    val filteredRoutes = remember(selectedCategoryFilter) {
        if (selectedCategoryFilter == null) {
            FlightRepository.PRESET_ROUTES
        } else {
            FlightRepository.PRESET_ROUTES.filter { it.category == selectedCategoryFilter }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Dark Airport FIDS Board Theme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Airport FIDS Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🛫", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "國際航班離站告示板",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF8FAFC)
                        )
                    }
                    Text(
                        text = "DEPARTURES • TAIPEI INT'L TERMINAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Passport / Logbook Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { onOpenPassport() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🛂", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "專注護照 (${uiState.passportStamps.size})",
                            color = Color(0xFFF1F5F9),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Flight Category Filter Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("全部航班 (${FlightRepository.PRESET_ROUTES.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }

                items(FlightCategory.values().filter { it != FlightCategory.CUSTOM }) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text("${cat.icon} ${cat.labelZh}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = false,
                        onClick = { showCustomRouteDialog = true },
                        label = { Text("⏱️ 自訂航線時間") },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF0284C7),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Flight Speed & Cabin Options Bar
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ 飛行時間轉換速率：",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "預計專注：${uiState.totalTargetSeconds / 60} 分鐘",
                            color = Color(0xFFF59E0B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(SpeedMultiplier.values()) { speed ->
                            val isSelected = uiState.speedMultiplier == speed
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF2563EB) else Color(0xFF0F172A))
                                    .clickable { onSelectSpeed(speed) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = speed.label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cabin Class Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💺 艙等選擇：",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            CabinClass.values().forEach { cabin ->
                                val isSelected = uiState.cabinClass == cabin
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Color(0xFFD97706) else Color(0xFF0F172A))
                                        .clickable { onSelectCabin(cabin) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = cabin.labelZh,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Flight List Board
            Text(
                text = "可搭乘航班列表 (點擊班機登機)：",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredRoutes) { route ->
                    val isSelected = uiState.selectedRoute.id == route.id
                    FlightBoardRow(
                        route = route,
                        isSelected = isSelected,
                        onClick = {
                            onSelectRoute(route)
                            showBoardingPassModal = true
                        }
                    )
                }
            }
        }

        // Custom Route Dialog
        if (showCustomRouteDialog) {
            CustomRouteDialog(
                onDismiss = { showCustomRouteDialog = false },
                onConfirm = { minutes, title ->
                    showCustomRouteDialog = false
                    val customRoute = FlightRepository.createCustomRoute(minutes, title)
                    onSelectRoute(customRoute)
                    showBoardingPassModal = true
                }
            )
        }

        // Boarding Pass Modal Overlay
        if (showBoardingPassModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable { showBoardingPassModal = false }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.clickable(enabled = false) {}) {
                    BoardingPassCard(
                        route = uiState.selectedRoute,
                        speedMultiplier = uiState.speedMultiplier,
                        cabinClass = uiState.cabinClass,
                        onBoardClick = {
                            showBoardingPassModal = false
                            onStartBoarding()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FlightBoardRow(
    route: FlightRoute,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    Color(0xFF38BDF8),
                    RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF161E2E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = route.flightNumber,
                        color = Color(0xFFF59E0B),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = route.airlineZh,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${route.departureAirport.code} ${route.departureAirport.cityZh} ➔ ${route.arrivalAirport.countryFlag} ${route.arrivalAirport.cityZh} (${route.arrivalAirport.code})",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF065F46))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "BOARDING 登機中",
                        color = Color(0xFF34D399),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${route.durationMinutes} 分鐘 (${route.distanceKm}km)",
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CustomRouteDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var minutesText by remember { mutableStateOf("25") }
    var titleText by remember { mutableStateOf("專注任務") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "⏱️ 設定自訂專注航線",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column {
                Text(text = "請輸入您預計進行專注學習或工作的時間（分鐘）：", fontSize = 13.sp, color = Color(0xFF475569))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it.filter { char -> char.isDigit() } },
                    label = { Text("專注時間 (分鐘)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("航向目的地 / 任務名稱") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val mins = minutesText.toIntOrNull() ?: 25
                    onConfirm(mins.coerceIn(1, 1440), titleText.ifBlank { "個人專注速飛" })
                }
            ) {
                Text("確定開航")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
