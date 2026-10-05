package com.example.rockpapersic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rockpapersic.audio.CabinAudioEngine
import com.example.rockpapersic.data.CabinClass
import com.example.rockpapersic.data.FlightLog
import com.example.rockpapersic.data.FlightRepository
import com.example.rockpapersic.data.FlightRoute
import com.example.rockpapersic.data.FlightState
import com.example.rockpapersic.data.PassportStamp
import com.example.rockpapersic.data.SpeedMultiplier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FlightUiState(
    val selectedRoute: FlightRoute = FlightRepository.PRESET_ROUTES[2], // TPE -> KIX default
    val speedMultiplier: SpeedMultiplier = SpeedMultiplier.FAST_5X,
    val cabinClass: CabinClass = CabinClass.BUSINESS,
    val flightState: FlightState = FlightState.TERMINAL,
    val totalTargetSeconds: Int = 1800, // Default 30 min timer
    val remainingSeconds: Int = 1800,
    val isPaused: Boolean = false,
    val windowShadeOpen: Float = 1.0f, // 1.0 = fully open, 0.0 = closed
    val isVoiceEnabled: Boolean = true,
    val isEngineNoiseEnabled: Boolean = true,
    val seatbeltSignOn: Boolean = true,
    val altitudeFeet: Int = 0,
    val speedKmh: Int = 0,
    val remainingDistanceKm: Int = 0,
    val progress: Float = 0f,
    val announcementText: String = "",
    val flightLogs: List<FlightLog> = emptyList(),
    val passportStamps: List<PassportStamp> = emptyList()
)

class FlightTimerViewModel(application: Application) : AndroidViewModel(application) {

    private val audioEngine = CabinAudioEngine(application)

    private val _uiState = MutableStateFlow(FlightUiState())
    val uiState: StateFlow<FlightUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var announcedHalfway = false
    private var announcedDescent = false

    init {
        loadLogsAndPassport()
        calculateTargetTime(_uiState.value.selectedRoute, _uiState.value.speedMultiplier)
    }

    fun loadLogsAndPassport() {
        viewModelScope.launch {
            val logs = FlightRepository.loadFlightLogs(getApplication())
            val stamps = FlightRepository.getPassportStamps(logs)
            _uiState.value = _uiState.value.copy(
                flightLogs = logs,
                passportStamps = stamps
            )
        }
    }

    fun selectRoute(route: FlightRoute) {
        _uiState.value = _uiState.value.copy(selectedRoute = route)
        calculateTargetTime(route, _uiState.value.speedMultiplier)
    }

    fun selectSpeedMultiplier(multiplier: SpeedMultiplier) {
        _uiState.value = _uiState.value.copy(speedMultiplier = multiplier)
        calculateTargetTime(_uiState.value.selectedRoute, multiplier)
    }

    fun selectCabinClass(cabinClass: CabinClass) {
        _uiState.value = _uiState.value.copy(cabinClass = cabinClass)
    }

    private fun calculateTargetTime(route: FlightRoute, multiplier: SpeedMultiplier) {
        val flightMinutes = route.durationMinutes
        val targetSeconds = maxOf((flightMinutes * 60) / multiplier.factor, 10)
        _uiState.value = _uiState.value.copy(
            totalTargetSeconds = targetSeconds,
            remainingSeconds = targetSeconds,
            remainingDistanceKm = route.distanceKm,
            progress = 0f
        )
    }

    fun startBoarding() {
        val route = _uiState.value.selectedRoute
        _uiState.value = _uiState.value.copy(
            flightState = FlightState.BOARDING,
            announcementText = "歡迎搭乘 ${route.airlineZh} ${route.flightNumber} 班機，前往 ${route.arrivalAirport.cityZh}。"
        )

        audioEngine.isVoiceEnabled = _uiState.value.isVoiceEnabled
        audioEngine.isEngineNoiseEnabled = _uiState.value.isEngineNoiseEnabled
        audioEngine.speakAnnouncement(
            "各位乘客您好，歡迎搭乘 ${route.airlineZh} ${route.flightNumber} 班機，由 ${route.departureAirport.cityZh} 前往 ${route.arrivalAirport.cityZh}。本次專注航程預計需要 ${formatMinutesText(route.durationMinutes)}。請繫好安全帶，專注之旅即將起飛！"
        )
    }

