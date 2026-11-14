package edu.virginia.cs4720.steploggerjbz2et

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import edu.virginia.cs4720.steploggerjbz2et.ui.theme.StepLoggerjbz2etTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    private val viewModel: StepViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var stepCounter: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SensorManager::class.java)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        viewModel.configure(accelerometer != null, stepCounter != null)
        enableEdgeToEdge()
        setContent {
            StepLoggerjbz2etTheme {
                LiveScreen(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        stepCounter?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> viewModel.onAccelerometer(
                event.values[0], event.values[1], event.values[2], event.timestamp
            )
            Sensor.TYPE_STEP_COUNTER -> viewModel.onStepCounter(event.values[0].toLong())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}