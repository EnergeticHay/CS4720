package edu.virginia.cs4720.steploggerjbz2et

import android.hardware.SensorManager
import kotlin.math.sqrt

class AccelStepDetector {
    private val window = ArrayDeque<Float>()
    private var previous = SensorManager.GRAVITY_EARTH
    private var rising = false
    private var lastStepNanos = 0L

    var smoothedMagnitude = SensorManager.GRAVITY_EARTH
        private set

    fun onSample(x: Float, y: Float, z: Float, timestampNanos: Long): Boolean {
        window.addLast(sqrt(x * x + y * y + z * z))
        if (window.size > WINDOW_SIZE) window.removeFirst()
        smoothedMagnitude = window.average().toFloat()

        val wasPeak = rising && smoothedMagnitude < previous && previous > STEP_THRESHOLD
        rising = smoothedMagnitude > previous
        previous = smoothedMagnitude

        if (wasPeak && timestampNanos - lastStepNanos >= MIN_STEP_GAP_NANOS) {
            lastStepNanos = timestampNanos
            return true
        }
        return false
    }

    companion object {
        const val STEP_THRESHOLD = 11.0f
        const val WINDOW_SIZE = 4
        const val MIN_STEP_GAP_NANOS = 300_000_000L
    }
}