    fun startFlight() {
        announcedHalfway = false
        announcedDescent = false

        _uiState.value = _uiState.value.copy(
            flightState = FlightState.TAKEOFF,
            isPaused = false,
            seatbeltSignOn = true
        )

        audioEngine.speakAnnouncement("Cabin crew, prepare for takeoff. 機長廣播：專注號班機準備起飛，請開啟全螢幕並進入專注模式！")

        startTimerLoop()
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.flightState != FlightState.ARRIVED) {
                delay(1000L)
                if (_uiState.value.isPaused) continue

                val currentRemaining = _uiState.value.remainingSeconds - 1
                val totalSecs = _uiState.value.totalTargetSeconds
                val elapsedSecs = totalSecs - currentRemaining
                val currentProgress = elapsedSecs.toFloat() / totalSecs.toFloat()
                val route = _uiState.value.selectedRoute

                // Calculate Telemetry
                val (newState, newAltitude, newSpeed) = when {
                    currentProgress < 0.10f -> {
                        // Takeoff Phase
                        val alt = (35000 * (currentProgress / 0.10f)).toInt()
                        val spd = (250 + (630 * (currentProgress / 0.10f))).toInt()
                        Triple(FlightState.TAKEOFF, alt, spd)
                    }
                    currentProgress >= 0.90f -> {
                        // Descent Phase
                        val descFactor = (1.0f - currentProgress) / 0.10f
                        val alt = (35000 * descFactor).toInt()
                        val spd = (300 + (580 * descFactor)).toInt()
                        Triple(FlightState.DESCENT, alt, spd)
                    }
                    else -> {
                        // Cruising Phase
                        Triple(FlightState.CRUISING, 35000, 880)
                    }
                }

                val distRemaining = (route.distanceKm * (1.0f - currentProgress)).toInt().coerceAtLeast(0)

                _uiState.value = _uiState.value.copy(
                    remainingSeconds = currentRemaining,
                    progress = currentProgress,
                    flightState = newState,
                    altitudeFeet = newAltitude,
                    speedKmh = newSpeed,
                    remainingDistanceKm = distRemaining,
                    seatbeltSignOn = (newState == FlightState.TAKEOFF || newState == FlightState.DESCENT)
                )

                // Announce Cruising Reached
                if (newState == FlightState.CRUISING && currentProgress >= 0.10f && currentProgress < 0.12f && !announcedHalfway) {
                    audioEngine.speakAnnouncement("各位乘客，我們已順利抵達巡航高度 35,000 英呎，航程平穩。請展開您的深層專注。")
                }

                // Announce Halfway
                if (currentProgress >= 0.50f && !announcedHalfway) {
                    announcedHalfway = true
                    audioEngine.speakAnnouncement("各位乘客，我們已順利飛行一半航程！進度非常理想，請繼續保持深層專注狀態。")
                }

                // Announce Descent
                if (currentProgress >= 0.90f && !announcedDescent) {
                    announcedDescent = true
                    audioEngine.speakAnnouncement("機長廣播：我們已開始向 ${route.arrivalAirport.cityZh} 降落，請準備收尾當前工作。")
                }
            }

            if (_uiState.value.remainingSeconds <= 0) {
                onFlightCompleted()
            }
        }
    }

    private fun onFlightCompleted() {
        val state = _uiState.value
        val route = state.selectedRoute

        _uiState.value = state.copy(
            remainingSeconds = 0,
            progress = 1.0f,
            flightState = FlightState.ARRIVED,
            altitudeFeet = 0,
            speedKmh = 0,
            remainingDistanceKm = 0,
            seatbeltSignOn = false,
            announcementText = "歡迎抵達 ${route.arrivalAirport.cityZh}！專注任務達成！"
        )

        audioEngine.speakAnnouncement(
            "叮咚！我們已順利降落 ${route.arrivalAirport.nameZh}。恭喜您圓滿完成本次 ${route.durationMinutes} 分鐘的專注航程！入境章已存入您的專注護照。"
        )

        // Save Flight Log
        val completedLog = FlightLog(
            flightNumber = route.flightNumber,
            routeTitle = "${route.departureAirport.code} ➔ ${route.arrivalAirport.code}",
            departureCode = route.departureAirport.code,
            arrivalCode = route.arrivalAirport.code,
            arrivalCityZh = route.arrivalAirport.cityZh,
            arrivalCountryFlag = route.arrivalAirport.countryFlag,
            durationCompletedMinutes = route.durationMinutes,
            focusMinutesTotal = state.totalTargetSeconds / 60,
            distanceFlownKm = route.distanceKm,
            cabinClass = state.cabinClass
        )

        FlightRepository.saveFlightLog(getApplication(), completedLog)
        loadLogsAndPassport()
    }

    fun togglePause() {
        val newPause = !_uiState.value.isPaused
        _uiState.value = _uiState.value.copy(isPaused = newPause)
        if (newPause) {
            audioEngine.speakAnnouncement("專注航程已暫停，機長提醒您適度休息。", playChimeBefore = false)
        } else {
            audioEngine.speakAnnouncement("專注航程繼續，祝您工作順利。", playChimeBefore = false)
        }
    }

    fun callServiceBell() {
        audioEngine.playCabinChime()
        audioEngine.speakAnnouncement("叮咚！空服員提醒：請深呼吸、補充水分，保持最佳專注氣氛！", playChimeBefore = false)
    }

    fun toggleVoice() {
        val newVoice = !_uiState.value.isVoiceEnabled
        _uiState.value = _uiState.value.copy(isVoiceEnabled = newVoice)
        audioEngine.isVoiceEnabled = newVoice
    }

    fun toggleEngineNoise() {
        val newNoise = !_uiState.value.isEngineNoiseEnabled
        _uiState.value = _uiState.value.copy(isEngineNoiseEnabled = newNoise)
        audioEngine.isEngineNoiseEnabled = newNoise
    }

    fun setWindowShadeOpen(openFraction: Float) {
        _uiState.value = _uiState.value.copy(windowShadeOpen = openFraction.coerceIn(0f, 1f))
    }

    fun returnToTerminal() {
        timerJob?.cancel()
        audioEngine.isEngineNoiseEnabled = false
        _uiState.value = _uiState.value.copy(
            flightState = FlightState.TERMINAL,
            isPaused = false
        )
        calculateTargetTime(_uiState.value.selectedRoute, _uiState.value.speedMultiplier)
    }

    private fun formatMinutesText(mins: Int): String {
        val h = mins / 60
        val m = mins % 60
        return if (h > 0) "${h}小時${m}分鐘" else "${m}分鐘"
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        audioEngine.release()
    }
}
