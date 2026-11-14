package edu.virginia.cs4720.steploggerjbz2et

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StepUiState(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val hasAccelerometer: Boolean = true
)

class StepViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StepUiState())
    val uiState: StateFlow<StepUiState> = _uiState.asStateFlow()

    private var lastUiUpdateNanos = 0L

    fun setHasAccelerometer(has: Boolean) {
        _uiState.update { it.copy(hasAccelerometer = has) }
    }

    fun onAccelerometer(x: Float, y: Float, z: Float, timestampNanos: Long) {
        if (timestampNanos - lastUiUpdateNanos < UI_INTERVAL_NANOS) return
        lastUiUpdateNanos = timestampNanos
        _uiState.update { it.copy(x = x, y = y, z = z) }
    }

    companion object {
        private const val UI_INTERVAL_NANOS = 100_000_000L
    }
}