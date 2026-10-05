package com.example.rockpapersic.data

import java.util.UUID

enum class FlightCategory(val labelZh: String, val icon: String) {
    DOMESTIC("國內快飛", "🛫"),
    SHORT_HAUL("短程亞洲", "✈️"),
    MEDIUM_HAUL("中程跨國", "🌐"),
    LONG_HAUL("長程洲際", "🌍"),
    CUSTOM("自訂專注", "⏱️")
}

enum class CabinClass(val labelZh: String, val code: String, val multiplier: Float) {
    ECONOMY("經濟客艙", "Y", 1.0f),
    BUSINESS("商務客艙", "C", 1.5f),
    FIRST("頭等客艙", "F", 2.0f)
}

data class Airport(
    val code: String,
    val nameZh: String,
    val nameEn: String,
    val cityZh: String,
    val country: String,
    val countryFlag: String
)

data class FlightRoute(
    val id: String,
    val flightNumber: String,
    val airlineZh: String,
    val airlineEn: String,
    val aircraft: String,
    val departureAirport: Airport,
    val arrivalAirport: Airport,
    val durationMinutes: Int,
    val distanceKm: Int,
    val category: FlightCategory,
    val gate: String,
    val isPopular: Boolean = false
)

enum class SpeedMultiplier(val factor: Int, val label: String) {
    REALTIME(1, "1x (現實時長)"),
    FAST_5X(5, "5x (快速專注)"),
    POMODORO_10X(10, "10x (番茄鐘)"),
    TURBO_30X(30, "30x (極速航行)"),
    EXPRESS_60X(60, "60x (超光速)")
}

enum class FlightState {
    TERMINAL,    // 航廈選擇班機中
    BOARDING,    // 登機廣播與確認
    TAKEOFF,     // 起飛階段 (爬升)
    CRUISING,    // 巡航專注中
    DESCENT,     // 開始降落階段
    ARRIVED      // 順利抵達目的地
}

data class FlightLog(
    val id: String = UUID.randomUUID().toString(),
    val flightNumber: String,
    val routeTitle: String,
    val departureCode: String,
    val arrivalCode: String,
    val arrivalCityZh: String,
    val arrivalCountryFlag: String,
    val durationCompletedMinutes: Int,
    val focusMinutesTotal: Int,
    val distanceFlownKm: Int,
    val cabinClass: CabinClass,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class PassportStamp(
    val airportCode: String,
    val cityNameZh: String,
    val countryFlag: String,
    val unlockedTimeMillis: Long,
    val totalVisits: Int
)
