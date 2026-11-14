package edu.virginia.cs4720.steploggerjbz2et

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class StepSource { HARDWARE, ACCELEROMETER, NONE }

data class StepUiState(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val magnitude: Float = 0f,
    val hasAccelerometer: Boolean = true,
    val source: StepSource = StepSource.NONE,
    val todaySteps: Int = 0
)

class StepViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StepUiState())
    val uiState: StateFlow<StepUiState> = _uiState.asStateFlow()

    private val detector = AccelStepDetector()
    private var configured = false
    private var todaySteps = 0
    private var lastCounterValue: Long? = null
    private var lastUiUpdateNanos = 0L

    fun configure(hasAccelerometer: Boolean, hasStepCounter: Boolean) {
        if (configured) return
        configured = true
        val source = when {
            hasStepCounter -> StepSource.HARDWARE
            hasAccelerometer -> StepSource.ACCELEROMETER
            else -> StepSource.NONE
        }
        _uiState.update { it.copy(hasAccelerometer = hasAccelerometer, source = source) }
    }

    fun onAccelerometer(x: Float, y: Float, z: Float, timestampNanos: Long) {
        val isStep = detector.onSample(x, y, z, timestampNanos)
        if (isStep && _uiState.value.source == StepSource.ACCELEROMETER) addSteps(1)

        if (timestampNanos - lastUiUpdateNanos < UI_INTERVAL_NANOS) return
        lastUiUpdateNanos = timestampNanos
        _uiState.update { it.copy(x = x, y = y, z = z, magnitude = detector.smoothedMagnitude) }
    }

    fun onStepCounter(total: Long) {
        if (_uiState.value.source != StepSource.HARDWARE) return
        val last = lastCounterValue
        lastCounterValue = total
        if (last == null) return
        val delta = if (total >= last) total - last else total
        if (delta > 0) addSteps(delta.toInt())
    }

    private fun addSteps(count: Int) {
        todaySteps += count
        _uiState.update { it.copy(todaySteps = todaySteps) }
    }

    companion object {
        private const val UI_INTERVAL_NANOS = 100_000_000L
    }
}