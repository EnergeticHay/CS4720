package edu.virginia.cs4720.steploggerjbz2et

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class StepSource { HARDWARE, ACCELEROMETER, NONE }

enum class StepAccess { NOT_ASKED, GRANTED, DENIED }

data class StepUiState(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val magnitude: Float = 0f,
    val hasAccelerometer: Boolean = true,
    val source: StepSource = StepSource.NONE,
    val access: StepAccess = StepAccess.NOT_ASKED,
    val canAskAgain: Boolean = true,
    val todaySteps: Int = 0
)

class StepViewModel(private val repository: StepRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(StepUiState())
    val uiState: StateFlow<StepUiState> = _uiState.asStateFlow()

    val history: StateFlow<List<DailySteps>> = repository.observeHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val detector = AccelStepDetector()
    private var configured = false
    private var ready = false
    private var currentDate = LocalDate.now()
    private var todaySteps = 0
    private var lastCounterValue: Long? = null
    private var lastUiUpdateNanos = 0L
    private var saveJob: Job? = null

    private val countingEnabled: Boolean
        get() = ready && _uiState.value.access == StepAccess.GRANTED

    init {
        viewModelScope.launch {
            repository.deleteOlderThan(currentDate.minusDays(StepRepository.RETENTION_DAYS))
            todaySteps = repository.stepsOn(currentDate)
            lastCounterValue = repository.lastCounterValue()
            ready = true
            _uiState.update { it.copy(todaySteps = todaySteps) }
        }
    }

    fun configure(hasAccelerometer: Boolean, hasStepCounter: Boolean, permissionGranted: Boolean) {
        if (configured) return
        configured = true
        val source = when {
            hasStepCounter -> StepSource.HARDWARE
            hasAccelerometer -> StepSource.ACCELEROMETER
            else -> StepSource.NONE
        }
        _uiState.update {
            it.copy(
                hasAccelerometer = hasAccelerometer,
                source = source,
                access = if (permissionGranted) StepAccess.GRANTED else StepAccess.NOT_ASKED
            )
        }
    }

    fun onPermissionResult(granted: Boolean, canAskAgain: Boolean) {
        _uiState.update {
            it.copy(
                access = if (granted) StepAccess.GRANTED else StepAccess.DENIED,
                canAskAgain = canAskAgain
            )
        }
    }

    fun refreshPermission(granted: Boolean) {
        val current = _uiState.value.access
        if (granted && current != StepAccess.GRANTED) {
            _uiState.update { it.copy(access = StepAccess.GRANTED) }
        } else if (!granted && current == StepAccess.GRANTED) {
            _uiState.update { it.copy(access = StepAccess.DENIED, canAskAgain = false) }
        }
    }

    fun onAccelerometer(x: Float, y: Float, z: Float, timestampNanos: Long) {
        val isStep = detector.onSample(x, y, z, timestampNanos)
        if (isStep && countingEnabled && _uiState.value.source == StepSource.ACCELEROMETER) addSteps(1)

        if (timestampNanos - lastUiUpdateNanos < UI_INTERVAL_NANOS) return
        lastUiUpdateNanos = timestampNanos
        _uiState.update { it.copy(x = x, y = y, z = z, magnitude = detector.smoothedMagnitude) }
    }

    fun onStepCounter(total: Long) {
        if (!countingEnabled || _uiState.value.source != StepSource.HARDWARE) return
        val last = lastCounterValue
        lastCounterValue = total
        if (last != null) {
            val delta = if (total >= last) total - last else total
            if (delta > 0) addSteps(delta.toInt())
        }
        scheduleSave()
    }

    fun flush() {
        if (!ready) return
        saveJob?.cancel()
        viewModelScope.launch { persist() }
    }

    fun clearHistory() {
        saveJob?.cancel()
        todaySteps = 0
        lastCounterValue = null
        _uiState.update { it.copy(todaySteps = 0) }
        viewModelScope.launch { repository.clearAll() }
    }

    private fun addSteps(count: Int) {
        rollDayIfNeeded()
        todaySteps += count
        _uiState.update { it.copy(todaySteps = todaySteps) }
        scheduleSave()
    }

    private fun rollDayIfNeeded() {
        val now = LocalDate.now()
        if (now == currentDate) return
        val finishedDate = currentDate
        val finishedSteps = todaySteps
        if (finishedSteps > 0) {
            viewModelScope.launch { repository.saveDay(finishedDate, finishedSteps) }
        }
        currentDate = now
        todaySteps = 0
    }

    private fun scheduleSave() {
        if (saveJob?.isActive == true) return
        saveJob = viewModelScope.launch {
            delay(SAVE_INTERVAL_MS)
            persist()
        }
    }

    private suspend fun persist() {
        if (todaySteps > 0) repository.saveDay(currentDate, todaySteps)
        repository.saveCounterValue(lastCounterValue)
    }

    companion object {
        private const val UI_INTERVAL_NANOS = 100_000_000L
        private const val SAVE_INTERVAL_MS = 2_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as StepLoggerApplication
                StepViewModel(app.repository)
            }
        }
    }
}