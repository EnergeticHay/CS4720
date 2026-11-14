package edu.virginia.cs4720.steploggerjbz2et

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import edu.virginia.cs4720.steploggerjbz2et.ui.theme.StepLoggerjbz2etTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    private val viewModel: StepViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var stepCounter: Sensor? = null
    private var stepListenerRegistered = false

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.onPermissionResult(granted, canAskAgain())
            if (granted) registerStepCounter()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SensorManager::class.java)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        viewModel.configure(accelerometer != null, stepCounter != null, hasStepPermission())
        enableEdgeToEdge()
        setContent {
            StepLoggerjbz2etTheme {
                LiveScreen(
                    viewModel = viewModel,
                    onAllowSteps = ::requestStepPermission,
                    onOpenSettings = ::openAppSettings
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        val granted = hasStepPermission()
        viewModel.refreshPermission(granted)
        if (granted) registerStepCounter()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        stepListenerRegistered = false
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

    private fun registerStepCounter() {
        val sensor = stepCounter ?: return
        if (stepListenerRegistered) return
        stepListenerRegistered =
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun hasStepPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) ==
                PackageManager.PERMISSION_GRANTED

    private fun canAskAgain(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)

    private fun requestStepPermission() {
        if (hasStepPermission()) {
            viewModel.onPermissionResult(true, false)
            registerStepCounter()
        } else {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    private fun openAppSettings() {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        )
    }
}