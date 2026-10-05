package com.example.rockpapersic.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object FlightRepository {

    // Preset Airports
    val TPE = Airport("TPE", "臺灣桃園國際機場", "Taiwan Taoyuan Int'l", "台北 / 桃園", "台灣", "🇹🇼")
    val TSA = Airport("TSA", "臺北松山機場", "Taipei Songshan Airport", "台北松山", "台灣", "🇹🇼")
    val HUN = Airport("HUN", "花蓮機場", "Hualien Airport", "花蓮", "台灣", "🇹🇼")
    val KHH = Airport("KHH", "高雄小港國際機場", "Kaohsiung Int'l", "高雄", "台灣", "🇹🇼")
    val NRT = Airport("NRT", "東京成田國際機場", "Narita Int'l Airport", "東京", "日本", "🇯🇵")
    val HND = Airport("HND", "東京羽田國際機場", "Tokyo Haneda Airport", "東京", "日本", "🇯🇵")
    val KIX = Airport("KIX", "關西國際機場", "Kansai Int'l Airport", "大阪", "日本", "🇯🇵")
    val HKG = Airport("HKG", "香港國際機場", "Hong Kong Int'l", "香港", "香港", "🇭🇰")
    val BKK = Airport("BKK", "曼谷素萬那普機場", "Suvarnabhumi Airport", "曼谷", "泰國", "🇹🇭")
    val SIN = Airport("SIN", "新加坡樟宜機場", "Singapore Changi", "新加坡", "新加坡", "🇸🇬")
    val ICN = Airport("ICN", "仁川國際機場", "Incheon Int'l Airport", "首爾", "韓國", "🇰🇷")
    val LAX = Airport("LAX", "洛杉磯國際機場", "Los Angeles Int'l", "洛杉磯", "美國", "🇺🇸")
    val CDG = Airport("CDG", "巴黎戴高樂機場", "Paris Charles de Gaulle", "巴黎", "法國", "🇫🇷")
    val HNL = Airport("HNL", "檀香山丹尼爾·井上機場", "Honolulu Airport", "夏威夷", "美國", "🇺🇸")

    val PRESET_ROUTES = listOf(
        FlightRoute(
            id = "route_tsa_hun",
            flightNumber = "B7 8901",
            airlineZh = "立榮航空",
            airlineEn = "UNI Air",
            aircraft = "ATR 72-600",
            departureAirport = TSA,
            arrivalAirport = HUN,
            durationMinutes = 35,
            distanceKm = 120,
            category = FlightCategory.DOMESTIC,
            gate = "Gate 3",
            isPopular = true
        ),
        FlightRoute(
            id = "route_tpe_hkg",
            flightNumber = "CI 909",
            airlineZh = "中華航空",
            airlineEn = "China Airlines",
            aircraft = "A330-300",
            departureAirport = TPE,
            arrivalAirport = HKG,
            durationMinutes = 105, // 1h 45m
            distanceKm = 800,
            category = FlightCategory.SHORT_HAUL,
            gate = "Gate A9",
            isPopular = true
        ),
        FlightRoute(
            id = "route_tpe_kix",
            flightNumber = "JX 820",
            airlineZh = "星宇航空",
            airlineEn = "STARLUX Airlines",
            aircraft = "A350-900",
            departureAirport = TPE,
            arrivalAirport = KIX,
            durationMinutes = 150, // 2h 30m
            distanceKm = 1700,
            category = FlightCategory.SHORT_HAUL,
            gate = "Gate B6",
            isPopular = true
        ),
        FlightRoute(
            id = "route_tsa_hnd",
            flightNumber = "JL 098",
            airlineZh = "日本航空",
            airlineEn = "Japan Airlines",
            aircraft = "B787-9 Dreamliner",
            departureAirport = TSA,
            arrivalAirport = HND,
            durationMinutes = 180, // 3h 00m
            distanceKm = 2100,
            category = FlightCategory.SHORT_HAUL,
            gate = "Gate 5",
            isPopular = true
        ),
        FlightRoute(
            id = "route_tpe_nrt",
            flightNumber = "BR 198",
            airlineZh = "長榮航空",
            airlineEn = "EVA Air",
            aircraft = "B787-10",
            departureAirport = TPE,
            arrivalAirport = NRT,
            durationMinutes = 195, // 3h 15m
            distanceKm = 2180,
            category = FlightCategory.SHORT_HAUL,
            gate = "Gate C5",
            isPopular = true
        ),
        FlightRoute(
            id = "route_tpe_bkk",
            flightNumber = "TG 633",
            airlineZh = "泰國國際航空",
            airlineEn = "Thai Airways",
            aircraft = "A350-900",
            departureAirport = TPE,
            arrivalAirport = BKK,
            durationMinutes = 230, // 3h 50m
            distanceKm = 2500,
            category = FlightCategory.MEDIUM_HAUL,
            gate = "Gate B4"
        ),
        FlightRoute(
            id = "route_tpe_sin",
            flightNumber = "SQ 877",
            airlineZh = "新加坡航空",
            airlineEn = "Singapore Airlines",
            aircraft = "B787-10",
            departureAirport = TPE,
            arrivalAirport = SIN,
            durationMinutes = 275, // 4h 35m
            distanceKm = 3200,
            category = FlightCategory.MEDIUM_HAUL,
            gate = "Gate D2"
        ),
        FlightRoute(
            id = "route_tpe_icn",
            flightNumber = "OZ 712",
            airlineZh = "韓亞航空",
            airlineEn = "Asiana Airlines",
            aircraft = "A330-300",
            departureAirport = TPE,
            arrivalAirport = ICN,
            durationMinutes = 160, // 2h 40m
            distanceKm = 1460,
            category = FlightCategory.SHORT_HAUL,
            gate = "Gate A7"
        ),
        FlightRoute(
            id = "route_tpe_hnl",
            flightNumber = "HA 808",
            airlineZh = "夏威夷航空",
            airlineEn = "Hawaiian Airlines",
            aircraft = "A330-200",
            departureAirport = TPE,
            arrivalAirport = HNL,
            durationMinutes = 560, // 9h 20m
            distanceKm = 8150,
            category = FlightCategory.LONG_HAUL,
            gate = "Gate C1"
        ),
        FlightRoute(
            id = "route_tpe_lax",
            flightNumber = "JX 002",
            airlineZh = "星宇航空",
            airlineEn = "STARLUX Airlines",
            aircraft = "A350-900",
            departureAirport = TPE,
            arrivalAirport = LAX,
            durationMinutes = 690, // 11h 30m
            distanceKm = 10900,
            category = FlightCategory.LONG_HAUL,
            gate = "Gate C8",
            isPopular = true
        ),
        FlightRoute(
            id = "route_tpe_cdg",
            flightNumber = "BR 087",
            airlineZh = "長榮航空",
            airlineEn = "EVA Air",
            aircraft = "B777-300ER",
            departureAirport = TPE,
            arrivalAirport = CDG,
            durationMinutes = 825, // 13h 45m
            distanceKm = 9800,
            category = FlightCategory.LONG_HAUL,
            gate = "Gate C3"
        )
    )

    fun createCustomRoute(minutes: Int, customName: String = "個人專注速飛"): FlightRoute {
        return FlightRoute(
            id = "custom_${System.currentTimeMillis()}",
            flightNumber = "FOCUS ${minutes}M",
            airlineZh = "極速專注號",
            airlineEn = "Focus Express",
            aircraft = "SkyJet X",
            departureAirport = TPE,
            arrivalAirport = Airport("NOW", "目標專注站", "Focus Destination", customName, "個人願景", "🎯"),
            durationMinutes = minutes,
            distanceKm = minutes * 15,
            category = FlightCategory.CUSTOM,
            gate = "Gate F1"
        )
    }

    private const val PREFS_NAME = "flight_timer_prefs"
    private const val KEY_FLIGHT_LOGS = "flight_logs_json"

    fun saveFlightLog(context: Context, log: FlightLog) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val logs = loadFlightLogs(context).toMutableList()
        logs.add(0, log) // Newest first

        val array = JSONArray()
        for (item in logs) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("flightNumber", item.flightNumber)
                put("routeTitle", item.routeTitle)
                put("departureCode", item.departureCode)
                put("arrivalCode", item.arrivalCode)
                put("arrivalCityZh", item.arrivalCityZh)
                put("arrivalCountryFlag", item.arrivalCountryFlag)
                put("durationCompletedMinutes", item.durationCompletedMinutes)
                put("focusMinutesTotal", item.focusMinutesTotal)
                put("distanceFlownKm", item.distanceFlownKm)
                put("cabinClass", item.cabinClass.name)
                put("timestampMillis", item.timestampMillis)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_FLIGHT_LOGS, array.toString()).apply()
    }

    fun loadFlightLogs(context: Context): List<FlightLog> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_FLIGHT_LOGS, null) ?: return emptyList()
        val result = mutableListOf<FlightLog>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(
                    FlightLog(
                        id = obj.optString("id"),
                        flightNumber = obj.optString("flightNumber"),
                        routeTitle = obj.optString("routeTitle"),
                        departureCode = obj.optString("departureCode"),
                        arrivalCode = obj.optString("arrivalCode"),
                        arrivalCityZh = obj.optString("arrivalCityZh"),
                        arrivalCountryFlag = obj.optString("arrivalCountryFlag"),
                        durationCompletedMinutes = obj.optInt("durationCompletedMinutes"),
                        focusMinutesTotal = obj.optInt("focusMinutesTotal"),
                        distanceFlownKm = obj.optInt("distanceFlownKm"),
                        cabinClass = try { CabinClass.valueOf(obj.optString("cabinClass")) } catch (e: Exception) { CabinClass.ECONOMY },
                        timestampMillis = obj.optLong("timestampMillis")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun getPassportStamps(logs: List<FlightLog>): List<PassportStamp> {
        val map = mutableMapOf<String, PassportStamp>()
        for (log in logs) {
            val existing = map[log.arrivalCode]
            if (existing != null) {
                map[log.arrivalCode] = existing.copy(
                    totalVisits = existing.totalVisits + 1,
                    unlockedTimeMillis = maxOf(existing.unlockedTimeMillis, log.timestampMillis)
                )
            } else {
                map[log.arrivalCode] = PassportStamp(
                    airportCode = log.arrivalCode,
                    cityNameZh = log.arrivalCityZh,
                    countryFlag = log.arrivalCountryFlag,
                    unlockedTimeMillis = log.timestampMillis,
                    totalVisits = 1
                )
            }
        }
        return map.values.sortedByDescending { it.unlockedTimeMillis }
    }
}
