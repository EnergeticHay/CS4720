package edu.virginia.cs4720.steploggerjbz2et

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.abs

private const val MAX_AXIS = 20f

@Composable
fun LiveScreen(
    viewModel: StepViewModel,
    onAllowSteps: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Step Logger", style = MaterialTheme.typography.headlineMedium)
        StepsCard(state, onAllowSteps, onOpenSettings)
        AccelerometerCard(state)
    }
}

@Composable
private fun StepsCard(
    state: StepUiState,
    onAllowSteps: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val appName = stringResource(R.string.app_name)
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Today's steps", style = MaterialTheme.typography.titleMedium)
            when {
                state.source == StepSource.NONE -> Text(
                    "This device has no step counter or accelerometer, so step counting is not available here."
                )
                state.access == StepAccess.GRANTED -> {
                    Text(
                        "%,d".format(state.todaySteps),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (state.source == StepSource.HARDWARE) {
                            "Counted by this device's hardware step counter."
                        } else {
                            "Estimated from the accelerometer because this device has no step counter."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                state.access == StepAccess.NOT_ASKED -> {
                    Text(
                        "$appName needs the Physical activity permission to count your steps, and your step totals never leave this phone.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onAllowSteps) { Text("Turn on step counting") }
                }
                else -> {
                    Text(
                        "Step counting is unavailable because the Physical activity permission is off. The live accelerometer below still works.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "To turn it on later, open Settings > Apps > $appName > Permissions > Physical activity and choose Allow.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onOpenSettings) { Text("Open Settings") }
                        if (state.canAskAgain) {
                            OutlinedButton(onClick = onAllowSteps) { Text("Ask again") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccelerometerCard(state: StepUiState) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Accelerometer (m/s²)", style = MaterialTheme.typography.titleMedium)
            if (state.hasAccelerometer) {
                AxisRow("X", state.x)
                AxisRow("Y", state.y)
                AxisRow("Z", state.z)
                HorizontalDivider()
                Text(
                    "Smoothed magnitude: %.2f".format(state.magnitude),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (state.source == StepSource.ACCELEROMETER) {
                    Text(
                        "A step counts when this peaks above %.1f".format(AccelStepDetector.STEP_THRESHOLD),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text("This device has no accelerometer, so live motion values are not available.")
            }
        }
    }
}

@Composable
private fun AxisRow(label: String, value: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(20.dp))
        LinearProgressIndicator(
            progress = { (abs(value) / MAX_AXIS).coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f)
        )
        Text(
            "%7.2f".format(value),
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace)
        )
    }
}